package fr.pinguet62.test.springsecurityjwt.web;

import fr.pinguet62.test.springsecurityjwt.web.ittest.TestController;
import fr.pinguet62.test.springsecurityjwt.web.ittest.TestSecurityConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import static fr.pinguet62.test.springsecurityjwt.web.JwtAuthenticationEntryPoint.ERROR_HEADER;
import static fr.pinguet62.test.springsecurityjwt.web.JwtAuthenticationEntryPoint.ERROR_STATUS;
import static fr.pinguet62.test.springsecurityjwt.web.JwtAuthenticationFilter.HEADER_KEY;
import static fr.pinguet62.test.springsecurityjwt.web.JwtAuthenticationFilter.TOKEN_PREFIX;
import static fr.pinguet62.test.springsecurityjwt.web.ittest.TestSecurityConfig.REALM_NAME;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.MOCK;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = MOCK, classes = {TestSecurityConfig.class, TestController.class})
public class WebITTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Before
    public void initMockMvc() {
        mockMvc = webAppContextSetup(context).apply(springSecurity()).build();
    }

    @Test
    public void notAuthenticated() throws Exception {
        mockMvc.perform(get("/subject"))
                .andExpect(status().is(ERROR_STATUS.value())); // see JwtHttpConfigurer#registerDefaultEntryPoint()
    }

    @Test
    public void badJwtToken() throws Exception {
        mockMvc.perform(get("/subject")
                .header(HEADER_KEY, TOKEN_PREFIX + "bad"))
                .andExpect(status().is(ERROR_STATUS.value())) // see JwtAuthenticationEntryPoint#commence()
                .andExpect(header().string(ERROR_HEADER, containsString(REALM_NAME)));
    }

    @Test
    public void ok() throws Exception {
        final String jwtToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJPbmxpbmUgSldUIEJ1aWxkZXIiLCJpYXQiOjE1MzkwNjcxMDIsImV4cCI6MTU3MDYwMzEwMiwiYXVkIjoid3d3LmV4YW1wbGUuY29tIiwic3ViIjoianJvY2tldEBleGFtcGxlLmNvbSIsIkdpdmVuTmFtZSI6IkpvaG5ueSIsIlN1cm5hbWUiOiJSb2NrZXQiLCJFbWFpbCI6Impyb2NrZXRAZXhhbXBsZS5jb20iLCJSb2xlIjpbIk1hbmFnZXIiLCJQcm9qZWN0IEFkbWluaXN0cmF0b3IiXX0.roAK_IV4_qlgEs5Q31oBJUVqEr-m_sBETCJ-tO8-hTk"; // http://jwtbuilder.jamiekurtz.com
        mockMvc.perform(get("/subject")
                .header(HEADER_KEY, TOKEN_PREFIX + jwtToken))
                .andExpect(status().isOk())
                .andExpect(content().string("jrocket@example.com"));
        mockMvc.perform(get("/jwtToken")
                .header(HEADER_KEY, TOKEN_PREFIX + jwtToken))
                .andExpect(status().isOk())
                .andExpect(content().string(jwtToken));
        mockMvc.perform(get("/authorities")
                .header(HEADER_KEY, TOKEN_PREFIX + jwtToken))
                .andExpect(status().isOk())
                .andExpect(content().string("[\"ROLE_Manager\",\"ROLE_Project Administrator\"]"));
    }

}
