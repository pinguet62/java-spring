package fr.pinguet62.springboot.wiremock;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.web.client.RestTemplate;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.test.context.TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS;

@WireMockApi(api = "facebook", propertyKey = "api.facebook.port") // mock configuration
@WireMockApi(api = "google", propertyKey = "api.google.port") // mock configuration
@SpringBootTest(properties = {
        "controller.facebook.url=http://localhost:${api.facebook.port}", // mock your app configuration
        "controller.google.url=http://localhost:${api.google.port}", // mock your app configuration
}, classes = WireMockTest.class)
// auto-configuration
@Import(WireMockTestExecutionListener.class)
@TestExecutionListeners(value = WireMockTestExecutionListener.class, mergeMode = MERGE_WITH_DEFAULTS)
class WireMockTest {

    @Value("${controller.facebook.url}") // your normal app configuration
    String facebookUrl;

    @Value("${controller.google.url}") // your normal app configuration
    String googleUrl;

    final RestTemplate restTemplate = new RestTemplate();

    @Test
    @WireMockCallMock(api = "facebook", method = GET, urlMatching = "/first", status = 200, body = "I'm first URL")
    @WireMockCallMock(api = "facebook", method = GET, urlMatching = "/second", status = 200, bodyResource = "classpath:fr/pinguet62/springboot/wiremock/test.txt")
    @WireMockCallMock(api = "google", method = GET, urlMatching = "/other", status = 200, body = "I'm the other")
    void test() {
        assertThat(restTemplate.getForEntity(facebookUrl + "/first", String.class).getBody(), is("I'm first URL"));
        assertThat(restTemplate.getForEntity(facebookUrl + "/second", String.class).getBody(), is("I'm second URL"));
        assertThat(restTemplate.getForEntity(googleUrl + "/other", String.class).getBody(), is("I'm the other"));
    }
}
