package io.gaia_app.runner;

import java.util.List;

/**
 * Interface of the executors.
 * This interface should be implemented for all Runner executors (docker, k8s, ...)
 */
public interface Executor {

    /**
     * Run a Gaia job step.
     * @param image the docker image to use
     * @param logger the logger to use. All the logs will be send to Gaia.
     * @param script the script to executre
     * @param jobEnv env vars to use when executing the script
     * @return the result code of the execution, 0 if everything went well, !0 otherwise
     */
    int executeJobStep(String image, StepLogger logger, String script, List<String> jobEnv);
}
