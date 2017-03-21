package com.groovycoder.groovyintegrationtesting

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ContextConfiguration
import org.testcontainers.containers.PostgreSQLContainer
import spock.lang.Specification

@ContextConfiguration
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
abstract class ExclusiveDatabaseSpecification extends Specification {

    static PostgreSQLContainer postgresContainer = {
        def c = new PostgreSQLContainer()
                .withUsername("groovy")
                .withPassword("mobydock")
                .withDatabaseName("groovy")
        c.setPortBindings(["5432:5432"])
        c
    }()


    def setupSpec() {
        postgresContainer.start()
        System.setProperty("spring.datasource.url", "jdbc:postgresql:groovy")
        System.setProperty("spring.datasource.username", "groovy")
        System.setProperty("spring.datasource.password", "mobydock")

    }

    def cleanup() {
        postgresContainer.stop()
        postgresContainer.start()
    }

    def cleanupSpec() {
        System.clearProperty("spring.datasource.url")
        System.clearProperty("spring.datasource.username")
        System.clearProperty("spring.datasource.password")
    }

}
