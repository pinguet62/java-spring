package fr.pinguet62.test.springdataencrypt;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.springframework.test.context.TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestExecutionListeners(mergeMode = MERGE_WITH_DEFAULTS, listeners = DbUnitTestExecutionListener.class)
public class EncryptionTest {

    @Autowired
    private EncryptedRepository repository;

    @DatabaseSetup("/dataset.xml")
    @Test
    public void test() {
        assertThat(repository.findById(1).get().getName(), is("azerty"));
        assertThat(repository.findById(2).get().getName(), is("0123456789"));
    }

}
