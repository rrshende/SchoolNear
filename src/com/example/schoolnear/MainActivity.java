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
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity implements LocationListener,
OnClickListener {

	boolean isGPSEnabled = false;

	private double latitude, longitude;
	SQLiteDatabase database;

	LocationManager locationManager;
	Button timebased, locationBased, showall;
	final Context context = this;
	String address;
	String radius;
	//boolean flagForRadius = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {

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
			String create = "CREATE TABLE IF NOT EXISTS Place1(id INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ "latitude TEXT, longitude TEXT, name TEXT, category TEXT, vicinity TEXT)";
			database.execSQL(create);

			locationManager = (LocationManager) getSystemService(Service.LOCATION_SERVICE);
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000 * 3, 0, this);
				//	locationManager.requestLocationUpdates(	LocationManager.NETWORK_PROVIDER, 1000 * 3, 0, this);

			Location location = locationManager
					.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			latitude = location.getLatitude();
			longitude = location.getLongitude();
			System.out.println("LAST KNOWN " + latitude + "lon " + longitude);

			// HIT GP
			// hitGooglePlaces();
			String a[] = { "grocery_or_supermarket", "bank", "atm",
					"gas_station", "pharmacy" };

			/*
			 * 
			 * pharmacy "pharmacy", "store", "health", "establishment" ]
			 * "dentist", "doctor", "health", "establishment" ] hospital [
			 * "night_club", "bar", "gym", "health", "spa", "establishment" ] [
			 * "restaurant", "food", "health", "establishment" ],
			 * "real_estate_agency", "jewelry_store", "store", "health",
			 * "establishment"
			 */

			SharedPreferences wmbPreference = PreferenceManager
					.getDefaultSharedPreferences(this);
			boolean isFirstRun = wmbPreference.getBoolean("FIRSTRUN", true);
			if (isFirstRun) {
				onClickAlert();
				// Code to run once

				// Get Home Location
				// new HomeLocation(this).getLoc();
				// new HomeLocation(MainActivity.this).execute();
				// Hit Google Places API
				/*while(!flagForRadius){
					System.out.println("SO");
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
			Toast.makeText(getApplicationContext(), "Enable GPS",
					Toast.LENGTH_LONG).show();
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
				// System.out.println("First check: "+category);
				// map.addMarker(new MarkerOptions().title(name).position(new
				// LatLng(lLatitude,lLongitude)).snippet(vicinity));

				// Store each Place in DB
				String insert = "INSERT INTO Place1(latitude, longitude, name, category, vicinity) VALUES("
						+ lLatitude
						+ ","
						+ lLongitude
						+ ","
						+ "'"
						+ name
						+ "'"
						+ ","
						+ "'"
						+ category
						+ "'"
						+ ","
						+ "'"
						+ vicinity
						+ "'" + ")";
				// System.out.println(insert);
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
			System.out.println("CH" + latitude);
			ArrayList<Place> findPlaces = service.findPlaces(latitude,
					longitude, places, radius);
			// Log.d("ROhit", "lat long is "+latitude+"	"+longitude);
			/*
			 * for (int i = 0; i < findPlaces.size(); i++) {
			 * 
			 * Place placeDetail = findPlaces.get(i); Log.e("Rohit", "places : "
			 * + placeDetail.getName()); }
			 */
			return findPlaces;
		}

	}

	@Override
	public void onLocationChanged(Location l) {

		/*
		 * LocationWork task = new LocationWork(l); myhandler.post(task);
		 * 
		 * Log.d("Location ", "on creatre reading"+System.currentTimeMillis());
		 */

	}

	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.button1:
			System.out.println("TIME");
			Intent j = new Intent(this, TimeBasedActivity.class);
			startActivity(j);

			break;
		case R.id.button2:
			System.out.println("LOCATION");
			Intent i = new Intent(this, LocationBasedActivity.class);
			startActivity(i);







			break;

		case R.id.button3:
			System.out.println("LOCATION");
			Intent k = new Intent(this, ShowReminders.class);
			startActivity(k);


		}

	}

	public void onClickAlert() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				context);

		// set title
		alertDialogBuilder.setTitle("Is this your HOME location");
		final EditText input = new EditText(this);
		// set dialog message
		alertDialogBuilder.setCancelable(false).setPositiveButton("Yes",new DialogInterface.OnClickListener(){

			public void onClick(DialogInterface dialog, int id) {/*
				AlertDialog.Builder radiusDialogBuilder = new AlertDialog.Builder(
						context);
				radiusDialogBuilder
				.setTitle("Enter the range in which you will like to locate palces.");
				radiusDialogBuilder.setView(input);

				radiusDialogBuilder.setPositiveButton("Save",
						new DialogInterface.OnClickListener() {

					@Override
					public void onClick(
							DialogInterface dialog,
							int which) {
						radius = input.getText()
								.toString();
						System.out.println("value "
								+ radius);
						// Save current GPS location and
						// radius
						getLocationFromGPS();
					}
				});

				AlertDialog r = radiusDialogBuilder.create();
				r.show();

			 */
				getLocationFromGPS();
			}
		})
		.setNegativeButton("No", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				AlertDialog.Builder addressDialogBuilder = new AlertDialog.Builder(
						context);
				addressDialogBuilder
				.setTitle("Enter your HOME address");
				addressDialogBuilder.setView(input);

				addressDialogBuilder.setPositiveButton("Save",
						new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog,int which) {
						address = input.getText().toString();
						System.out.println("value " + address);
						// Call Geocoder
						Utility u = new Utility();
						u.getGeoCoder(context, address);
						latitude = u.lat;
						longitude = u.lng;
						///////////////////////////



						/*AlertDialog.Builder radiusDialogBuilder = new AlertDialog.Builder(context);
						radiusDialogBuilder.setTitle("Enter the range in which you will like to locate palces.");
						radiusDialogBuilder.setView(input);

						radiusDialogBuilder.setPositiveButton("Save",new DialogInterface.OnClickListener() {

							@Override
							public void onClick(
									DialogInterface dialog,	int which) {
								radius = input.getText().toString();
								System.out.println("value "+ radius);
								// Save current GPS location and
								// radius

							}
						});

						AlertDialog r = radiusDialogBuilder.create();
						r.show();
*/
					}




					///////////////////////////////
				}
						);

				AlertDialog r = addressDialogBuilder.create();
				r.show();

			}
		});

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();

		// INSERT CURRENT LOCATION INTO DB
		String name = "HOME";
		String category = "HOME";
		String vicinity = "HOME";

		String insert = "INSERT INTO Place1(latitude, longitude, name, category, vicinity) VALUES("
				+ latitude
				+ ","
				+ longitude
				+ ","
				+ "'"
				+ name
				+ "'"
				+ ","
				+ "'" + category + "'" + "," + "'" + vicinity + "'" + ")";
		// System.out.println(insert);
		database.execSQL(insert);
		//flagForRadius = true;
	}

	public void getLocationFromGPS() {
		locationManager = (LocationManager) getSystemService(Service.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,	1000 * 60 * 3, 0, this);
				//locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,		1000 * 60 * 3, 0, this);

		Location location = locationManager
				.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		latitude = location.getLatitude();
		longitude = location.getLongitude();
		System.out.println("Inside getLocationFromGPS");
		System.out.println(latitude);
		System.out.println(longitude);
	}

}
