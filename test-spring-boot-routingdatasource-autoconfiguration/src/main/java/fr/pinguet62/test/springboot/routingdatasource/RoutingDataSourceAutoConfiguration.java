package fr.pinguet62.test.springboot.routingdatasource;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.stereotype.Component;

// Condition
@ConditionalOnClass(DataSource.class)
@ConditionalOnMissingBean(DataSource.class)
// Config
@Configuration
@EnableConfigurationProperties
public class RoutingDataSourceAutoConfiguration {

    @Component
    @ConfigurationProperties(prefix = "spring")
    public static class DataSourceMap {
        private Map<String, DataSourceProperties> datasource = new HashMap<>();

        public Map<String, DataSourceProperties> getDatasource() {
            return datasource;
        }

        public void setDatasource(Map<String, DataSourceProperties> value) {
            datasource = value;
        }
    }

    @ConditionalOnPropertyGroup("spring.datasource")
    @Bean
    public AbstractRoutingDataSource routingdataSource() {
        return new AbstractRoutingDataSource() {
            @Override
            protected Object determineCurrentLookupKey() {
                return RoutingDataSourceLookupHolder.get();
            }
        };
    }

}