package com.example.schoolnear;

import java.util.ArrayList;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;

public class LocationService extends Service implements LocationListener {
	LocationManager locationManager;
	SQLiteDatabase database;
	double latitude,longitude;
	String category="";
	private Handler myhandler = new Handler();
	String sPID = "";

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		System.out.println("HERE IN LS");
		locationManager = (LocationManager) getSystemService(Service.LOCATION_SERVICE);
		//locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000*60, 0, this);
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,1000*60, 0, this);
		//set this value
		Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		latitude= location.getLatitude();
		longitude = location.getLongitude();
		database = openOrCreateDatabase("mySqliteDatabase.db", SQLiteDatabase.CREATE_IF_NECESSARY, null);
		return super.onStartCommand(intent, flags, startId);
	}

	private void findDistance(int rId) {

		ArrayList<Double> listLat = new ArrayList<Double>();
		ArrayList<Double> listLng = new ArrayList<Double>();
		ArrayList<String> listPlaceId = new ArrayList<String>();

		String select = "SELECT * FROM Place1 where category='"+category+"'";
		System.out.println("Query is "+select);
		Cursor c = database.rawQuery(select, new String[0]);
		System.out.println("Count is "+c.getCount());
		while(c.moveToNext()){
			listPlaceId.add(c.getString(0));
			listLat.add(c.getDouble(1));
			listLng.add(c.getDouble(2));

		}
		sPID = Integer.toString(rId)+':'+category;
		System.out.println("Reminder ID: "+sPID);
		for(int i=0;i<listLat.size();i++){
			double theta = listLng.get(i) - longitude;
			double dist = Math.sin(Math.toRadians(listLat.get(i))) * Math.sin(Math.toRadians(latitude))
					+	  Math.cos(Math.toRadians(listLat.get(i))) * Math.cos(Math.toRadians(latitude)) * Math.cos(Math.toRadians(theta));
			dist = Math.acos(dist); 
			dist = Math.toDegrees(dist); 
			double miles = dist * 60 * 1.1515*1.609344*1000;
			if(miles<2500){
				System.out.println(i+": "+listPlaceId.get(i));

				sPID = sPID + ':' + listPlaceId.get(i);

			}


		}
		System.out.println("Final SPID"+sPID);
		String a[] = sPID.split(":");
		if(a.length>2){
			Intent intent = new Intent(this, NotificationService.class);
			intent.setData(Uri.parse(""+sPID));
			AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
			PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, 0);
			alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pendingIntent);
		}
	}


	private class LocationWork implements Runnable{
		Location e;
		public LocationWork(Location arg) {
			e = arg;
		}
		@Override
		public void run() {

			latitude = e.getLatitude();
			longitude = e.getLongitude();
			System.out.println("Lat long from Service when it is changed" + latitude +" "+ longitude);
			String locationReminderCategory = "SELECT id,category FROM reminders where time = ''";
			Cursor c = database.rawQuery(locationReminderCategory, null);

			if(c.getCount()==0){
				System.out.println("No location reminder set hence service can be stopped");
				//find logic to do that
			}
			else{
				//get category from cursor
				while(c.moveToNext()){
					category = c.getString(1);
					System.out.println("Category from reminder"+category);
					findDistance(c.getInt(0));
				}

			}
		}


	}


	@Override
	public void onLocationChanged(Location arg0) {
		//IF you are away from home location then only check around you
		/*double theta = listLng.get(i) - longitude;
		double dist = Math.sin(Math.toRadians(listLat.get(i))) * Math.sin(Math.toRadians(latitude))
				+	  Math.cos(Math.toRadians(listLat.get(i))) * Math.cos(Math.toRadians(latitude)) * Math.cos(Math.toRadians(theta));
		dist = Math.acos(dist); 
		dist = Math.toDegrees(dist); 
		double miles = dist * 60 * 1.1515*1.609344*1000;*/
		System.out.println("In location change");
		LocationWork task = new LocationWork(arg0);
		myhandler.post(task);
	}


	@Override
	public void onProviderDisabled(String arg0) {


	}


	@Override
	public void onProviderEnabled(String arg0) {


	}


	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {


	}


	@Override
	public IBinder onBind(Intent arg0) {

		System.out.println("inside binder");


		return null;
	}








}

