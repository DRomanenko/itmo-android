package com.github.dromanenko.images

import android.app.IntentService
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import java.net.URL

const val KEY_RESPONSE = "response"

class ImageService :
    IntentService("ImageLoader") {
    companion object {
        var map: HashMap<String, Bitmap> = HashMap()
        private const val TOKEN_DOWNLOAD_HD = "download_HD"
    }

    override fun onHandleIntent(intent: Intent?) {
        val url = intent?.getStringExtra(KEY_URL) ?: return
        var image: Bitmap? = null
        if (!map.containsKey(url)) {
            try {
                Log.i(TOKEN_DOWNLOAD_HD, "Download from $url")
                image = BitmapFactory.decodeStream(URL(url).openStream())
            } catch (e: Exception) {
                Log.e(TOKEN_DOWNLOAD_HD, "Failed download from $url")
            }
            map[url] = image!!
        }

        sendBroadcast(Intent(KEY_RESPONSE).apply {
            putExtra(KEY_URL, url)
        })
    }
}