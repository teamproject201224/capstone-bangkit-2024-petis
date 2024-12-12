package com.teamproject.petis.ui.auth

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Patterns
import android.view.View
import android.view.animation.AnimationUtils
import android.view.animation.BounceInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.google.firebase.auth.FirebaseAuth
import com.teamproject.petis.R
import com.teamproject.petis.databinding.ActivityLoginBinding
import com.teamproject.petis.ui.main.MainActivity
import com.teamproject.petis.utils.SharedPreferencesManager

class LoginActivity : AppCompatActivity() {
    private lateinit var sharedPreferencesManager: SharedPreferencesManager
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferencesManager = SharedPreferencesManager(this)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()

        if (auth.currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        setupViews()
        playAnimation()
    }

    private fun setupViews() {
        binding.signupTv.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        binding.btnLogin.setOnClickListener {
            binding.btnLogin.animate()
                .scaleX(0.9f)
                .scaleY(0.9f)
                .setDuration(100)
                .withEndAction {
                    binding.btnLogin.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(100)
                        .start()
                }
                .start()

            val email = binding.emailET.text.toString().trim()
            val password = binding.passET.text.toString().trim()

            when {
                email.isEmpty() -> {
                    binding.emailLayout.error = "Enter Email"
                    binding.emailET.requestFocus()
                    return@setOnClickListener
                }
                !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                    binding.emailLayout.error = "Invalid Email Format"
                    binding.emailET.requestFocus()
                    return@setOnClickListener
                }
                password.isEmpty() -> {
                    binding.passLayout.error = "Enter Password"
                    binding.passET.requestFocus()
                    return@setOnClickListener
                }
                password.length < 6 -> {
                    binding.passLayout.error = "Password must be at least 6 characters"
                    binding.passET.requestFocus()
                    return@setOnClickListener
                }
                else -> {
                    loginUser(email, password)
                }
            }
        }
    }

    private fun playAnimation() {
        val views = listOf(
            binding.logoImage,
            binding.loginTitle,
            binding.emailLayout,
            binding.passLayout,
            binding.btnLogin,
            binding.signupTv
        )

        views.forEachIndexed { index, view ->
            val alphaAnimator = ObjectAnimator.ofFloat(view, View.ALPHA, 0f, 1f).apply {
                duration = 500
                startDelay = index * 100L
                interpolator = DecelerateInterpolator()
            }

            val translateAnimator = ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, 50f, 0f).apply {
                duration = 500
                startDelay = index * 100L
                interpolator = DecelerateInterpolator()
            }

            AnimatorSet().apply {
                playTogether(alphaAnimator, translateAnimator)
                start()
            }
        }
    }

    private fun showLoadingAnimation() {
        val loadingDialog = Dialog(this)
        loadingDialog.setContentView(R.layout.dialog_loading)
        loadingDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        loadingDialog.setCancelable(false)

        val lottiAnimationView = loadingDialog.findViewById<LottieAnimationView>(R.id.lottieAnimationView)
        lottiAnimationView.setAnimation(R.raw.loading_animation) // Pastikan Anda memiliki file JSON animasi
        lottiAnimationView.loop(true)
        lottiAnimationView.playAnimation()

        loadingDialog.show()

        Handler(Looper.getMainLooper()).postDelayed({
            loadingDialog.dismiss()
            navigateToMainActivity()
        }, 2000)
    }

    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    showLoadingAnimation()
                } else {
                    Toast.makeText(
                        this,
                        "Login failed: ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(intent)
        finish()
    }

    private fun showForgotPasswordDialog() {
        val email = binding.emailET.text.toString().trim()

        if (email.isEmpty()) {
            binding.emailLayout.error = "Enter Email to Reset Password"
            binding.emailET.requestFocus()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailLayout.error = "Invalid Email Format"
            binding.emailET.requestFocus()
            return
        }

        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        this,
                        "Password reset email sent",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        this,
                        "Failed to send reset email: ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
}