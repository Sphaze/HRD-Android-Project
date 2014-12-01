package com.example.prj1;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class LoginActivity extends Activity implements OnClickListener{

	EditText port;
	EditText ip;
	Button btn1;	


	static String port_text;
	static String ip_text;

	boolean correctSyntax = false;

	static boolean noProblemsOccurred = true;

	static int portnum;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.login);

		port = (EditText)findViewById(R.id.portbox);
		ip = (EditText)findViewById(R.id.ipbox);
		btn1 = (Button)findViewById(R.id.connectbtn);


		btn1.setOnClickListener(this);
	}


	@Override
	public void onClick(View v) {

		if(v.getId() == btn1.getId())
		{
			port_text = port.getText().toString();
			ip_text = ip.getText().toString();


			try
			{
				if(!ip_text.contains(".") || !Character.isDigit(ip_text.charAt(0)) || ip_text.length() > 15 || ip_text.length() < 7)                                            
				{
					throw new Exception();
				}
				else
				{
					correctSyntax = true;
				}
			}
			catch (Exception e) 
			{
				Toast.makeText(getApplicationContext(), "Invalid IP", Toast.LENGTH_SHORT).show();
			}

			try
			{
				portnum = Integer.parseInt(port_text);			
			}
			catch(NumberFormatException ex)
			{
				correctSyntax = false;
				Toast.makeText(getApplicationContext(), "Invalid port", Toast.LENGTH_SHORT).show();
			}

			noProblemsOccurred = true;

			if(correctSyntax == true && noProblemsOccurred == true)
			{
				Intent myIntent = new Intent(LoginActivity.this, LoadingScreen.class);

				startActivity(myIntent);
			}		
		}
	}

	public void clearbtnPress(View v) {
		port.setText("");
		ip.setText("");
	}

	@Override
	public void onSaveInstanceState(Bundle outState){
		super.onSaveInstanceState(outState);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{

		switch(item.getItemId())
		{
		case R.id.about:
			new AlertDialog.Builder(this)
			.setTitle("About")
			.setMessage(R.string.about)
			.show();
			break;

		case R.id.exit:
			moveTaskToBack(false);
			break;

		case R.id.contact:
			new AlertDialog.Builder(this)
			.setTitle("Contact")
			.setMessage(R.string.contact)
			.show();
			break;
		}


		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.inflatormenu, menu);
		return true;
	}
}











