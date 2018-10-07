package fr.pinguet62.test.springboot.routingdatasource;

import static fr.pinguet62.test.springboot.routingdatasource.RoutingDataSourceAutoConfigurationTest.CustomerConfig.CustomerDataSourceConfig.MOCK;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import fr.pinguet62.test.springboot.routingdatasource.RoutingDataSourceAutoConfigurationTest.CustomerConfig.CustomerDataSourceConfig;

/** @see RoutingDataSourceAutoConfiguration */
public class RoutingDataSourceAutoConfigurationTest {

    @RunWith(SpringRunner.class)
    @SpringBootTest(classes = { RoutingDataSourceAutoConfiguration.class, NoConfig.class })
    public static class NoConfig {
        @Autowired(required = false)
        private DataSource dataSource;

        @Test
        public void noDataSource() {
            assertNull(dataSource);
        }
    }

    @RunWith(SpringRunner.class)
    @SpringBootTest(classes = { RoutingDataSourceAutoConfiguration.class, OnlyWithRoutingConfig.class })
    // config
    @TestPropertySource(properties = {
            // @formatter:off
            "spring.datasource.first.url=foo",
            "spring.datasource.second.url=bar"
            // @formatter:on
    })
    public static class OnlyWithRoutingConfig {
        @Autowired
        private DataSource dataSource;

        @Test
        public void useRoutingAutoConfiguration() throws SQLException {
            assertTrue(dataSource instanceof AbstractRoutingDataSource);

            RoutingDataSourceLookupHolder.set("first");
            assertEquals("foo", dataSource.getConnection().getMetaData().getURL());
            RoutingDataSourceLookupHolder.set("second");
            assertEquals("bar", dataSource.getConnection().getMetaData().getURL());
        }
    }

    @RunWith(SpringRunner.class)
    @SpringBootTest(classes = { RoutingDataSourceAutoConfiguration.class, OnlyWithRoutingConfig.class })
    // config
    @TestPropertySource(properties = "spring.datasource.url=foo")
    public static class WithDefaultAutoConfig {
        @Autowired
        private DataSource dataSource;

        @Test
        public void useDefaultAutoConfiguration() throws SQLException {
            assertFalse(dataSource instanceof AbstractRoutingDataSource);
            assertEquals("foo", dataSource.getConnection().getMetaData().getURL());
        }
    }

    @RunWith(SpringRunner.class)
    @SpringBootTest(classes = { RoutingDataSourceAutoConfiguration.class, CustomerConfig.class })
    // config
    @Import(CustomerDataSourceConfig.class)
    public static class CustomerConfig {
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
        public void useCustom() throws SQLException {
            assertEquals(MOCK, dataSource);
        }
    }

}