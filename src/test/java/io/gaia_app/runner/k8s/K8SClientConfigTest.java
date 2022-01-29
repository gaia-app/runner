package io.gaia_app.runner.k8s;

import io.gaia_app.runner.test_utils.LastLogRecordHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.assertj.core.api.Assertions.assertThat;

class K8SClientConfigTest {

    private LastLogRecordHandler lastLogRecordHandler = new LastLogRecordHandler();

    @BeforeEach
    void setUp() {
        var logger = Logger.getLogger("K8SClientConfig");
        logger.addHandler(lastLogRecordHandler);
    }

    @Test
    void testLoadNamespaceFromInCluster() throws IOException {
        var clientConfig = new K8SClientConfig();
        var properties = new K8SConfigurationProperties();

        var path = Files.createTempFile("test", "namespace");
        Files.writeString(path, "gaia-unit-test-namespace");

        clientConfig.loadNamespaceFromInCluster(properties, path);

        assertThat(properties.getNamespace()).isEqualTo("gaia-unit-test-namespace");
    }

    @Test
    void testLoadNamespaceFromInCluster_shouldDoNothingIfFileDoesntExists() throws IOException {
        var clientConfig = new K8SClientConfig();

        var properties = new K8SConfigurationProperties();

        var path = Path.of("non-existing-file-path");

        clientConfig.loadNamespaceFromInCluster(properties, path);

        assertThat(properties.getNamespace()).isNull();

        assertThat(lastLogRecordHandler.getLastLog().getLevel()).isEqualTo(Level.SEVERE);
        assertThat(lastLogRecordHandler.getLastLog().getMessage()).contains("Could not load Kubernetes namespace info");
    }

}