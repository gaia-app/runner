package io.gaia_app.runner.docker;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource(properties = {
        "gaia.runner.docker.daemon-url=tcp://localhost:2375",
        "gaia.runner.api.password=random-password",
})
class DockerConfigurationPropertiesTest {

    @Autowired
    private DockerConfigurationProperties properties;

    @Test
    void testGeneralProperties(){
        assertThat(properties.getDaemonUrl()).isEqualTo("tcp://localhost:2375");
    }

}