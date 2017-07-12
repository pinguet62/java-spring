package fr.pinguet62.test.messagesourcedatabase;

import java.util.Locale;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends CrudRepository<Message, String>, MessageRepositoryCustom {

    Message findValueByCodeAndLocale(String code, String locale);

}

interface MessageRepositoryCustom {

    /**
     * @param locale Ordered {@link Locale}.
     * @return The {@link Message#getValue()}<br>
     *         {@code null} if not found.
     */
    String findValueByCodeAndLocaleOrDefault(String code, Set<String> locales);

}

class MessageRepositoryImpl implements MessageRepositoryCustom {

    @Autowired
    private MessageRepository messageRepository;

    @Override
    public String findValueByCodeAndLocaleOrDefault(String code, Set<String> locales) {
        for (String loc : locales) {
            Message message = messageRepository.findValueByCodeAndLocale(code, loc);
            if (message != null)
                return message.getValue();
        }
        return null;
    }

}