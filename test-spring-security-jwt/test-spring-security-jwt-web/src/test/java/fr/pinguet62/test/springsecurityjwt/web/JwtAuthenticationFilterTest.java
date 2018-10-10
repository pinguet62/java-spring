package fr.pinguet62.test.springsecurityjwt.web;

import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.FilterChain;

import static fr.pinguet62.test.springsecurityjwt.web.JwtAuthenticationFilter.HEADER_KEY;
import static fr.pinguet62.test.springsecurityjwt.web.JwtAuthenticationFilter.TOKEN_PREFIX;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class JwtAuthenticationFilterTest {

    private AuthenticationManager authenticationManager;
    private AuthenticationEntryPoint authenticationEntryPoint;
    private JwtAuthenticationFilter filter;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private FilterChain filterChain;

    @Before
    public void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Before
    public void initMocks() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        filterChain = mock(FilterChain.class);
        authenticationManager = mock(AuthenticationManager.class);
        authenticationEntryPoint = mock(AuthenticationEntryPoint.class);
        filter = new JwtAuthenticationFilter(authenticationManager, authenticationEntryPoint);
    }

    @Test
    public void noHeader_continue() throws Exception {
        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication(), nullValue());
    }

    @Test
    public void malFormattedHeader_continue() throws Exception {
        request.addHeader(HEADER_KEY, "not a JWT token");

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication(), nullValue());
    }

    @Test
    public void malFormattedTokenAndFailureNotIgnored_callsAuthenticationEntryPointAndStop() throws Exception {
        request.addHeader(HEADER_KEY, TOKEN_PREFIX + "bad token");

        filter.doFilterInternal(request, response, filterChain);

        verify(authenticationEntryPoint).commence(eq(request), eq(response), any(AuthenticationException.class));
        verify(filterChain, times(0)).doFilter(request, response);
    }

    @Test
    public void authenticationFailsAndFailureNotIgnored_callsAuthenticationEntryPointAndStop() throws Exception {
        request.addHeader(HEADER_KEY, TOKEN_PREFIX + "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c");
        when(authenticationManager.authenticate(any(Authentication.class))).thenThrow(new AuthenticationServiceException("..."));

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(0)).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication(), nullValue());
    }

    @Test
    public void authenticationSuccess_continue() throws Exception {
        request.addHeader(HEADER_KEY, TOKEN_PREFIX + "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c");
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(authentication);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication(), is(authentication));
    }

}
