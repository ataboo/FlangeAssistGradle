package com.atasoft.flangeassist.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.atasoft.flangeassist.*;

public class ReporterFragment extends Fragment implements View.OnClickListener {

    //TODO: switch to reporter when published
    public static final String packageName = "com.atasoft.flangeassist";

    public static final String bmReportPackage = "com.atasoft.boilermakerreporter";
    public static final int SUPER = 0;
    public static final int APPRENTICE = 1;

    View thisFragView;
    Context context;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.reporter_frag, container, false);
        thisFragView = v;
        this.context = v.getContext();
        setupViews();
        return v;
    }

    @Override
    public void onResume() {
        checkIfInstalled();
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.report_play_launch:
                launchPlayStore(packageName);
                break;
            case R.id.reportLaunchAppren:
                launchReporter(APPRENTICE);
                break;
            case R.id.reportLaunchSuper:
                launchReporter(SUPER);
                break;
        }
    }

    Button launchApprenticeButton;
    Button launchSuperButton;
    ImageView notInstallImage;
    TextView notInstallText;
    private void setupViews(){
        this.launchApprenticeButton = (Button) thisFragView.findViewById(R.id.reportLaunchAppren);
        launchApprenticeButton.setOnClickListener(this);
        this.launchSuperButton = (Button) thisFragView.findViewById(R.id.reportLaunchSuper);
        launchSuperButton.setOnClickListener(this);
        this.notInstallText = (TextView) thisFragView.findViewById(R.id.reporterNotFoundText);
        this.notInstallImage = (ImageView) thisFragView.findViewById(R.id.report_play_launch);
        notInstallImage.setOnClickListener(this);

        checkIfInstalled();
    }

    private void checkIfInstalled(){
        if(launchApprenticeButton == null){
            Log.e("ReporterFragment", "checkIfInstalled called before views setup.");
            return;
        }
        boolean alreadyInstalled = isPackageInstalled("com.atasoft.boilermakerreporter", context);
        launchApprenticeButton.setVisibility((alreadyInstalled) ? View.VISIBLE: View.GONE);
        launchSuperButton.setVisibility((alreadyInstalled) ? View.VISIBLE: View.GONE);
        notInstallText.setVisibility((alreadyInstalled) ? View.GONE: View.VISIBLE);
        notInstallImage.setVisibility((alreadyInstalled) ? View.GONE: View.VISIBLE);
    }

    private static boolean isPackageInstalled(String packageName, Context context){
        PackageManager pm = context.getPackageManager();
        try{
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e){
            return false;
        }
    }

    private void launchPlayStore(String packageName) {
        try{
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName)));
        } catch (android.content.ActivityNotFoundException anfe){
            Log.w("ReporterFragment", "Playstore not found, launching URI.");
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id="
                    + packageName)));
        }
    }

    private void launchReporter(int startMode){
        Intent intent;
        PackageManager manager = context.getPackageManager();
        try {
            intent = manager.getLaunchIntentForPackage(bmReportPackage);
            if (intent == null) {
                throw new PackageManager.NameNotFoundException();
            }
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.putExtra("launch_mode", startMode);
            startActivity(intent);
        } catch (PackageManager.NameNotFoundException nnfe){
            nnfe.printStackTrace();
            Log.e("ReporterFragment", bmReportPackage + " launch intent not found");
            Toast.makeText(context, "Boilermaker Reporter failed to launch.",
                    Toast.LENGTH_SHORT).show();
        }
    }
}
