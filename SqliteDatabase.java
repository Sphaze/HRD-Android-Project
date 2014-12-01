package com.example.prj1;

import java.sql.SQLException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.*;
import android.os.*;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;

public class SqliteDatabase extends ResultsActivity implements OnClickListener{


	SQLiteDatabase db;

	EditText name;
	EditText country;
	EditText mode;
	EditText freq;
	EditText callsign;
	EditText equipment;

	Button savebutton;
	Thread thrd1, thrd2;

	String new_name;
	String new_country;
	String new_mode;
	String new_freq;
	String new_callsign;
	String new_equipment;
	
	private Activity database;


	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);	
		
		Intent dataCall = getIntent();
		Bundle mBundle = dataCall.getExtras();  

		long requestCode = mBundle.getLong("requestCode");	
				
		database = this;
		
		if(requestCode == 1007)
		{
			setContentView(R.layout.database_form);

			savebutton = (Button)findViewById(R.id.saveBtn);
			name = (EditText)findViewById(R.id.nameBox);
			country = (EditText)findViewById(R.id.countryBox);
			mode = (EditText)findViewById(R.id.modeBox);
			freq = (EditText)findViewById(R.id.freqBox);
			callsign = (EditText)findViewById(R.id.callsignBox);
			equipment = (EditText)findViewById(R.id.equipmentBox);

			savebutton.setOnClickListener(this);

			Log.i("TESTS", "mode setting " + modeSettingForDatabase);
			Log.i("TESTS", "freq setting " + frequencyValForDatabase);


			database.runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					mode.setText(modeSettingForDatabase.toString());			
					freq.setText(frequencyValForDatabase.toString() + " Hz");
				}
			});

			openDatabase();
			
			if(tableAlreadyExists("hrd_contacts"))
			{
				return;
			}
			else
			{
				createTable();
			}
			
		}
	}


	private void openDatabase()
	{
		try
		{
			String path = Environment.getExternalStorageDirectory().getPath();
			String myDbPath = path + "/" + "hrd.db";
			
			
			db = SQLiteDatabase.openDatabase(myDbPath, null, SQLiteDatabase.CREATE_IF_NECESSARY) ;		
		}
		catch (SQLiteException e) 
		{
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();        	
		}

	}

	private void createTable()
	{

		db.beginTransaction();
		try
		{

			db.execSQL("create table hrd_contacts ("
					+ " ID integer PRIMARY KEY autoincrement, " 
					+ " Name  text, " 
					+ " Country  text, " 
					+ " Mode  text, " 
					+ " Frequency  text, " 
					+ " Callsign text, "
					+ " Equipment text );  ");

			/** commit changes **/
			db.setTransactionSuccessful();

			Log.i("TESTS", "made table inside insertData()");
		}
		finally 
		{
			db.endTransaction();
		}
	}


	private void insertData() throws SQLException
	{
		db.beginTransaction();

		try
		{		
			/** make a new row with data for the contact **/
			db.execSQL("insert into hrd_contacts(Name, Country, Mode, Frequency, Callsign, Equipment)" + 
					"values ('"+new_name+" ', '"+new_country+"', '"+new_mode+"', '"+new_freq+"', '"+new_equipment+"', '"+new_callsign+"');" );

			db.setTransactionSuccessful();

			Toast.makeText(this, "Data successfully added to database", Toast.LENGTH_SHORT).show();
		}
		catch (SQLiteException e) 
		{
			Toast.makeText(this, e.getMessage(),Toast.LENGTH_SHORT).show();
		}
		finally 
		{
			db.endTransaction();

		}

	}


	@Override
	public void onClick(View v) {

		if(v.getId() == savebutton.getId())
		{
			new_name = name.getText().toString();
			new_country = country.getText().toString();
			new_mode = mode.getText().toString();
			new_freq = freq.getText().toString();
			new_callsign = callsign.getText().toString();
			new_equipment = equipment.getText().toString();

			try 
			{				
				insertData();
				
				displayInsertedData();
			} 
			catch (SQLException e) 
			{
				e.printStackTrace();
			}
		}
	}


	public boolean tableAlreadyExists(String tableName) 
	{
		Cursor cursor = db.rawQuery("select * from sqlite_master where tbl_name = '"+tableName+"'", null);

		if(cursor != null) 
		{
			if(cursor.getCount() > 0) 
			{
				cursor.close();
				return true;
			}

			cursor.close();
		}

		return false;
	}




	private void displayInsertedData()
	{

		try
		{
			String[] columns = { "ID", "Name", "Country", "Mode", "Frequency", "Callsign", "Equipment" };

			Cursor c = db.query("hrd_contacts", columns, null, null, null, null, "ID");


			int idCol = c.getColumnIndex("ID");
			int nameCol = c.getColumnIndex("Name");
			int countryCol = c.getColumnIndex("Country");
			int modeCol = c.getColumnIndex("Mode");
			int freqCol = c.getColumnIndex("Frequency");
			int callsignCol = c.getColumnIndex("Callsign");
			int equipmentCol = c.getColumnIndex("Equipment");

			c.moveToLast();

			columns[0] = Integer.toString((c.getInt(idCol)));
			columns[1] = c.getString(nameCol);
			columns[2] = c.getString(countryCol);
			columns[3] = c.getString(modeCol);
			columns[4] = c.getString(freqCol);
			columns[5] = c.getString(callsignCol);
			columns[6] = c.getString(equipmentCol);

			new AlertDialog.Builder(this)
			.setTitle("Added")
			.setMessage( "\n" + "Name: " 
					+ columns[1] + "\n" + "Country: "     
					+ columns[2] + "\n" + "Mode: " 
					+ columns[3] + "\n" + "Frequency: "     
					+ columns[4] + "\n" + "Call sign: "
					+ columns[5] + "\n" + "Equipment: "     
					+ columns[6] + "\n" )
					.show();
		} 
		catch (Exception e)
		{
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
		}
	}
	
	@Override
	public void onPause()
	{
		super.onPause();		
	}
	
	@Override
	public void onDestroy()
	{
		super.onDestroy();

		db.close();
	}
	
	public void clearAllFields(View v) {
		
		name.setText("");
		country.setText("");
		mode.setText("");
		freq.setText("");
		callsign.setText("");
		equipment.setText("");
	}
}








