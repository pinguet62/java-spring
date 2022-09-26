package fr.pinguet62.test.springsecurityjwt.webflux;

import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.ServerAuthenticationEntryPointFailureHandler;
import reactor.core.publisher.Mono;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

public class JwtAuthenticationWebFilter extends AuthenticationWebFilter {

    public static final String TOKEN_PREFIX = "bearer ";
    public static final String HEADER_KEY = AUTHORIZATION;

    public JwtAuthenticationWebFilter() {
        this(Mono::just);
    }

    public JwtAuthenticationWebFilter(ReactiveAuthenticationManager authenticationManager) {
        super(authenticationManager);
        setServerAuthenticationConverter(new JwtAuthenticationConverter());
        setAuthenticationFailureHandler(new ServerAuthenticationEntryPointFailureHandler(new JwtAuthenticationEntryPoint()));
    }

}
