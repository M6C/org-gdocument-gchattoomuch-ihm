/*
 * Copyright (C) 2011 Prasanta Paul, http://prasanta-paul.blogspot.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.prasanta;

import java.util.ArrayList;

import org.gdocument.gchattoomuch.ihm.R;
import org.gdocument.gchattoomuch.lib.interfaces.IAuthenticationResult;
import org.gdocument.gchattoomuch.lib.manager.AuthentificationManager;
import org.gdocument.gchattoomuch.lib.task.UserLoginTask;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cameleon.common.android.factory.FactoryDialog;
import com.pras.SpreadSheet;
import com.pras.SpreadSheetFactory;
import com.pras.auth.Authenticator;

/**
 * @author Prasanta Paul
 *
 */
public class GSSAct extends Activity implements IAuthenticationResult {
    
	ArrayList<SpreadSheet> spreadSheets;
	TextView tv;
	ListView list;
	Dialog dialog;
	Handler handler = new Handler();
	private Authenticator authenticator;

	@Override
    public void onCreate(Bundle savedInstanceState) 
	{
        super.onCreate(savedInstanceState);
        
        list = new ListView(this.getApplicationContext());
        //setContentView(R.layout.main);
        //tv = (TextView)findViewById(R.id.sp_count);
        tv = new TextView(this.getApplicationContext());

        dialog = new Dialog(GSSAct.this);
		dialog.setTitle("Please wait");
		TextView tv = new TextView(GSSAct.this.getApplicationContext());
		tv.setText("Featching SpreadSheet list from your account...");
		dialog.setContentView(tv);
	}

	@Override
	protected void onResume() {
		super.onResume();

		dialog.show();

		new UserLoginTask(this, null).execute();
	}

	public void onAuthenticationFinish(String authToken) {
	}

	public void onAuthenticationResult(String authToken) {
		AuthentificationManager authentificationManager = AuthentificationManager.getInstance(this);
		authentificationManager.onAuthenticationResult(authToken);

		final Bundle authBundle = authentificationManager.getAuthBundle();
		authenticator = new Authenticator() {
			public String getAuthToken(String service) {
				String authToken = authBundle.getString(service);
				return authToken;
			}	        				
		};

		// Init and Read SpreadSheet list from Google Server
        new MyTask().execute();
	}

	public void onAuthenticationCancel() {
		if(dialog.isShowing())
			dialog.cancel();
	}

	private class MyTask extends AsyncTask {
		private boolean onLongClick = false;

//		@Override
//		protected void onPreExecute() {
//			// TODO Auto-generated method stub
//			super.onPreExecute();
//			dialog.show();
//		}

		@Override
		protected Object doInBackground(Object... params) {
			// Read Spread Sheet list from the server.
			SpreadSheetFactory factory = SpreadSheetFactory.getInstance(authenticator);
		    spreadSheets = factory.getAllSpreadSheets();    
			return null;
		}
		
		@Override
		protected void onPostExecute(Object result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if(dialog.isShowing())
				dialog.cancel();
			
			if(spreadSheets == null || spreadSheets.size() == 0){
		        tv.setText("No spreadsheet exists in your account...");
		        setContentView(tv);
		    }
		    else{
		        //tv.setText(spreadSheets.size() + "  spreadsheets exists in your account...");
		    	ArrayAdapter<String> arrayAdaper = new ArrayAdapter<String>(GSSAct.this.getApplicationContext(), android.R.layout.simple_list_item_1);
		    	for(int i=0; i<spreadSheets.size(); i++){
		    		SpreadSheet sp = spreadSheets.get(i);
		    		arrayAdaper.add(sp.getTitle());
		    	}
		    	list.addHeaderView(tv);
		    	list.setAdapter(arrayAdaper);
		    	tv.setText("Number of SpreadSheets ("+ spreadSheets.size() +")");
		    	
		    	list.setOnItemClickListener(new OnItemClickListener(){

					public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
						if (!onLongClick) {
							// Show details of the SpreadSheet
							if(position == 0)
								return;
							
							Toast.makeText(GSSAct.this.getApplicationContext(), "Showing SP details, please wait...", Toast.LENGTH_LONG).show();
							
							// Start SP Details Activity 
							Intent i = new Intent(GSSAct.this, GSSDetails.class);
							i.putExtra("sp_id", position - 1);
							startActivity(i);
						} else {
							onLongClick = false;
						}
					}
		    	});
		    	list.setOnItemLongClickListener(new OnItemLongClickListener() {

					public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
						if (!onLongClick) {
							onLongClick = true;
							// Read Spread Sheet list from the server.
							final SpreadSheetFactory factory = SpreadSheetFactory.getInstance();
							// Read from local Cache
							ArrayList<SpreadSheet> sps = factory.getAllSpreadSheets(false);
							final SpreadSheet sp = sps.get(position - 1);
							if (sp != null) {
								OnClickListener onClickOkListener = new OnClickListener() {
									public void onClick(DialogInterface dialog, int which) {
										new AsyncTask() {
											@Override
											protected Object doInBackground(Object... params) {
												factory.deleteSpreadSheet(sp.getKey());
												return null;
											}
										}.execute();
									}
								};
						    	FactoryDialog.getInstance().buildOkCancelDialog(GSSAct.this, onClickOkListener, R.string.app_name, R.string.txt_delete).show();
							}
						}
						return false;
					}
				});
		    	setContentView(list);
		    }
		}

	}
}