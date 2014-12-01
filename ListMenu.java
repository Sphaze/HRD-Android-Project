package com.example.prj1;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockListFragment;

public class ListMenu extends SherlockListFragment{


	String[] list_contents = {
			"Set Frequency",
			"Get Frequency", 
			"Set Mode",
			"Get Mode",
			"Set Lock/Unlock",
			"PTT Push/Release",
			"Frequency Slider",
			"Logger"
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		return inflater.inflate(R.layout.list, container, false);	//links the layout to the activity
	}

	
	//onActivityCreated is used to access/modify the UI elements
	@Override
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		setListAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, list_contents));

	}
	

	public void onListItemClick(ListView l, View v, int position, long id){

		super.onListItemClick(l, v, position, id);
		Object obj = this.getListAdapter().getItem(position);
		String pos = obj.toString();


		if(pos.equals("Get Frequency"))
		{
			id = 1000;

			Log.i("ListActivity", "found: " + pos);
			Intent intent = new Intent(getActivity(), ResultsActivity.class);
			
			Bundle data = new Bundle();
			data.putLong("requestCode", id);
			intent.putExtras(data);


			startActivity(intent);
		}
		else if(pos.equals("Set Frequency"))
		{
			id = 1001;

			Log.i("ListActivity", "found: " + pos);
			Intent intent = new Intent(getActivity(), ResultsActivity.class);
			
			Bundle data = new Bundle();
			data.putLong("requestCode", id);
			intent.putExtras(data);
			
			
			startActivity(intent);
		}		
		else if(pos.equals("Set Mode"))
		{
			id = 1002;

			Log.i("ListActivity", "found: " + pos);
			Intent intent = new Intent(getActivity(), ResultsActivity.class);
			
			Bundle data = new Bundle();
			data.putLong("requestCode", id);
			intent.putExtras(data);
			

			startActivity(intent);
		}
		else if(pos.equals("Get Mode"))
		{
			id = 1003;

			Log.i("ListActivity", "found: " + pos);
			Intent intent = new Intent(getActivity(), ResultsActivity.class);
			
			Bundle data = new Bundle();
			data.putLong("requestCode", id);
			intent.putExtras(data);
			

			startActivity(intent);
		}
		else if(pos.equals("Set Lock/Unlock"))
		{
			id = 1004;

			Log.i("ListActivity", "found: " + pos);
			Intent intent = new Intent(getActivity(), ResultsActivity.class);
			
			Bundle data = new Bundle();
			data.putLong("requestCode", id);
			intent.putExtras(data);
			

			startActivity(intent);
		}
		else if(pos.equals("PTT Push/Release"))
		{
			id = 1005;

			Log.i("ListActivity", "found: " + pos);
			Intent intent = new Intent(getActivity(), ResultsActivity.class);
			
			Bundle data = new Bundle();
			data.putLong("requestCode", id);
			intent.putExtras(data);
			

			startActivity(intent);
		}
		else if(pos.equals("Frequency Slider"))
		{
			id = 1006;

			Log.i("ListActivity", "found: " + pos);
			Intent intent = new Intent(getActivity(), Slider.class);
			
			Bundle data = new Bundle();
			data.putLong("requestCode", id);
			intent.putExtras(data);
			

			startActivity(intent);
		}
		else if(pos.equals("Logger"))
		{
			id = 1007;

			Log.i("ListActivity", "found: " + pos);
			Intent intent = new Intent(getActivity(), SqliteDatabase.class);
			
			Bundle data = new Bundle();
			data.putLong("requestCode", id);
			intent.putExtras(data);
			

			startActivity(intent);
		}
	}

}




