package fr.pinguet62.test.springsecurityjwt.web;

import org.springframework.security.authentication.AuthenticationProvider;

public abstract class JwtAuthenticationProvider implements AuthenticationProvider {

    @Override
    public boolean supports(Class<?> authentication) {
        return JwtAuthenticationToken.class.isAssignableFrom(authentication);
    }

}
