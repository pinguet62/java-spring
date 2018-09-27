package fr.pinguet62.test.springdatathrowifnotfound;

import fr.pinguet62.test.springdatathrowifnotfound.config.NotFoundException;
import fr.pinguet62.test.springdatathrowifnotfound.sample.SampleRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SampleTest {

    @Autowired
    private SampleRepository repository;

    @Test
    public void test_findByIdOrThrow() {
        NotFoundException error = null;
        try {
            repository.findByIdOrThrow(42);
        } catch (NotFoundException e) {
            error = e;
        }
        assertNotNull(error);
    }

    @Test
    public void test_generatedMethod_notAnnotated() {
        assertNull(repository.findByIdOrName(42, "unknown"));
    }

    @Test
    public void test_generatedMethod_annotated() {
        NotFoundException error = null;
        try {
            repository.findByName("unknown");
        } catch (NotFoundException e) {
            error = e;
        }
        assertNotNull(error);
    }

}
