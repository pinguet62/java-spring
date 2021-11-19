package fr.pinguet62.test.springsecurityjwt.web;

import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

import static java.util.Collections.emptyList;

@RequiredArgsConstructor
public class JwtAuthenticationToken implements Authentication {

    @Getter
    private final String subject;

    @Getter
    private final String jwtToken;

    @Getter
    private final DecodedJWT decodedJWT;

    @Override
    public Object getPrincipal() {
        return subject;
    }

    @Override
    public Object getCredentials() {
        return jwtToken;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return emptyList();
    }

    @Override
    public Object getDetails() {
        return decodedJWT;
    }

    @Override
    public boolean isAuthenticated() {
        return true;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        throw new IllegalArgumentException();
    }

    @Override
    public String getName() {
        return subject;
    }
}
