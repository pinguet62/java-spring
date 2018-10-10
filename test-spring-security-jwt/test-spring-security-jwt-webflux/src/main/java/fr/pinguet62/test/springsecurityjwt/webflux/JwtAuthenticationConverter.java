package fr.pinguet62.test.springsecurityjwt.webflux;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.function.Function;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static reactor.core.publisher.Mono.empty;
import static reactor.core.publisher.Mono.just;

public class JwtAuthenticationConverter implements Function<ServerWebExchange, Mono<Authentication>> {

    public static final String TOKEN_PREFIX = "bearer ";
    public static final String HEADER_KEY = AUTHORIZATION;

    @Override
    public Mono<Authentication> apply(ServerWebExchange exchange) {
        ServerHttpRequest request = exchange.getRequest();

        String header = request.getHeaders().getFirst(HEADER_KEY);
        if (header == null || !header.toLowerCase().startsWith(TOKEN_PREFIX)) {
            return empty();
        }

        String jwtToken = header.substring(TOKEN_PREFIX.length());
        DecodedJWT decoded;
        try {
            decoded = JWT.decode(jwtToken);
        } catch (JWTVerificationException e) {
            return empty();
        }

        Authentication authentication = new JwtAuthenticationToken(decoded.getSubject(), jwtToken, decoded);
        return just(authentication);
    }

}
