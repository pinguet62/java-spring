package fr.pinguet62.jackson.undefined;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

class JacksonTest {

    static final ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new NillableModule());
    }

    @Getter
    @Setter
    static class DefaultPojo {
        private String attr;
    }

    static class ProxySetterPojo {
        private Nillable<String> attr = Nillable.undefined();

        public Nillable<String> getAttr() {
            return attr;
        }

        private void setAttr(String value) {
            attr = Nillable.ofDefined(value);
        }
    }

    static class SupportPojo {
        private Nillable<String> attr = Nillable.undefined();

        public Nillable<String> getAttr() {
            return attr;
        }
    }

    @Test
    void test_default_null() throws IOException {
        String json = "{}";
        DefaultPojo pojo = objectMapper.readValue(json, DefaultPojo.class);
        assertThat(pojo.getAttr(), is(nullValue()));
    }

    @Test
    void test_proxySetter_undefined() throws IOException {
        String json = "{}";
        ProxySetterPojo pojo = objectMapper.readValue(json, ProxySetterPojo.class);
        assertThat(pojo.getAttr(), is(Nillable.undefined()));
    }

    @Test
    void test_proxySetter_null() throws IOException {
        String json = "{ \"attr\": null}";
        ProxySetterPojo pojo = objectMapper.readValue(json, ProxySetterPojo.class);
        assertThat(pojo.getAttr().isSet(), is(true));
    }

    @Test
    void test_proxySetter_present() throws IOException {
        String json = "{ \"attr\": \"value\" }";
        ProxySetterPojo pojo = objectMapper.readValue(json, ProxySetterPojo.class);
        assertThat(pojo.getAttr().get(), is("value"));
    }

    @Test
    void test_support_undefined() throws IOException {
        String json = "{}";
        SupportPojo pojo = objectMapper.readValue(json, SupportPojo.class);
        assertThat(pojo.getAttr(), is(Nillable.undefined()));
    }

    @Test
    void test_support_null() throws IOException {
        String json = "{ \"attr\": null}";
        SupportPojo pojo = objectMapper.readValue(json, SupportPojo.class);
        assertThat(pojo.getAttr().isSet(), is(true));
    }

    @Test
    void test_support_present() throws IOException {
        String json = "{ \"attr\": \"value\" }";
        SupportPojo pojo = objectMapper.readValue(json, SupportPojo.class);
        assertThat(pojo.getAttr().get(), is("value"));
    }

}
