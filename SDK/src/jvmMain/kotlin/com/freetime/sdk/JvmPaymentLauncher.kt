package com.freetime.sdk

actual fun openPaymentUri(uri: String, context: Any?) {
    println("**************************************************")
    println("PAYMENT REQUIRED")
    println("Please scan the following URI with your wallet:")
    println(uri)
    println("**************************************************")
}
