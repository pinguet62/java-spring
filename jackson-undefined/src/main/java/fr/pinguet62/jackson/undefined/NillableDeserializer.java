package fr.pinguet62.jackson.undefined;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.ValueInstantiator;
import com.fasterxml.jackson.databind.deser.std.ReferenceTypeDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;

class NillableDeserializer extends ReferenceTypeDeserializer<Nillable<?>> {

    public NillableDeserializer(
            JavaType fullType,
            ValueInstantiator inst,
            TypeDeserializer typeDeser,
            JsonDeserializer<?> deser) {
        super(fullType, inst, typeDeser, deser);
    }

    @Override
    protected ReferenceTypeDeserializer<Nillable<?>> withResolved(TypeDeserializer typeDeser, JsonDeserializer<?> valueDeser) {
        return new NillableDeserializer(_fullType, _valueInstantiator, typeDeser, valueDeser);
    }

    @Override
    public Nillable<?> getNullValue(DeserializationContext ctxt) {
        return Nillable.ofDefined(null);
    }

    @Override
    public Nillable<?> referenceValue(Object contents) {
        return Nillable.ofDefined(contents);
    }

    @Override
    public Nillable<?> updateReference(Nillable<?> reference, Object contents) {
        return Nillable.ofDefined(contents);
    }

    @Override
    public Object getReferenced(Nillable<?> reference) {
        return reference.get();
    }
}
