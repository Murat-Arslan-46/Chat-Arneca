package com.marslan.chatarneca

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity


class LoginActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)
        supportFragmentManager.findFragmentById(R.id.login_container)
        supportActionBar?.hide()
    }
}