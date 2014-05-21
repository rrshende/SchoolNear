package com.example.schoolnear;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class AddLocationBasedReminder extends Activity implements OnClickListener {
	private EditText note;
	private Button add;
	private int rid;
	private SQLiteDatabase database;
	private String setTime = "", category;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_location_based_reminder);
		Bundle bundle = getIntent().getExtras();
		category = bundle.getString("category");
		note = (EditText) findViewById(R.id.editText1);
		add= (Button) findViewById(R.id.button1);
		add.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button1:
			try {
				database = openOrCreateDatabase("mySqliteDatabase.db", SQLiteDatabase.CREATE_IF_NECESSARY, null);
				String create = "CREATE TABLE IF NOT EXISTS reminders (id INTEGER PRIMARY KEY AUTOINCREMENT," +	"time TEXT, note TEXT, category TEXT)";
				database.execSQL(create);
				String insert = "INSERT INTO reminders(time, note, category) VALUES("+"'"+setTime+"'"+","+"'"+note.getText().toString()+"'"+","+"'"+category+"'"+")";
				database.execSQL(insert);
				Intent intent = new Intent(this, LocationService.class);
				String select = "SELECT MAX(id) FROM reminders";
				Cursor c = database.rawQuery(select, null);
				if(c.moveToFirst()){
					rid = c.getInt(0);
					intent.setData(Uri.parse(""+ rid));
				}
				AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
				PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, 0);
				alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pendingIntent);
				String selectq = "SELECT * FROM Places";
				Cursor x = database.rawQuery(selectq, new String[0]);
				while(x.moveToNext()){
					//System.out.println("PID "+x.getString(0)+"NAME : "+x.getString(3)+" LAT : "+x.getString(1)+"LONG : "+x.getString(2)+"category: "+x.getString(4));

				}
				System.out.println("Total entries "+x.getCount());

			}catch (Exception e) {
				// TODO: handle exception
			}
			database.close();
			Intent j = new Intent(this, ShowReminders.class);
			startActivity(j);
			break;
		}
	}



}

