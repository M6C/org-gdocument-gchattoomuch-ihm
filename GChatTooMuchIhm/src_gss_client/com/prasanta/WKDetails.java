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

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.pras.SpreadSheet;
import com.pras.SpreadSheetFactory;
import com.pras.WorkSheet;
import com.pras.WorkSheetCell;
import com.pras.WorkSheetRow;

/**
 * @author Prasanta Paul
 *
 */
public class WKDetails extends Activity {
	
	ArrayList<WorkSheetRow> rows;
	String[] cols;
	ListView list;
	TextView tv;
	boolean listInverse = false;
	private int wkID;
	private int spID;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent intent = getIntent();
		wkID = intent.getIntExtra("wk_id", -1);
		spID = intent.getIntExtra("sp_id", -1);
		
		if(wkID == -1 || spID == -1){
			finish();
			return;
		}
		
		list = new ListView(this.getApplicationContext());
		tv = new TextView(this.getApplicationContext());
		
		new MyTask(wkID, spID).execute();
	}
	
	private class MyTask extends AsyncTask{

		private Dialog dialog;
		private int wkID;
		private int spID;
		private String messageIndexOutOfBound = null;
		
		public MyTask(int wkID, int spID) {
			this.wkID = wkID;
			this.spID = spID;
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			dialog = new Dialog(WKDetails.this);
			dialog.setTitle("Please wait");
			TextView tv = new TextView(WKDetails.this.getApplicationContext());
			tv.setText("Featching records...");
			dialog.setContentView(tv);
			dialog.show();
		}

		@Override
		protected Object doInBackground(Object... params) {
			// Read Spread Sheet list from the server.
			SpreadSheetFactory factory = SpreadSheetFactory.getInstance();
			// Read from local Cache
			ArrayList<SpreadSheet> sps = factory.getAllSpreadSheets(false);
			SpreadSheet sp = sps.get(spID);
			ArrayList<WorkSheet> allWorkSheet = sp.getAllWorkSheets(false);
			if (wkID < allWorkSheet.size()) {
				messageIndexOutOfBound = null;
				WorkSheet wk = allWorkSheet.get(wkID);
				cols = wk.getColumns();
				rows = wk.getData(false);
			} else {
				messageIndexOutOfBound = "WKDetails IndexOutOfBound wkID:" + wkID + " >= allWorkSheet.size:" + allWorkSheet.size();
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
				return;
			}

			if(rows == null || rows.size() == 0){
				tv.setText("No record exists....");
				setContentView(tv);
				return;
			}
			
			StringBuffer record = new StringBuffer();
			
			Log.i("Prasanta", "Columns: "+ cols.length);
			
			record.append("Number of : ");
			if(cols != null){
				record.append("Columns="+ cols.length +" ");
			}
			record.append("Records="+ rows.size());
			tv.setText(record);
			
	    	ArrayAdapter<String> arrayAdaper = new ArrayAdapter<String>(WKDetails.this.getApplicationContext(), android.R.layout.simple_list_item_1);
	    	Log.i("Prasanta", "Number of entries..."+ arrayAdaper.getCount());
	    	list.addHeaderView(tv);
	    	list.setAdapter(arrayAdaper);

			int size = rows.size();
	    	for(int i=0; i<size; i++){
	    		int idx = (listInverse ? size - i - 1 : i);
				StringBuffer recordRow = new StringBuffer();
				WorkSheetRow row = rows.get(idx);
				recordRow.append("[ Row ID "+ (idx + 1) +" ]\n");
				
				ArrayList<WorkSheetCell> cells = row.getCells();

				for(int j=0; j<cells.size(); j++){
					WorkSheetCell cell = cells.get(j);
					if (j>0) {
						recordRow.append("\n");
					}
					recordRow.append(cell.getName() +" = "+ cell.getValue()); 
				}

				arrayAdaper.add(recordRow.toString());
			}

	    	list.setOnItemClickListener(new OnItemClickListener(){

				public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
					listInverse = !listInverse;
					list.removeHeaderView(tv);
					new MyTask(WKDetails.this.wkID, WKDetails.this.spID).execute();
				}
			});
			setContentView(list);
		}
	}
}
