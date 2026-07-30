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

    private var ivIcon: ImageView? = null
    private var tvTitle: TextView? = null
    private var tvDescription: TextView? = null
    private val executor = Executors.newSingleThreadExecutor()

    init {
        try {
            val view = LayoutInflater.from(context).inflate(R.layout.freetime_promotion_item, this, true)
            ivIcon = view.findViewById(R.id.ivPromoIcon)
            tvTitle = view.findViewById(R.id.tvPromoTitle)
            tvDescription = view.findViewById(R.id.tvPromoDescription)
            visibility = View.GONE
        } catch (e: Exception) {
            e.printStackTrace()
            visibility = View.GONE
        }
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
        try {
            val title = tvTitle ?: return
            val desc = tvDescription ?: return
            
            title.text = promo.title
            desc.text = promo.description
            
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
        } catch (e: Exception) {
            e.printStackTrace()
            visibility = View.GONE
        }
    }

    private fun loadIcon(url: String) {
        executor.execute {
            try {
                val stream = URL(url).openStream()
                val bitmap = android.graphics.BitmapFactory.decodeStream(stream)
                if (bitmap != null) {
                    post { 
                        try {
                            ivIcon?.setImageBitmap(bitmap)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
