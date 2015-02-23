package org.gdocument.gchattoomuch.ihm.manager;

import java.util.ArrayList;

import org.gdocument.gtracergps.launcher.log.Logger;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class SmsManager {

	private static String TAG = SmsManager.class.getName();

	private static SmsManager instance = null;
	private android.telephony.SmsManager smsManager;

	private SmsManager() {
		smsManager = android.telephony.SmsManager.getDefault();
	}

	public static SmsManager getInstance() {
		if (instance == null) {
			instance = new SmsManager();
		}
		return instance;
	}

	public void send(Context context, String phonenumber, String message) {
		try {
			logMe(message);

			ArrayList<String> divideMessage = smsManager.divideMessage(message);
			logMe("send message:" + divideMessage);
			ArrayList<PendingIntent> listOfIntents = new ArrayList<PendingIntent>();
			for (int i = 0; i < divideMessage.size(); i++) {
				int id = (int) System.currentTimeMillis();//0;
				PendingIntent pi = PendingIntent.getBroadcast(context, id, new Intent(), 0);
				listOfIntents.add(pi);
			}
			smsManager.sendMultipartTextMessage(phonenumber, null, divideMessage, listOfIntents, null);
		} catch (RuntimeException ex) {
			ex.printStackTrace();
		}
	}
	
	private void logMe(String message) {
		Logger.logMe(TAG, message);
	}
}