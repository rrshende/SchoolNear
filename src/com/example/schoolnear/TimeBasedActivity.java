package com.example.schoolnear;




import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

public class TimeBasedActivity extends ListActivity {
	
	private static final int REQUEST_CODE = 10;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
		String[] values = new String[] { "Send SMS", "Send Email", "Set Alarm",
				"Call Someone" };
		MainListAdapter adapter = new MainListAdapter(this, values);
		setListAdapter(adapter);
        
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		String item = (String) getListAdapter().getItem(position);
		Intent i;

			i = new Intent(this, AddReminderActivity.class);
			i.putExtra("category", item);
			i.putExtra("type", "time");
			startActivityForResult(i, REQUEST_CODE);

	}

}