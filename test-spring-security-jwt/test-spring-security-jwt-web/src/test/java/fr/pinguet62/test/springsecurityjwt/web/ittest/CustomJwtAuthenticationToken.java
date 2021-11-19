package fr.pinguet62.test.springsecurityjwt.web.ittest;

import com.auth0.jwt.interfaces.DecodedJWT;
import fr.pinguet62.test.springsecurityjwt.web.JwtAuthenticationToken;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class CustomJwtAuthenticationToken extends JwtAuthenticationToken {

    @Getter
    private final Collection<? extends GrantedAuthority> authorities;

    public CustomJwtAuthenticationToken(String subject, String jwtToken, DecodedJWT decodedJWT, Collection<? extends GrantedAuthority> authorities) {
        super(subject, jwtToken, decodedJWT);
        this.authorities = authorities;
    }
}
