package com.groovycoder.groovyintegrationtesting

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
class Book {

    @Id
    @GeneratedValue
    Long id

    String title
    String author
}
