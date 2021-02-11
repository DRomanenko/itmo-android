package com.github.dromanenko.animations

import android.os.Bundle
import android.view.animation.AlphaAnimation
import android.view.animation.LinearInterpolator
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tv.startAnimation(AlphaAnimation(0f, 1f).apply {
            duration = 1000L
            repeatCount = AlphaAnimation.INFINITE
            repeatMode = AlphaAnimation.REVERSE
            interpolator = LinearInterpolator()
        })
    }

    override fun onStop() {
        super.onStop()
        tv.animation.cancel()
    }
}