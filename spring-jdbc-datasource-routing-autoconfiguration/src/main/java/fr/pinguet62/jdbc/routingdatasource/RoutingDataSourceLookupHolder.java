package fr.pinguet62.jdbc.routingdatasource;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * @see AbstractRoutingDataSource#determineCurrentLookupKey()
 */
public class RoutingDataSourceLookupHolder {

    private static final ThreadLocal<Object> HOLDER = new ThreadLocal<>();

    public static Object get() {
        return HOLDER.get();
    }

    public static void set(Object value) {
        HOLDER.set(value);
    }
}
