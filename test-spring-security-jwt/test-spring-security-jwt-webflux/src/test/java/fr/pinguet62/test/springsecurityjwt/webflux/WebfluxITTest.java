package fr.pinguet62.test.springsecurityjwt.webflux;

import fr.pinguet62.test.springsecurityjwt.webflux.ittest.TestController;
import fr.pinguet62.test.springsecurityjwt.webflux.ittest.TestSecurityConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;

import static fr.pinguet62.test.springsecurityjwt.webflux.JwtAuthenticationEntryPoint.ERROR_HEADER;
import static fr.pinguet62.test.springsecurityjwt.webflux.JwtAuthenticationEntryPoint.ERROR_STATUS;
import static fr.pinguet62.test.springsecurityjwt.webflux.JwtAuthenticationWebFilter.HEADER_KEY;
import static fr.pinguet62.test.springsecurityjwt.webflux.JwtAuthenticationWebFilter.TOKEN_PREFIX;
import static fr.pinguet62.test.springsecurityjwt.webflux.ittest.TestSecurityConfig.REALM_NAME;
import static java.util.Arrays.asList;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.MOCK;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = MOCK, classes = {TestSecurityConfig.class, TestController.class})
@AutoConfigureWebTestClient
public class WebfluxITTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void notAuthenticated() {
        webTestClient.get().uri("/subject")
                .exchange()
                .expectStatus().isEqualTo(ERROR_STATUS.value()); // see TODO
    }

    @Test
    public void badJwtToken() {
        webTestClient.get().uri("/subject")
                .header(HEADER_KEY, TOKEN_PREFIX + "bad")
                .exchange()
                .expectStatus().isEqualTo(ERROR_STATUS.value()) // see TODO
                .expectHeader().valueMatches(ERROR_HEADER, ".*" + REALM_NAME + ".*");
    }

    @Test
    public void ok() {
        final String jwtToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJPbmxpbmUgSldUIEJ1aWxkZXIiLCJpYXQiOjE1MzkwNjcxMDIsImV4cCI6MTU3MDYwMzEwMiwiYXVkIjoid3d3LmV4YW1wbGUuY29tIiwic3ViIjoianJvY2tldEBleGFtcGxlLmNvbSIsIkdpdmVuTmFtZSI6IkpvaG5ueSIsIlN1cm5hbWUiOiJSb2NrZXQiLCJFbWFpbCI6Impyb2NrZXRAZXhhbXBsZS5jb20iLCJSb2xlIjpbIk1hbmFnZXIiLCJQcm9qZWN0IEFkbWluaXN0cmF0b3IiXX0.roAK_IV4_qlgEs5Q31oBJUVqEr-m_sBETCJ-tO8-hTk"; // http://jwtbuilder.jamiekurtz.com
        webTestClient.get().uri("/subject")
                .header(HEADER_KEY, TOKEN_PREFIX + jwtToken)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("jrocket@example.com");
        webTestClient.get().uri("/jwtToken")
                .header(HEADER_KEY, TOKEN_PREFIX + jwtToken)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo(jwtToken);
        webTestClient.get().uri("/authorities")
                .header(HEADER_KEY, TOKEN_PREFIX + jwtToken)
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<String>>() {
                }).isEqualTo(asList("ROLE_Manager", "ROLE_Project Administrator"));
    }

}
