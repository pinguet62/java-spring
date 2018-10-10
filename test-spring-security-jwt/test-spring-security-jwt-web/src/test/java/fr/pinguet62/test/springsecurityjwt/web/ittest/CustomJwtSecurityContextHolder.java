package fr.pinguet62.test.springsecurityjwt.web.ittest;

import fr.pinguet62.test.springsecurityjwt.web.JwtAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Objects;
import java.util.Optional;

import static java.util.Optional.of;

public class CustomJwtSecurityContextHolder {

    public static Optional<CustomJwtAuthenticationToken> getAuthentication() {
        return of(SecurityContextHolder.getContext())
                .map(SecurityContext::getAuthentication)
                .filter(Objects::nonNull)
                .filter(it -> it instanceof CustomJwtAuthenticationToken)
                .map(CustomJwtAuthenticationToken.class::cast);
    }

    public static Optional<String> getSubject() {
        return getAuthentication().map(JwtAuthenticationToken::getSubject);
    }

    public static Optional<String> getJwtToken() {
        return getAuthentication().map(JwtAuthenticationToken::getJwtToken);
    }

}
