package com.mzw.rippleview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (!rippleView.isRippleAnimationRunning())
            rippleView.startRippleAnimation()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (rippleView.isRippleAnimationRunning()) {
            rippleView.stopRippleAnimation()
        }
    }
}
