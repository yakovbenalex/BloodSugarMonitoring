package ru.opalevapps.EveryGlic.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.opalevapps.EveryGlic.ui.MainActivity

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // start main Activity and finish splash
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}