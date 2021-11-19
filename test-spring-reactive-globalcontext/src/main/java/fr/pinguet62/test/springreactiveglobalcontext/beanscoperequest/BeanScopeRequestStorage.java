package fr.pinguet62.test.springreactiveglobalcontext.beanscoperequest;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static org.springframework.context.annotation.ScopedProxyMode.TARGET_CLASS;
import static org.springframework.web.context.WebApplicationContext.SCOPE_REQUEST;

@Component
@Scope(value = SCOPE_REQUEST, proxyMode = TARGET_CLASS)
public class BeanScopeRequestStorage {

    @Getter
    @Setter
    private String value;
}
