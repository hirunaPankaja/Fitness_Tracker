package com.example.gym_workout.registrationSteps

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.gym_workout.SetPassword
import com.example.gym_workout.utils.NavigationHelper
import com.example.gym_workout.R

class OTP_verification : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.otp_verification)

        val otp1 = findViewById<EditText>(R.id.otp1)
        val otp2 = findViewById<EditText>(R.id.otp2)
        val otp3 = findViewById<EditText>(R.id.otp3)
        val otp4 = findViewById<EditText>(R.id.otp4)
        val verifyButton = findViewById<Button>(R.id.btnVerifyOtp)

        val sentOtp = intent.getIntExtra("otp", -1)

        // Move focus automatically when user types in OTP fields
        otp1.addTextChangedListener(createTextWatcher(otp2))
        otp2.addTextChangedListener(createTextWatcher(otp3))
        otp3.addTextChangedListener(createTextWatcher(otp4))
        otp4.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s?.length == 1) verifyButton.performClick() // Automatically verify OTP
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // When verify button is clicked
        verifyButton.setOnClickListener {
            val enteredOtp = "${otp1.text}${otp2.text}${otp3.text}${otp4.text}"

            if (sentOtp != -1 && enteredOtp == sentOtp.toString()) {
                Toast.makeText(this, "OTP Verified Successfully", Toast.LENGTH_SHORT).show()
                NavigationHelper.navigateToNextStep(this, SetPassword::class.java)
            } else {
                Toast.makeText(this, "Invalid OTP", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Helper function to create TextWatcher and move focus
    private fun createTextWatcher(nextEditText: EditText): TextWatcher {
        return object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s?.length == 1) nextEditText.requestFocus()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }
    }
}
