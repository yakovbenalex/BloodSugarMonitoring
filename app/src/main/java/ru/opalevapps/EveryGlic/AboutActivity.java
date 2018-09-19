package ru.opalevapps.EveryGlic;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class AboutActivity extends AppCompatActivity {
    String version;
    TextView tvAppVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_about);

        tvAppVersion = findViewById(R.id.tvAppVersion);

        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        version = getString(R.string.version) + " - " + version;
        tvAppVersion.setText(version);
//        tvAppVersion.setText(String.format("%d - %s", R.string.version, version));

    }
}