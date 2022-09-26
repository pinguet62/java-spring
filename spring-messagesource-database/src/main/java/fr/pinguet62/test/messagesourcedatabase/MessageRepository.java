package fr.pinguet62.test.messagesourcedatabase;

import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Locale;
import java.util.Set;

@Repository
public interface MessageRepository extends CrudRepository<Message, String>, MessageRepositoryCustom {

    Message findValueByCodeAndLocale(String code, String locale);
}

interface MessageRepositoryCustom {

    /**
     * @param locale Ordered {@link Locale}.
     * @return The {@link Message#getTranslation()}<br>
     * {@code null} if not found.
     */
    String findValueByCodeAndLocaleOrDefault(String code, Set<String> locales);
}

class MessageRepositoryImpl implements MessageRepositoryCustom {

    @Autowired // workaround: circular reference
    @Lazy
    @Setter
    private MessageRepository messageRepository;

    @Override
    public String findValueByCodeAndLocaleOrDefault(String code, Set<String> locales) {
        for (String loc : locales) {
            Message message = messageRepository.findValueByCodeAndLocale(code, loc);
            if (message != null)
                return message.getTranslation();
        }
        return null;
    }
}
