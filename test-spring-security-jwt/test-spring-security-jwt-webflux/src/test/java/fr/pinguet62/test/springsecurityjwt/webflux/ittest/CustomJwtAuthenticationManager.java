package fr.pinguet62.test.springsecurityjwt.webflux.ittest;

import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import fr.pinguet62.test.springsecurityjwt.webflux.JwtAuthenticationToken;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import reactor.core.publisher.Mono;

import java.util.Collection;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;
import static reactor.core.publisher.Mono.just;

/**
 * Extract {@link GrantedAuthority} from {@link DecodedJWT} field "Role",
 * and build new {@link JwtAuthenticationToken} initialized.
 */
public class CustomJwtAuthenticationManager implements ReactiveAuthenticationManager {

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) authentication;
        DecodedJWT decodedJWT = jwtAuthenticationToken.getDecodedJWT();
        Claim role = decodedJWT.getClaim("Role");
        String[] roles = role.asArray(String.class);
        Collection<? extends GrantedAuthority> authorities = stream(roles).map(r -> "ROLE_" + r).map(SimpleGrantedAuthority::new).collect(toList());
        CustomJwtAuthenticationToken customAuthentication = new CustomJwtAuthenticationToken(jwtAuthenticationToken.getSubject(), jwtAuthenticationToken.getJwtToken(), jwtAuthenticationToken.getDecodedJWT(), authorities);
        return just(customAuthentication);
    }

}
