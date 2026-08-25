package com.example.showcase
import io.micronaut.runtime.Micronaut
class Application { companion object { @JvmStatic fun main(args: Array<String>) { Micronaut.run(Application::class.java, *args) } } }
