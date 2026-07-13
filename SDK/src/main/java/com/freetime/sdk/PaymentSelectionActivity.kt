package com.freetime.sdk

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class PaymentSelectionActivity : AppCompatActivity() {

    companion object {
        private var sdkInstance: FreetimePay? = null
        private var currentRequest: PaymentRequest? = null
        private var resultCallback: ((PaymentResult) -> Unit)? = null

        fun launch(
            context: Context,
            sdk: FreetimePay,
            request: PaymentRequest,
            onResult: (PaymentResult) -> Unit
        ) {
            sdkInstance = sdk
            currentRequest = request
            resultCallback = onResult
            
            val intent = Intent(context, PaymentSelectionActivity::class.java)
            if (context !is Activity) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.freetime_payment_selection)

        val request = currentRequest ?: run {
            finish()
            return
        }
        val sdk = sdkInstance ?: run {
            finish()
            return
        }

        findViewById<TextView>(R.id.tvAmount).text = "Total: ${request.amount} ${request.currency}"

        val container = findViewById<LinearLayout>(R.id.providerContainer)
        sdk.getAvailableProviders().forEach { provider ->
            val button = Button(this).apply {
                val buttonPadding = (16 * resources.displayMetrics.density).toInt()
                setPadding(buttonPadding, buttonPadding, buttonPadding, buttonPadding)
                text = "Pay with ${provider.name}"
                isAllCaps = false
                setOnClickListener {
                    sdk.processPayment(this@PaymentSelectionActivity, provider.name, request) { result ->
                        resultCallback?.invoke(result)
                        finish()
                    }
                }
            }
            container.addView(button)
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        if (isFinishing) {
            sdkInstance = null
            currentRequest = null
            resultCallback = null
        }
    }
}
