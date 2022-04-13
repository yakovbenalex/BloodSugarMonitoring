package ru.opalevapps.EveryGlic.ui

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import ru.opalevapps.EveryGlic.R

class AboutActivity : AppCompatActivity() {
    lateinit var version: String
    private lateinit var tvAppVersion: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info_about)

        tvAppVersion = findViewById(R.id.tvAppVersion)

        try {
            val packageInfo = this.packageManager.getPackageInfo(packageName, 0)
            version = packageInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        version = "${getString(R.string.version)} - $version"
        tvAppVersion.text = version
        //        tvAppVersion.setText(String.format("%d - %s", R.string.version, version));
    }
}