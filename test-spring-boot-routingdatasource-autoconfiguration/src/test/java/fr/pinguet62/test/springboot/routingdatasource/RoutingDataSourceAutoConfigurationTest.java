package fr.pinguet62.test.springboot.routingdatasource;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;
import java.sql.SQLException;

import static fr.pinguet62.test.springboot.routingdatasource.RoutingDataSourceAutoConfigurationTest.CustomerConfigTest.CustomerDataSourceConfig;
import static fr.pinguet62.test.springboot.routingdatasource.RoutingDataSourceAutoConfigurationTest.CustomerConfigTest.CustomerDataSourceConfig.MOCK;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

@RunWith(Enclosed.class)
public class RoutingDataSourceAutoConfigurationTest {

    @RunWith(SpringRunner.class)
    @SpringBootTest(classes = {RoutingDataSourceAutoConfiguration.class, NoConfigTest.class})
    // config: none
    public static class NoConfigTest {
        @Autowired(required = false)
        private DataSource dataSource;

        @Test
        public void noDataSource() {
            assertThat(dataSource, is(nullValue()));
        }
    }

    @RunWith(SpringRunner.class)
    @SpringBootTest(classes = {RoutingDataSourceAutoConfiguration.class, OnlyWithRoutingConfigTest.class})
    // config
    @TestPropertySource(properties = {
            "spring.datasource.first.url=jdbc:h2:mem:foo",
            "spring.datasource.second.url=jdbc:h2:mem:bar"
    })
    public static class OnlyWithRoutingConfigTest {
        @Autowired
        private DataSource dataSource;

        @Test
        public void useRoutingAutoConfiguration() throws SQLException {
            assertThat(dataSource, is(instanceOf(AbstractRoutingDataSource.class)));

            RoutingDataSourceLookupHolder.set("first");
            assertThat(dataSource.getConnection().getMetaData().getURL(), is("jdbc:h2:mem:foo"));
            RoutingDataSourceLookupHolder.set("second");
            assertThat(dataSource.getConnection().getMetaData().getURL(), is("jdbc:h2:mem:bar"));
        }
    }

    @RunWith(SpringRunner.class)
    @SpringBootTest(classes = {RoutingDataSourceAutoConfiguration.class, CustomerConfigTest.class})
    // config
    @Import(CustomerDataSourceConfig.class)
    public static class CustomerConfigTest {
        public static class CustomerDataSourceConfig {
            static final DataSource MOCK = mock(DataSource.class);

            @Bean
            public DataSource dataSource() {
                return MOCK;
            }
        }

        @Autowired
        private DataSource dataSource;

        @Test
        public void useCustom() {
            assertThat(dataSource, is(MOCK));
        }
    }

}
