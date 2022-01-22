package io.gaia_app.runner.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.exception.DockerException;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.Volume;
import io.gaia_app.runner.Executor;
import io.gaia_app.runner.RunnerStep;
import io.gaia_app.runner.StepLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Service to run docker container
 */
@Service
@ConditionalOnProperty(name="gaia.runner.executor", havingValue = "docker")
public class DockerExecutor implements Executor {

    private static final Logger LOG = LoggerFactory.getLogger(DockerExecutor.class);

    private DockerClient dockerClient;

    @Autowired
    public DockerExecutor(DockerClient dockerClient) {
        this.dockerClient = dockerClient;
    }

    @Override
    public int executeJobStep(RunnerStep step, StepLogger logger) {
        var image = step.getImage();
        var script = step.getScript();
        var jobEnv = step.getEnv();

        try {
            var env = new ArrayList<String>();
            env.add("TF_IN_AUTOMATION=true");
            env.addAll(jobEnv);

            // pulling the image
            dockerClient.pullImageCmd(image)
                .start()
                .awaitCompletion();

            // create a new container
            var createContainerCmd = dockerClient.createContainerCmd(image)
                // resetting entrypoint to empty
                .withEntrypoint()
                // using /bin/sh as command
                .withCmd("/bin/sh")
                // using env vars
                .withEnv(env)
                // open stdin
                .withTty(false)
                .withStdinOpen(true)
                // bind mount docker sock (to be able to use docker provider in terraform)
                .withHostConfig(HostConfig.newHostConfig().withBinds(new Bind("/var/run/docker.sock", new Volume("/var/run/docker.sock"))));

            var container = createContainerCmd.exec();
            var containerId = container.getId();

            // start the container
            dockerClient.startContainerCmd(containerId).exec();

            // write the content of the script to the container's std in
            try (
                var out = new PipedOutputStream();
                var in = new PipedInputStream(out);
                var containerLogsCallback = new ResultCallback.Adapter<Frame>(){
                    @Override
                    public void onNext(Frame frame) {
                        logger.log(new String(frame.getPayload()));
                    }
                }

                ){
                dockerClient.attachContainerCmd(container.getId())
                    .withFollowStream(true)
                    .withStdOut(true)
                    .withStdErr(true)
                    .withStdIn(in)
                    .exec(containerLogsCallback);

                out.write((script + "\n").getBytes());
                out.flush();

                containerLogsCallback.awaitCompletion(1800, TimeUnit.SECONDS);
            }

            // wait for the container to exit
            var containerExit = dockerClient.waitContainerCmd(containerId)
                .start()
                // with a timeout of 30 minutes
                .awaitStatusCode(1800, TimeUnit.SECONDS);

            dockerClient.removeContainerCmd(containerId).exec();

            return containerExit;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOG.error("Interrupted Exception", e);
            return 99;
        } catch (DockerException | IOException e) {
            LOG.error("Exception when running job", e);
            return 99;
        }
    }

}
