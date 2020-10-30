package io.gaia_app.runner;

import io.gaia_app.stacks.bo.Step;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StepPollerTest {

    @InjectMocks
    private StepPoller stepPoller;

    @Mock
    private StepRunner stepRunner;

    @Mock
    private RestTemplate restTemplate;

    @Test
    void getJobAndRun_shouldGetAJobAndRunIt(){
        // given
        ReflectionTestUtils.setField(stepPoller, "gaiaUrl", "http://localhost:8080");

        var step = new RunnerStep(new Step(), "hashicorp/terraform:latest", "echo 'Hello'", List.of());
        when(restTemplate.getForObject("http://localhost:8080/api/runner/steps/request", RunnerStep.class)).thenReturn(step);

        // when
        stepPoller.getJobAndRun();

        // then
        verify(stepRunner).runStep(step);
    }

    @Test
    void getJobAndRun_shouldDoNothingWhenNoJobIsAvailable(){
        // given
        ReflectionTestUtils.setField(stepPoller, "gaiaUrl", "http://localhost:8080");

        var notFound = HttpClientErrorException.create(HttpStatus.NOT_FOUND, null, null, null, null);
        when(restTemplate.getForObject("http://localhost:8080/api/runner/steps/request", RunnerStep.class)).thenThrow(notFound);

        // when
        stepPoller.getJobAndRun();

        // then
        verifyNoInteractions(stepRunner);
    }

}