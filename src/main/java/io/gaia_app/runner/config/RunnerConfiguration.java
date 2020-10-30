package io.gaia_app.runner.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.concurrent.Executor;

@Configuration
@EnableScheduling
@EnableAsync
@EnableConfigurationProperties
public class RunnerConfiguration {

    @Bean
    public Executor taskExecutor(RunnerConfigurationProperties properties) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(properties.getConcurrency());
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("gaia-runner-");
        executor.initialize();
        return executor;
    }

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setInterceptors(List.of(new BasicAuthenticationInterceptor("gaia-runner","password")));
        return restTemplate;
    }

}
