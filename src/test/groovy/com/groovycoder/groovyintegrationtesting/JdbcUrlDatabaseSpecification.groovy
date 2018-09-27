package com.groovycoder.groovyintegrationtesting

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

@ContextConfiguration
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
abstract class JdbcUrlDatabaseSpecification extends Specification {

    def setupSpec() {
        System.setProperty("spring.datasource.url", "jdbc:tc:postgresql:9.6.8://hostname/databasename")
        System.setProperty("spring.datasource.driver-class-name", "org.testcontainers.jdbc.ContainerDatabaseDriver")
    }

    def cleanupSpec() {
        System.clearProperty("spring.datasource.url")
        System.clearProperty("spring.datasource.driver-class-name")
    }

}
