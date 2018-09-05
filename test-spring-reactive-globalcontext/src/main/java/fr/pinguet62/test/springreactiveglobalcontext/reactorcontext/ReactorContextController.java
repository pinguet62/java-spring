package fr.pinguet62.test.springreactiveglobalcontext.reactorcontext;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static java.time.Duration.ofMillis;

@RestController
@RequestMapping("/ReactorContext")
public class ReactorContextController {

    @GetMapping("/a")
    public Flux<Integer> a() {
        return Flux.range(1, 10)
                // .flatMap(it -> ReactorStorageStorage.setValue("A").map((x) -> it)) // no effect: immutable in current hierarchy
                .delayElements(ofMillis(1))
                .flatMap(it -> printTestReactive("A", it).map((x) -> it))
                .subscriberContext(ReactorStorageStorage.withContext("A"));
    }

    @GetMapping("/b")
    public Flux<Integer> b() {
        return Flux.range(1, 10)
                // .flatMap(it -> ReactorStorageStorage.setValue("B").map((x) -> it)) // no effect: immutable in current hierarchy
                .delayElements(ofMillis(1000))
                .flatMap(it -> printTestReactive("B", it).map((x) -> it))
                .subscriberContext(ReactorStorageStorage.withContext("B"));
    }

    private Mono<?> printTestReactive(String name, int index) {
        return ReactorStorageStorage.getValue()
                .doOnNext(value -> {
                    String message = name.equals("A") ? "> [A] " : "\t\t\t\t\t\t\t\t> [B] ";
                    message += " - ";
                    message += "#" + index;
                    message += " - ";
                    message += value;
                    if (!value.equals(name))
                        message += " ERROR !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!";
                    System.out.println(message);
                });
    }
}
