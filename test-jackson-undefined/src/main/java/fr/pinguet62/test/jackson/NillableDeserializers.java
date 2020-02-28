package fr.pinguet62.test.jackson;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.Deserializers;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.type.ReferenceType;

public class NillableDeserializers extends Deserializers.Base {

    @Override
    public JsonDeserializer<?> findReferenceDeserializer(
            ReferenceType refType,
            DeserializationConfig config,
            BeanDescription beanDesc,
            TypeDeserializer contentTypeDeserializer,
            JsonDeserializer<?> contentDeserializer) {
        if (refType.hasRawClass(Nillable.class)) {
            return new NillableDeserializer(refType, null, contentTypeDeserializer, contentDeserializer);
        }
        return null;
    }

}
