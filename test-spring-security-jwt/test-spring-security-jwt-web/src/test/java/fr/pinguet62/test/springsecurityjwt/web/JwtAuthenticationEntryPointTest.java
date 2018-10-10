package fr.pinguet62.test.springsecurityjwt.web;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static fr.pinguet62.test.springsecurityjwt.web.JwtAuthenticationEntryPoint.ERROR_HEADER;
import static fr.pinguet62.test.springsecurityjwt.web.JwtAuthenticationEntryPoint.ERROR_STATUS;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertThat;

public class JwtAuthenticationEntryPointTest {

    @Test
    public void commence() throws IOException {
        JwtAuthenticationEntryPoint authenticationEntryPoint = new JwtAuthenticationEntryPoint();
        HttpServletRequest request = new MockHttpServletRequest();
        HttpServletResponse response = new MockHttpServletResponse();
        AuthenticationException authException = new AuthenticationServiceException("...");

        authenticationEntryPoint.commence(request, response, authException);

        assertThat(response.getHeader(ERROR_HEADER), startsWith("Bearer realm=\""));
        assertThat(response.getStatus(), is(ERROR_STATUS.value()));
    }

}
