package io.gaia_app.runner;

import io.gaia_app.runner.docker.DockerRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class StepRunner {

    private static final Logger LOG = LoggerFactory.getLogger(StepRunner.class);

    private static final String API_RUNNER_STEPS = "/api/runner/steps/";

    private final DockerRunner dockerRunner;

    private final RestTemplate restTemplate;

    @Value("${gaia.url}")
    private String gaiaUrl;

    public StepRunner(DockerRunner dockerRunner, RestTemplate restTemplate) {
        this.dockerRunner = dockerRunner;
        this.restTemplate = restTemplate;
    }

    @Async
    public void runStep(RunnerStep runnerStep) {
        var script = runnerStep.getScript();
        var step = runnerStep.getStep();
        var env = runnerStep.getEnv();
        var image = runnerStep.getImage();

        // tell gaia that the job starts
        this.restTemplate.put(gaiaUrl+ API_RUNNER_STEPS +step.getId()+"/start", null);

        LOG.info("Starting step {} execution.", step.getId());

        // configure a logger to ship logs back to gaia
        StepLogger logger = log -> this.restTemplate.put(gaiaUrl+API_RUNNER_STEPS+step.getId()+"/logs", log);

        var result = dockerRunner.runJobStepInContainer(image, logger, script, env);

        LOG.info("Finished step {} execution with result code {}.", step.getId(), result);
        LOG.info("Sending result.");

        // sending final result code
        this.restTemplate.put(gaiaUrl+API_RUNNER_STEPS+step.getId()+"/status", result);
    }
}
