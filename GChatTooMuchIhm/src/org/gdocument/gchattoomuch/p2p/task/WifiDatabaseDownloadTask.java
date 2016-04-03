package org.gdocument.gchattoomuch.p2p.task;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.gdocument.gchattoomuch.lib.log.Logger;
import org.gdocument.gchattoomuch.p2p.common.P2PConstant;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;

import com.cameleon.common.android.inotifier.INotifierMessage;

public class WifiDatabaseDownloadTask extends AsyncTask<Void, Void, String> {

	private String TAG = WifiDatabaseDownloadTask.class.getName();

	private Context context;
	private INotifierMessage notifier;
	private IProcessNotification processNotification;
	private ServerSocket serverSocket = null;
	private int timeOut = P2PConstant.P2P_DOWNLOAD_TIMEOUT;
	private boolean flagRunning = false;
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

	public WifiDatabaseDownloadTask(Context context, INotifierMessage notifier) {
		this.context = context;
		this.notifier = notifier;
	}

	public WifiDatabaseDownloadTask(Context context, INotifierMessage notifier, int timeOut) {
		this.context = context;
		this.notifier = notifier;
		this.timeOut = timeOut;
	}

	@Override
	protected String doInBackground(Void... params) {
		logMe("doInBackground");
		try {
			// Create a server socket
			final File f = new File(Environment.getExternalStorageDirectory() + "/" + context.getPackageName()
					+ "/database-" + sdf.format(new Date()) + ".zip");
			final int port = P2PConstant.getPort();
			serverSocket = new ServerSocket(port);
			serverSocket.setSoTimeout(timeOut);
			synchronized (this) {
				new Thread() {
					public void run() {
						try {
							logMe("Download on port:" + port + " Waiting... (time out:" + timeOut + ")");
							if (processNotification != null) {
								processNotification.onStart();
							}
							// Wait for client connections. This call blocks until a connection is accepted from a client
							Socket client = serverSocket.accept();
							logMe("Download on port:" + port + " Starting or Time out expired!!!");
							if (serverSocket != null && !serverSocket.isClosed()) {
								logMe("create file:" + f.getPath());
								File dirs = new File(f.getParent());
								if (!dirs.exists()) {
									dirs.mkdirs();
								}
								f.createNewFile();
								InputStream inputstream = client.getInputStream();
								copyFile(inputstream, new FileOutputStream(f));
							}
						} catch (SocketTimeoutException e) {
							WifiDatabaseDownloadTask.this.notify(e);
						} catch (IOException e) {
							logMe(e);
						} catch(RuntimeException e) {
							logMe(e);
						} finally {
							flagRunning = false;
						}
					};
				}.start();
	
				flagRunning = true;
				while(flagRunning) {
					try {
						wait(500);
					} catch (Exception e) {
						flagRunning = false;
					}
				}
			}
			return f.getAbsolutePath();
		} catch (IOException e) {
			logMe(e);
		} catch(RuntimeException e) {
			logMe(e);
		} finally {
			closeSocket();
		}
		return null;
	}

	@Override
	protected void onPostExecute(String result) {
		closeSocket();
	}

	@Override
	protected void onCancelled() {
//		closeSocket();
		flagRunning = false;
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
				if (processNotification != null) {
					processNotification.onFinish();
				}
			}
		}
	}

	private void copyFile(InputStream in, OutputStream out) throws IOException {
		logMe("copyFile");
		long size = 0;
		try {
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				size += len;
				out.write(buf, 0, len);
			}
		} finally {
			out.close();
			in.close();
			logMe("copyFile size:" + size);
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

	public IProcessNotification getProcessNotification() {
		return processNotification;
	}

	public void setProcessNotification(IProcessNotification processNotification) {
		this.processNotification = processNotification;
	}

	public interface IProcessNotification {
		public void onCreate();
		public void onStart();
		public void onFinish();
	}
}