package fr.pinguet62.test.springsecurityjwt.web;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import static fr.pinguet62.test.springsecurityjwt.web.JwtAuthenticationEntryPoint.ERROR_HEADER;
import static fr.pinguet62.test.springsecurityjwt.web.JwtAuthenticationEntryPoint.ERROR_STATUS;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;

class JwtAuthenticationEntryPointTest {

    @Test
    void commence() throws IOException {
        JwtAuthenticationEntryPoint authenticationEntryPoint = new JwtAuthenticationEntryPoint();
        HttpServletRequest request = new MockHttpServletRequest();
        HttpServletResponse response = new MockHttpServletResponse();
        AuthenticationException authException = new AuthenticationServiceException("...");

        authenticationEntryPoint.commence(request, response, authException);

        assertThat(response.getHeader(ERROR_HEADER), startsWith("Bearer realm=\""));
        assertThat(response.getStatus(), is(ERROR_STATUS.value()));
    }
}
