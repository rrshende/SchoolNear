package com.example.schoolnear;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity implements LocationListener,OnClickListener {
	private final Context context = this;
	private double latitude, longitude;
	private SQLiteDatabase database;
	private LocationManager locationManager;
	private Button timebased, locationBased, showall;
	private String address;
	private String radius;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		boolean isFirstRun;
		SharedPreferences wmbPreference = PreferenceManager.getDefaultSharedPreferences(this);
		if (getIntent().getBooleanExtra("EXIT", false)) {
			finish();
		}
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		timebased = (Button) findViewById(R.id.button1);
		locationBased = (Button) findViewById(R.id.button2);
		showall = (Button) findViewById(R.id.button3);
		timebased.setOnClickListener(this);
		locationBased.setOnClickListener(this);
		showall.setOnClickListener(this);

		try {

			database = openOrCreateDatabase("mySqliteDatabase.db",
					SQLiteDatabase.CREATE_IF_NECESSARY, null);
			String create = "CREATE TABLE IF NOT EXISTS Places(id INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ "latitude TEXT, longitude TEXT, name TEXT, category TEXT, vicinity TEXT)";
			database.execSQL(create);

			locationManager = (LocationManager) getSystemService(Service.LOCATION_SERVICE);
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000 * 3, 0, this);
			//	locationManager.requestLocationUpdates(	LocationManager.NETWORK_PROVIDER, 1000 * 3, 0, this);

			Location location = locationManager
					.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			latitude = location.getLatitude();
			longitude = location.getLongitude();
			//System.out.println("LAST KNOWN " + latitude + "lon " + longitude);

			// HIT GP
			// hitGooglePlaces();
			String a[] = { "grocery_or_supermarket", "bank", "atm","gas_station", "pharmacy"};

			/*
			 * 
			 *  "store", "health", "establishment" ]
			 * "dentist", "doctor", "health", "establishment" ] hospital [
			 * "night_club", "bar", "gym", "health", "spa", "establishment" ] [
			 * "restaurant", "food", "health", "establishment" ],
			 * "real_estate_agency", "jewelry_store", "store", "health",
			 * "establishment"
			 */
			isFirstRun = wmbPreference.getBoolean("FIRSTRUN", true);
			if (isFirstRun) {
				onClickAlert("Home");
				// Code to run once

				// Get Home Location
				// new HomeLocation(this).getLoc();
				// new HomeLocation(MainActivity.this).execute();
				// Hit Google Places API
				/*while(!flagForRadius){
					//System.out.println("SO");
					if (flagForRadius) {
						break;
					}
				}*/
				for (int i = 0; i < a.length; i++) {
					new GetPlaces(MainActivity.this, a[i]).execute();
				}

				SharedPreferences.Editor editor = wmbPreference.edit();
				editor.putBoolean("FIRSTRUN", false);
				editor.commit();
			}

			// findDistance();

		} catch (Exception e) {
			Toast.makeText(getApplicationContext(), "Enable GPS or Check your internet connection",Toast.LENGTH_LONG).show();
			isFirstRun = wmbPreference.getBoolean("FIRSTRUN", false);

		}

	}

	private class GetPlaces extends AsyncTask<Void, Void, ArrayList<Place>> {

		private ProgressDialog dialog;
		private Context context;
		private String places;

		public GetPlaces(Context context, String places) {
			this.context = context;
			this.places = places;
		}

		@Override
		protected void onPostExecute(ArrayList<Place> result) {
			String vicinity = "";
			String category = "";
			String name = "";
			double lLatitude = 0, lLongitude = 0;

			super.onPostExecute(result);
			if (dialog.isShowing()) {
				dialog.dismiss();
			}
			for (int i = 0; i < result.size(); i++) {
				name = result.get(i).getName();
				name = name.replace('\'', ' ');
				lLatitude = result.get(i).getLatitude();
				lLongitude = result.get(i).getLongitude();
				vicinity = result.get(i).getVicinity().replace('\'', ' ');

				category = result.get(i).getCategory().replace('\'', ' ');

				// Store each Place in DB
				String insert = "INSERT INTO Places(latitude, longitude, name, category, vicinity) VALUES("
						+ lLatitude+ ","+ lLongitude+ ","+ "'"+ name+ "'"+ ","
						+ "'"
						+ category
						+ "'"
						+ ","
						+ "'"
						+ vicinity
						+ "'" + ")";
				database.execSQL(insert);
			}
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog = new ProgressDialog(context);
			dialog.setCancelable(false);
			dialog.setMessage("Loading...");
			dialog.isIndeterminate();
			dialog.show();
		}

		@Override
		protected ArrayList<Place> doInBackground(Void... arg0) {
			PlacesService service = new PlacesService(
					"AIzaSyDq8bjfHxORQaZIRSV7Re18Xmbz3KFqWyQ");
			//System.out.println("CH" + latitude);
			ArrayList<Place> findPlaces = service.findPlaces(latitude,
					longitude, places, radius);
			return findPlaces;
		}

	}

	@Override
	public void onLocationChanged(Location l) {

	}

	@Override
	public void onProviderDisabled(String arg0) {

	}

	@Override
	public void onProviderEnabled(String arg0) {


	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button1:
			Intent j = new Intent(this, TimeBasedActivity.class);
			startActivity(j);
			break;

		case R.id.button2:
			Intent i = new Intent(this, LocationBasedActivity.class);
			startActivity(i);
			break;

		case R.id.button3:
			Intent k = new Intent(this, ShowReminders.class);
			startActivity(k);
		}

	}

	public void onClickAlert(String place) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
		/*	SET TITLE	*/
		alertDialogBuilder.setTitle("Are you at "+place+"?");
		final EditText input = new EditText(this);
		/*	SET DIALOGUE MESSAGE	*/
		alertDialogBuilder.setCancelable(false).setPositiveButton("Yes",new DialogInterface.OnClickListener(){

			public void onClick(DialogInterface dialog, int id) {getLocationFromGPS();
			}
		})
		.setNegativeButton("No", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				AlertDialog.Builder addressDialogBuilder = new AlertDialog.Builder(context);
				addressDialogBuilder.setTitle("Then reter your address");
				addressDialogBuilder.setView(input);

				addressDialogBuilder.setPositiveButton("Save",new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog,int which) {
						address = input.getText().toString();
						/*	CALLL GEOCODER	*/
						Utility u = new Utility();
						u.getGeoCoder(context, address);
						latitude = u.lat;
						longitude = u.lng;
					}
				}
						);

				AlertDialog r = addressDialogBuilder.create();
				r.show();

			}
		});

		/*	CREATE ALERT	*/
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();

		/*	INSERT CURRENT LOCATION INTO DB		*/
		String name = place;
		String category = place;
		String vicinity = place;

		String select  ="SELECT * FROM Places WHERE name ='"+name+"'"; 
		Cursor c = database.rawQuery(select, new String[0]);
		if(c.moveToFirst()){
			System.out.println("Inside update");
			String update = "UPDATE Places SET latitude = "+latitude+", longitude="+longitude+" WHERE name ='"+name+"'";
			System.out.println(update);
			database.execSQL(update);
		}
		else{
			//System.out.println("insert");
			String insert = "INSERT INTO Places(latitude, longitude, name, category, vicinity) VALUES("
					+ latitude+ ","+ longitude+ ","+ "'"+ name+ "'"+ ","+ "'" + category + "'" + "," + "'" + vicinity + "'" + ")";
			database.execSQL(insert);
		}
	}

	public void getLocationFromGPS() {
		locationManager = (LocationManager) getSystemService(Service.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,	1000 * 60 * 3, 0, this);
		//locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,		1000 * 60 * 3, 0, this);

		Location location = locationManager
				.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		latitude = location.getLatitude();
		longitude = location.getLongitude();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.layout.menu, menu);
		return true;
	}

	/**
	 * Event Handling for Individual menu item selected
	 * Identify single menu item by it's id
	 * */
	/*@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{

		switch (item.getItemId())
		{
		case R.id.menu_show_reminders:
			Intent j = new Intent(this, ShowReminders.class);
			startActivity(j);
			return true;
		case R.id.menu_Home_Location:
			onClickAlert("Home");
			Toast.makeText(MainActivity.this, "HOME Location Set", Toast.LENGTH_SHORT).show();
			return true;
		case R.id.menu_Office_Location:
			onClickAlert("Office");
			Toast.makeText(MainActivity.this, "OFFICE Location Set", Toast.LENGTH_SHORT).show();
			return true;
		case R.id.menu_Reminder_Frequency:
			Toast.makeText(MainActivity.this, "menu_Reminder_Frequency is Selected", Toast.LENGTH_SHORT).show();
			return true;
		case R.id.menu_Location_Range:
			Intent intent = new Intent(this, LocationService.class);
			intent.putExtra("NEARBY_LOCATION_RANGE", getRangeFromAlertDialogueBox());
			return true;
		case R.id.menu_preferences:
			Toast.makeText(MainActivity.this, "Preferences is Selected", Toast.LENGTH_SHORT).show();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private String getRangeFromAlertDialogueBox() {
		final EditText input = new EditText(this);
		AlertDialog.Builder radiusDialogBuilder = new AlertDialog.Builder(context);
		radiusDialogBuilder.setTitle("Enter the range in which you will like to locate palces.");
		radiusDialogBuilder.setView(input);

		radiusDialogBuilder.setPositiveButton("Save",new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog,	int which) {
				radius = input.getText().toString();
				System.out.println(radius);
			}
		});
		AlertDialog r = radiusDialogBuilder.create();
		r.show();
		System.out.println("Radius "+radius);
		return radius;
	}*/
	
	
	
}
