package fr.pinguet62.test.springsecurityjwt.web;

import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

import static java.util.Collections.emptyList;

public class JwtAuthenticationToken implements Authentication {

    private final String subject;

    private final String jwtToken;

    private final DecodedJWT decodedJWT;

    public JwtAuthenticationToken(String subject, String jwtToken, DecodedJWT decodedJWT) {
        this.subject = subject;
        this.jwtToken = jwtToken;
        this.decodedJWT = decodedJWT;
    }

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

    public String getSubject() {
        return subject;
    }

    public String getJwtToken() {
        return jwtToken;
    }

    public DecodedJWT getDecodedJWT() {
        return decodedJWT;
    }

}
