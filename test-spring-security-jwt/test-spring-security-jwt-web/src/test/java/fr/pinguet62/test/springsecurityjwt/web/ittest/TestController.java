package fr.pinguet62.test.springsecurityjwt.web.ittest;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static java.util.stream.Collectors.toList;

@RestController
public class TestController {

    @GetMapping("/subject")
    public String showSubject() {
        return CustomJwtSecurityContextHolder.getSubject().orElse(null);
    }

    @GetMapping("/jwtToken")
    public String showJwtToken() {
        return CustomJwtSecurityContextHolder.getJwtToken().orElse(null);
    }

    @GetMapping("/authorities")
    public List<String> showAuthorities() {
        return CustomJwtSecurityContextHolder.getAuthentication()
                .map(Authentication::getAuthorities)
                .map(it -> it.stream().map(GrantedAuthority::getAuthority).collect(toList()))
                .get();
    }
}
