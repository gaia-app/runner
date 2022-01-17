package io.gaia_app.runner.k8s;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.gaia_app.runner.Executor;
import io.gaia_app.runner.StepLogger;
import io.kubernetes.client.Attach;
import io.kubernetes.client.PodLogs;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1EnvVarBuilder;
import io.kubernetes.client.openapi.models.V1PodBuilder;
import io.kubernetes.client.util.wait.Wait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Duration;
import java.util.ArrayList;

@Service
@ConditionalOnProperty(name = "gaia.runner.executor", havingValue = "k8s")
public class K8SExecutor implements Executor {

    private static final System.Logger LOGGER = System.getLogger("K8SExecutor");

    private CoreV1Api coreV1Api;

    private K8SConfigurationProperties properties;

    @Autowired
    K8SExecutor(CoreV1Api coreV1Api, K8SConfigurationProperties properties) {
        this.coreV1Api = coreV1Api;
        this.properties = properties;
    }

    @Override
    public int executeJobStep(String image, StepLogger logger, String script, List<String> jobEnv) {
        var namespace = properties.getNamespace();
        var podName = "gaia-job-" + UUID.randomUUID();

        var env = new ArrayList<String>();
        env.add("TF_IN_AUTOMATION=true");
        env.addAll(jobEnv);

        // try to create a pod definition
        var envVars = env.stream()
                .map(it -> it.split("="))
                .map(it -> new V1EnvVarBuilder().withName(it[0]).withValue(it[1]).build())
                .toList();

        var pod = new V1PodBuilder()
                .withNewMetadata()
                .withName(podName)
                .withNamespace(namespace)
                .endMetadata()
                .withNewSpec()
                .withRestartPolicy("Never")
                .addNewContainer()
                .withName("terraform")
                .withCommand("/bin/sh")
                // open stdin to send commands when attaching to the pod
                .withStdin(true)
                .withEnv(envVars)
                .withImage(image)
                .endContainer()
                .endSpec()
                .build();

        LOGGER.log(System.Logger.Level.INFO, "Creating pod {0}", podName);
        try {
            coreV1Api.createNamespacedPod(namespace, pod, null, null, null);
        } catch (ApiException e) {
            LOGGER.log(System.Logger.Level.ERROR, "Unable to create Kubernetes pod, Kubernetes API response : {0}", getApiExceptionResponseBodyMessage(e));
            return 99;
        }

        LOGGER.log(System.Logger.Level.INFO, "Wait for the pod {0} to be running", podName);
        Wait.poll(Duration.ofSeconds(1), Duration.ofMinutes(1), () -> {
            try {
                var podStatus = coreV1Api.readNamespacedPod(podName, namespace, null).getStatus();
                return "Running".equals(podStatus.getPhase());
            } catch (ApiException e) {
                LOGGER.log(System.Logger.Level.ERROR, "Unable to get status of pod {0} : {1}", podName, getApiExceptionResponseBodyMessage(e));
                return false;
            }
        });

        // attach to the running pod and write the content of the script to the container's stdin
        LOGGER.log(System.Logger.Level.INFO, "Executing script in pod {0}", podName);
        try {
            var attachment = new Attach().attach(pod, true);
            var stdin = attachment.getStandardInputStream();
            stdin.write((script + "\n").getBytes());
            stdin.flush();

            LOGGER.log(System.Logger.Level.INFO, "Getting pod {0} logs", podName);
            // copy logs (this is done in a sync way, maybe do that in a separate thread?
            try (var stdout = new PodLogs().streamNamespacedPodLog(pod)) {
                var logsReader = new BufferedReader(new InputStreamReader(stdout));
                logsReader.lines().forEach(logger::log);
            }

        } catch (ApiException e) {
            LOGGER.log(System.Logger.Level.ERROR, "Unable to send script to Kubernetes pod {0} : {1}", podName, getApiExceptionResponseBodyMessage(e));
            return destroyPodAndQuit(podName, namespace);
        } catch (IOException | NullPointerException e) {
            LOGGER.log(System.Logger.Level.ERROR, "Unable to send script to Kubernetes pod {0} : {1}", podName, e.getMessage());
            return destroyPodAndQuit(podName, namespace);
        }

        LOGGER.log(System.Logger.Level.INFO, "Wait for the pod {0} to be completed", podName);
        Wait.poll(Duration.ofSeconds(1), Duration.ofMinutes(30), () -> {
            try {
                var podStatus = coreV1Api.readNamespacedPod(podName, namespace, null).getStatus();
                return "Succeeded".equals(podStatus.getPhase()) || "Failed".equals(podStatus.getPhase());
            } catch (ApiException e) {
                LOGGER.log(System.Logger.Level.ERROR, "Unable to get completion status of pod {0} : {1}", podName, getApiExceptionResponseBodyMessage(e));
            }
            return false;
        });

        int statusCode = -1;
        LOGGER.log(System.Logger.Level.INFO, "Getting exit code of pod {0}", podName);
        try {
            var podStatus = coreV1Api.readNamespacedPod(podName, namespace, null).getStatus();
            statusCode = podStatus.getContainerStatuses().get(0).getState().getTerminated().getExitCode();
            LOGGER.log(System.Logger.Level.INFO, "Pod {0} exited with status code {1}", podName, statusCode);
        } catch (ApiException e) {
            LOGGER.log(System.Logger.Level.ERROR, "Unable to get exit code of pod {0} : {1}", podName, getApiExceptionResponseBodyMessage(e));
        }

        LOGGER.log(System.Logger.Level.INFO, "Deleting the pod {0}", podName);
        try {
            coreV1Api.deleteNamespacedPod(podName, namespace, null, null, null, null, null, null);
        } catch (ApiException e) {
            LOGGER.log(System.Logger.Level.ERROR, "Unable to delete Kubernetes pod, Kubernetes API response : {0}", e.getResponseBody());
            LOGGER.log(System.Logger.Level.ERROR, "You may need to run `kubectl delete pod {0} -n {0}` to remove the dangling pod", podName, namespace);
            return -1;
        }

        return statusCode;
    }

    record ApiExceptionResponseBody(String message) {
    }

    private String getApiExceptionResponseBodyMessage(ApiException e) {
        if (e.getMessage().isBlank()) {
            var objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            try {
                var body = objectMapper.readValue(e.getResponseBody(), ApiExceptionResponseBody.class);
                return body.message;
            } catch (JsonProcessingException ex) {
                LOGGER.log(System.Logger.Level.ERROR, "Could not process response message in body: {0}", e.getResponseBody());
            }
        }
        return e.getMessage();
    }

    /**
     * Error mgmt method that destroys the given pod
     * @return
     */
    private int destroyPodAndQuit(String podName, String namespace){
        try {
            coreV1Api.deleteNamespacedPod(podName, namespace, null, null, null, null, null, null);
        } catch (ApiException e) {
            LOGGER.log(System.Logger.Level.ERROR, "Unable to delete Kubernetes pod, Kubernetes API response : {0}", e.getResponseBody());
        }
        return 99;
    }
}
