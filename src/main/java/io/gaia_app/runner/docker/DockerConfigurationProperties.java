package io.gaia_app.runner.docker;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix="gaia.runner.docker")
public class DockerConfigurationProperties {

    private String daemonUrl;

    public String getDaemonUrl() {
        return daemonUrl;
    }

    public void setDaemonUrl(String daemonUrl) {
        this.daemonUrl = daemonUrl;
    }

}

