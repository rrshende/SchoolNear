package com.example.schoolnear;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Context;
import android.os.AsyncTask;

public class HomeLocation extends AsyncTask<Void, Void, Void>{
	String youraddress = "4824 Grenville Square, Baltimore";
	double lat,lng;
	private Context context;
	
	StringBuilder stringBuilder;
	public HomeLocation(Context c) {
		this.context = c;
	}

	/*@Override
	protected void onPreExecute() {
		super.onPreExecute();
		dialog = new ProgressDialog(context);
		dialog.setCancelable(false);
		dialog.setMessage("Loading.......");
		dialog.isIndeterminate();
		dialog.show();
	}*/

	@Override
	protected Void doInBackground(Void... params) {
		try {
			String uri = "https://maps.googleapis.com/maps/api/geocode/json?address=4824 Grenville Square, Baltimore&sensor=false";
			HttpGet httpGet = new HttpGet(uri);
			HttpClient client = new DefaultHttpClient();
			HttpResponse response;
			stringBuilder = new StringBuilder();
			response = client.execute(httpGet);
			HttpEntity entity = response.getEntity();
			InputStream stream = entity.getContent();
			int b;
			while ((b = stream.read()) != -1) {
				stringBuilder.append((char) b);
			}
		} catch (ClientProtocolException e) {
			//System.out.println("ClientProtocolException"+e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			//System.out.println("IOException"+e.getMessage());
			e.printStackTrace();
		}

		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject = new JSONObject(stringBuilder.toString());

			lng = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
					.getJSONObject("geometry").getJSONObject("location")
					.getDouble("lng");

			lat = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
					.getJSONObject("geometry").getJSONObject("location")
					.getDouble("lat");

			//System.out.println("from "+lat);
			//System.out.println("from "+lng);
		} catch (JSONException e) {
			//System.out.println("IOException"+e.getMessage());
			e.printStackTrace();
		}
		return null;
	}


	public void getLoc(){
		try {
			//System.out.println("Inside get loc");
			//String uri = "http://maps.google.com/maps/api/geocode/json?address=" +
					//youraddress + "&sensor=false";
			String uri = "https://maps.googleapis.com/maps/api/geocode/json?address=" +
					youraddress + "&sensor=false";
			HttpGet httpGet = new HttpGet(uri);
			HttpClient client = new DefaultHttpClient();
			HttpResponse response;
			stringBuilder = new StringBuilder();
			response = client.execute(httpGet);
			HttpEntity entity = response.getEntity();
			InputStream stream = entity.getContent();
			int b;
			while ((b = stream.read()) != -1) {
				stringBuilder.append((char) b);
			}
		} catch (ClientProtocolException e) {
			//System.out.println("ClientProtocolException"+e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			//System.out.println("IOException"+e.getMessage());
			e.printStackTrace();
		}

		JSONObject jsonObject = new JSONObject();
		try {
			//System.out.println("rohit1");
			jsonObject = new JSONObject(stringBuilder.toString());

			lng = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
					.getJSONObject("geometry").getJSONObject("location")
					.getDouble("lng");

			lat = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
					.getJSONObject("geometry").getJSONObject("location")
					.getDouble("lat");

			//System.out.println("from "+lat);
			//System.out.println("from "+lng);
		} catch (JSONException e) {
			//System.out.println("IOException"+e.getMessage());
			e.printStackTrace();
		}
		//return null;
	}
}

