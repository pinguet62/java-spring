package fr.pinguet62.test.springreactiveglobalcontext.threadlocal;

public class ThreadLocalStorage {

    private static final ThreadLocal<String> threadLocal = new ThreadLocal();

    public static String getValue() {
        return threadLocal.get();
    }

    public static void setValue(String value) {
        threadLocal.set(value);
    }

}
