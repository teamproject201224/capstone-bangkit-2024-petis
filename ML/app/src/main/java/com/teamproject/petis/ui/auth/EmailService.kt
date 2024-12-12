package com.teamproject.petis.ui.auth

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Properties
import javax.mail.Authenticator
import javax.mail.Message
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

class EmailService(private val context: Context) {

    suspend fun sendOtpEmail(email: String, otp: Int): Boolean = withContext(Dispatchers.IO) {
        try {
            val properties = Properties().apply {
                put("mail.smtp.host", "smtp.gmail.com")
                put("mail.smtp.port", "587")
                put("mail.smtp.auth", "true")
                put("mail.smtp.starttls.enable", "true")
            }

            val session = Session.getInstance(properties, object : Authenticator() {
                override fun getPasswordAuthentication(): PasswordAuthentication {
                    return PasswordAuthentication(
                        "capstone201224@gmail.com",
                        "kconnsirvgxkxioi"
                    )
                }
            })

            val message = MimeMessage(session).apply {
                setFrom(InternetAddress("capstone201224@gmail.com"))
                addRecipient(Message.RecipientType.TO, InternetAddress(email))
                subject = "Your OTP Code"
                setText("Your OTP verification code is: $otp")
            }

            Transport.send(message)
            Log.d("EmailService", "OTP Email sent successfully to $email")
            true
        } catch (e: Exception) {
            Log.e("EmailService", "Failed to send OTP email", e)
            false
        }
    }
}