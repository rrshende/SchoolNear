package com.example.schoolnear;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.drive.internal.u;

/*import android.util.Log;*/


public class PlacesService {
	private String API_KEY;

	public PlacesService(String apikey) {
		this.API_KEY = apikey;
	}

	public void setApiKey(String apikey) {
		this.API_KEY = apikey;
	}

	public ArrayList<Place> findPlaces(double latitude, double longitude,
			String placeSpacification, String radius) {

		String urlString = makeUrl(latitude, longitude, placeSpacification, radius);

		try {
			String json = getJSON(urlString);

			//////System.out.println(json);
			JSONObject object = new JSONObject(json);
			JSONArray array = object.getJSONArray("results");
			ArrayList<Place> arrayList = new ArrayList<Place>();
			for (int i = 0; i < array.length(); i++) {
				try {
					Place place = Place.jsonToPontoReferencia((JSONObject) array.get(i),placeSpacification);
					//Log.v("Places Services ", "" + place);
					arrayList.add(place);
				} catch (Exception e) {
				}
			}
			//
			for(int j=0;j<2;j++){
				String pageToken = (String) object.get("next_page_token");
				urlString = makeUrl(pageToken);
				object = new JSONObject(json);
				array = object.getJSONArray("results");
				for (int i = 0; i < array.length(); i++) {
					try {
						Place place = Place.jsonToPontoReferencia((JSONObject) array.get(i),placeSpacification);
						//Log.v("Places Services ", "" + place);
						arrayList.add(place);
					} catch (Exception e) {
					}
				}
			}
			//System.out.println("SIZE of list is now "+arrayList.size());
			return arrayList;
		} catch (JSONException ex) {
			Logger.getLogger(PlacesService.class.getName()).log(Level.SEVERE,
					null, ex);
		}
		return null;
	}

	private String makeUrl(String pageToken) {
		StringBuilder urlString = new StringBuilder("https://maps.googleapis.com/maps/api/place/search/json?");
		urlString.append("pagetoken=");
		urlString.append(pageToken);
		urlString.append("&sensor=false&key=");
		urlString.append(API_KEY);
		return urlString.toString();
	}

	// https://maps.googleapis.com/maps/api/place/search/json?location=28.632808,77.218276&radius=500&types=atm&sensor=false&key=apikey
	private String makeUrl(double latitude, double longitude, String place,String radius) {
		//System.out.println("INSIDE PLACE SERVICE"+radius);
		StringBuilder urlString = new StringBuilder(
				"https://maps.googleapis.com/maps/api/place/search/json?");
		if (place.equals("")) {
			urlString.append("&location=");
			urlString.append(Double.toString(latitude));
			urlString.append(",");
			urlString.append(Double.toString(longitude));
			//urlString.append("&radius="+radius);
			//urlString.append("&radius=10000");
			urlString.append("&rankby=distance");
			urlString.append("&sensor=false&key=" + API_KEY);
			//System.out.println("U"+urlString);
		} 
		else {
			urlString.append("&location=");
			urlString.append(Double.toString(latitude));
			urlString.append(",");
			urlString.append(Double.toString(longitude));
			//urlString.append("&radius="+radius);
			//urlString.append("&radius=10000");
			urlString.append("&rankby=distance");
			urlString.append("&types=" + place);
			urlString.append("&sensor=false&key=" + API_KEY);
			//System.out.println("U"+urlString);
		}
		////System.out.println(urlString);

		return urlString.toString();
	}

	protected String getJSON(String url) {
		return getUrlContents(url);
	}

	private String getUrlContents(String theUrl) {
		StringBuilder content = new StringBuilder();

		try {
			URL url = new URL(theUrl);
			URLConnection urlConnection = url.openConnection();
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(urlConnection.getInputStream()), 8);
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				content.append(line + "\n");
			}
			bufferedReader.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
		////System.out.println("From GP");
		////System.out.println("GP "+content);
		return content.toString();
	}
}
