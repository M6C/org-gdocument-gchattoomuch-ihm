package org.gdocument.gchattoomuch.p2p.activity;


import org.gdocument.gchattoomuch.ihm.R;
import org.gdocument.gchattoomuch.ihm.activity.MainActivity;
import org.gdocument.gchattoomuch.ihm.browser.db.activity.BrowserDatabaseActivity;
import org.gdocument.gchattoomuch.ihm.manager.SmsManager;
import org.gdocument.gchattoomuch.lib.manager.SmsLanguageManager;
import org.gdocument.gchattoomuch.p2p.activity.listener.OnClickConnectionToDownloadListener;
import org.gdocument.gchattoomuch.p2p.activity.listener.OnClickConnectionToUploadListener;
import org.gdocument.gchattoomuch.p2p.activity.listener.OnClickStartStopServerListener;
import org.gdocument.gchattoomuch.p2p.common.P2PConstant;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.cameleon.common.android.factory.FactoryDialog;
import com.cameleon.common.android.inotifier.INotifierMessage;

public class P2PActivity extends Activity {

	private EditText tvSmsContent;
	private EditText tvSmsPhone;
	private EditText tvTimeOut;
	private TextView tvMessage;
	private Notifier notifier;

	protected void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.p2p);

		notifier = new Notifier();
		
		tvSmsContent = (EditText) findViewById(R.id.tv_SmsContent);
        tvSmsPhone = (EditText) findViewById(R.id.tv_SmsPhone);
        tvMessage = (TextView) findViewById(R.id.tv_Message);
        tvTimeOut = (EditText) findViewById(R.id.tv_TimeOut);
        tvTimeOut.setText(Integer.toString(P2PConstant.P2P_DOWNLOAD_TIMEOUT));
	};

	public void onClickStartStopServer(View view) {
    	final Button btnUpload = (Button)view;

    	int timeOut = P2PConstant.P2P_DOWNLOAD_TIMEOUT;
    	try {
    		timeOut = Integer.parseInt(tvTimeOut.getText().toString());
    	} catch (RuntimeException e) {
    		e.printStackTrace();
    	}

    	OnClickListener onClickStartStopServerListener = new OnClickStartStopServerListener(this, notifier, timeOut, tvMessage, btnUpload);
    	FactoryDialog.getInstance().buildOkCancelDialog(this, onClickStartStopServerListener, R.string.app_name, R.string.btn_text_server_download_db).show();
	}

	public void onClickConnectionDownload(View view) {
    	final Button button = (Button)view;
    	OnClickListener onClickConnectionDownloadListener = new OnClickConnectionToDownloadListener(this, notifier, button);
    	FactoryDialog.getInstance().buildOkCancelDialog(this, onClickConnectionDownloadListener, R.string.app_name, R.string.btn_text_start_server).show();
	}

	public void onClickConnectionUpload(View view) {
    	final Button btnUpload = (Button)view;

    	int timeOut = P2PConstant.P2P_DOWNLOAD_TIMEOUT;
    	try {
    		timeOut = Integer.parseInt(tvTimeOut.getText().toString());
    	} catch (RuntimeException e) {
    		e.printStackTrace();
    	}

    	OnClickListener onClickConnectionUploadListener = new OnClickConnectionToUploadListener(this, notifier, timeOut, btnUpload);
    	FactoryDialog.getInstance().buildOkCancelDialog(this, onClickConnectionUploadListener, R.string.app_name, R.string.btn_text_start_upload_server).show();
	}

    public void onClickCallSendDb(View view) {
    	final String message = buildSmsLanguage(SmsLanguageManager.MSG_LANGUAGE.SEND_DB.language);
    	String phone = tvSmsPhone.getText().toString();

    	OnClickListener onClickOkListener = new OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
//				if (uploadTask == null) {
					tvMessage.setText("");
//				}
				executeSmsLanguage(message);
			}
		};
		String text = String.format(getString(R.string.btn_text_server_call_send_db_message), message, phone);
    	FactoryDialog.getInstance().buildOkCancelDialog(this, onClickOkListener, R.string.app_name, text).show();
    }
    
    public void onClickOpenDatabase(View view) {
    	startActivity(new Intent(this, BrowserDatabaseActivity.class));
    }

    public void onClickStartMainActivity(View view) {
    	startActivity(new Intent(this, MainActivity.class));
    }

    private String buildSmsLanguage(String message) {
    	String ret = message;
    	String smsContent = tvSmsContent.getText().toString();
    	if (smsContent != null && !smsContent.isEmpty()) {
    		ret = smsContent + " " + message;
    	}
    	return ret;
    }
    private void executeSmsLanguage(String message) {
    	String phone = tvSmsPhone.getText().toString();
		tvMessage.append("Sending message:'" + message + "' to phone:'" + phone + "'\r\n");
    	SmsManager.getInstance().send(this, phone, message);
    }

	private class Notifier implements INotifierMessage {

		@Override
		public void notifyError(Exception ex) {
			notifyMessage(ex.getMessage());
		}

		@Override
		public void notifyMessage(final String msg) {
			runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					tvMessage.append(msg + "\r\n");
				}
			});
		}
		
	}
}