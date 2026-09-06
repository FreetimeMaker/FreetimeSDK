package com.freetime.sdk

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL

class PromotionView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var ivIcon: ImageView? = null
    private var tvTitle: TextView? = null
    private var tvDescription: TextView? = null
    private val scope = CoroutineScope(Dispatchers.Main)

    init {
        // Commented out to unblock KMP build until resource issue is resolved
        /*
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
        */
        visibility = View.GONE
    }

    fun loadPromotion(config: DeveloperConfig) {
        val manager = PromotionManager(config)
        scope.launch {
            val promos = manager.fetchPromotions()
            val promo = promos.firstOrNull()
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
        scope.launch {
            try {
                val bitmap = withContext(Dispatchers.IO) {
                    val stream = URL(url).openStream()
                    android.graphics.BitmapFactory.decodeStream(stream)
                }
                if (bitmap != null) {
                    ivIcon?.setImageBitmap(bitmap)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
