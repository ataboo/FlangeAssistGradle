package com.atasoft.flangeassist;

import android.content.*;
import android.os.*;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.*;
import android.view.*;

import com.atasoft.shared.NavDrawerAdaptor;
import com.atasoft.flangeassist.fragments.NavigationDrawerFragment;
import com.atasoft.flangeassist.fragments.cashcounter.CashCounter;
import com.atasoft.flangeassist.fragments.cashcounter.counterobjects.TextureBox;
import com.atasoft.flangeassist.fragments.settings.PreferenceMenu;


public class MainActivity extends AppCompatActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    private Fragment lastFrag;
    public static MainActivity staticRef;
    public static TextureBox TEXTURE_BOX = new TextureBox();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
        staticRef = this;
		super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        NavigationDrawerFragment mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        CharSequence mTitle = getTitle();

        mNavigationDrawerFragment.setUp(R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
	}

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // draw the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();

        Fragment newFrag = NavDrawerAdaptor.getFragmentAtIndex(position);

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
}

