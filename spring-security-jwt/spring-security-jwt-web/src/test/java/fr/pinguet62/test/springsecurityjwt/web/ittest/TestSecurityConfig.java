package fr.pinguet62.test.springsecurityjwt.web.ittest;

import fr.pinguet62.test.springsecurityjwt.web.JwtHttpConfigurer;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableAutoConfiguration
@EnableWebSecurity
public class TestSecurityConfig {

    public static final String REALM_NAME = "expected";

    @Bean
    SecurityFilterChain securityWebFilterChain(HttpSecurity httpSecurity) throws Exception {
        // @formatter:off
        return httpSecurity
                .apply(new JwtHttpConfigurer<>())
                        .realmName(REALM_NAME).and()
                .authorizeRequests()
                        .anyRequest().authenticated()
                        .and()
                .authenticationProvider(new CustomJwtAuthenticationProvider())
                .build();
        // @formatter:on
    }
}
