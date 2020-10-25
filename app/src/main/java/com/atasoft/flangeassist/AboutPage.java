package com.atasoft.flangeassist;

import android.app.ActionBar;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;


public class AboutPage extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.about);
        ActionBar actionBar = getActionBar();
        actionBar.setHomeButtonEnabled(true);

        TextView versionView = findViewById(R.id.versionInfo);
        TextView aboutBlurdTextView = findViewById(R.id.aboutblurb);
        try {
            String versionInfo = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            String versionBlurb = getString(R.string.version_info);
            versionBlurb = versionBlurb.replace("#VERSION", versionInfo);
            versionView.setText(versionBlurb);

        } catch (PackageManager.NameNotFoundException e){
            e.printStackTrace();
        }

        aboutBlurdTextView.setMovementMethod(LinkMovementMethod.getInstance());

    }
}
