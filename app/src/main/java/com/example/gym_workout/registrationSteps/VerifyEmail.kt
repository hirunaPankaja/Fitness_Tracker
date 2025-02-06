package com.example.gym_workout.registrationSteps

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.gym_workout.R
import java.util.Properties
import javax.mail.*
import javax.mail.internet.*
import kotlin.concurrent.thread

class VerifyEmail : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.verify_email)

        val emailInput = findViewById<EditText>(R.id.etEmail)
        val verifyButton = findViewById<Button>(R.id.btnVerifyEmail)

        // Inside VerifyEmail class
        verifyButton.setOnClickListener {
            val email = emailInput.text.toString().trim()

            // Validate email format
            if (email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                // Simulate OTP generation
                val otp = (1000..9999).random()

                // Send OTP to email in background thread
                thread {
                    val sent = sendOtpEmail(email, otp)

                    runOnUiThread {
                        if (sent) {
                            Toast.makeText(this, "OTP sent to $email", Toast.LENGTH_SHORT).show()

                            // Navigate to OTP screen with OTP extra
                            val intent = Intent(this, OTP_verification::class.java)
                            intent.putExtra("otp", otp)
                            startActivity(intent)
                        } else {
                            Toast.makeText(this, "Failed to send OTP", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
                Toast.makeText(this, "Enter a valid email", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun sendOtpEmail(recipient: String, otp: Int): Boolean {
        val senderEmail = "hirunapankaja9@gmail.com" // Your email address
        val senderPassword = "vifm lykm fvwq vjya" // Your email password or app-specific password

        val props = Properties().apply {
            put("mail.smtp.host", "smtp.gmail.com")
            put("mail.smtp.socketFactory.port", "465")
            put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory")
            put("mail.smtp.auth", "true")
            put("mail.smtp.port", "465")
        }

        return try {
            val session = Session.getInstance(props, object : Authenticator() {
                override fun getPasswordAuthentication(): PasswordAuthentication {
                    return PasswordAuthentication(senderEmail, senderPassword)
                }
            })

            val message = MimeMessage(session).apply {
                setFrom(InternetAddress(senderEmail))
                setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient))
                subject = "OTP for Email Verification"
                setText("Your OTP for email verification is: $otp")
            }

            Transport.send(message)
            Log.d("OTP_SEND", "OTP sent successfully to $recipient")
            true
        } catch (e: MessagingException) {
            Log.e("OTP_SEND", "MessagingException: Failed to send OTP", e)
            false
        } catch (e: Exception) {
            Log.e("OTP_SEND", "Exception: Failed to send OTP", e)
            false
        }
    }
}
