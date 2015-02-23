package org.gdocument.gchattoomuch.ihm.listener;

import org.gdocument.gchattoomuch.lib.constant.ConstantsService;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;

public class OnClickSendApkListenerOk implements OnClickListener {

	private Activity context;

	public OnClickSendApkListenerOk(Activity context) {
		this.context = context;
	}

	public void onClick(View v) {
		Intent intent = new Intent(ConstantsService.SERVICE_INTENT_FILTER_ACTION_SEND_APK);
		intent.putExtra("SEND_APK", true);
    	context.startActivity(intent);
	}
}