package fr.pinguet62.test.springboot.routingdatasource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;
import java.sql.SQLException;

import static fr.pinguet62.test.springboot.routingdatasource.ITTest.TestConfig;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestConfig.class, properties = {
        "spring.datasource.first.url=jdbc:h2:mem:foo",
        "spring.datasource.second.url=jdbc:h2:mem:bar"
})
public class ITTest {

    @SpringBootApplication
    static class TestConfig {
    }

    @Autowired
    private DataSource dataSource;

    @Test
    public void test() throws SQLException {
        assertThat(dataSource, is(instanceOf(AbstractRoutingDataSource.class)));

        RoutingDataSourceLookupHolder.set("first");
        assertThat(dataSource.getConnection().getMetaData().getURL(), is("jdbc:h2:mem:foo"));
        RoutingDataSourceLookupHolder.set("second");
        assertThat(dataSource.getConnection().getMetaData().getURL(), is("jdbc:h2:mem:bar"));
    }

}
