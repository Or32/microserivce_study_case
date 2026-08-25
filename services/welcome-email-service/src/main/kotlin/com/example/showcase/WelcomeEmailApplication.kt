package com.example.showcase
import io.micronaut.runtime.Micronaut
class WelcomeEmailApplication { companion object { @JvmStatic fun main(args: Array<String>) { Micronaut.run(WelcomeEmailApplication::class.java, *args) } } }
