package io.gaia_app.runner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
public class StepPoller {

    private static final Logger logger = LoggerFactory.getLogger(StepPoller.class);

    @Value("${gaia.url}")
    private String gaiaUrl;

    private final RestTemplate restTemplate;

    private final StepRunner stepRunner;

    public StepPoller(StepRunner stepRunner, RestTemplate restTemplate) {
        this.stepRunner = stepRunner;
        this.restTemplate = restTemplate;
    }

    /**
     * Poll for steps every five second
     */
    @Scheduled(fixedDelay=5*1000)
    void getJobAndRun(){
        try {
            logger.info("Polling for pending steps");
            var runnerJob = restTemplate.getForObject(gaiaUrl + "/api/runner/steps/request", RunnerStep.class);
            if (runnerJob == null) {
                logger.info("No steps to run");
                return;
            }
            // job is run asynchronously
            logger.info("Step found {}. Running.", runnerJob.getStep().getId());
            stepRunner.runStep(runnerJob);
        }
        catch(HttpClientErrorException.NotFound e){
            // when having a not found, no job has to be run
            logger.info("No steps to run");
        }
        catch (RestClientException e){
            logger.error("Unable to connect to gaia find jobs to run : {}", e.getMessage());
        }
    }
}
