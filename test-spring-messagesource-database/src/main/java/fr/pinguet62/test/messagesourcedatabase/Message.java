package fr.pinguet62.test.messagesourcedatabase;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;

import fr.pinguet62.test.messagesourcedatabase.Message.MessageId;

@Entity
@IdClass(MessageId.class)
public class Message {

    public static class MessageId implements Serializable {

        private static final long serialVersionUID = 1L;

        private String code;

        private String locale;

        public String getCode() {
            return code;
        }

        public String getLocale() {
            return locale;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public void setLocale(String locale) {
            this.locale = locale;
        }

    }

    @Id
    private String code;

    @Id
    private String locale;

    private String value;

    public String getCode() {
        return code;
    }

    public String getLocale() {
        return locale;
    }

    public String getValue() {
        return value;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public void setValue(String message) {
        this.value = message;
    }

}