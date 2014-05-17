package com.example.schoolnear;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;


public class Utility {
	public double lat=0,lng=0;
	
	public void getGeoCoder(Context con, String locationName){
		
		Geocoder gc = new Geocoder(con);
		List<Address> list;
		try {
			list = gc.getFromLocationName(locationName, 1);
			Address home = list.get(0);
			System.out.println("inside geocoder");
			System.out.println("rohit "+home.getLatitude());
			System.out.println("rohit "+home.getLongitude());
			lat = home.getLatitude();
			lng = home.getLongitude();
		} catch (IOException e) {
			System.out.println("Exception in Geocoder");
		//	Toast.makeText(null, "Enter valid address", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
		
	}
}
