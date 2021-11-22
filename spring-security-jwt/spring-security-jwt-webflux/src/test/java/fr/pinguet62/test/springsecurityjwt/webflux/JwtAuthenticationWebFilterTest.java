package fr.pinguet62.test.springsecurityjwt.webflux;

import org.junit.jupiter.api.Test;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;

class JwtAuthenticationWebFilterTest {

    ServerWebExchange exchange;
    WebFilterChain chain;

    @Test
    void noHeader_continue() throws Exception {
//        JwtAuthenticationWebFilter webFiler = new JwtAuthenticationWebFilter();
//
//        WebTestClient client = WebTestClient.bindToWebHandler(exchange -> Mono.empty()).webFilter(webFiler).build();

//        client.get().uri("/").exchange()
//                .returnResult();
    }
}
