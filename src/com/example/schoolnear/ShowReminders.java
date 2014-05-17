package com.example.schoolnear;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Toast;

public class ShowReminders extends ListActivity{
	SQLiteDatabase database;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		try{
			database = openOrCreateDatabase("mySqliteDatabase.db", SQLiteDatabase.CREATE_IF_NECESSARY, null);
			String select= "SELECT * FROM reminders";
			Cursor c = database.rawQuery(select, new String[0]);
			int count=c.getCount();
			System.out.println("counttt" + count);
			String[] values = new String[count];
			count = 0;
			while(c.moveToNext()){
				values[count] = ""+ c.getString(1) + "~" + c.getString(2)+ "~" + c.getString(3);
				System.out.println(values[count]);
				count++;
			}
			database.close();
			MainListAdapterReminders adapter = new MainListAdapterReminders(this, values);
			setListAdapter(adapter);
		}
		catch(Exception e){
			Toast.makeText(this, "No Remniders Added!!", Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void finish() {
		super.finish();

		Intent intent = new Intent(this, MainActivity.class);
		//intent.putExtra("EXIT", true);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(intent);
	}

}
