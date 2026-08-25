package com.example.showcase
import io.micronaut.runtime.Micronaut
class CustomerProvisioningApplication { companion object { @JvmStatic fun main(args: Array<String>) { Micronaut.run(CustomerProvisioningApplication::class.java, *args) } } }
