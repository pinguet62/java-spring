package fr.pinguet62.test.springreactiveglobalcontext.beanscoperequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import static java.time.Duration.ofMillis;

@RestController
@RequestMapping("/BeanRequestScope")
public class BeanScopeRequestController {

    @Autowired
    private BeanScopeRequestStorage storage;

    @GetMapping("/a")
    public Flux<Integer> a() {
        return Flux.range(1, 10)
                .doOnNext(it -> storage.setValue("A"))
                .delayElements(ofMillis(900))
                .doOnNext(it -> printTest("A", it));
    }

    @GetMapping("/b")
    public Flux<Integer> b() {
        return Flux.range(1, 10)
                .doOnNext(it -> storage.setValue("B"))
                .delayElements(ofMillis(1000))
                .doOnNext(it -> printTest("B", it));
    }

    private void printTest(String name, int index) {
        String value = storage.getValue();
        String message = name.equals("A") ? "> [A] " : "\t\t\t\t\t\t\t\t> [B] ";
        message += " - ";
        message += "#" + index;
        message += " - ";
        message += value;
        if (!value.equals(name))
            message += " ERROR !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!";
        System.out.println(message);
    }
}
