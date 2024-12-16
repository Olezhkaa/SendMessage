package com.example.sendmessage

import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.telephony.SmsManager
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatDelegate

class MainActivity : AppCompatActivity() {

    private lateinit var sendMessageButton: Button
    private lateinit var telephoneNumberEditText: EditText

    private var telephoneNumber: String = ""

    private val SMS_PERMISSION_CODE = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        telephoneNumberEditText = findViewById(R.id.telephoneNumberEditText)
        sendMessageButton = findViewById(R.id.sendMessageButton)
        sendMessageButton.setOnClickListener(View.OnClickListener {
            telephoneNumber = telephoneNumberEditText.text.toString().trim()
            if (telephoneNumber != "") {
                checkAndRequestSmsPermission()
            }
            else Toast.makeText(this, "Заполните поле: Номер телефона", Toast.LENGTH_LONG).show()
        })
    }

    private fun checkAndRequestSmsPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.SEND_SMS)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.SEND_SMS), SMS_PERMISSION_CODE)
        } else {
            sendSmsWithCode(telephoneNumber)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == SMS_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                sendSmsWithCode(telephoneNumber)
            } else {
                Toast.makeText(this, "Необходимо разрешение для отправки SMS", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun sendSmsWithCode(phoneNumber: String) {
        val code = (1000..9999).random()  // Генерация 4-значного кода
        val message = "Ваш код подтверждения: $code"

        try {
            val smsManager = SmsManager.getDefault()
            smsManager.sendTextMessage(phoneNumber, null, message, null, null)
            Toast.makeText(this, "SMS отправлено на номер: $phoneNumber", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Ошибка отправки SMS: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}