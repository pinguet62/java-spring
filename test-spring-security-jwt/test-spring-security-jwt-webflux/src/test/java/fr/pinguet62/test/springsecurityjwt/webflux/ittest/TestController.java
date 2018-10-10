package fr.pinguet62.test.springsecurityjwt.webflux.ittest;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@SpringBootApplication
public class TestController {

    @GetMapping("/subject")
    public Mono<String> showSubject() {
        return CustomReactiveJwtSecurityContextHolder.getSubject();
    }

    @GetMapping("/jwtToken")
    public Mono<String> showJwtToken() {
        return CustomReactiveJwtSecurityContextHolder.getJwtToken();
    }

    @GetMapping("/authorities")
    public Mono<List<String>> showAuthorities() {
        return CustomReactiveJwtSecurityContextHolder.getAuthentication()
                .flatMapIterable(Authentication::getAuthorities)
                .map(GrantedAuthority::getAuthority)
                .collectList();
    }

}
