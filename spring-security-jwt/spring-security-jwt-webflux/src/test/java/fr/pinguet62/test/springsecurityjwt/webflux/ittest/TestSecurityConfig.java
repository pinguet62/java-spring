package fr.pinguet62.test.springsecurityjwt.webflux.ittest;

import fr.pinguet62.test.springsecurityjwt.webflux.JwtAuthenticationEntryPoint;
import fr.pinguet62.test.springsecurityjwt.webflux.JwtAuthenticationWebFilter;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

import static org.springframework.security.config.web.server.SecurityWebFiltersOrder.AUTHENTICATION;

@Configuration
@EnableAutoConfiguration
@EnableWebFluxSecurity
public class TestSecurityConfig {

    public static final String REALM_NAME = "expected";

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        JwtAuthenticationEntryPoint authenticationEntryPoint = new JwtAuthenticationEntryPoint();
        authenticationEntryPoint.setRealm(REALM_NAME);

        // @formatter:off
        return http
                .addFilterAt(new JwtAuthenticationWebFilter(new CustomJwtAuthenticationManager()), AUTHENTICATION)
                .exceptionHandling()
                        .authenticationEntryPoint(authenticationEntryPoint).and()
                .authorizeExchange()
                        .anyExchange().authenticated().and()
                .build();
        // @formatter:on
    }
}
