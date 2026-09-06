package com.freetime.sdk

actual fun openPaymentUri(uri: String, context: Any?) {
    println("Payment URI: $uri")
    println("Please open this URI in a crypto wallet.")
}
