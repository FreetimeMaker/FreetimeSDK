package com.freetime.sdk

import android.os.Handler
import android.os.Looper
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Executors

object PromotionManager {
    private const val DEFAULT_PROMO_URL = "https://raw.githubusercontent.com/FreetimeMaker/FreetimeSDK/master/promotions.json"
    private val executor = Executors.newSingleThreadExecutor()
    private val handler = Handler(Looper.getMainLooper())

    fun fetchPromotion(config: DeveloperConfig, callback: (Promotion?) -> Unit) {
        if (!config.enablePromotions) {
            callback(null)
            return
        }

        val urlToFetch = config.customPromotionUrl ?: DEFAULT_PROMO_URL

        executor.execute {
            try {
                val url = URL(urlToFetch)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connectTimeout = 5000
                connection.readTimeout = 5000

                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                val response = StringBuilder()
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    response.append(line)
                }
                reader.close()

                val json = JSONObject(response.toString())
                val promosArray = json.getJSONArray("promotions")
                if (promosArray.length() > 0) {
                    val index = (0 until promosArray.length()).random()
                    val promoJson = promosArray.getJSONObject(index)
                    val promo = Promotion(
                        id = promoJson.getString("id"),
                        title = promoJson.getString("title"),
                        description = promoJson.getString("description"),
                        iconUrl = promoJson.getString("iconUrl"),
                        targetUrl = promoJson.getString("targetUrl")
                    )
                    handler.post { callback(promo) }
                } else {
                    handler.post { callback(null) }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                handler.post { callback(null) }
            }
        }
    }
}
