package com.groovycoder.groovyintegrationtesting

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.PostgreSQLContainer
import spock.lang.Specification

import java.util.stream.Stream

@SpringBootTest
abstract class AbstractSingletonIntegrationTest extends Specification {


    static {
        PostgreSQLContainer postgresContainer = new PostgreSQLContainer()
                .withUsername("groovy")
                .withPassword("mobydock")
                .withDatabaseName("groovy")

        GenericContainer mongoContainer = new GenericContainer("mongo:latest")
                .withExposedPorts(27017)


        Stream.of(postgresContainer, mongoContainer)
                .parallel()
                .forEach({it.start()})

        System.setProperty("spring.datasource.url", postgresContainer.jdbcUrl)
        System.setProperty("spring.datasource.username", postgresContainer.username)
        System.setProperty("spring.datasource.password", postgresContainer.password)

        System.setProperty("spring.data.mongodb.host", mongoContainer.containerIpAddress)
        System.setProperty("spring.data.mongodb.port", mongoContainer.firstMappedPort as String)
    }



}
