package com.atasoft.flangeassist;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.widget.TextView;


public class AboutPage extends ActionBarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.about);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);

        TextView versionView = (TextView) findViewById(R.id.versionInfo);
        try {
            String versionInfo = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            String versionBlurb = getString(R.string.version_info);
            versionBlurb = versionBlurb.replace("#VERSION", versionInfo);
            versionView.setText(versionBlurb);
        } catch (PackageManager.NameNotFoundException e){
            e.printStackTrace();
        }

    }
}
