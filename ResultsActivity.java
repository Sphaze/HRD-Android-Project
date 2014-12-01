package com.example.prj1;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class ResultsActivity extends Activity{

	TextView getFreqData, getModeData;	
	EditText setFreqData;	
	String rsp;
	RadioButton selectedRbLock, selectedRbPtt;
	RadioGroup radioGroupLock, radioGroupPtt;

	Button freqSendbtn, setLock, setptt;

	Spinner spinner;
	boolean data_processing = false;

	private Activity resultsActivity;

	static String frequencyValForDatabase, modeSettingForDatabase;


	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		resultsActivity = this;

		final Intent dataCall = getIntent();
		final Bundle mBundle = dataCall.getExtras();  

		long requestCode = mBundle.getLong("requestCode");

		frequencyValForDatabase = Long.toString(getFreq());
		modeSettingForDatabase = getDropdownMode();


		final String[] modes = {"Select", "AM", "FM", "CW", "CW-R", "USB", "LSB", "RTTY", "RTTY-R"};

		if(requestCode == 1000)
		{
			setContentView(R.layout.get_frequency_screen);
			getFreqData = (TextView)findViewById(R.id.getFreqBox); 
			
			resultsActivity.runOnUiThread(new Runnable() {
				
				@Override
				public void run() {	
					getFreqData.setText("" + getFreq() + " Hz");				
				}
			});

		}		
		else if(requestCode == 1001)
		{
			setContentView(R.layout.set_frequency_screen);
			setFreqData = (EditText)findViewById(R.id.setFreqBox);
			freqSendbtn = (Button)findViewById(R.id.setFreq);

			if(setFreqData == null)
			{
				data_processing = false;
			}

			freqSendbtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					try
					{
						String freqString = setFreqData.getText().toString();	
						setFreq(freqString);
					}
					catch(NumberFormatException e)
					{
						Toast.makeText(getApplicationContext(), "Must input a number in Hz for the frequency", Toast.LENGTH_SHORT).show();
					}

					data_processing = true;	

					if(data_processing == true)
					{
						setResult(Activity.RESULT_OK, dataCall);					
					}
				}
			});
		}
		else if(requestCode == 1002)
		{
			setContentView(R.layout.set_mode_screen);

			spinner = (Spinner)findViewById(R.id.modespinner);

			ArrayAdapter<String>adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, modes);

			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinner.setAdapter(adapter);

			spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

				RspHandler handler = new RspHandler();

				@Override
				public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


					switch (position) {

					case 0:	
						break;
					case 1:
						new CommandContext("set dropdown Mode AM").send(handler);
						break;
					case 2:
						new CommandContext("set dropdown Mode FM").send(handler);
						break;
					case 3:
						new CommandContext("set dropdown Mode CW").send(handler);
						break;
					case 4:
						new CommandContext("set dropdown Mode CW-R").send(handler);
						break;
					case 5:
						new CommandContext("set dropdown Mode USB").send(handler);
						break;
					case 6:
						new CommandContext("set dropdown Mode LSB").send(handler);
						break;
					case 7:
						new CommandContext("set dropdown Mode RTTY").send(handler);
						break;
					case 8:
						new CommandContext("set dropdown Mode RTTY-R").send(handler);
						break;
					}
				}

				@Override
				public void onNothingSelected(AdapterView<?> parent) {
				}

			});
		}
		else if(requestCode == 1003)
		{
			setContentView(R.layout.get_mode_screen);
			getModeData = (TextView)findViewById(R.id.getModeBox);
			
			resultsActivity.runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					getModeData.setText(getDropdownMode());
				}
			});
		}
		else if(requestCode == 1004)
		{
			setContentView(R.layout.set_lock_unlock);

			radioGroupLock = (RadioGroup)findViewById(R.id.lockStates);
			setLock = (Button)findViewById(R.id.setLockState);

			setLock.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					//get selected radio button from radioGroup
					int selectedId = radioGroupLock.getCheckedRadioButtonId();

					//find the radio button by returned id
					selectedRbLock = (RadioButton)findViewById(selectedId);

					RspHandler handler=new RspHandler();

					if(selectedRbLock.getText().equals("Lock on"))
					{			
						new CommandContext("set button-select Lock 1").send(handler);
					}
					else if(selectedRbLock.getText().equals("Lock off"))
					{
						new CommandContext("set button-select Lock 0").send(handler);
					}
				}
			});	
		}
		else if(requestCode == 1005)
		{
			setContentView(R.layout.ptt_push_release);

			radioGroupPtt = (RadioGroup)findViewById(R.id.PttStates);
			setptt = (Button)findViewById(R.id.setPttState);

			setptt.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					//get selected radio button from radioGroup
					int selectedId = radioGroupPtt.getCheckedRadioButtonId();

					//find the radio button by returned id
					selectedRbPtt = (RadioButton)findViewById(selectedId);
					RspHandler handler = new RspHandler();

					if(selectedRbPtt.getText().equals("Push"))
					{
						new CommandContext("set button-select TX 1").send(handler);
					}
					else if(selectedRbPtt.getText().equals("Release"))
					{
						new CommandContext("set button-select TX 0").send(handler);
					}
				}
			});	
		}
	}

	public void clearbtnPress(View v) {
		setFreqData.setText("");
	}


	public void returnPress(View v) {
		finish();	//go back to previous activity	
	}


	public boolean setFreq(final String freq) {

		boolean result = false;

		final StringBuilder sb = new StringBuilder("set frequency-hz ");
		sb.append(freq.toString());

		RspHandler handler = new RspHandler();
		new CommandContext(sb.toString()).send(handler);


		handler=new RspHandler();
		new CommandContext("get frequency").send(handler);

		/** handler should return the current frequency **/
		if(freq.equals(handler.waitForResponse()))
		{
			result = true;
		}

		return result;
	}



	public long getFreq() {

		RspHandler handler=new RspHandler();

		new CommandContext("get frequency").send(handler);

		String freq=handler.waitForResponse();

		return Long.valueOf(freq).longValue();
	}


	public String getDropdownMode()
	{
		RspHandler handler=new RspHandler();


		new CommandContext ("get dropdown-text {Mode}").send(handler);

		String mode = handler.waitForResponse(); //returns the mode as "Mode: {Mode}"
		String []splitter = mode.split("Mode: ");

		return splitter[1].toString(); //return only the mode without "Mode: "
	}
}








