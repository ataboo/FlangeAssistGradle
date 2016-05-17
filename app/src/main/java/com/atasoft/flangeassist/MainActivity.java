package com.atasoft.flangeassist;


import android.content.*;
import android.os.*;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.*;
import android.view.*;

import com.atasoft.adapters.NavDrawerAdaptor;
import com.atasoft.flangeassist.fragments.NavigationDrawerFragment;
import com.atasoft.flangeassist.fragments.cashcounter.CashCounter2;
import com.atasoft.flangeassist.fragments.cashcounter.counterobjects.TextureBox;
import com.atasoft.flangeassist.fragments.settings.PreferenceMenu;


public class MainActivity extends ActionBarActivity
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

        if(lastFrag instanceof CashCounter2){
            MainActivity.TEXTURE_BOX.dispose();
        }
        lastFrag = newFrag;
    }

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_actions, menu);
        restoreActionBar();
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

    public void restoreActionBar() {
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(android.support.v7.app.ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }
	
}

