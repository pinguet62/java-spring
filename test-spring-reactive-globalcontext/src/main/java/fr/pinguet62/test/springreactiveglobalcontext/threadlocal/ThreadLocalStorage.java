package fr.pinguet62.test.springreactiveglobalcontext.threadlocal;

import lombok.Getter;
import lombok.Setter;

public class ThreadLocalStorage {

    @Getter
    @Setter
    private static final ThreadLocal<String> threadLocal = new ThreadLocal();

    public static String getValue() {
        return threadLocal.get();
    }

    public static void setValue(String value) {
        threadLocal.set(value);
    }
}
