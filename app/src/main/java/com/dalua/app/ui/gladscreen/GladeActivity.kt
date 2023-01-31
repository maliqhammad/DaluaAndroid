package com.dalua.app.ui.gladscreen

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.dalua.app.R
import com.dalua.app.ui.home.homeactivity.HomeActivity
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK

import android.content.Intent.FLAG_ACTIVITY_NEW_TASK




class GladeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_glade)
    }

    fun goToHome(view: View) {
        startActivity(Intent(this,HomeActivity::class.java).apply {
            flags= FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK
        })
    }
}