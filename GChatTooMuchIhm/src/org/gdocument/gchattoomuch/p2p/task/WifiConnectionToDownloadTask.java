package org.gdocument.gchattoomuch.p2p.task;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.gdocument.gchattoomuch.lib.log.Logger;
import org.gdocument.gchattoomuch.p2p.common.P2PConstant;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;

import com.cameleon.common.android.inotifier.INotifierMessage;
import com.cameleon.common.tool.NetworkUtil;

public class WifiConnectionToDownloadTask extends AsyncTask<Void, Void, Void> {

	private static final String TAG = WifiConnectionToDownloadTask.class.getName();
	private static final String FILENAME_DATABASE_END_ZIP = ".zip";
	private static final String FILENAME_DATABASE_START = "database-";

	private Context context;
	private INotifierMessage notifier;
	private ServerSocket serverSocket = null;
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");


	public WifiConnectionToDownloadTask(Context context) {
		this(context, null);
	}

	public WifiConnectionToDownloadTask(Context context, INotifierMessage notifier) {
		this.context = context;
		this.notifier = notifier;
	}

	@Override
	protected Void doInBackground(Void... params) {
		logMe("doInBackground");
		try {
			/**
			 * * Create a server socket and wait for client connections. This *
			 * call blocks until a connection is accepted from a client
			 */
			String host = P2PConstant.getHostClient();
			int port = P2PConstant.getPortClient();
			int timeout = P2PConstant.P2P_DOWNLOAD_TIMEOUT;
			File file = new File(Environment.getExternalStorageDirectory() + "/" + context.getPackageName()
					+ "/" + FILENAME_DATABASE_START + sdf.format(new Date()) + FILENAME_DATABASE_END_ZIP);
			logMe("create file:" + file.getPath());
			File dirs = new File(file.getParent());
			if (!dirs.exists()) {
				dirs.mkdirs();
			}
			file.createNewFile();

			NetworkUtil.downloadFile(host, port, timeout, file, notifier);
		} catch (IOException e) {
			logMe(e);
		} catch(RuntimeException e) {
			logMe(e);
		} finally {
			closeSocket();
		}
		return null;
	}

	/** * Start activity that can handle the JPEG image */
	@Override
	protected void onPostExecute(Void result) {
		if (result != null) {
			logMe("File copied - " + result);
		}
		closeSocket();
	}

	@Override
	protected void onCancelled() {
		closeSocket();
		super.onCancelled();
	}

	private void closeSocket() {
		logMe("closeSocket serverSocket isnull:" + (serverSocket==null));
		if (serverSocket != null) {
			try {
				serverSocket.close();
			} catch (IOException e) {
				logMe(e);
			} catch(RuntimeException e) {
				logMe(e);
			} finally {
				serverSocket = null;
			}
		}
	}

	private void notify(String message) {
		if (notifier != null) {
			notifier.notifyMessage(message);
		}
	}

	private void notify(Exception ex) {
		if (notifier != null) {
			notifier.notifyError(ex);
		}
	}

	private void logMe(String message) {
		Logger.logMe(TAG, message);
		notify(message);
	}

	private void logMe(Exception ex) {
		Logger.logMe(TAG, ex);
		notify(ex);
    }
}