package org.gdocument.gchattoomuch.ihm.listener;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;

public class OnClickSendApkListenerOk implements OnClickListener {

	private Activity context;
	private String action;

	public OnClickSendApkListenerOk(Activity context, String action) {
		this.context = context;
		this.action = action;
	}

	public void onClick(View v) {
		Intent intent = new Intent(action);
		intent.putExtra("SEND_APK", true);
    	context.startActivity(intent);
	}
}