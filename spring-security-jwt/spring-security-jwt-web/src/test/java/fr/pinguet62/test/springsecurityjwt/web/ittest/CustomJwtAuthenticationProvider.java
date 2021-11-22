package fr.pinguet62.test.springsecurityjwt.web.ittest;

import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import fr.pinguet62.test.springsecurityjwt.web.JwtAuthenticationProvider;
import fr.pinguet62.test.springsecurityjwt.web.JwtAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;

/**
 * Extract {@link org.springframework.security.core.GrantedAuthority} from {@link DecodedJWT} field "Role",
 * and build new {@link JwtAuthenticationToken} initialized.
 */
public class CustomJwtAuthenticationProvider extends JwtAuthenticationProvider {

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) authentication;
        DecodedJWT decodedJWT = jwtAuthenticationToken.getDecodedJWT();
        Claim role = decodedJWT.getClaim("Role");
        String[] roles = role.asArray(String.class);
        Collection<? extends GrantedAuthority> authorities = stream(roles).map(r -> "ROLE_" + r).map(SimpleGrantedAuthority::new).collect(toList());
        return new CustomJwtAuthenticationToken(jwtAuthenticationToken.getSubject(), jwtAuthenticationToken.getJwtToken(), jwtAuthenticationToken.getDecodedJWT(), authorities);
    }
}
