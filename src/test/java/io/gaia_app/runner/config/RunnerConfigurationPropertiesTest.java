package io.gaia_app.runner.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = {RunnerConfiguration.class, RunnerConfigurationProperties.class})
@TestPropertySource(properties = {
        "gaia.runner.concurrency=1",
        "gaia.runner.api.username=my-gaia-api-username",
        "gaia.runner.api.password=my-gaia-api-password"
})
class RunnerConfigurationPropertiesTest {

    @Autowired
    private RunnerConfigurationProperties properties;

    @Autowired
    private RunnerConfigurationProperties.RunnerApiProperties runnerApiProperties;

    @Test
    void testGeneralProperties(){
        assertThat(properties.getConcurrency()).isEqualTo(1);
    }

    @Test
    void testRunnerApiProperties(){
        assertThat(runnerApiProperties.getUsername()).isEqualTo("my-gaia-api-username");
        assertThat(runnerApiProperties.getPassword()).isEqualTo("my-gaia-api-password");
    }

}