package com.groovycoder.groovyintegrationtesting

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.annotation.DirtiesContext
import spock.lang.Stepwise

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@Stepwise
class SingletonSmokeTests extends AbstractSingletonIntegrationTest {

    @Autowired
    BookRepository bookRepository

    @Autowired
    CustomerRepository customerRepository

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

    // Mongo tests

    def "repository is initially empty"() {
        expect:
        customerRepository.findAll().size() == 0
    }

    def "customers can be saved to repository"() {
        given: "some customers"
        def alice = new Customer(firstName: "Alice", lastName: "Smith")
        def bob = new Customer(firstName: "Bob", lastName: "Smith")
        def customers = [alice, bob]

        when: "saving them"
        customerRepository.saveAll(customers)

        then: "an id is assigned"
        customers*.id != null
    }

    def "searching works"() {
        expect:
        customerRepository.findByFirstName("Alice")

        and:
        customerRepository.findByLastName("Smith").size() == 2
    }

}
