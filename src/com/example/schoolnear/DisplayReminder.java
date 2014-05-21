package com.example.schoolnear;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class DisplayReminder extends Activity implements
android.view.View.OnClickListener {
	TextView t;
	SQLiteDatabase database;
	String category;
	String message, time;
	Button showReminders;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_reminder);

		t = (TextView) findViewById(R.id.textView1);
		showReminders = (Button) findViewById(R.id.button1);
		showReminders.setOnClickListener(this);

		// Bundle b = getIntent().getExtras();
		// int id = b.getInt("IDDD");

		String id = getIntent().getDataString();
		String s[] = id.split(":");
		//System.out.println("ID: " + s[0]);
		database = openOrCreateDatabase("mySqliteDatabase.db",
				SQLiteDatabase.CREATE_IF_NECESSARY, null);
		String select = "SELECT * FROM reminders WHERE id ="
				+ Integer.parseInt(s[0]);
		Cursor c = database.rawQuery(select, new String[0]);

		if (c.moveToFirst()) {
			time = c.getString(1);
			category = c.getString(3);
			message = c.getString(2);
			//System.out.println("From Notificatpion categoty is " + category);
			t.setText(category);
		}

		String table_name = "reminders";
		String where = "id = ?";
		String[] whereArgs = { s[0] };
		database.delete(table_name, where, whereArgs);
		//System.out.println("after deleete queryyyyyyyyyy");
		t.setText("Reminder - " + time + " " + message + " " + category
				+ " deleted");

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button1:
			Intent j = new Intent(this, ShowReminders.class);
			startActivity(j);

			break;
		}

	}

}

