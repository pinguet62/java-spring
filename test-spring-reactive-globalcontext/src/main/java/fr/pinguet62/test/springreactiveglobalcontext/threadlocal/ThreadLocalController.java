package fr.pinguet62.test.springreactiveglobalcontext.threadlocal;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import static java.time.Duration.ofMillis;

@RestController
@RequestMapping("/ThreadLocal")
public class ThreadLocalController {

    @GetMapping("/a")
    public Flux<Integer> a() {
        return Flux.range(1, 10)
                .doOnNext(it -> ThreadLocalStorage.setValue("A"))
                .delayElements(ofMillis(900))
                .doOnNext(it -> printTest("A", it));
    }

    @GetMapping("/b")
    public Flux<Integer> b() {
        return Flux.range(1, 10)
                .doOnNext(it -> ThreadLocalStorage.setValue("B"))
                .delayElements(ofMillis(1000))
                .doOnNext(it -> printTest("B", it));
    }

    private void printTest(String name, int index) {
        String value = ThreadLocalStorage.getValue();
        String message = name.equals("A") ? "> [A] " : "\t\t\t\t\t\t\t\t> [B] ";
        message += " - ";
        message += "#" + index;
        message += " - ";
        message += value;
        if (value == null || !value.equals(name))
            message += " ERROR !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!";
        System.out.println(message);
    }
}
