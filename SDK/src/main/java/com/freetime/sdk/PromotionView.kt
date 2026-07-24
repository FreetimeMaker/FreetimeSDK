package com.freetime.sdk

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import java.net.URL
import java.util.concurrent.Executors

class PromotionView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val ivIcon: ImageView
    private val tvTitle: TextView
    private val tvDescription: TextView
    private val executor = Executors.newSingleThreadExecutor()

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.freetime_promotion_item, this, true)
        ivIcon = view.findViewById(R.id.ivPromoIcon)
        tvTitle = view.findViewById(R.id.tvPromoTitle)
        tvDescription = view.findViewById(R.id.tvPromoDescription)
        visibility = View.GONE
    }

    fun loadPromotion(config: DeveloperConfig) {
        PromotionManager.fetchPromotion(config) { promo ->
            if (promo != null) {
                displayPromotion(promo)
            } else {
                visibility = View.GONE
            }
        }
    }

    private fun displayPromotion(promo: Promotion) {
        tvTitle.text = promo.title
        tvDescription.text = promo.description
        
        // Simple image loader fallback (no Glide/Coil to keep SDK small)
        loadIcon(promo.iconUrl)

        setOnClickListener {
            try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(promo.targetUrl))
                context.startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        
        visibility = View.VISIBLE
    }

    private fun loadIcon(url: String) {
        executor.execute {
            try {
                val stream = URL(url).openStream()
                val bitmap = android.graphics.BitmapFactory.decodeStream(stream)
                post { ivIcon.setImageBitmap(bitmap) }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
