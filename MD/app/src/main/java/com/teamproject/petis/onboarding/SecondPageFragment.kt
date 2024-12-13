package com.teamproject.petis.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.teamproject.petis.R

class SecondPageFragment : Fragment(), OnboardingPage {
    private lateinit var imageView: ImageView
    private lateinit var titleText: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_second_page, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imageView = view.findViewById(R.id.splashLogo)
        titleText = view.findViewById(R.id.textView)

        // Panggil animasi saat fragment pertama kali dibuat
        animatePageEntry()
    }

    override fun onPageSelected() {
        // Pastikan imageView sudah diinisialisasi sebelum memanggil animatePageEntry
        if (::imageView.isInitialized) {
            animatePageEntry()
        }
    }

    private fun animatePageEntry() {
        imageView.apply {
            alpha = 0f
            scaleX = 0.5f
            scaleY = 0.5f
            animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(800)
                .setInterpolator(OvershootInterpolator())
                .start()
        }

        titleText.apply {
            translationY = 100f
            alpha = 0f
            animate()
                .translationY(0f)
                .alpha(1f)
                .setStartDelay(300)
                .setDuration(600)
                .setInterpolator(DecelerateInterpolator())
                .start()
        }
    }
}