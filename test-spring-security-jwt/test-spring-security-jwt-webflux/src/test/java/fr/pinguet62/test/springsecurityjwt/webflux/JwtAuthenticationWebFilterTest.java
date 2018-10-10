package fr.pinguet62.test.springsecurityjwt.webflux;

import org.junit.Test;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;

public class JwtAuthenticationWebFilterTest {

    private ServerWebExchange exchange;
    private WebFilterChain chain;

    @Test
    public void noHeader_continue() throws Exception {
//        JwtAuthenticationWebFilter webFiler = new JwtAuthenticationWebFilter();
//
//        WebTestClient client = WebTestClient.bindToWebHandler(exchange -> Mono.empty()).webFilter(webFiler).build();

//        client.get().uri("/").exchange()
//                .returnResult();
    }

}
