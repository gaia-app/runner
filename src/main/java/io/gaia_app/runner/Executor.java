package io.gaia_app.runner;

/**
 * Interface of the executors.
 * This interface should be implemented for all Runner executors (docker, k8s, ...)
 */
public interface Executor {

    /**
     * Run a Gaia job step.
     * @param runnerStep the step to run. It contains the script and the env vars to use.
     * @param logger the logger to use. All the logs will be send to Gaia.
     * @return the result code of the execution, 0 if everything went well, !0 otherwise
     */
    int executeJobStep(RunnerStep runnerStep, StepLogger logger);
}
