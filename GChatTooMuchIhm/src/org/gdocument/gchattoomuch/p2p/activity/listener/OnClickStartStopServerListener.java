package org.gdocument.gchattoomuch.p2p.activity.listener;

import org.gdocument.gchattoomuch.ihm.R;
import org.gdocument.gchattoomuch.p2p.common.P2PConstant;
import org.gdocument.gchattoomuch.p2p.task.WifiDatabaseDownloadTask;
import org.gdocument.gchattoomuch.p2p.task.WifiDatabaseDownloadTask.IProcessNotification;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.AsyncTask;
import android.widget.Button;
import android.widget.TextView;

import com.cameleon.common.android.inotifier.INotifierMessage;

public class OnClickStartStopServerListener implements OnClickListener {
	private AsyncTask<Void, Void, String> uploadTask = null;

	private Context context = null;
	private INotifierMessage notifier;
	private int timeOut;
	private TextView tvMessage;
	private Button btnUpload;

	public OnClickStartStopServerListener(Context context, INotifierMessage notifier, int timeOut, TextView tvMessage, Button btnUpload) {
		this.context = context;
		this.notifier = notifier;
		this.timeOut = timeOut;
		this.tvMessage = tvMessage;
		this.btnUpload = btnUpload;
	}

	public void onClick(DialogInterface dialog, int which) {
		if (uploadTask == null) {
			tvMessage.setText("");
			btnUpload.setText(context.getString(R.string.btn_text_stop_server));
			uploadTask = createWifiDatabaseDownloadTask(btnUpload, timeOut).execute();
		} else {
			btnUpload.setText(context.getString(R.string.btn_text_start_server));
			uploadTask.cancel(true);
			uploadTask = null;
		}
	}

	public AsyncTask<Void, Void, String> getTask() {
		return uploadTask;
	}

	private WifiDatabaseDownloadTask createWifiDatabaseDownloadTask(final Button btnUpload, final int timeOut) {
		WifiDatabaseDownloadTask ret = new WifiDatabaseDownloadTask(context, notifier, timeOut) {
			protected void onPostExecute(String result) {
				super.onPostExecute(result);
				btnUpload.setText(context.getString(R.string.btn_text_start_server));
				if (timeOut == 0) {
					notifier.notifyMessage("restart server");
					uploadTask = createWifiDatabaseDownloadTask(btnUpload, timeOut);
				} else {
					uploadTask = null;
				}
			};
		};
		if (timeOut == 0) {
			ret.setProcessNotification(new IProcessNotification() {
				@Override
				public void onStart() {}
				
				@Override
				public void onFinish() {
					createWifiDatabaseDownloadTask(btnUpload, timeOut).execute();
				}
				
				@Override
				public void onCreate() {}
			});
		}
		return ret;
	}
}