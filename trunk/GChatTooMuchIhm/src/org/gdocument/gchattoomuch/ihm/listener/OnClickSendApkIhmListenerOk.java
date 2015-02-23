package org.gdocument.gchattoomuch.ihm.listener;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.View.OnClickListener;

import com.cameleon.common.tool.ApkTool;

public class OnClickSendApkIhmListenerOk implements OnClickListener {

	private Activity context;

	public OnClickSendApkIhmListenerOk(Activity context) {
		this.context = context;
	}

	public void onClick(View v) {
		try {
			String sourceDir = ApkTool.getInstance(context).querySourceDir(context.getPackageName());
			if (sourceDir != null) {
				Intent intent = new Intent();
				intent.setAction(Intent.ACTION_SEND);
				intent.setType("application/octet-stream");
				intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(sourceDir)));
				context.startActivity(intent);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}