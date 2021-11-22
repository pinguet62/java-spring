package fr.pinguet62.test.springboot.routingdatasource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import static java.util.stream.Collectors.toMap;
import static org.springframework.boot.context.properties.bind.Bindable.ofInstance;

// Condition
@ConditionalOnClass(DataSource.class)
@ConditionalOnMissingBean({DataSource.class, AbstractRoutingDataSource.class})
// Config
@Configuration
@EnableConfigurationProperties
public class RoutingDataSourceAutoConfiguration {

    @Autowired
    private Environment environment;

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
    public AbstractRoutingDataSource routingDataSource(DataSourceMap dataSourceMap) {
        Map<Object, Object> targetDataSources = dataSourceMap.getDatasource().entrySet().stream()
                .collect(toMap(
                        Entry::getKey,
                        e -> buildFrom("spring.datasource" + "." + e.getKey())));

        AbstractRoutingDataSource abstractRoutingDataSource = new AbstractRoutingDataSource() {
            @Override
            protected Object determineCurrentLookupKey() {
                return RoutingDataSourceLookupHolder.get();
            }
        };
        abstractRoutingDataSource.setTargetDataSources(targetDataSources);
        return abstractRoutingDataSource;
    }

    /**
     * @param propertyPath e.g. {@code "spring.datasource.first"}
     *                     containing {@code "spring.datasource.first.url"} & co.
     */
    private DataSource buildFrom(String propertyPath) {
        DataSourceProperties dataSourceProperties = new DataSourceProperties();
        dataSourceProperties = Binder.get(environment).bind(propertyPath, ofInstance(dataSourceProperties)).get();

        DataSource dataSource = dataSourceProperties.initializeDataSourceBuilder().build();
        // dataSource = Binder.get(environment).bind(propertyPath, ofInstance(dataSource)).get();

        return dataSource;
    }
}
