package io.gaia_app.runner.k8s;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix="gaia.runner.k8s")
@ConditionalOnProperty(name="gaia.runner.executor", havingValue = "k8s")
class K8SConfigurationProperties {

    private String namespace;

    String getNamespace() {
        return namespace;
    }

    void setNamespace(String namespace) {
        this.namespace = namespace;
    }
}
