package fr.pinguet62.test.springsecurityjwt.web.ittest;

import fr.pinguet62.test.springsecurityjwt.web.JwtHttpConfigurer;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableAutoConfiguration
@EnableWebSecurity
public class TestSecurityConfig extends WebSecurityConfigurerAdapter {

    public static final String REALM_NAME = "expected";

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // @formatter:off
        http
                .apply(new JwtHttpConfigurer<>())
                        .realmName(REALM_NAME).and()
                .authorizeRequests()
                        .anyRequest().authenticated();
        // @formatter:on
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(new CustomJwtAuthenticationProvider());
    }

}
