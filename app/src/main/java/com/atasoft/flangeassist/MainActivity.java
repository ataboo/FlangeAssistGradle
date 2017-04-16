package com.atasoft.flangeassist;


import android.content.*;
import android.os.*;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.*;
import android.util.Log;
import android.view.*;

import com.atasoft.flangeassist.fragments.callout.CalloutDialogFrag;
import com.atasoft.flangeassist.fragments.callout.daters.hire.ClassificationFilter;
import com.atasoft.shared.NavDrawerAdaptor;
import com.atasoft.flangeassist.fragments.NavigationDrawerFragment;
import com.atasoft.flangeassist.fragments.cashcounter.CashCounter;
import com.atasoft.flangeassist.fragments.cashcounter.counterobjects.TextureBox;
import com.atasoft.flangeassist.fragments.settings.PreferenceMenu;

import java.util.EnumSet;


public class MainActivity extends AppCompatActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    private NavigationDrawerFragment mNavigationDrawerFragment;
    private CharSequence mTitle;
    private Fragment lastFrag;
    public static MainActivity staticRef;
    public static TextureBox TEXTURE_BOX = new TextureBox();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
        this.staticRef = this;
		super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        mNavigationDrawerFragment.setUp(R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
	}

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // draw the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();

        Fragment newFrag = NavDrawerAdaptor.getItem(position);

        fragmentManager.beginTransaction()
                .replace(R.id.container, newFrag)
                .commit();

        if(lastFrag instanceof CashCounter){
            MainActivity.TEXTURE_BOX.dispose();
        }
        lastFrag = newFrag;
    }

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
        int menuRes = R.menu.main_actions;

        if(lastFrag instanceof CustomMenuLayout) {
            menuRes = ((CustomMenuLayout) lastFrag).getMenuRes();
        }

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(menuRes, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
        switch(item.getItemId()){
            case R.id.action_settings:
                openSettings();
                return true;
            case R.id.action_about:
                openAbout();
                return true;
            case R.id.callout_settings:
                openCalloutSettings();
        }
		return super.onOptionsItemSelected(item);
    }

    private void openSettings() {
        Intent intent = new Intent(this, PreferenceMenu.class);
        startActivity(intent);
    }

    private void openAbout(){
        Intent intent = new Intent(this, AboutPage.class);
        startActivity(intent);
    }

    private void openCalloutSettings() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        if (lastFrag instanceof CalloutDialogFrag.DismissListener) {
            DialogFragment newDialog = CalloutDialogFrag.newInstance((CalloutDialogFrag.DismissListener)lastFrag);
            newDialog.show(ft, "dialog");
        } else {
            Log.e("FlangeAssist", "Failed to set dismiss listener for callout settings.");
        }

    }
}

