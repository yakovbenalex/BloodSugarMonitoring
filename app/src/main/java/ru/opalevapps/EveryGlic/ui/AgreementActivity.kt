package ru.opalevapps.EveryGlic.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.Toast
import ru.opalevapps.EveryGlic.R

class AgreementActivity : AppCompatActivity(), View.OnClickListener {
    // views declare
    private lateinit var btnConfirm: Button

    // prefs vars
    private var firstRun = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agreement)
        initViews()

        // get firstRun value
        val sharedPref = getSharedPreferences(PreferencesActivity.KEY_PREFS, MODE_PRIVATE)
        firstRun = sharedPref.getBoolean(PreferencesActivity.KEY_PREFS_FIRST_RUN_AGREEMENT, true)
        if (!firstRun) {
            btnConfirm.setText(R.string.ok)
        } else {
            supportActionBar!!.setDisplayHomeAsUpEnabled(false)
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btnConfirm -> {
                // set firstRun flag to false
                val sharedPref = getSharedPreferences(PreferencesActivity.KEY_PREFS, MODE_PRIVATE)
                val prefEditor = sharedPref.edit()
                prefEditor.putBoolean(PreferencesActivity.KEY_PREFS_FIRST_RUN_AGREEMENT, false)
                prefEditor.apply()
                finish()
            }
            else -> {}
        }
    }

    override fun onBackPressed() {
        if (firstRun) {
            if (back_pressed + 2000 > System.currentTimeMillis()) {
                moveTaskToBack(true)
                finishAffinity()
            } else {
                Toast.makeText(
                    baseContext,
                    R.string.click_the_back_button_again_to_cancel_the_agreement,
                    Toast.LENGTH_SHORT
                ).show()
            }
            back_pressed = System.currentTimeMillis()
        } else {
            super.onBackPressed()
            finish()
        }
    }

    // initialize views on screen and their listening
    private fun initViews() {
        // find views on screen by id
        btnConfirm = findViewById(R.id.btnConfirm)

        // listeners
        btnConfirm.setOnClickListener(this)
    }

    companion object {
        // temporary variables
        private var back_pressed: Long = 0
    }
}