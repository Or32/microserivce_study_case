package com.example.showcase
import io.micronaut.runtime.Micronaut
class ValidationApplication { companion object { @JvmStatic fun main(args: Array<String>) { Micronaut.run(ValidationApplication::class.java, *args) } } }
