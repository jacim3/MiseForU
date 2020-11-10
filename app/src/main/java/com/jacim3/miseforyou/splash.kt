package com.jacim3.miseforyou

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class splash: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            Thread.sleep(1500)
        } catch (e: InterruptedException) {
        }
        val intent = Intent(applicationContext, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onBackPressed() {

    }
}