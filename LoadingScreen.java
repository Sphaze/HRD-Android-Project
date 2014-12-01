package com.example.prj1;

import java.net.InetAddress;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;


public class LoadingScreen extends LoginActivity
{

	private ProgressDialog progressBar;

	static LoadingScreen instance;

	static boolean connected = false;


	static LoadingScreen getInstance()
	{
		return instance;
	}


	public int getPort()
	{
		return portnum;
	}

	public String getIP()
	{
		return ip_text;
	}


	@Override
	public void onCreate(Bundle savedInstanceState){

		super.onCreate(savedInstanceState);	
		setContentView(R.layout.activity_main);

		if(noProblemsOccurred)
		{
			instance = this;

			try
			{
				/** start the client on the port and IP address received from the static variables in LoginActivity 
				 * the client object is statically declared in the BaseActivity class **/

				BaseActivity.client = new Client(InetAddress.getByName(ip_text), portnum);
				Thread t = new Thread(BaseActivity.client);
				t.setName("Ham Radio Deluxe Client");
				t.setDaemon(true);
				t.start();

				connected = true;
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}

			progressBar = new ProgressDialog(this);
			progressBar.setCancelable(true);
			progressBar.setMessage("Loading...");
			progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressBar.setIndeterminate(true);
			progressBar.show();


			new Handler().postDelayed(new Runnable(){ 

				@Override 
				public void run() { 
						
					LoadingScreen.this.finish(); 
					
					Intent mainIntent = new Intent(LoadingScreen.this, SetupActivity.class); 
					startActivity(mainIntent);	

					progressBar.cancel();
				} 
			}, 2000);
		}
		else    
		{
			//if problems have occurred (noProblemsOccurred variable becomes false) - finish this activity and go back to login screen                            
			finish();
		}
	}
}









