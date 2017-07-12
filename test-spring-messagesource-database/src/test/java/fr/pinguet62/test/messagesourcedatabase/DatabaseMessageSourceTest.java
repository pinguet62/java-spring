package fr.pinguet62.test.messagesourcedatabase;

import static java.util.Locale.CHINESE;
import static java.util.Locale.ENGLISH;
import static java.util.Locale.FRANCE;
import static java.util.Locale.FRENCH;
import static java.util.Locale.GERMAN;
import static java.util.Locale.GERMANY;
import static java.util.Locale.ITALIAN;
import static java.util.Locale.UK;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

/** @see DatabaseMessageSource */
public class DatabaseMessageSourceTest {

    public static final Object[] args = {};

    @RunWith(SpringRunner.class)
    @ContextConfiguration(classes = DatabaseMessageSourceConfig.class)
    @Sql("/test-data.sql")
    @Sql(statements = "delete from MESSAGE;", executionPhase = AFTER_TEST_METHOD)
    public static class DatabaseWithLanguage {

        @Autowired
        private DatabaseMessageSource messageSource;

        @Before
        public void initDefaultLocale() {
            messageSource.setDefaultLocale(ITALIAN);
        }

        @Test
        public void test_LocaleLanguage() {
            // Database: LocaleLanguage
            // Get from: LocaleLanguage
            // Result: LocaleLanguage
            assertEquals("Deutsch", messageSource.getMessage("key", args, GERMANY));
        }

        @Test
        public void test_Locale() {
            // Database: LocaleLanguage
            // Get from: Locale
            // Result: default
            assertEquals("Italiano", messageSource.getMessage("key", args, GERMAN));
        }

        @Test
        public void test_default() {
            // Database: LocaleLanguage
            // Get from: ?
            // Result: default
            assertEquals("Italiano", messageSource.getMessage("key", args, CHINESE));
        }
    }

    @RunWith(SpringRunner.class)
    @ContextConfiguration(classes = DatabaseMessageSourceConfig.class)
    @Sql("/test-data.sql")
    @Sql(statements = "delete from MESSAGE;", executionPhase = AFTER_TEST_METHOD)
    public static class DatabaseWithLocale {

        @Autowired
        private DatabaseMessageSource messageSource;

        @Before
        public void initDefaultLocale() {
            messageSource.setDefaultLocale(ITALIAN);
        }

        @Test
        public void test_LocaleLanguage() {
            // Database: Locale
            // Get from: LocaleLanguage
            // Result: Locale
            assertEquals("English", messageSource.getMessage("key", args, UK));
        }

        @Test
        public void test_Locale() {
            // Database: Locale
            // Get from: Locale
            // Result: Locale
            assertEquals("English", messageSource.getMessage("key", args, ENGLISH));
        }

        @Test
        public void test_default() {
            // Database: Locale
            // Get from: ?
            // Result: default
            assertEquals("Italiano", messageSource.getMessage("key", args, CHINESE));
        }
    }

    @RunWith(SpringRunner.class)
    @ContextConfiguration(classes = DatabaseMessageSourceConfig.class)
    @Sql("/test-data.sql")
    @Sql(statements = "delete from MESSAGE;", executionPhase = AFTER_TEST_METHOD)
    public static class DatabaseWithLocaleAndLocaleLanguage {

        @Autowired
        private DatabaseMessageSource messageSource;

        @Before
        public void initDefaultLocale() {
            messageSource.setDefaultLocale(ITALIAN);
        }

        @Test
        public void test_LocaleLanguage() {
            // Database: Locale + LocaleLanguage
            // Get from: LocaleLanguage
            // Result: LocaleLanguage
            assertEquals("Français de France", messageSource.getMessage("key", args, FRANCE));
        }

        @Test
        public void test_Locale() {
            // Database: Locale + LocaleLanguage
            // Get from: Locale
            // Result: Locale
            assertEquals("Français", messageSource.getMessage("key", args, FRENCH));
        }

        @Test
        public void test_default() {
            // Database: Locale + LocaleLanguage
            // Get from: ?
            // Result: default
            assertEquals("Italiano", messageSource.getMessage("key", args, CHINESE));
        }
    }

}