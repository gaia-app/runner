package io.gaia_app.runner.docker

import io.gaia_app.runner.StepLogger
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource

@SpringBootTest(classes = [DockerExecutor::class, DockerJavaClientConfig::class, DockerConfigurationProperties::class])
@EnableConfigurationProperties
@TestPropertySource(properties = [
    "gaia.runner.executor=docker",
])
class DockerExecutorIT {

    @Autowired
    private lateinit var dockerExecutor: DockerExecutor

    private val image = "hashicorp/terraform:0.13.0"

    private val printlnLogger = StepLogger { println(it) }

    @Test
    fun `runContainerForJob() should work with a simple script`() {
        val script = "echo 'Hello World'; exit 0;"

        assertEquals(0, dockerExecutor.executeJobStep(image, printlnLogger, script, listOf()).toLong())
    }

    @Test
    fun `runContainerForJob() should stop work with a simple script`() {
        val script = "set -e; echo 'Hello World'; false; exit 0;"

        assertEquals(1, dockerExecutor.executeJobStep(image, printlnLogger, script, listOf()).toLong())
    }

    @Test
    fun `runContainerForJob() should return the script exit code`() {
        val script = "exit 5"

        assertEquals(5, dockerExecutor.executeJobStep(image, printlnLogger, script, listOf()).toLong())
    }

    @Test
    fun `runContainerForJob() should feed step with container logs`() {
        val script = "echo 'hello world'; exit 0;"

        val logs = mutableListOf<String>()
        val listLogger = StepLogger { logs.add(it) }

        dockerExecutor.executeJobStep(image, listLogger, script, listOf())
        assertThat(logs).isEqualTo(listOf("hello world\n"))
    }

    @Test
    fun `runContainerForJob() use env of the job`() {
        val script = "echo \$AWS_ACCESS_KEY_ID; exit 0;"

        val logs = mutableListOf<String>()
        val listLogger = StepLogger { logs.add(it) }

        dockerExecutor.executeJobStep(image, listLogger, script, listOf("AWS_ACCESS_KEY_ID=SOME_ACCESS_KEY"))

        assertThat(logs).isEqualTo(listOf("SOME_ACCESS_KEY\n"))
    }

    @Test
    fun `runContainerForJob() use TF_IN_AUTOMATION env var`() {
        val script = "echo \$TF_IN_AUTOMATION; exit 0;"

        val logs = mutableListOf<String>()
        val listLogger = StepLogger { logs.add(it) }

        dockerExecutor.executeJobStep(image, listLogger, script, listOf())

        assertThat(logs).isEqualTo(listOf("true\n"));
    }
}