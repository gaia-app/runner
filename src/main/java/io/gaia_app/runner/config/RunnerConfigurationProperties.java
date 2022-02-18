package io.gaia_app.runner.config;

import io.gaia_app.runner.ExecutorType;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

@Configuration
@ConfigurationProperties(prefix = "gaia.runner")
public class RunnerConfigurationProperties {

    private int concurrency = 10;

    private ExecutorType executor = ExecutorType.DOCKER;

    public int getConcurrency() {
        return concurrency;
    }

    public void setConcurrency(int concurrency) {
        this.concurrency = concurrency;
    }

    public ExecutorType getExecutor() {
        return executor;
    }

    public void setExecutor(ExecutorType executor) {
        this.executor = executor;
    }

    @Bean
    @ConfigurationProperties(prefix = "gaia.runner.api")
    @Validated
    public RunnerApiProperties properties(){
        return new RunnerApiProperties("gaia-runner");
    }

    public static class RunnerApiProperties {

        @NotBlank
        private String username;

        @NotBlank
        private String password;

        public RunnerApiProperties(String username) {
            this.username = username;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

    }
}
