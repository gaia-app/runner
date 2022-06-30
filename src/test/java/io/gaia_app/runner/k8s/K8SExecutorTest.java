package io.gaia_app.runner.k8s;

import io.gaia_app.runner.RunnerStep;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.*;
import okhttp3.Request;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class K8SExecutorTest {

    public static final String POD_NAME = "gaia-job-1234";
    public static final String NAMESPACE = "test-namespace";
    @InjectMocks
    private K8SExecutor executor;

    @Mock
    private CoreV1Api k8sApi;

    @Mock
    private ApiClient apiClient;

    @Mock
    private K8SConfigurationProperties properties;

    @BeforeEach
    void setUp() {
        when(properties.getNamespace()).thenReturn(NAMESPACE);
    }

    @Nested
    class ErrorMgmt {

        @Test
        void shouldDoNothing_whenPodCantBeCreated() throws ApiException {
            var k8sException = new ApiException("Could not create pod");
            doThrow(k8sException).when(k8sApi).createNamespacedPod(anyString(), any(V1Pod.class), isNull(), isNull(), isNull(), isNull());

            var step = new RunnerStep("1234", "terraform:latest", "echo 'Hello'", List.of());
            var result = executor.executeJobStep(step, log -> {});

            assertThat(result).isEqualTo(99);

            verify(k8sApi).createNamespacedPod(anyString(), any(V1Pod.class), isNull(), isNull(), isNull(), isNull());

            verifyNoMoreInteractions(k8sApi);
        }

        @Test
        void shouldDestroyPod_whenPodCanBeCreated_butScriptCantBeExecuted() throws ApiException {
            var runningPod = new V1Pod();
            runningPod.setStatus(new V1PodStatus());
            runningPod.getStatus().setPhase("Running");
            when(k8sApi.readNamespacedPod(POD_NAME, NAMESPACE, null)).thenReturn(runningPod);

            var step = new RunnerStep("1234", "terraform:latest", "echo 'Hello'", List.of());
            var result = executor.executeJobStep(step, log -> {});

            assertThat(result).isEqualTo(99);

            verify(k8sApi).createNamespacedPod(anyString(), any(V1Pod.class), isNull(), isNull(), isNull(), isNull());
            verify(k8sApi).deleteNamespacedPod(anyString(), anyString(), isNull(), isNull(), isNull(), isNull(), isNull(), isNull());
            verifyNoMoreInteractions(k8sApi);
        }

    }

}