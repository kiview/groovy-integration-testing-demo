package com.groovycoder.groovyintegrationtesting

import groovy.transform.ToString
import org.springframework.data.annotation.Id

@ToString
class Customer {

    @Id
    String id

    String firstName
    String lastName
}
