package com.example.schoolnear;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;

public class NotificationService extends IntentService {

	public NotificationService() {
		super("rohit");

	}

	@Override
	protected void onHandleIntent(Intent intent) {
		NotificationManager mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		Notification notification = new Notification(R.drawable.ic_launcher,
				"Notify Alarm strart", System.currentTimeMillis());
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		String s = intent.getDataString();
		
		
		
		// int s = intent.getExtras().getInt("id");
		System.out.println("notificationnnnnnn " + s);
		String sArray[] = s.split(":");
		if(sArray.length>2){
			//call maps to display
			System.out.println("SHOW ON MAP");
			Intent myIntent = new Intent(this, ShowOnMap.class);
			myIntent.setData(Uri.parse(s));
			PendingIntent contentIntent = PendingIntent.getActivity(this, 0,myIntent, 0);
			notification.setLatestEventInfo(this, "Location Based Reminder", sArray[1],
					contentIntent);
			mNM.notify(2, notification);
		}
		else{
			//display remonder
			//sArray = sArray[1].split("~");
			String q[] = sArray[1].split("~");
			System.out.println("0 "+q[0]);
			System.out.println("1 "+q[1]);
			System.out.println("TIME BASED "+sArray[0]);
			if (q[0].trim().equalsIgnoreCase("Send SMS")) {
				System.out.println("INSIDE sms");
				/*  Intent smsIntent = new Intent(Intent.ACTION_SEND);
					 smsIntent.addCategory(Intent.CATEGORY_DEFAULT);
					 smsIntent.setType("vnd.android-dir/mms-sms"); 
					*/
					
				Uri uri = Uri.parse("smsto:");   
				Intent smsIntent = new Intent(Intent.ACTION_SENDTO, uri);   
				smsIntent.putExtra("sms_body", "");   
			
				
				
				PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
								smsIntent, 0);
						notification.setLatestEventInfo(this, "Time Based Reminder", q[0]+"-"+q[1],
								contentIntent);
						mNM.notify(2, notification);
					
			} else if (q[0].trim().equalsIgnoreCase("Send Email")) {
				  final Intent emailIntent = new
						  Intent(android.content.Intent.ACTION_SEND);
						  
					//	  Fill it with Data
						  emailIntent.setType("plain/text"); //
					
					
				//		  emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Text");
						  
						//  Send it off to the Activity-Chooser
					/*	  getApplicationContext().startActivity(Intent.createChooser(emailIntent,
						  "Send mail..."));*/
						  
						  PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
									emailIntent, 0);
							notification.setLatestEventInfo(this, "Time Based Reminder", q[0]+"-"+q[1],
									contentIntent);
							mNM.notify(2, notification);
					
			} else if (q[0].trim().equalsIgnoreCase("Call Someone")) {
				System.out.println("inside  call");
				/* Intent Eintent= new Intent(Intent.ACTION_PICK,  Contacts.CONTENT_URI);
				 Eintent.addCategory(Intent.CATEGORY_DEFAULT);
				 Eintent.setType("vnd.android-dir/mms-sms"); 
					*/
				
				 Intent z = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"));
	         //       startActivity(intent);
				 

				       // startActivityForResult(intent, PICK_CONTACT);

					 
					 PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
								z, 0);
						notification.setLatestEventInfo(this, "TimeBasedReminder", q[0]+"-"+q[1],
								contentIntent);
						mNM.notify(2, notification);
					
			} 
			
			else {
			
			Intent myIntent = new Intent(this, DisplayReminder.class);
			myIntent.setData(Uri.parse(s));
			PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
					myIntent, 0);
			notification.setLatestEventInfo(this, "TimeBasedReminder", q[0]+"-"+q[1],
					contentIntent);
			mNM.notify(2, notification);
		}
		}
		// int s1 = Integer.parseInt(s);
		// System.out.println("parsing "+s1);
		
	
	}

}
