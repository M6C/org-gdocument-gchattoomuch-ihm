package org.gdocument.gchattoomuch.p2p.activity.listener;

import org.gdocument.gchattoomuch.ihm.R;
import org.gdocument.gchattoomuch.p2p.task.WifiConnectionToUploadTask;
import org.gdocument.gchattoomuch.p2p.task.interfaces.IProcessNotification;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.AsyncTask;
import android.widget.Button;

import com.cameleon.common.android.inotifier.INotifierMessage;

public class OnClickConnectionToUploadListener implements OnClickListener {
	private AsyncTask<Void, Void, Void> task = null;

	private Context context = null;
	private INotifierMessage notifier;
	private int timeOut;
	private Button button;

	public OnClickConnectionToUploadListener(Context context, INotifierMessage notifier, int timeOut, Button btnUpload) {
		this.context = context;
		this.notifier = notifier;
		this.timeOut = timeOut;
		this.button = btnUpload;
	}

	public void onClick(DialogInterface dialog, int which) {
		if (task == null) {
			button.setText(context.getString(R.string.btn_text_stop_upload_server));
			task = createTask(button, timeOut).execute();
		} else {
			button.setText(context.getString(R.string.btn_text_start_upload_server));
			task.cancel(true);
			task = null;
		}
	}

	public AsyncTask<Void, Void, Void> getTask() {
		return task;
	}

	private WifiConnectionToUploadTask createTask(final Button btnUpload, final int timeOut) {
		WifiConnectionToUploadTask ret = new WifiConnectionToUploadTask(context, notifier, timeOut) {
			protected void onPostExecute(Void v) {
				super.onPostExecute(v);
				btnUpload.setText(context.getString(R.string.btn_text_start_server));
				if (timeOut == 0) {
					notifier.notifyMessage("restart server");
					task = createTask(btnUpload, timeOut);
				} else {
					task = null;
				}
			};
		};
		if (timeOut == 0) {
			ret.setProcessNotification(new IProcessNotification() {
				@Override
				public void onStart() {}
				
				@Override
				public void onFinish() {
					createTask(btnUpload, timeOut).execute();
				}
				
				@Override
				public void onCreate() {}
			});
		}
		return ret;
	}
}