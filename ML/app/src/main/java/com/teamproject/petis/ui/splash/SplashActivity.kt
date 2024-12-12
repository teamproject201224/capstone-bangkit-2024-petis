package com.teamproject.petis.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.teamproject.petis.R
import com.teamproject.petis.onboarding.OnboardingActivity
import com.teamproject.petis.ui.auth.LoginActivity
import com.teamproject.petis.ui.main.MainActivity

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Delay to show the splash screen for 3 seconds
        Handler(Looper.getMainLooper()).postDelayed({
            val sharedPref = getSharedPreferences("onboarding", MODE_PRIVATE)
            val isFirstTime = sharedPref.getBoolean("isFirstTime", true)
            val isOnboardingCompleted = sharedPref.getBoolean("onboarding_completed", false)

            if (isFirstTime) {
                // If it's the first time the app is launched, show OnboardingActivity
                startActivity(Intent(this, OnboardingActivity::class.java))

                // Mark that the onboarding has been completed for subsequent launches
                val editor = sharedPref.edit()
                editor.putBoolean("isFirstTime", false)  // Update flag to false
                editor.apply()

            } else {
                // If onboarding is completed, check login status
                if (isOnboardingCompleted) {
                    val user = FirebaseAuth.getInstance().currentUser
                    if (user != null) {
                        // If user is already logged in, go to MainActivity
                        startActivity(Intent(this, MainActivity::class.java))
                    } else {
                        // Otherwise, go to LoginActivity
                        startActivity(Intent(this, LoginActivity::class.java))
                    }
                } else {
                    // If onboarding is not completed, go to OnboardingActivity
                    startActivity(Intent(this, OnboardingActivity::class.java))
                }
            }

            finish() // Finish SplashActivity so it's removed from the back stack
        }, 3000) // 3000 milliseconds = 3 seconds
    }
}

