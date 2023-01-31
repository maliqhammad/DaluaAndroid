package com.dalua.app.ui.welcomescrn

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.dalua.app.R
import com.dalua.app.ui.registration.login.LoginActivity
import com.dalua.app.ui.registration.signup.SignupActivity

class WelcomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)
    }

    fun goToLogin(view: View) {
        startActivity(Intent(this, LoginActivity::class.java))
    }

    fun goToSignup(view: View) {
        startActivity(Intent(this, SignupActivity::class.java))
    }
}