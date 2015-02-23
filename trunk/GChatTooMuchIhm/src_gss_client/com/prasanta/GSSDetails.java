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

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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
import com.pras.WorkSheet;

/**
 * Show list of WorkSheets
 * 
 * @author Prasanta Paul
 *
 */
public class GSSDetails extends Activity {

	ListView list;
	int spID = -1;
	ArrayList<WorkSheet> workSheets;
	TextView tv;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent intent = getIntent();
		spID = intent.getIntExtra("sp_id", -1);
		
		if(spID == -1){
			finish();
			return;
		}
		
		list = new ListView(this.getApplicationContext());
		tv = new TextView(this.getApplicationContext());
		
		new MyTask().execute();
	}
	
	private class MyTask extends AsyncTask{

		private String messageIndexOutOfBound = null;
		private boolean onLongClick = false;
 		Dialog dialog;
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			dialog = new Dialog(GSSDetails.this);
			dialog.setTitle("Please wait");
			TextView tv = new TextView(GSSDetails.this.getApplicationContext());
			tv.setText("Featching SpreadSheet details...");
			dialog.setContentView(tv);
			if (!dialog.isShowing()) {
				dialog.show();
			}
		}

		@Override
		protected Object doInBackground(Object... params) {
			// Read Spread Sheet list from the server.
			SpreadSheetFactory factory = SpreadSheetFactory.getInstance();
			// Read from local Cache
			ArrayList<SpreadSheet> sps = factory.getAllSpreadSheets(false);
			if (spID < sps.size()) {
				SpreadSheet sp = sps.get(spID); 
				workSheets = sp.getAllWorkSheets();
			} else {
				messageIndexOutOfBound = "GSSDetails IndexOutOfBound wkID:" + spID + " >= allWorkSheet.size:" + sps.size();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Object result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if(dialog.isShowing())
				dialog.cancel();
			
			if (messageIndexOutOfBound != null) {
				tv.setText(messageIndexOutOfBound);
				setContentView(tv);
			} else if(workSheets == null || workSheets.size() == 0){
		        tv.setText("No spreadsheet exists in your account...");
		        setContentView(tv);
		    } else {
		        //tv.setText(spreadSheets.size() + "  spreadsheets exists in your account...");
		    	ArrayAdapter<String> arrayAdaper = new ArrayAdapter<String>(GSSDetails.this.getApplicationContext(), android.R.layout.simple_list_item_1);
		    	for(int i=0; i<workSheets.size(); i++){
		    		WorkSheet wk = workSheets.get(i);
		    		arrayAdaper.add(wk.getTitle());
		    	}
		    	Log.i("Prasanta", "Number of entries..."+ arrayAdaper.getCount());
		    	list.addHeaderView(tv);
		    	list.setAdapter(arrayAdaper);
		    	tv.setText("Number of WorkSheets ("+ workSheets.size() +")");
		    	
		    	list.setOnItemClickListener(new OnItemClickListener(){

					public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
						if (!onLongClick) {
							// Show details of the SpreadSheet
							if(position == 0)
								return;
							
							Toast.makeText(GSSDetails.this.getApplicationContext(), "Showing WorkSheet details, please wait...", Toast.LENGTH_LONG).show();
							
							// Start SP Details Activity 
							Intent i = new Intent(GSSDetails.this, WKDetails.class);
							i.putExtra("wk_id", position - 1);
							i.putExtra("sp_id", spID);
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
							SpreadSheetFactory factory = SpreadSheetFactory.getInstance();
							// Read from local Cache
							ArrayList<SpreadSheet> sps = factory.getAllSpreadSheets(false);
							final SpreadSheet sp = sps.get(spID);
							final WorkSheet wk = sp.getAllWorkSheets(false).get(position - 1);
							if (wk != null) {
								OnClickListener onClickOkListener = new OnClickListener() {
									public void onClick(DialogInterface dialog, int which) {
										new AsyncTask() {
											@Override
											protected Object doInBackground(Object... params) {
												sp.deleteWorkSheet(wk);
												return null;
											}
										}.execute();
									}
								};
						    	FactoryDialog.getInstance().buildOkCancelDialog(GSSDetails.this, onClickOkListener, R.string.app_name, R.string.txt_delete).show();
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
