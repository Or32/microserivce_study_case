package com.example.showcase
import io.micronaut.runtime.Micronaut
class PaymentChargingApplication { companion object { @JvmStatic fun main(args: Array<String>) { Micronaut.run(PaymentChargingApplication::class.java, *args) } } }
