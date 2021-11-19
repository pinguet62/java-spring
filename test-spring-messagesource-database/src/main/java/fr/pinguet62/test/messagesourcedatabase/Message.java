package fr.pinguet62.test.messagesourcedatabase;

import fr.pinguet62.test.messagesourcedatabase.Message.MessageId;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
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

    private String value;
}
