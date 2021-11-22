package fr.pinguet62.test.springsecurityjwt.webflux;

import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.util.Assert;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static java.lang.String.format;
import static org.springframework.http.HttpHeaders.WWW_AUTHENTICATE;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

public class JwtAuthenticationEntryPoint implements ServerAuthenticationEntryPoint {

    static final String ERROR_HEADER = WWW_AUTHENTICATE;
    static final HttpStatus ERROR_STATUS = UNAUTHORIZED;
    private static final String DEFAULT_REALM = "Realm";
    private static String WWW_AUTHENTICATE_FORMAT = "Bearer realm=\"%s\"";

    private String headerValue = createHeaderValue(DEFAULT_REALM);

    @Override
    public Mono<Void> commence(ServerWebExchange exchange, AuthenticationException e) {
        return Mono.fromRunnable(() -> {
            ServerHttpResponse response = exchange.getResponse();
            response.setStatusCode(ERROR_STATUS);
            response.getHeaders().set(ERROR_HEADER, headerValue);
        });
    }

    public void setRealm(String realm) {
        this.headerValue = createHeaderValue(realm);
    }

    private static String createHeaderValue(String realm) {
        Assert.notNull(realm, "realm cannot be null");
        return format(WWW_AUTHENTICATE_FORMAT, realm);
    }
}
