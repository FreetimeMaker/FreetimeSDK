package com.freetime.sdk

import android.app.Activity
import android.content.Intent
import android.net.Uri

actual fun openPaymentUri(uri: String, context: Any?) {
    val activity = context as? Activity ?: throw IllegalArgumentException("Android requires an Activity context")
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
    activity.startActivity(intent)
}
