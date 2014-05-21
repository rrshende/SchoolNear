package com.example.schoolnear;

import java.text.SimpleDateFormat;
import java.util.Date;

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
import android.widget.TimePicker;
import android.widget.Toast;

public class AddReminderActivity extends Activity implements OnClickListener {

	private Button addReminder;
	private EditText notes, date;
	private SQLiteDatabase database;
	private TimePicker timePicker;
	private String currentDate;
	private String setTime, category, type;
	private int rid;
	private long timeDiff = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_reminder);

		Bundle bundle = getIntent().getExtras();
		category = bundle.getString("category");
		type = bundle.getString("type");
		notes = (EditText) findViewById(R.id.editText2);
		date = (EditText) findViewById(R.id.editText1);
		Date dateCurrent = new Date();
		currentDate = new SimpleDateFormat("MM/dd/yyyy").format(dateCurrent).toString();
		date.setText(currentDate);
		timePicker = (TimePicker) findViewById(R.id.timePicker1);
		timePicker.setIs24HourView(true);
		addReminder = (Button) findViewById(R.id.button1);
		addReminder.setOnClickListener(this);
	}
	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.button1:
			String dateEntered = date.getText().toString();
			setTime = dateEntered + " " + timePicker.getCurrentHour() + ":"+ timePicker.getCurrentMinute() + ":" + "00";
			Date dateCurrent = new Date();
			currentDate = new SimpleDateFormat("MM/dd/yyyy").format(dateCurrent).toString();
			currentDate = currentDate + " " + dateCurrent.getHours() + ":"+ dateCurrent.getMinutes() + ":" + dateCurrent.getSeconds();
			SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

			Date d1 = null;
			Date d2 = null;

			try {
				d1 = format.parse(currentDate);
				d2 = format.parse(setTime);

				// in milliseconds
				timeDiff = d2.getTime() - d1.getTime();
			} 
			catch (Exception e) {
				e.printStackTrace();
			}

			if (timeDiff > 0) {
				try {
					database = openOrCreateDatabase("mySqliteDatabase.db",SQLiteDatabase.CREATE_IF_NECESSARY, null);
					String create = "CREATE TABLE IF NOT EXISTS reminders (id INTEGER PRIMARY KEY AUTOINCREMENT,"
							+ "time TEXT, note TEXT, category TEXT)";
					database.execSQL(create);

					String insert = "INSERT INTO reminders(time, note, category) VALUES("+ "'"+ setTime+ "'"+ ","	+ "'"+ notes.getText().toString()+ "'"+ ",'"+ category + "'" + ")";
					// //System.out.println(insert);
					database.execSQL(insert);
					Intent intent = new Intent(this, NotificationService.class);
					String select = "SELECT MAX(id) FROM reminders";
					Cursor c = database.rawQuery(select, null);
					if (c.moveToFirst()) {
						rid = c.getInt(0);
						intent.setData(Uri.parse("" + rid+":"+category+" ~ "+notes.getText().toString()));
					}
					AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
					PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, 0);
					alarmManager.set(AlarmManager.RTC_WAKEUP,System.currentTimeMillis() + timeDiff,pendingIntent);
					Toast.makeText(this,"Alarm set in " + timeDiff / 1000 + " seconds",Toast.LENGTH_LONG).show();

				} 
				catch (Exception e) {
					e.printStackTrace();
				}
				Intent j = new Intent(this, ShowReminders.class);
				startActivity(j);
				break;
			}
			else {
				Toast.makeText(this,"Invalid date or time",Toast.LENGTH_LONG).show();
			}
		}
	}
}
