package com.example.gym_workout.registrationSteps

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.gym_workout.R
import com.example.gym_workout.database.DatabaseHelper
import java.util.Properties
import javax.mail.*
import javax.mail.internet.*
import kotlin.random.Random
import kotlin.concurrent.thread

class VerifyEmail : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.verify_email)

        dbHelper = DatabaseHelper(this)

        val emailInput = findViewById<EditText>(R.id.etEmail)
        val verifyButton = findViewById<Button>(R.id.btnVerifyEmail)

        verifyButton.setOnClickListener {
            val email = emailInput.text.toString().trim()

            // Validate email format
            if (email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                // Save the email in the database
                saveEmailToDatabase(email)

                // Simulate OTP generation with secure random number
                val otp = Random.nextInt(1000, 9999)

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

    // Save email in the database
    private fun saveEmailToDatabase(email: String) {
        val contentValues = ContentValues().apply {
            put("email", email) // Save the email in the database
            put("stepCompleted", 11) // Track progress
        }

        val isUpdated = dbHelper.saveOrUpdateStepData(contentValues, "user1")
        if (isUpdated) {
            Log.d("VerifyEmail", "Email saved successfully: $email")
        } else {
            Log.e("VerifyEmail", "Failed to save email")
        }
    }

    // Send OTP to the email
    private fun sendOtpEmail(recipient: String, otp: Int): Boolean {
        val senderEmail = "hirunapankaja9@gmail.com" // Replace with your email address
        val senderPassword = "vifm lykm fvwq vjya" // Replace with your email password

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
