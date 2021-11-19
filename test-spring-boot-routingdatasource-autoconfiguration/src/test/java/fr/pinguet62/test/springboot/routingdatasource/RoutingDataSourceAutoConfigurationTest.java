package fr.pinguet62.test.springboot.routingdatasource;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.test.context.TestPropertySource;

import javax.sql.DataSource;
import java.sql.SQLException;

import static fr.pinguet62.test.springboot.routingdatasource.RoutingDataSourceAutoConfigurationTest.CustomerConfigTest.CustomerDataSourceConfig;
import static fr.pinguet62.test.springboot.routingdatasource.RoutingDataSourceAutoConfigurationTest.CustomerConfigTest.CustomerDataSourceConfig.MOCK;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.mock;

class RoutingDataSourceAutoConfigurationTest {

    @Nested
    @SpringBootTest(classes = RoutingDataSourceAutoConfiguration.class)
            // config: none
    class NoConfigTest {
        @Autowired(required = false)
        DataSource dataSource;

        @Test
        void noDataSource() {
            assertThat(dataSource, is(nullValue()));
        }
    }

    @Nested
    @SpringBootTest(classes = RoutingDataSourceAutoConfiguration.class)
    // config
    @TestPropertySource(properties = {
            "spring.datasource.first.url=jdbc:h2:mem:foo",
            "spring.datasource.second.url=jdbc:h2:mem:bar"
    })
    class OnlyWithRoutingConfigTest {
        @Autowired
        DataSource dataSource;

        @Test
        void useRoutingAutoConfiguration() throws SQLException {
            assertThat(dataSource, is(instanceOf(AbstractRoutingDataSource.class)));

            RoutingDataSourceLookupHolder.set("first");
            assertThat(dataSource.getConnection().getMetaData().getURL(), is("jdbc:h2:mem:foo"));
            RoutingDataSourceLookupHolder.set("second");
            assertThat(dataSource.getConnection().getMetaData().getURL(), is("jdbc:h2:mem:bar"));
        }
    }

    @Nested
    @SpringBootTest(classes = RoutingDataSourceAutoConfiguration.class)
    // config
    @Import(CustomerDataSourceConfig.class)
    class CustomerConfigTest {
        static class CustomerDataSourceConfig {
            static final DataSource MOCK = mock(DataSource.class);

            @Bean
            DataSource dataSource() {
                return MOCK;
            }
        }

        @Autowired
        DataSource dataSource;

        @Test
        void useCustom() {
            assertThat(dataSource, is(MOCK));
        }
    }
}
