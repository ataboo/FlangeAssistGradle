package com.atasoft.flangeassist;


import android.annotation.*;
import android.app.*;
import android.content.*;
import android.os.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;

//See if it's working

public class MainActivity extends Activity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);
		setupButtons();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_actions, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	private void openSettings() {
		Intent intent = new Intent(this, PreferenceMenu.class);
	    startActivity(intent);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
			case R.id.action_settings:
				openSettings();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
    public void onClick(View v) {
		switch (v.getId()) {
			case R.id.main_toolButton:
				launchTools();
				break;
			case R.id.main_paychequeButton:
				launchPayCalc();
				break;
			case R.id.main_linkButton:
				launchLinks();
				break;
			case R.id.main_settingsButton:
				openSettings();
				break;
			case R.id.main_aboutButton:
				launchAbout();
				break;
		}
    }
	
	private Button toolButton;
	private Button paychequeButton;
	private Button linkButton;
	private Button settingsButton;
	private Button aboutButton;
	private Button[] buttonArr;
	private void setupButtons(){
		this.toolButton = (Button) findViewById(R.id.main_toolButton);
		this.paychequeButton = (Button) findViewById(R.id.main_paychequeButton);
		this.linkButton = (Button) findViewById(R.id.main_linkButton);
		this.settingsButton = (Button) findViewById(R.id.main_settingsButton);
		this.aboutButton = (Button) findViewById(R.id.main_aboutButton);
		this.buttonArr = new Button[]{toolButton, paychequeButton, linkButton, settingsButton, aboutButton};
		for(Button b: buttonArr){
			b.setOnClickListener(this);
		}
	}
		
	private void launchTools(){
		Intent intent = new Intent(this, ToolsActivity.class);
	    startActivity(intent);
	}
	
	private void launchPayCalc(){
		Intent intent = new Intent(this, FragFramer.class);
		intent.putExtra("launch_frag", FragFramer.PAY_CALC);
	    startActivity(intent);
	}
	
	private void launchLinks(){
		Intent intent = new Intent(this, FragFramer.class);
		intent.putExtra("launch_frag", FragFramer.HALL);
	    startActivity(intent);
	}
	
	private void launchAbout(){
		Intent intent = new Intent(this, FragFramer.class);
		intent.putExtra("launch_frag", FragFramer.ABOUT);
	    startActivity(intent);	
	}
	
}

