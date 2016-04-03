package org.gdocument.gchattoomuch.p2p.task;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

import org.gdocument.gchattoomuch.lib.log.Logger;
import org.gdocument.gchattoomuch.p2p.common.P2PConstant;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;

import com.cameleon.common.android.inotifier.INotifierMessage;
import com.cameleon.common.tool.FileTool;

public class WifiConnectionToUploadTask extends AsyncTask<Void, Void, Void> {

	private String TAG = WifiConnectionToUploadTask.class.getName();

	private INotifierMessage notifier;
	private ServerSocket serverSocket = null;
	private Context context;


	public WifiConnectionToUploadTask(Context context, INotifierMessage notifier) {
		this.context = context;
		this.notifier = notifier;
	}

	@Override
	protected Void doInBackground(Void... params) {
		logMe("doInBackground");
		String path = Environment.getExternalStorageDirectory() + "/" + context.getPackageName();
		String regEx = "^[database-][.zip]$";
		List<String> listFile = FileTool.getInstance().getListFile(path, regEx);
		if (listFile.size() > 0) {
			String filename = listFile.get(0);
			logMe("filename to upload:" + filename);
			waitConnectionToUpload(filename);
		}
		return null;
	}

	private void waitConnectionToUpload(String filename) {
		try {
			/**
			 * * Create a server socket and wait for client connections. This *
			 * call blocks until a connection is accepted from a client
			 */
			int port = P2PConstant.getPortClient();
			serverSocket = new ServerSocket(port);
			serverSocket.setSoTimeout(P2PConstant.P2P_UPLOAD_TIMEOUT);
			logMe("Connection on port:" + port + " Waiting...");
			Socket client = serverSocket.accept();
			logMe("Connection on port:" + port + " Starting or Time out expired!!!");

			/**
			 * If this code is reached, a client has connected and transfer data
			 */
			uploadFile(filename, client);
		} catch (IOException e) {
			logMe(e);
		} catch(RuntimeException e) {
			logMe(e);
		} finally {
			closeSocket();
		}
	}

	private void uploadFile(String filename, Socket client) {
		logMe("uploadFile filename:" + filename);
	    try {
			FileTool.copy(new FileInputStream(new File(filename)), client.getOutputStream());
		} catch (FileNotFoundException e) {
			notify(e);
		} catch (IOException e) {
			notify(e);
		}
	}

	/** * Start activity that can handle the JPEG image */
	@Override
	protected void onPostExecute(Void result) {
		notify("File copied - " + result);
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