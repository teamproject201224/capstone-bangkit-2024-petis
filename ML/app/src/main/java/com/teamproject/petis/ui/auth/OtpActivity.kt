package com.teamproject.petis.ui.auth

import android.animation.ObjectAnimator
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.teamproject.petis.R
import com.teamproject.petis.databinding.ActivityOtpBinding
import com.teamproject.petis.utils.SharedPreferencesManager
import kotlinx.coroutines.launch
import kotlin.random.Random

class OtpActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOtpBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var emailService: EmailService
    private var generatedOtp: Int = 0
    private var email: String? = null
    private var name: String? = null
    private var password: String? = null
    private lateinit var sharedPreferencesManager: SharedPreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferencesManager = SharedPreferencesManager(this)
        binding = ActivityOtpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        emailService = EmailService(this)

        name = intent.getStringExtra("name")
        email = intent.getStringExtra("email")
        password = intent.getStringExtra("password")
        generatedOtp = intent.getIntExtra("otp", 0)

        binding.showEmail.text = "OTP sent to: $email"

        val name = intent.getStringExtra("name")
        name?.let {
            sharedPreferencesManager.saveUserName(it)
        }

        setupOtpInput()

        addFadeInAnimation(binding.showEmail)
        addFadeInAnimation(binding.button)
        addFadeInAnimation(binding.tvResend)
        addFadeInAnimation(binding.otp1)
        addFadeInAnimation(binding.otp2)
        addFadeInAnimation(binding.otp3)
        addFadeInAnimation(binding.otp4)
        addFadeInAnimation(binding.otp5)
        addFadeInAnimation(binding.otp6)

        binding.button.setOnClickListener {
            addBounceAnimation(binding.button)
            val enteredOtp = getEnteredOtp()
            if (enteredOtp == generatedOtp) {
                registerUser()
            } else {
                Toast.makeText(this, "Invalid OTP", Toast.LENGTH_SHORT).show()
            }
        }

        binding.tvResend.setOnClickListener {
            addTranslateAnimation(binding.tvResend)
            resendOtp()
        }
    }

    private fun registerUser() {
        if (name.isNullOrBlank() || email.isNullOrBlank() || password.isNullOrBlank()) {
            Toast.makeText(this, "Please fill all registration details", Toast.LENGTH_SHORT).show()
            return
        }

        auth.createUserWithEmailAndPassword(email!!, password!!)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser

                    name?.let {
                        sharedPreferencesManager.saveUserName(it)
                    }

                    if (user != null) {
                        showRegistrationSuccessDialog()
                    } else {
                        Toast.makeText(this, "Registration failed", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    val errorMessage = task.exception?.message ?: "Registration failed"
                    Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun showRegistrationSuccessDialog() {
        runOnUiThread {
            val dialogView = layoutInflater.inflate(R.layout.dialog_registration_success, null)
            val dialog = AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(false)
                .create()

            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.show()

            val btnLogin = dialogView.findViewById<Button>(R.id.btnLogin)

            btnLogin.setOnClickListener {
                dialog.dismiss()
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
                finish()
            }
        }
    }

    private fun setupOtpInput() {
        binding.otp1.doOnTextChanged { text, _, _, _ -> if (text?.length == 1) binding.otp2.requestFocus() }
        binding.otp2.doOnTextChanged { text, _, _, _ -> if (text?.length == 1) binding.otp3.requestFocus() }
        binding.otp3.doOnTextChanged { text, _, _, _ -> if (text?.length == 1) binding.otp4.requestFocus() }
        binding.otp4.doOnTextChanged { text, _, _, _ -> if (text?.length == 1) binding.otp5.requestFocus() }
        binding.otp5.doOnTextChanged { text, _, _, _ -> if (text?.length == 1) binding.otp6.requestFocus() }
    }

    private fun getEnteredOtp(): Int {
        return try {
            (binding.otp1.text.toString() +
                    binding.otp2.text.toString() +
                    binding.otp3.text.toString() +
                    binding.otp4.text.toString() +
                    binding.otp5.text.toString() +
                    binding.otp6.text.toString()).toInt()
        } catch (e: NumberFormatException) {
            0
        }
    }

    private fun resendOtp() {
        email?.let {
            generatedOtp = Random.nextInt(100000, 999999)

            lifecycleScope.launch {
                val emailSent = emailService.sendOtpEmail(it, generatedOtp)

                if (emailSent) {
                    Toast.makeText(this@OtpActivity, "OTP Resent", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@OtpActivity, "Failed to resend OTP", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    // Animasi Fade-In
    private fun addFadeInAnimation(view: View, duration: Long = 1000) {
        ObjectAnimator.ofFloat(view, "alpha", 0f, 1f).apply {
            this.duration = duration
            start()
        }
    }

    // Animasi Bounce
    private fun addBounceAnimation(view: View) {
        ObjectAnimator.ofFloat(view, "scaleX", 1f, 1.1f, 1f).apply {
            duration = 3000
            start()
        }
        ObjectAnimator.ofFloat(view, "scaleY", 1f, 1.1f, 1f).apply {
            duration = 3000
            start()
        }
    }

    // Animasi Translate
    private fun addTranslateAnimation(view: View) {
        ObjectAnimator.ofFloat(view, "translationY", 0f, -20f, 0f).apply {
            duration = 4000
            start()
        }
    }
}
