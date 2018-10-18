package com.groovycoder.groovyintegrationtesting

import com.mongodb.MongoClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.jdbc.datasource.DriverManagerDataSource
import org.springframework.test.annotation.DirtiesContext
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.PostgreSQLContainer
import spock.lang.Specification
import spock.lang.Stepwise

import javax.annotation.PreDestroy
import javax.sql.DataSource

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@Stepwise
class BeanDatabaseTests extends Specification {

    @Autowired
    BookRepository bookRepository

    def "book repository is empty"() {
        expect:
        bookRepository.count() == 0
    }


    def "books can be saved to repository"() {
        given: "a book"
        def book = new Book(
                title: "Moby Dick",
                author: "Herman Melville"
        )

        when: "saving it to repository"
        bookRepository.save(book)

        then: "it's assigend an id"
        book.id != null
    }


    def "repository contains saved book"() {
        expect:
        bookRepository.count() == 1

        and:
        def firstBook = bookRepository.findAll().first()
        firstBook.title == "Moby Dick"
    }


    // Mongo tests start here

    @Autowired
    CustomerRepository repository

    def "repository is initially empty"() {
        expect:
        repository.findAll().size() == 0
    }

    def "customers can be saved to repository"() {
        given: "some customers"
        def alice = new Customer(firstName: "Alice", lastName: "Smith")
        def bob = new Customer(firstName: "Bob", lastName: "Smith")
        def customers = [alice, bob]

        when: "saving them"
        repository.saveAll(customers)

        then: "an id is assigned"
        customers*.id != null
    }

    def "searching works"() {
        expect:
        repository.findByFirstName("Alice")

        and:
        repository.findByLastName("Smith").size() == 2
    }

    @TestConfiguration
    static class TestcontainersConfiguration {

        PostgreSQLContainer postgresContainer = new PostgreSQLContainer()

        GenericContainer mongoContainer = new GenericContainer("mongo:latest")
                .withExposedPorts(27017)

        @Bean
        DataSource dataSource() {
            postgresContainer.start()

            DriverManagerDataSource dataSource = new DriverManagerDataSource()
            dataSource.setUrl(postgresContainer.jdbcUrl)
            dataSource.setUsername(postgresContainer.username)
            dataSource.setPassword(postgresContainer.password)

            return dataSource
        }

        @Bean
        MongoClient mongoClient() {
            mongoContainer.start()

            String mongoHost = mongoContainer.containerIpAddress
            int mongoPort = mongoContainer.getMappedPort(27017)

            return new MongoClient(mongoHost, mongoPort)
        }

        @PreDestroy
        destroy() {
            postgresContainer.stop()
            mongoContainer.stop()
        }

    }

}
