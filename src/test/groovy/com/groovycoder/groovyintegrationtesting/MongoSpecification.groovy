package com.groovycoder.groovyintegrationtesting

import com.groovycoder.spockdockerextension.Testcontainers
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.util.EnvironmentTestUtils
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ContextConfiguration
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.PostgreSQLContainer
import spock.lang.Shared
import spock.lang.Specification

@Testcontainers
@ContextConfiguration(initializers = Initializer)
@SpringBootTest(classes = GroovyIntegrationTestingDemoApplication)
@DirtiesContext
abstract class MongoSpecification extends Specification {

    @Shared
    GenericContainer mongoContainer = new GenericContainer("mongo:latest")
            .withExposedPorts(27017)

    static GenericContainer staticContainerHandle

    @Shared
    PostgreSQLContainer postgresContainer = new PostgreSQLContainer()
            .withDatabaseName("groovy")
            .withUsername("groovy")
            .withPassword("mobydock")

    static PostgreSQLContainer staticPostgresContainerHandle

    def setupSpec() {
        staticContainerHandle = mongoContainer
        staticPostgresContainerHandle = postgresContainer
    }

    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            EnvironmentTestUtils.addEnvironment("testcontainers", configurableApplicationContext.getEnvironment(),
                    "spring.data.mongodb.host=" + staticContainerHandle.containerIpAddress,
                    "spring.data.mongodb.port=" + staticContainerHandle.getMappedPort(27017),
                    "spring.datasource.url=" + staticPostgresContainerHandle.jdbcUrl,
                    "spring.datasource.username=" + staticPostgresContainerHandle.username,
                    "spring.datasource.password=" + staticPostgresContainerHandle.password,
            )
        }
    }

}
