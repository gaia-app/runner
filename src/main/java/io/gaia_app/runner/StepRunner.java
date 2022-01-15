package io.gaia_app.runner;

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

    private final Executor executor;

    private final RestTemplate restTemplate;

    @Value("${gaia.url}")
    private String gaiaUrl;

    public StepRunner(Executor executor, RestTemplate restTemplate) {
        this.executor = executor;
        this.restTemplate = restTemplate;
    }

    @Async
    public void runStep(RunnerStep runnerStep) {
        var stepId = runnerStep.getId();
        var image = runnerStep.getImage();
        var script = runnerStep.getScript();
        var env = runnerStep.getEnv();

        // tell gaia that the job starts
        this.restTemplate.put(gaiaUrl+ API_RUNNER_STEPS +stepId+"/start", null);

        LOG.info("Starting step {} execution.", stepId);

        // configure a logger to ship logs back to gaia
        StepLogger logger = log -> this.restTemplate.put(gaiaUrl+API_RUNNER_STEPS+stepId+"/logs", log);

        var result = executor.executeJobStep(image, logger, script, env);

        LOG.info("Finished step {} execution with result code {}.", stepId, result);
        LOG.info("Sending result.");

        // sending final result code
        this.restTemplate.put(gaiaUrl+API_RUNNER_STEPS+stepId+"/end", result);
    }
}
