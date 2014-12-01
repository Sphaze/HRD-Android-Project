package com.example.prj1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class Slider extends Activity implements OnClickListener{

	Button range_1, range_2, range_3, range_4, range_5, plus, minus;
	SeekBar horzbar;
	TextView sliderText;
	int stepSize = 1;
	Handler handler = new Handler();
	int freqVal = 30000;
	int MAX_FREQUENCY = 60000000;
	
	private Activity sliderActivity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		sliderActivity = this;
		
		final Intent dataCall = getIntent();
		final Bundle mBundle = dataCall.getExtras();  


		long requestCode = mBundle.getLong("requestCode");	


		if(requestCode == 1006)
		{
			setContentView(R.layout.frequency_slider);

			horzbar = (SeekBar)findViewById(R.id.horizontalSeekBar);
			sliderText = (TextView)findViewById(R.id.frequencyRead);
			sliderText.setTextSize(65);


			range_1 = (Button) findViewById(R.id.btnHz);
			range_2 = (Button) findViewById(R.id.btnKhz);
			range_3 = (Button) findViewById(R.id.btn10Khz);
			range_4 = (Button) findViewById(R.id.btn100Khz);
			range_5 = (Button) findViewById(R.id.btnMhz);
			plus = (Button) findViewById(R.id.plusBtn);
			minus = (Button) findViewById(R.id.minusBtn);

			range_1.setOnClickListener(this);
			range_2.setOnClickListener(this);
			range_3.setOnClickListener(this);
			range_4.setOnClickListener(this);
			range_5.setOnClickListener(this);

			horzbar.setMax(MAX_FREQUENCY);

			horzbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

				@Override
				public void onStopTrackingTouch(SeekBar seekBar) {

				}

				@Override
				public void onStartTrackingTouch(SeekBar seekBar) {

				}

				@Override
				public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

					progress = (progress / stepSize) * stepSize;
					freqVal = progress;

					Log.i("TESTS", "seekbar max: " + seekBar.getMax());


					if (freqVal >= 30000 && freqVal != 0 && freqVal <= seekBar.getMax()) 
					{
						seekBar.setProgress(freqVal);

						sliderActivity.runOnUiThread(new Runnable() {
							
							@Override
							public void run() {
								sliderText.setText("" + freqVal);
								
								RspHandler handler = new RspHandler();
								new CommandContext("set frequency-hz " + freqVal).send(handler);
							}
						});
					}
				}
			});



			plus.setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {

					if(event.getAction() == MotionEvent.ACTION_DOWN)
					{				
						handler.post(positiveIncrements);
					}
					if(event.getAction() == MotionEvent.ACTION_UP){		
						handler.removeCallbacks(positiveIncrements);
					}

					return true;
				}		  
			});


			minus.setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {

					if(event.getAction() == MotionEvent.ACTION_DOWN)
					{				
						handler.post(negativeIncrements);
					}
					if(event.getAction() == MotionEvent.ACTION_UP){		
						handler.removeCallbacks(negativeIncrements);
					}

					return true;
				}		  
			});
		}
	}

	@Override
	public void onClick(View v) {


		if (v.getId() == range_1.getId()) {
			stepSize = 1;
		} else if (v.getId() == range_2.getId()) {

			stepSize = 1000;
		} else if (v.getId() == range_3.getId()) {

			stepSize = 10000;
		} else if (v.getId() == range_4.getId()) {

			stepSize = 100000;
		} else if (v.getId() == range_5.getId()) {

			stepSize = 1000000;
		}
	}


	private Runnable positiveIncrements = new Runnable() {
		@Override
		public void run() {

			if(freqVal <= MAX_FREQUENCY)
			{
				freqVal++;
				horzbar.setProgress(freqVal);

				handler.postDelayed(this, 100);
			}
		}
	};


	private Runnable negativeIncrements = new Runnable() {
		@Override
		public void run() {

			if(freqVal >= 30000)
			{
				freqVal--;
				horzbar.setProgress(freqVal);

				handler.postDelayed(this, 100);
			}
		}
	};
}





