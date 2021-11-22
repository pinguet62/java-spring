package fr.pinguet62.test.jackson;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.Module;

public class NillableModule extends Module {

    @Override
    public String getModuleName() {
        return "NillableModule";
    }

    @Override
    public Version version() {
        return new Version(
                1, 1, 1, null,
                "fr.pinguet62.test", "test-jackson-undefined");
    }

    @Override
    public void setupModule(SetupContext context) {
        context.addDeserializers(new NillableDeserializers());
        context.addTypeModifier(new NillableTypeModifier());
    }
}
