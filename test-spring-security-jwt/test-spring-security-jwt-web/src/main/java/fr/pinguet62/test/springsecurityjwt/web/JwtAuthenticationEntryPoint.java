package fr.pinguet62.test.springsecurityjwt.web;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.springframework.http.HttpHeaders.WWW_AUTHENTICATE;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint, InitializingBean {

    static final String ERROR_HEADER = WWW_AUTHENTICATE;
    static final HttpStatus ERROR_STATUS = UNAUTHORIZED;

    @Getter
    @Setter
    private String realmName;

    @Override
    public void afterPropertiesSet() {
        Assert.hasText(realmName, "realmName must be specified");
    }

    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        response.addHeader(ERROR_HEADER, "Bearer realm=\"" + realmName + "\"");
        response.sendError(ERROR_STATUS.value(), ERROR_STATUS.getReasonPhrase());
    }
}
