package com.github.dromanenko.images

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_image.*

const val KEY_URL = "url"

class ImageActivity : AppCompatActivity() {
    private var broadcastReceiver: BroadcastReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)

        startService(Intent(this, ImageService::class.java).apply {
            putExtra(KEY_URL, intent.getStringExtra(KEY_URL))
        })

        pb.visibility = View.VISIBLE

        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val url = intent?.getStringExtra(KEY_URL)
                if (url != null) {
                    iv.setImageBitmap(ImageService.map[url])
                    iv.visibility = View.VISIBLE
                    pb.visibility = View.GONE
                }
            }
        }

        registerReceiver(
            broadcastReceiver,
            IntentFilter(KEY_RESPONSE)
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(broadcastReceiver)
    }
}
