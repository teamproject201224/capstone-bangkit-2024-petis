package com.teamproject.petis.ui.auth

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.teamproject.petis.databinding.ActivityRegisterBinding
import com.teamproject.petis.utils.SharedPreferencesManager
import kotlinx.coroutines.launch
import kotlin.random.Random

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var emailService: EmailService
    private var generatedOtp: Int = 0
    private lateinit var sharedPreferencesManager: SharedPreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferencesManager = SharedPreferencesManager(this)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        emailService = EmailService(this)

        playAnimation()

        binding.btnNext.setOnClickListener {
            val name = binding.nameET.text.toString().trim()
            val email = binding.emailET.text.toString().trim()
            val password = binding.passET.text.toString().trim()
            val confirmPassword = binding.confirmPassET.text.toString().trim()

            when {
                name.isEmpty() -> {
                    binding.nameLayout.error = "Enter Name"
                    binding.nameET.requestFocus()
                    return@setOnClickListener
                }

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

                confirmPassword.isEmpty() -> {
                    binding.confirmPassLayout.error = "Confirm Password"
                    binding.confirmPassET.requestFocus()
                    return@setOnClickListener
                }

                password != confirmPassword -> {
                    binding.confirmPassLayout.error = "Passwords do not match"
                    binding.confirmPassET.requestFocus()
                    return@setOnClickListener
                }

                else -> {
                    generatedOtp = Random.nextInt(100000, 999999)

                    lifecycleScope.launch {
                        val emailSent = emailService.sendOtpEmail(email, generatedOtp)

                        if (emailSent) {
                            val intent =
                                Intent(this@RegisterActivity, OtpActivity::class.java).apply {
                                    putExtra("name", name)
                                    putExtra("email", email)
                                    putExtra("password", password)
                                    putExtra("otp", generatedOtp)
                                }
                            startActivity(intent)
                        } else {
                            Toast.makeText(
                                this@RegisterActivity,
                                "Failed to send OTP. Please try again.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
    }

    private fun playAnimation() {
        val views = listOf(
            binding.nameLayout,
            binding.emailLayout,
            binding.passLayout,
            binding.confirmPassLayout,
            binding.btnNext
        )

        views.forEachIndexed { index, view ->
            val alphaAnimator = ObjectAnimator.ofFloat(view, View.ALPHA, 0f, 1f).apply {
                duration = 500
                startDelay = index * 100L
                interpolator = DecelerateInterpolator()
            }

            val scaleAnimator = ObjectAnimator.ofFloat(view, View.SCALE_X, 0.5f, 1f).apply {
                duration = 500
                startDelay = index * 100L
                interpolator = DecelerateInterpolator()
            }

            val scaleYAnimator = ObjectAnimator.ofFloat(view, View.SCALE_Y, 0.5f, 1f).apply {
                duration = 500
                startDelay = index * 100L
                interpolator = DecelerateInterpolator()
            }

            AnimatorSet().apply {
                playTogether(alphaAnimator, scaleAnimator, scaleYAnimator)
                start()
            }
        }
    }
}
