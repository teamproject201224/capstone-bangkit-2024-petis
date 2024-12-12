package com.teamproject.petis.ui.custom

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.ImageView
import android.widget.TextView
import com.teamproject.petis.R

class CustomLoadingDialog(context: Context) : Dialog(context) {
    private lateinit var loadingImage: ImageView
    private lateinit var loadingText: TextView

    init {
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setContentView(R.layout.dialog_custom_loading)
        setCancelable(false)

        loadingImage = findViewById(R.id.loadingImage)
        loadingText = findViewById(R.id.loadingText)

        setupAnimation()
    }

    private fun setupAnimation() {
        val rotateAnimation = RotateAnimation(
            0f, 360f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        ).apply {
            duration = 1500
            repeatCount = Animation.INFINITE
            repeatMode = Animation.RESTART
        }

        loadingImage.startAnimation(rotateAnimation)
    }

    fun updateText(message: String) {
        loadingText.text = message
    }
}