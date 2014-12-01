package com.example.prj1;

import java.util.Arrays;
import java.util.LinkedList;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.TextView;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class SetupActivity extends BaseActivity{


	private Fragment mContent;
	TextView debug;


	String str;
	String ip_address;
	String saved_ip;
	String saved_rsp;
	String radio;

	public static final String PREFS_NAME = "myprefs";

	int port_num;
	int saved_port;


	boolean setupStatus = false;
	boolean connectionError = false;	
	boolean initializationComplete = false;
	boolean responseExecuted = false;


	private final Command getRadio=new Command("Get Radio");
	private final Command getContext=new Command("Get Context");


	public SetupActivity(){
		super(R.string.app_name); //this sets up using the name inside the ActionBar
	}


	class InitialSetupTask extends AsyncTask<Void,String,String>{


		StringBuilder incoming_data = new StringBuilder();
		StringBuilder saved_data = new StringBuilder();


		@Override
		protected String doInBackground(Void... params) {

			if(LoadingScreen.connected == true)
			{
				try
				{
					if(!radio.equals("error"))
					{
						connectionError = false;
						
						responseExecuted = true;
					}
					else if(radio.equals(null))
					{
						connectionError = true;

						return "PROBLEM";
					}
					else 
					{
						connectionError = true;

						return "PROBLEM";
					}
				}
				catch(RuntimeException e)
				{
					e.printStackTrace();
				}

				if(responseExecuted == true)
				{
					incoming_data.append("\nConnected: " + radio);

					return incoming_data.toString();
				}
				else if(port_num == saved_port && ip_address.equals(saved_ip) && connectionError == false)
				{
					/** data has been deleted on back button press, state was stored, print saved state **/                  
					saved_data.append("\nResponse: " + saved_rsp);

					return saved_data.toString();
				}
			}

			return null;
		}


		@Override
		protected void onPostExecute(String resultingData)
		{
			super.onPostExecute(resultingData);

			if(connectionError == true)
			{
				debug.setText("A problem has been encountered while trying to connect");
			}			
			else
			{	
				debug.setText(resultingData.toString());

				if(setupStatus == true)
				{
					Log.i("TESTS","Setup OK");
					debug.append("\n\n\ntip: slide the screen with \t\t your finger");
				}		
			}
		}
	}


	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		if (savedInstanceState != null)
			mContent = getSupportFragmentManager().getFragment(savedInstanceState, "mContent");
		if (mContent == null)
			mContent = new MainView();

		setContentView(R.layout.content_frame);
		getSupportFragmentManager()
		.beginTransaction()
		.replace(R.id.content_frame, mContent).commit();

		setBehindContentView(R.layout.menu_frame);
		getSupportFragmentManager()
		.beginTransaction()
		.replace(R.id.menu_frame, new ListMenu()).commit(); //slides to the randomList class

		getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		setSlidingActionBarEnabled(true); //if set to true, the ActionBar slides along with the page

		debug = (TextView)findViewById(R.id.debug); 


		SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);	
		saved_rsp = preferences.getString("response", "Not Found");
		saved_ip = preferences.getString("ip", "Not Found");
		saved_port = preferences.getInt("port",0000);


		LoadingScreen ls = LoadingScreen.getInstance();

		port_num = ls.getPort();
		ip_address = ls.getIP();


		RspHandler handler = new RspHandler();
		InitialSetupTask setup = new InitialSetupTask();

		getRadio.sendCommand(handler);
		radio = handler.waitForResponse(); //the response from the handler now should return the name of the radio "IC-756ProIII" in this case

		setup.execute();

		try 
		{			
			Log.i("TESTS", "task result: " + setup.get());

			if(setup.get().equals("PROBLEM"))
			{
				errorThread.start();
			}
			else
			{	
				initRadioCommands();
			}
		} 
		catch(Exception e) 
		{			
			e.printStackTrace();
		}		
	}


	public boolean initRadioCommands()
	{
		if(LoadingScreen.connected == true)
		{
			RspHandler handler = new RspHandler();

			//send the context "Get Context"
			getContext.sendCommand(handler);


			//the response from the handler should print back the context of the radio (a number)
			hrdContext=handler.waitForResponse(); 
			Log.i("TESTS","Context ok? " + hrdContext);


			CommandContext getButtons = new CommandContext("Get Buttons");
			CommandContext getDropDowns = new CommandContext("Get Dropdowns");
			
			getButtons.send(handler);
			
			Log.i("TESTS","got buttons? " + handler.waitForResponse());

			handler=new RspHandler();
			getDropDowns.send(handler);

			
			freqModes.clear(); //remove all the elements from the set

			for(String st:handler.waitForResponse().toLowerCase().replace(" ", "~").split(","))
			{
				handler=new RspHandler();

				final StringBuilder sb=new StringBuilder("get dropdown-list {");
				sb.append(st).append('}');


				final CommandContext getDropDownList = new CommandContext(sb.toString());

				getDropDownList.send(handler);

				Log.i("TESTS","got dropdown list? " + handler.waitForResponse());

				//the handler will now find what's in the drop down list and store the data in a fixed sized list
				//it will then store this data in a LinkedList
				freqModes.put(st, new LinkedList<String>(Arrays.asList(handler.waitForResponse().toLowerCase().replace(" ", "~").split(","))));
			}            


			handler=new RspHandler();

			//Test Lock/Unlock 
			new CommandContext("set button-select Lock 1").send(handler); //set lock to 1

			String lockOn = handler.waitForResponse(); //should return "OK" if lock on is okay
			Log.i("TESTS","lock on - ok? " + lockOn);

			handler=new RspHandler();


			new CommandContext("get button-select Lock").send(handler);

			String lockstate = handler.waitForResponse(); //should return the state of the lock (1)
			Log.i("TESTS","lock state ok? " + lockstate);



			new CommandContext("set button-select Lock 0").send(handler); //set lock back to 0

			handler=new RspHandler();


			new CommandContext("get frequency").send(handler); //should return the correct frequency (over 30Khz minimum)

			if((handler.waitForResponse()).equals("0"))
			{
				initializationComplete = false;
			}


			initializationComplete = true;
			Log.e("ERRORLOCATION","initializationComplete? " + initializationComplete);
		}

		if(!initializationComplete)
		{
			setupStatus = false;

			Log.e("ERRORLOCATION", "something went wrong setting up commands");
		}
		else
		{
			setupStatus = true;
		}


		return setupStatus;
	}


	Thread errorThread = new Thread(){

		@Override
		public void run(){

			try 
			{
				synchronized(this)
				{
					wait(2500);

					finish();
					LoginActivity.noProblemsOccurred = false;			
				}
			}
			catch(InterruptedException ex)
			{     
				Log.e("ERRORLOC", "synchronized thread " + ex.getMessage());
			}       
		}
	};



	@Override
	public void onStart()
	{
		super.onStart();
		Log.i("TESTS","onStart()");	
	}

	@Override
	public void onPause()
	{
		super.onPause();

		/** STORE VALUES BETWEEN INSTANCES HERE **/
		SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();

		if(radio.toString() != null)
		{
			Log.i("TESTS","onPause() Saved response, port and IP");
			editor.putInt("port", port_num);
			editor.putString("ip", ip_address);
			editor.putString("response", radio);
		}

		editor.commit();

	}

	@Override
	public void onResume()
	{
		super.onResume();
		Log.i("TESTS","onResume()");

	}

	@Override
	public void onSaveInstanceState(Bundle outState){
		super.onSaveInstanceState(outState);
		getSupportFragmentManager().putFragment(outState, "mContent", mContent);
		Log.i("TESTS","onSaveInstanceState()");
	}

	@Override
	public void onRestoreInstanceState(Bundle outState)
	{
		super.onRestoreInstanceState(outState);
		Log.i("TESTS","onRestoreInstanceState()");	
	}

	@Override
	public void onStop()
	{
		super.onStop();
		Log.i("TESTS","onStop()");
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();	
	}


	public void switchContent(Fragment fragment){
		mContent = fragment;
		getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
		getSlidingMenu().showContent();
	}
}




























