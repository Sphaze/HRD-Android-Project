package com.example.prj1;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;


public class BaseActivity extends SlidingFragmentActivity {

	private int mTitleRes;
	protected ListFragment menuFragment;

	/**
	 	Client object is declared here - Needs to be static as data 
		is shared on this client object between all of the the subclasses of this class
	 **/
	static Client client = null;  
	static String hrdContext = null;	
	static final Map<String, List<String>> freqModes = new HashMap<String, List<String>>();	

	public BaseActivity(int titleRes){
		mTitleRes = titleRes; //this is required to set the title of the action bar
	}
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setTitle(mTitleRes);

		setBehindContentView(R.layout.menu_frame); //set a blank layout for the BehindContentView
		FragmentTransaction ft = this.getSupportFragmentManager().beginTransaction(); //in onCreate, begin the transaction of creating a new list  
		
		menuFragment = new ListMenu();
		ft.replace(R.id.menu_frame, menuFragment);
		ft.commit();

		//the sliding menu is taken from the free JeremyFeinstein library
		SlidingMenu sm = getSlidingMenu();

		//set shadow effect with pixels in Java instead of setting values in xml layout
		sm.setShadowWidth(15);
		sm.setShadowDrawable(R.drawable.shadow);
		sm.setBehindOffset(60);
		sm.setFadeDegree(0.35f);
		sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN); //activate buttons to be pressed
		getSupportActionBar().setDisplayHomeAsUpEnabled(true); //set as true to use home button
		getSupportActionBar().hide();

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch(item.getItemId()){
		case android.R.id.home:
			toggle();
			return true;
		}
		return onOptionsItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		getSupportMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

}
