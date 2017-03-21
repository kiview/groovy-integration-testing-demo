package com.groovycoder.groovyintegrationtesting

import org.rnorth.ducttape.ratelimits.RateLimiter
import org.rnorth.ducttape.ratelimits.RateLimiterBuilder
import org.rnorth.ducttape.unreliables.Unreliables
import org.testcontainers.containers.GenericContainer

import java.sql.Connection
import java.sql.Driver
import java.sql.SQLException
import java.util.concurrent.TimeUnit

class JdbcWaitStrategy extends GenericContainer.AbstractWaitStrategy {

    private static final Object DRIVER_LOAD_MUTEX = new Object();

    String username
    String password
    String jdbcUrl
    String driverClassName
    String testQueryString

    Driver driver

    private static final RateLimiter DB_CONNECT_RATE_LIMIT = RateLimiterBuilder.newBuilder()
            .withRate(10, TimeUnit.SECONDS)
            .withConstantThroughput()
            .build();

    @Override
    protected void waitUntilReady() {


        logger().info("Waiting for database connection to become available at {} using query '{}'", getJdbcUrl(), getTestQueryString())
        Unreliables.retryUntilSuccess(120, TimeUnit.SECONDS, {


            Connection connection = DB_CONNECT_RATE_LIMIT.getWhenReady({createConnection("")})

            boolean success = connection.createStatement().execute(testQueryString)

            if (success) {
                logger().info("Obtained a connection to container ({})", jdbcUrl);
                return connection;
            } else {
                throw new SQLException("Failed to execute test query");
            }
        })

    }

    /**
     * Obtain an instance of the correct JDBC driver for this particular database container type
     * @return a JDBC Driver
     */
    Driver getJdbcDriverInstance() {

        synchronized (DRIVER_LOAD_MUTEX) {
            if (driver == null) {
                try {
                    driver = (Driver) Class.forName(driverClassName).newInstance()
                } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                    throw new RuntimeException("Could not get Driver", e)
                }
            }
        }

        return driver;
    }

    /**
     * Creates a connection to the underlying containerized database instance.
     *
     * @param queryString   any special query string parameters that should be appended to the JDBC connection URL. The
     *                      '?' character must be included
     * @return              a Connection
     * @throws SQLException if there is a repeated failure to create the connection
     */
    Connection createConnection(String queryString) throws SQLException {
        final Properties info = new Properties()
        info.put("user", username)
        info.put("password", password)
        final String url = this.getJdbcUrl() + queryString

        final Driver jdbcDriverInstance = getJdbcDriverInstance()

        try {
            return Unreliables.retryUntilSuccess(120, TimeUnit.SECONDS, {jdbcDriverInstance.connect(url, info)})
        } catch (Exception e) {
            throw new SQLException("Could not create new connection", e)
        }
    }

}