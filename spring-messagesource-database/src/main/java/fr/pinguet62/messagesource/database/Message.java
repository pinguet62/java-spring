package fr.pinguet62.messagesource.database;

import fr.pinguet62.messagesource.database.Message.MessageId;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import java.io.Serializable;

@Getter
@Setter
@Entity
@IdClass(MessageId.class)
public class Message {

    @Getter
    @Setter
    public static class MessageId implements Serializable {

        private static final long serialVersionUID = 1L;

        private String code;

        private String locale;
    }

    @Id
    private String code;

    @Id
    private String locale;

    private String translation;
}
