package com.example.schoolnear;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


public class ShowOnMap extends Activity implements LocationListener,OnClickListener {
	private GoogleMap map;
	private CameraPosition cameraPosition;
	private MarkerOptions marker; 
	SQLiteDatabase database;
	final Context context = this;
	String[] array;
	LocationManager locationManager;
	Button delete,snooze;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_on_map);
		database = openOrCreateDatabase("mySqliteDatabase.db", SQLiteDatabase.CREATE_IF_NECESSARY, null);
		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		locationManager = (LocationManager) getSystemService(Service.LOCATION_SERVICE);
		//locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000*60, 0, this);
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,1000*60*5, 0, this);
		delete = (Button) findViewById(R.id.buttonDelete);
		snooze = (Button) findViewById(R.id.buttonSnooze);
		delete.setOnClickListener(this);
		snooze.setOnClickListener(this);
		String received  = getIntent().getDataString();
		System.out.println("received "+received);
		array = received.split(":");

		for(int i=2;i<array.length;i++){
			int pid = Integer.parseInt(array[i]);
			String select = "SELECT * FROM Place1 where id="+pid;
			Cursor c = database.rawQuery(select, new String[0]);
			if(c.moveToFirst()){
				double lLatitude = c.getDouble(1);
				double lLongitude = c.getDouble(2);
				String name = c.getString(3);
				String vicinity = c.getString(5);
				map.addMarker(new MarkerOptions().title(name).position(new LatLng(lLatitude,lLongitude)).snippet(vicinity));
				putMarkerOnMap(lLatitude, lLongitude);
			}

		}


	}

	/*private void removeReminder() {
		System.out.println("INSIDE");
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
		alertDialogBuilder.setTitle("Snooze or Dismiss?");
		alertDialogBuilder.setCancelable(false).setPositiveButton("Snooze", new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				//Do nothing
			}

		}
				).setNegativeButton("Dismiss", new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface dialog, int which) {
						//delete from reminder
						String table_name = "reminders";
						String where = "id = ?";
						String[] whereArgs = { array[0] };
						database.delete(table_name, where, whereArgs);
					}


				});

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();

	}*/

	private void putMarkerOnMap(double latitude,double longitude) {


		cameraPosition = new CameraPosition.Builder().target(new LatLng(latitude, longitude)).zoom(15).build();
		map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

		marker = new MarkerOptions().position(new LatLng(latitude, longitude));
		map.addMarker(marker);
		map.getUiSettings().setZoomControlsEnabled(true);
		map.getUiSettings().setCompassEnabled(true);
		map.getUiSettings().setRotateGesturesEnabled(true);

		//ASK 
	}

	@Override
	protected void onStop() {
		System.out.println("on Stop");
		super.onStop();
	}

	@Override
	public void finish() {
		super.finish();
		Intent intent = new Intent(this,MainActivity.class);
		intent.putExtra("EXIT", true);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(intent);
		
	}
	@Override
	protected void onPause() {
		System.out.println("on pause");
	super.onPause();
	}

	@Override
	public void onLocationChanged(Location arg0) {
		map.addMarker(new MarkerOptions().title("Your Poistion").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).position(new LatLng(arg0.getLatitude(),arg0.getLongitude())));
		//marker;
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
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.buttonDelete:
			String table_name = "reminders";
			String where = "id = ?";
			String[] whereArgs = { array[0] };
			database.delete(table_name, where, whereArgs);
			Intent i = new Intent(this,ShowReminders.class);
			startActivity(i);
			break;
		case R.id.buttonSnooze:
			Toast.makeText(this, "Reminder Snoozed", Toast.LENGTH_LONG).show();
			finish();
			break;
		}

	}
}
