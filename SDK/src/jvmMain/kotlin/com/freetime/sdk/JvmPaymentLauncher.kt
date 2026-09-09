package com.freetime.sdk

import java.awt.Desktop
import java.net.URI

actual fun openPaymentUri(uri: String, context: Any?) {
    try {
        if (Desktop.isDesktopSupported()) {
            Desktop.getDesktop().browse(URI(uri))
        } else {
            println("Desktop not supported. Please open URI manually: $uri")
        }
    } catch (e: Exception) {
        e.printStackTrace()
        println("Failed to open payment URI: $uri")
    }
}
