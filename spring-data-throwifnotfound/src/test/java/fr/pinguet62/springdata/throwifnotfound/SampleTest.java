package fr.pinguet62.springdata.throwifnotfound;

import fr.pinguet62.springdata.throwifnotfound.sample.SampleApplication;
import fr.pinguet62.springdata.throwifnotfound.sample.SampleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest(classes = SampleApplication.class)
class SampleTest {

    @Autowired
    SampleRepository repository;

    @Test
    void test_findByIdOrThrow() {
        NotFoundException error = null;
        try {
            repository.findByIdOrThrow(42);
        } catch (NotFoundException e) {
            error = e;
        }
        assertNotNull(error);
    }

    @Test
    void test_generatedMethod_notAnnotated() {
        assertNull(repository.findByIdOrName(42, "unknown"));
    }

    @Test
    void test_generatedMethod_annotated() {
        NotFoundException error = null;
        try {
            repository.findByName("unknown");
        } catch (NotFoundException e) {
            error = e;
        }
        assertNotNull(error);
    }
}
