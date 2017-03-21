package com.groovycoder.groovyintegrationtesting

import org.springframework.data.repository.CrudRepository

interface BookRepository extends CrudRepository<Book, Long> {

}