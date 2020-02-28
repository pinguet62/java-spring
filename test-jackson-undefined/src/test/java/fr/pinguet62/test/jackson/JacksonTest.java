package fr.pinguet62.test.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

public class JacksonTest {

    private static final ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new NillableModule());
    }

    static class DefaultPojo {
        private String attr;

        public String getAttr() {
            return attr;
        }
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
    public void test_default_null() throws IOException {
        String json = "{}";
        DefaultPojo pojo = objectMapper.readValue(json, DefaultPojo.class);
        assertThat(pojo.getAttr(), is(nullValue()));
    }

    @Test
    public void test_proxySetter_undefined() throws IOException {
        String json = "{}";
        ProxySetterPojo pojo = objectMapper.readValue(json, ProxySetterPojo.class);
        assertThat(pojo.getAttr(), is(Nillable.undefined()));
    }

    @Test
    public void test_proxySetter_null() throws IOException {
        String json = "{ \"attr\": null}";
        ProxySetterPojo pojo = objectMapper.readValue(json, ProxySetterPojo.class);
        assertThat(pojo.getAttr().isSet(), is(true));
    }

    @Test
    public void test_proxySetter_present() throws IOException {
        String json = "{ \"attr\": \"value\" }";
        ProxySetterPojo pojo = objectMapper.readValue(json, ProxySetterPojo.class);
        assertThat(pojo.getAttr().get(), is("value"));
    }

    @Test
    public void test_support_undefined() throws IOException {
        String json = "{}";
        SupportPojo pojo = objectMapper.readValue(json, SupportPojo.class);
        assertThat(pojo.getAttr(), is(Nillable.undefined()));
    }

    @Test
    public void test_support_null() throws IOException {
        String json = "{ \"attr\": null}";
        SupportPojo pojo = objectMapper.readValue(json, SupportPojo.class);
        assertThat(pojo.getAttr().isSet(), is(true));
    }

    @Test
    public void test_support_present() throws IOException {
        String json = "{ \"attr\": \"value\" }";
        SupportPojo pojo = objectMapper.readValue(json, SupportPojo.class);
        assertThat(pojo.getAttr().get(), is("value"));
    }

}
