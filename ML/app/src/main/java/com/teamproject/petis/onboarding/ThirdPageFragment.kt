package com.teamproject.petis.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import androidx.fragment.app.Fragment
import com.teamproject.petis.R
import com.teamproject.petis.databinding.FragmentThirdPageBinding

class ThirdPageFragment(
    private val onGetStartedClick: (() -> Unit)? = null
) : Fragment(), OnboardingPage {
    private var _binding: FragmentThirdPageBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentThirdPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Pastikan binding sudah diinisialisasi
        binding.btnGetStarted.setOnClickListener {
            onGetStartedClick?.invoke()
        }

        // Panggil animasi saat fragment pertama kali dibuat
        animatePageEntry()
    }

    override fun onPageSelected() {
        // Pastikan binding sudah diinisialisasi sebelum memanggil animatePageEntry
        if (_binding != null) {
            animatePageEntry()
        }
    }

    private fun animatePageEntry() {
        binding.splashLogo.apply {
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

        binding.btnGetStarted.apply {
            translationY = 100f
            alpha = 0f
            animate()
                .translationY(0f)
                .alpha(1f)
                .setStartDelay(600)
                .setDuration(600)
                .setInterpolator(DecelerateInterpolator())
                .start()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Set binding ke null untuk menghindari memory leak
    }
}