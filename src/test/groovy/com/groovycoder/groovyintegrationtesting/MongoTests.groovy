package com.groovycoder.groovyintegrationtesting

import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Stepwise

@Stepwise
class MongoTests extends MongoSpecification {

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


}
