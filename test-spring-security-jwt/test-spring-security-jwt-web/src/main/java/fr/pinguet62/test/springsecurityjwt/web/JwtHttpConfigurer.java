package fr.pinguet62.test.springsecurityjwt.web;

import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.ExceptionHandlingConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.DelegatingAuthenticationEntryPoint;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.util.matcher.AndRequestMatcher;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestHeaderRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.accept.ContentNegotiationStrategy;
import org.springframework.web.accept.HeaderContentNegotiationStrategy;

import java.util.Collections;
import java.util.LinkedHashMap;

import static java.util.Arrays.asList;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.MediaType.ALL;
import static org.springframework.http.MediaType.TEXT_HTML;

public class JwtHttpConfigurer<B extends HttpSecurityBuilder<B>> extends AbstractHttpConfigurer<JwtHttpConfigurer<B>, B> {

    private static final RequestHeaderRequestMatcher X_REQUESTED_WITH = new RequestHeaderRequestMatcher("X-Requested-With", "XMLHttpRequest");

    private static final String DEFAULT_REALM = "Realm";

    private AuthenticationEntryPoint authenticationEntryPoint;
    private final JwtAuthenticationEntryPoint jwtAuthEntryPoint = new JwtAuthenticationEntryPoint();

    public JwtHttpConfigurer() {
        realmName(DEFAULT_REALM);

        LinkedHashMap<RequestMatcher, AuthenticationEntryPoint> entryPoints = new LinkedHashMap<>();
        entryPoints.put(X_REQUESTED_WITH, new HttpStatusEntryPoint(UNAUTHORIZED));

        DelegatingAuthenticationEntryPoint defaultEntryPoint = new DelegatingAuthenticationEntryPoint(entryPoints);
        defaultEntryPoint.setDefaultEntryPoint(this.jwtAuthEntryPoint);
        this.authenticationEntryPoint = defaultEntryPoint;
    }

    public JwtHttpConfigurer<B> realmName(String realmName) {
        this.jwtAuthEntryPoint.setRealmName(realmName);
        this.jwtAuthEntryPoint.afterPropertiesSet();
        return this;
    }

    public JwtHttpConfigurer<B> authenticationEntryPoint(AuthenticationEntryPoint authenticationEntryPoint) {
        this.authenticationEntryPoint = authenticationEntryPoint;
        return this;
    }

    @Override
    public void init(B http) {
        registerDefaults(http);
    }

    private void registerDefaults(B http) {
        ContentNegotiationStrategy contentNegotiationStrategy = http.getSharedObject(ContentNegotiationStrategy.class);
        if (contentNegotiationStrategy == null) {
            contentNegotiationStrategy = new HeaderContentNegotiationStrategy();
        }

        MediaTypeRequestMatcher restMatcher = new MediaTypeRequestMatcher(
                contentNegotiationStrategy, MediaType.APPLICATION_ATOM_XML,
                MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_JSON,
                MediaType.APPLICATION_OCTET_STREAM, MediaType.APPLICATION_XML,
                MediaType.MULTIPART_FORM_DATA, MediaType.TEXT_XML);
        restMatcher.setIgnoredMediaTypes(Collections.singleton(ALL));

        MediaTypeRequestMatcher allMatcher = new MediaTypeRequestMatcher(contentNegotiationStrategy, ALL);
        allMatcher.setUseEquals(true);

        RequestMatcher notHtmlMatcher = new NegatedRequestMatcher(new MediaTypeRequestMatcher(contentNegotiationStrategy, TEXT_HTML));
        RequestMatcher restNotHtmlMatcher = new AndRequestMatcher(asList(notHtmlMatcher, restMatcher));

        RequestMatcher preferredMatcher = new OrRequestMatcher(asList(X_REQUESTED_WITH, restNotHtmlMatcher, allMatcher));

        registerDefaultEntryPoint(http, preferredMatcher);
        registerDefaultLogoutSuccessHandler(http, preferredMatcher);
    }

    private void registerDefaultEntryPoint(B http, RequestMatcher preferredMatcher) {
        ExceptionHandlingConfigurer<B> exceptionHandling = http.getConfigurer(ExceptionHandlingConfigurer.class);
        if (exceptionHandling == null) {
            return;
        }
        exceptionHandling.defaultAuthenticationEntryPointFor(postProcess(authenticationEntryPoint), preferredMatcher);
    }

    private void registerDefaultLogoutSuccessHandler(B http, RequestMatcher preferredMatcher) {
        LogoutConfigurer<B> logout = http.getConfigurer(LogoutConfigurer.class);
        if (logout == null) {
            return;
        }
        LogoutConfigurer<B> handler = logout.defaultLogoutSuccessHandlerFor(postProcess(new HttpStatusReturningLogoutSuccessHandler(NO_CONTENT)), preferredMatcher);
    }

    @Override
    public void configure(B http) {
        AuthenticationManager authenticationManager = http.getSharedObject(AuthenticationManager.class);
        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(authenticationManager, this.authenticationEntryPoint);
        jwtAuthenticationFilter = postProcess(jwtAuthenticationFilter);
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    }

}
