package com.freetime.sdk

import platform.Foundation.NSURL
import platform.UIKit.UIApplication

actual fun openPaymentUri(uri: String, context: Any?) {
    val url = NSURL(string = uri)
    if (url != null) {
        UIApplication.sharedApplication.openURL(url)
    }
}
