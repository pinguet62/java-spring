package fr.pinguet62.test.springsecurityjwt.webflux.ittest;

import fr.pinguet62.test.springsecurityjwt.webflux.JwtAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import reactor.core.publisher.Mono;

public class CustomReactiveJwtSecurityContextHolder {

    public static Mono<JwtAuthenticationToken> getAuthentication() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .filter(it -> it != null)
                .filter(it -> it instanceof JwtAuthenticationToken)
                .map(JwtAuthenticationToken.class::cast);
    }

    public static Mono<String> getSubject() {
        return getAuthentication().map(JwtAuthenticationToken::getSubject);
    }

    public static Mono<String> getJwtToken() {
        return getAuthentication().map(JwtAuthenticationToken::getJwtToken);
    }
}
