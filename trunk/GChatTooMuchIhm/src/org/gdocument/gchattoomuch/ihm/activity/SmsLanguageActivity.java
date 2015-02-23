package org.gdocument.gchattoomuch.ihm.activity;

import org.gdocument.gchattoomuch.ihm.R;
import org.gdocument.gchattoomuch.ihm.manager.SmsManager;
import org.gdocument.gchattoomuch.lib.log.Logger;
import org.gdocument.gchattoomuch.lib.manager.SmsLanguageManager;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.cameleon.common.android.factory.FactoryDialog;
import com.prasanta.GSSAct;

public class SmsLanguageActivity extends Activity {

	private static final String TAG = SmsLanguageActivity.class.getName();

	private EditText tvSmsContent;
	private EditText tvSmsPhone;

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreate(Bundle data) {

        super.onCreate(data);
        setContentView(R.layout.sms_language);
        tvSmsContent = (EditText) findViewById(R.id.tvSmsContent);
        tvSmsPhone = (EditText) findViewById(R.id.tvSmsPhone);
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
}