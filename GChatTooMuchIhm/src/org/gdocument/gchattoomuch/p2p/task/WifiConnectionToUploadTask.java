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
import org.gdocument.gchattoomuch.p2p.task.interfaces.IProcessNotification;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;

import com.cameleon.common.android.inotifier.INotifierMessage;
import com.cameleon.common.tool.FileTool;

public class WifiConnectionToUploadTask extends AsyncTask<Void, Void, Void> {

	private String TAG = WifiConnectionToUploadTask.class.getName();
	private static final String FILENAME_DATABASE_END_ZIP = ".zip";
	private static final String FILENAME_DATABASE_START = "database-";

	private INotifierMessage notifier;
	private ServerSocket serverSocket = null;
	private Context context;
	private int timeOut = P2PConstant.P2P_UPLOAD_TIMEOUT;
	private IProcessNotification processNotification;


	public WifiConnectionToUploadTask(Context context, INotifierMessage notifier, int timeOut) {
		this.context = context;
		this.notifier = notifier;
		this.timeOut = timeOut;
	}

	public void setProcessNotification(IProcessNotification processNotification) {
		this.processNotification = processNotification;
	}

	@Override
	protected Void doInBackground(Void... params) {
		logMe("doInBackground");
		waitConnectionToUpload();
		return null;
	}


	private File getFileToUpload() {
		File ret = null;
		long timeFileModified = 0;
		String regEx = "^["+FILENAME_DATABASE_START+"]["+FILENAME_DATABASE_END_ZIP+"]$";
		String path = Environment.getExternalStorageDirectory() + "/" + context.getPackageName();
		List<String> fileList = FileTool.getInstance().getListFile(path, regEx);
		for(String filename : fileList) {
			File file = new File(filename);
			if (file.isFile() && file.lastModified() > timeFileModified) {
				ret = file;
				timeFileModified = file.lastModified();
			}
		}
		return ret;
	}

	private void waitConnectionToUpload() {
		try {
			/**
			 * * Create a server socket and wait for client connections. This *
			 * call blocks until a connection is accepted from a client
			 */
			int port = P2PConstant.getPortClient();
			serverSocket = new ServerSocket(port);
			serverSocket.setSoTimeout(this.timeOut);
			logMe("Connection on port:" + port + " Waiting...");
			Socket client = serverSocket.accept();
			logMe("Connection on port:" + port + " Starting or Time out expired!!!");

			/**
			 * If this code is reached, a client has connected and transfer data
			 */
			File file = getFileToUpload();
			if (file != null) {
				logMe("Upload on port:" + port + " Starting for file:" + file.getAbsolutePath());
				uploadFile(file, client);
	        } else {
	        	logMe("No file to Upload");
			}
		} catch (IOException e) {
			logMe(e);
		} catch(RuntimeException e) {
			logMe(e);
		} finally {
			closeSocket();
		}
	}

	private void uploadFile(File file, Socket client) {
	    try {
			FileTool.copy(new FileInputStream(file), client.getOutputStream());
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