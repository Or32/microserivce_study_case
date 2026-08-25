package com.example.showcase
import io.micronaut.runtime.Micronaut
class ReceiptApplication { companion object { @JvmStatic fun main(args: Array<String>) { Micronaut.run(ReceiptApplication::class.java, *args) } } }
