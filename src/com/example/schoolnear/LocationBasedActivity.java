package com.example.schoolnear;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

public class LocationBasedActivity extends ListActivity {


	private static final int REQUEST_CODE = 10;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		String[] values = new String[] { "grocery_or_supermarket", "bank", "atm",
				"gas_station", "pharmacy" };
		MainListAdapter adapter = new MainListAdapter(this, values);
		setListAdapter(adapter);

	
	}
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		String item = (String) getListAdapter().getItem(position);
		Intent i;

			i = new Intent(this, AddLocationBasedReminder.class);
			i.putExtra("category", item);
			i.putExtra("type", "location");
			
			startActivityForResult(i, REQUEST_CODE);

	}
	

}
