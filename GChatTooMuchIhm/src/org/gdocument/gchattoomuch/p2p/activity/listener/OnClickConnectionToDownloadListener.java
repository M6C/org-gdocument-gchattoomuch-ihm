package org.gdocument.gchattoomuch.p2p.activity.listener;

import org.gdocument.gchattoomuch.ihm.R;
import org.gdocument.gchattoomuch.p2p.task.WifiConnectionToDownloadTask;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.AsyncTask;
import android.widget.Button;

import com.cameleon.common.android.inotifier.INotifierMessage;

public class OnClickConnectionToDownloadListener implements OnClickListener {
	private AsyncTask<Void, Void, Void> task = null;

	private Context context = null;
	private INotifierMessage notifier;
	private Button button;

	public OnClickConnectionToDownloadListener(Context context, INotifierMessage notifier, Button button) {
		this.context = context;
		this.notifier = notifier;
		this.button = button;
	}

	public void onClick(DialogInterface dialog, int which) {
		if (task == null) {
			button.setText(context.getString(R.string.btn_text_stop_server));
			task = createTask(button).execute();
		} else {
			button.setText(context.getString(R.string.btn_text_start_server));
			task.cancel(true);
			task = null;
		}
	}

	public AsyncTask<Void, Void, Void> getTask() {
		return task;
	}

	private WifiConnectionToDownloadTask createTask(final Button btnUpload) {
		WifiConnectionToDownloadTask ret = new WifiConnectionToDownloadTask(context, notifier) {
			protected void onPostExecute(Void v) {
				super.onPostExecute(v);
				btnUpload.setText(context.getString(R.string.btn_text_start_server));
			};
		};
		return ret;
	}
}