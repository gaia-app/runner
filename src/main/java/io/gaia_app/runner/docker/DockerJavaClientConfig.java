package io.gaia_app.runner.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.okhttp.OkDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URI;

/**
 * Configuration of the docker transport
 */
@Configuration
public class DockerJavaClientConfig {

    private final DockerConfigurationProperties configurationProperties;

    public DockerJavaClientConfig(DockerConfigurationProperties configurationProperties) {
        this.configurationProperties = configurationProperties;
    }

    /**
     * builds the DockerClientConfig based on Gaia's settings
     * @return a docker client configuration
     */
    @Bean
    DockerClientConfig dockerClientConfig() {
        return DefaultDockerClientConfig.createDefaultConfigBuilder()
            .withDockerHost(configurationProperties.getDaemonUrl())
            .build();
    }

    /**
     * builds the docker client
     * @param config the configuration (host/registries...)
     * @param httpClient this docker http client to use (okhttp, etc...)
     * @return
     */
    @Bean
    DockerClient client(DockerClientConfig config, DockerHttpClient httpClient){
        return DockerClientImpl.getInstance(config, httpClient);
    }

    @Bean
    DockerHttpClient httpClient(){
        return new OkDockerHttpClient.Builder()
                .dockerHost(URI.create(configurationProperties.getDaemonUrl()))
                .build();
    }


}
