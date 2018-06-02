package com.groovycoder.groovyintegrationtesting

import com.groovycoder.spockdockerextension.Testcontainers
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ContextConfiguration
import org.testcontainers.containers.PostgreSQLContainer
import spock.lang.Shared
import spock.lang.Specification

@Testcontainers
@ContextConfiguration(initializers = Initializer)
@SpringBootTest(classes = GroovyIntegrationTestingDemoApplication)
@DirtiesContext
abstract class SharedDatabaseSpecification extends Specification {

    @Shared
    PostgreSQLContainer postgresContainer = new PostgreSQLContainer()
            .withDatabaseName("groovy")
            .withUsername("groovy")
            .withPassword("mobydock")

    static PostgreSQLContainer staticContainerHandle

    def setupSpec() {
        staticContainerHandle = postgresContainer
    }

    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues values = TestPropertyValues.of(
                    "spring.datasource.url=" + staticContainerHandle.jdbcUrl,
                    "spring.datasource.username=" + staticContainerHandle.username,
                    "spring.datasource.password=" + staticContainerHandle.password,
            )
            values.applyTo(configurableApplicationContext)
        }
    }

}
