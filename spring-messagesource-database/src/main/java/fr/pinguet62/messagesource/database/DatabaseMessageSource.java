package fr.pinguet62.messagesource.database;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.context.support.AbstractMessageSource;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

@RequiredArgsConstructor
@Component
public class DatabaseMessageSource extends AbstractMessageSource {

    private final MessageRepository repository;

    @Setter
    private Locale defaultLocale;

    @Override
    protected MessageFormat resolveCode(String code, Locale locale) {
        Set<String> locales = new LinkedHashSet<>(3);
        locales.add(locale.toString());
        if (locale.toString().contains("_"))
            locales.add(locale.getLanguage()); // parent: language without country
        if (defaultLocale != null)
            locales.add(defaultLocale.toString());

        String message = repository.findValueByCodeAndLocaleOrDefault(code, locales);
        if (message == null)
            return null;
        return new MessageFormat(message);
    }
}
