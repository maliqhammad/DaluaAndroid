package com.dalua.app.ui.splashscreen

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.dalua.app.R
import com.dalua.app.ui.home.homeactivity.HomeActivity
import com.dalua.app.ui.welcomescrn.WelcomeActivity
import com.dalua.app.utils.ProjectUtil


class SplashScreenActivity : AppCompatActivity() {

    lateinit var imageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        imageView = findViewById(R.id.image_view)

        Handler(Looper.getMainLooper()).postDelayed({

            if (ProjectUtil.getUserObjects(this) != null)
                startActivity(Intent(this, HomeActivity::class.java))
            else
                startActivity(Intent(this, WelcomeActivity::class.java))

            finish()
        }, 2000)
    }
}
