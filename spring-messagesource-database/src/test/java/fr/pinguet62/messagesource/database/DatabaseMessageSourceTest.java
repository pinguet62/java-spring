package fr.pinguet62.messagesource.database;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

import static java.util.Locale.CHINESE;
import static java.util.Locale.ENGLISH;
import static java.util.Locale.FRANCE;
import static java.util.Locale.FRENCH;
import static java.util.Locale.GERMAN;
import static java.util.Locale.GERMANY;
import static java.util.Locale.ITALIAN;
import static java.util.Locale.UK;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;

/**
 * @see DatabaseMessageSource
 */
class DatabaseMessageSourceTest {

    static final Object[] args = {};

    @Nested
    @SpringBootTest(classes = DatabaseMessageSourceConfig.class)
    @Sql(value = "/test-data.sql", config = @SqlConfig(encoding = "UTF-8"))
    @Sql(statements = "delete from MESSAGE;", executionPhase = AFTER_TEST_METHOD)
    class DatabaseWithLanguage {

        @Autowired
        private DatabaseMessageSource messageSource;

        @BeforeEach
        void initDefaultLocale() {
            messageSource.setDefaultLocale(ITALIAN);
        }

        @Test
        void test_LocaleLanguage() {
            // Database: LocaleLanguage
            // Get from: LocaleLanguage
            // Result: LocaleLanguage
            assertEquals("Deutsch", messageSource.getMessage("key", args, GERMANY));
        }

        @Test
        void test_Locale() {
            // Database: LocaleLanguage
            // Get from: Locale
            // Result: default
            assertEquals("Italiano", messageSource.getMessage("key", args, GERMAN));
        }

        @Test
        void test_default() {
            // Database: LocaleLanguage
            // Get from: ?
            // Result: default
            assertEquals("Italiano", messageSource.getMessage("key", args, CHINESE));
        }
    }

    @Nested
    @SpringBootTest(classes = DatabaseMessageSourceConfig.class)
    @Sql(value = "/test-data.sql", config = @SqlConfig(encoding = "UTF-8"))
    @Sql(statements = "delete from MESSAGE;", executionPhase = AFTER_TEST_METHOD)
    class DatabaseWithLocale {

        @Autowired
        DatabaseMessageSource messageSource;

        @BeforeEach
        void initDefaultLocale() {
            messageSource.setDefaultLocale(ITALIAN);
        }

        @Test
        void test_LocaleLanguage() {
            // Database: Locale
            // Get from: LocaleLanguage
            // Result: Locale
            assertEquals("English", messageSource.getMessage("key", args, UK));
        }

        @Test
        void test_Locale() {
            // Database: Locale
            // Get from: Locale
            // Result: Locale
            assertEquals("English", messageSource.getMessage("key", args, ENGLISH));
        }

        @Test
        void test_default() {
            // Database: Locale
            // Get from: ?
            // Result: default
            assertEquals("Italiano", messageSource.getMessage("key", args, CHINESE));
        }
    }

    @Nested
    @SpringBootTest(classes = DatabaseMessageSourceConfig.class)
    @Sql(value = "/test-data.sql", config = @SqlConfig(encoding = "UTF-8"))
    @Sql(statements = "delete from MESSAGE;", executionPhase = AFTER_TEST_METHOD)
    class DatabaseWithLocaleAndLocaleLanguage {

        @Autowired
        DatabaseMessageSource messageSource;

        @BeforeEach
        void initDefaultLocale() {
            messageSource.setDefaultLocale(ITALIAN);
        }

        @Test
        void test_LocaleLanguage() {
            // Database: Locale + LocaleLanguage
            // Get from: LocaleLanguage
            // Result: LocaleLanguage
            assertEquals("Français de France", messageSource.getMessage("key", args, FRANCE));
        }

        @Test
        void test_Locale() {
            // Database: Locale + LocaleLanguage
            // Get from: Locale
            // Result: Locale
            assertEquals("Français", messageSource.getMessage("key", args, FRENCH));
        }

        @Test
        void test_default() {
            // Database: Locale + LocaleLanguage
            // Get from: ?
            // Result: default
            assertEquals("Italiano", messageSource.getMessage("key", args, CHINESE));
        }
    }
}
