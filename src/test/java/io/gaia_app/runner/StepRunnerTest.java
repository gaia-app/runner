package io.gaia_app.runner;

import io.gaia_app.runner.docker.DockerRunner;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StepRunnerTest {

    @InjectMocks
    private StepRunner stepRunner;

    @Mock
    private DockerRunner dockerRunner;

    @Mock
    private RestTemplate restTemplate;

    @Test
    void runStep_shouldRunAStep() {
        // given
        var image = "hashicorp/terraform:0.13.0";
        var script = "echo 'Hello'";
        var runnerStep = new RunnerStep("12", image, script, List.of());

        // when
        stepRunner.runStep(runnerStep);

        // then
        verify(dockerRunner).runJobStepInContainer(eq(image), any(StepLogger.class), eq(script), eq(List.of()));
    }

    @Test
    void runStep_shouldRunNotifyThatTheStepHasStartedAndEnded() {
        // given
        var image = "hashicorp/terraform:0.13.0";
        var script = "echo 'Hello'";
        var runnerStep = new RunnerStep("12", image, script, List.of());

        ReflectionTestUtils.setField(stepRunner, "gaiaUrl", "http://localhost:8080");

        when(dockerRunner.runJobStepInContainer(eq(image), any(StepLogger.class), eq(script), eq(List.of()))).thenReturn(2);

        // when
        stepRunner.runStep(runnerStep);

        // then
        verify(restTemplate).put("http://localhost:8080/api/runner/steps/12/start", null);
        verify(restTemplate).put("http://localhost:8080/api/runner/steps/12/end", 2);
    }
}