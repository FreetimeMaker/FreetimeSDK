package com.freetime.sdk

import android.app.Activity
import android.content.Intent
import android.net.Uri

actual fun openPaymentUri(uri: String, context: Any?) {
    val ctx = context as? android.content.Context ?: throw IllegalArgumentException("Android requires a Context")
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
    if (ctx !is Activity) {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    ctx.startActivity(intent)
}
