package fr.pinguet62.test.springsecurityjwt.webflux.ittest;

import com.auth0.jwt.interfaces.DecodedJWT;
import fr.pinguet62.test.springsecurityjwt.webflux.JwtAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class CustomJwtAuthenticationToken extends JwtAuthenticationToken {

    private final Collection<? extends GrantedAuthority> authorities;

    public CustomJwtAuthenticationToken(String subject, String jwtToken, DecodedJWT decodedJWT, Collection<? extends GrantedAuthority> authorities) {
        super(subject, jwtToken, decodedJWT);
        this.authorities = authorities;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

}
