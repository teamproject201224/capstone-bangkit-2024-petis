package com.teamproject.petis.onboarding

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.teamproject.petis.R
import com.teamproject.petis.adapter.OnboardingAdapter
import com.teamproject.petis.ui.auth.LoginActivity
import kotlin.math.abs

class OnboardingActivity : AppCompatActivity() {
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var onboardingAdapter: OnboardingAdapter

    private val pageColors = listOf(
        R.color.onboarding_page1_bg,
        R.color.onboarding_page2_bg,
        R.color.onboarding_page3_bg
    )

    private val tabColors = listOf(
        R.color.tab_active_color,
        R.color.tab_active_color,
        R.color.tab_active_color
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN

        setContentView(R.layout.activity_onboarding)

        viewPager = findViewById(R.id.viewPager)
        tabLayout = findViewById(R.id.tabLayout)

        onboardingAdapter = OnboardingAdapter(this) { position ->
            if (position == 2) {
                navigateToLogin()
            }
        }
        viewPager.adapter = onboardingAdapter

        setupViewPagerTransformation()
        setupTabLayout()
        saveOnboardingState()
    }

    private fun setupViewPagerTransformation() {
        viewPager.apply {
            isUserInputEnabled = true

            setPageTransformer { page, position ->
                page.apply {
                    val normalizedPosition = abs(position)

                    translationX = -position * width * 0.7f
                    scaleX = 1 - (0.2f * normalizedPosition)
                    scaleY = 1 - (0.2f * normalizedPosition)
                    alpha = 1 - (0.5f * normalizedPosition)
                    rotationY = position * 20
                }
            }

            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    window.decorView.setBackgroundColor(
                        resources.getColor(pageColors[position], theme)
                    )
                    updateTabColors(position)

                    // Memanggil metode untuk memperbarui tampilan di fragment
                    val fragment = onboardingAdapter.createFragment(position)
                    if (fragment is OnboardingPage) {
                        fragment.onPageSelected()
                    }
                }
            })
        }
    }

    private fun setupTabLayout() {
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            val customTab = layoutInflater.inflate(R.layout.custom_tab_indicator, null)
            tab.customView = customTab
        }.attach()

        tabLayout.apply {
            setSelectedTabIndicatorHeight(0)
            tabGravity = TabLayout.GRAVITY_CENTER
            tabMode = TabLayout.MODE_FIXED
        }

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.customView?.findViewById<View>(R.id.tabIndicator)?.apply {
                    animate()
                        .scaleX(1.5f)
                        .scaleY(1.5f)
                        .setDuration(200)
                        .start()
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                tab?.customView?.findViewById<View>(R.id.tabIndicator)?.apply {
                    animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(200)
                        .start()
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun updateTabColors(currentPosition: Int) {
        tabLayout.setSelectedTabIndicatorColor(
            resources.getColor(tabColors[currentPosition], theme)
        )
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun saveOnboardingState() {
        val sharedPreferences = getSharedPreferences("onboarding", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("onboarding_completed", true)
        editor.apply()
    }
}