package fr.pinguet62.springkafkalistener.override;

import lombok.Getter;
import lombok.Setter;

import java.util.Collection;
import java.util.Properties;

@Getter
@Setter
public class KafkaListenerProperties {

    private Collection<String> topics;
    private String groupId = null;
    private Properties properties = null;
}
