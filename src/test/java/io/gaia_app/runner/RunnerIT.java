package io.gaia_app.runner;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.test.web.client.response.MockRestResponseCreators;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

import static org.awaitility.Awaitility.await;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;

@SpringBootTest
@AutoConfigureWebClient
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "gaia.url=https://gaia-app.io",
        "gaia.runner.username=gaia-runner",
        "gaia.runner.password=gaia-runner-password",
})
class RunnerIT {

    @Autowired
    private RestTemplate restTemplate;

    @Test
    void runnerShouldRunJobs(){
        // given
        MockRestServiceServer server = MockRestServiceServer.bindTo(restTemplate).build();

        server.expect(ExpectedCount.manyTimes(), requestTo("https://gaia-app.io/api/runner/steps/request"))
                .andExpect(MockRestRequestMatchers.header("Authorization", "Basic Z2FpYS1ydW5uZXI6cGFzc3dvcmQ="))
                .andRespond(MockRestResponseCreators.withSuccess(new ClassPathResource("/rest/step-request.json"), MediaType.APPLICATION_JSON));

        server.expect(requestTo("https://gaia-app.io/api/runner/steps/12/start"))
                .andExpect(MockRestRequestMatchers.method(HttpMethod.PUT))
                .andExpect(MockRestRequestMatchers.header("Authorization", "Basic Z2FpYS1ydW5uZXI6cGFzc3dvcmQ="))
                .andRespond(MockRestResponseCreators.withSuccess());

        server.expect(requestTo("https://gaia-app.io/api/runner/steps/12/logs"))
                .andExpect(MockRestRequestMatchers.method(HttpMethod.PUT))
                .andExpect(MockRestRequestMatchers.header("Authorization", "Basic Z2FpYS1ydW5uZXI6cGFzc3dvcmQ="))
                .andExpect(MockRestRequestMatchers.content().json("[gaia] using image hashicorp/terraform:latest\n"))
                .andRespond(MockRestResponseCreators.withSuccess());

        server.expect(requestTo("https://gaia-app.io/api/runner/steps/12/status"))
                .andExpect(MockRestRequestMatchers.method(HttpMethod.PUT))
                .andExpect(MockRestRequestMatchers.header("Authorization", "Basic Z2FpYS1ydW5uZXI6cGFzc3dvcmQ="))
                .andExpect(MockRestRequestMatchers.content().json("0"))
                .andRespond(MockRestResponseCreators.withSuccess());

        await()
                .atMost(Duration.ofSeconds(30))
                .untilAsserted(server::verify);
    }



}