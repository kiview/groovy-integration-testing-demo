package com.groovycoder.groovyintegrationtesting

import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Stepwise

@Stepwise
class SharedDatabaseTests extends SharedDatabaseSpecification {

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

}
