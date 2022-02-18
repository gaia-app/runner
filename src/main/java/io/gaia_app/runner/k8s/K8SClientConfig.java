package io.gaia_app.runner.k8s;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.util.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

@Configuration
@ConditionalOnProperty(name = "gaia.runner.executor", havingValue = "k8s")
public class K8SClientConfig {

    private static final Logger LOGGER = Logger.getLogger("K8SClientConfig");

    private static final Path NAMESPACE_FILE_PATH = Path.of(Config.SERVICEACCOUNT_NAMESPACE_PATH);

    @Bean
    CoreV1Api coreV1Api() throws IOException {
        ApiClient client = Config.defaultClient();
        io.kubernetes.client.openapi.Configuration.setDefaultApiClient(client);

        return new CoreV1Api(client);
    }

    /**
     * Dynamically configures namespace by reading in-cluster configuration if available
     */
    @Autowired
    void configureNamespace(K8SConfigurationProperties properties) {
        if (properties.getNamespace() == null) {
            loadNamespaceFromInCluster(properties, NAMESPACE_FILE_PATH);
        }
    }

    void loadNamespaceFromInCluster(K8SConfigurationProperties properties, Path path) {
        try {
            // try to load in-cluster namespace
            LOGGER.log(Level.INFO, "Kubernetes namespace not configured, try to read in-cluster namespace");
            if (Files.exists(path)) {
                properties.setNamespace(Files.readString(path));
                LOGGER.log(Level.INFO, "Loaded namespace {0}", properties.getNamespace());
            } else {
                LOGGER.log(Level.SEVERE, "Could not load Kubernetes namespace info. Please configure the `gaia.runner.k8s.namespace` property or `GAIA_RUNNER_K8S_NAMESPACE` env var.");
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Could not load Kubernetes namespace info. Please configure the `gaia.runner.k8s.namespace` property or `GAIA_RUNNER_K8S_NAMESPACE` env var.");
        }
    }
}
