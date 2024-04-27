package fr.pinguet62.springkafkalistener.override;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class KafkaListenerMethodsProperties {

    private Map<String, KafkaListenerProperties> listeners = new HashMap<>();
}
