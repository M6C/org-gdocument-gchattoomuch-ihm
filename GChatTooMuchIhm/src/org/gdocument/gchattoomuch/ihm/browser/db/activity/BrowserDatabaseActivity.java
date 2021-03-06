package org.gdocument.gchattoomuch.ihm.browser.db.activity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.gdocument.gchattoomuch.ihm.R;
import org.gdocument.gchattoomuch.ihm.browser.db.adapter.BrowserDatabaseAdapter;
import org.gdocument.gchattoomuch.ihm.browser.db.manager.ContentProviderManager;
import org.gdocument.gchattoomuch.ihm.browser.db.manager.ContentProviderManager.Column;
import org.gdocument.gchattoomuch.ihm.browser.db.manager.ContentProviderManager.ColumnDate;
import org.gdocument.gchattoomuch.ihm.browser.db.manager.ContentProviderManager.ColumnNumber;
import org.gdocument.gchattoomuch.ihm.browser.db.model.ContentProviderData;
import org.gdocument.gchattoomuch.ihm.browser.db.model.DatabaseItem;
import org.gdocument.gchattoomuch.ihm.browser.db.service.DbContentProviderService;
import org.gdocument.gchattoomuch.lib.log.Logger;
import org.gdocument.gchattoomuch.p2p.task.ExtractDataTask;
import org.gdocument.gchattoomuch.p2p.task.ExtractDataTask.ExtractEvent;
import org.gdocument.gchattoomuch.p2p.task.ExtractDataTask.INotifierExtract;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cameleon.common.android.factory.FactoryDialog;
import com.cameleon.common.tool.FileTool;

public class BrowserDatabaseActivity extends Activity {

	private static final String TAG = BrowserDatabaseActivity.class.getName();

	private static final int CODE_ACTIVITY_RESULT_CHOOSER = 10;
    private String[][] filterValue = {{"", ""}, {"address", "8356"}, {"address", "2939"}, {"address", "7969"}, {"address not", "683469658"}};
    private int filterIndex = 1;

	private TextView tvNameDatabase;
	private ListView lvData;

	private List<ContentProviderData> data = null;
	private BaseAdapter adapter = null;
	private List<DatabaseItem> databaseItemList = new ArrayList<DatabaseItem>();

	private EditText etFilterColumn;
	private EditText etFilterValue;
	private Button btnOrder;

	private DatabaseItem currentDatabase;
	private Column columnOrder = null;
	private boolean orderDesc = true;

	private Notifier notifier = new Notifier();

	private ProgressDialog progressDialog;

	private DbContentProviderService dbContentProviderService;
	
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.browser_database);
        tvNameDatabase = (TextView) findViewById(R.id.tv_name_database);
        lvData = (ListView) findViewById(R.id.lv_data);
        etFilterColumn = (EditText) findViewById(R.id.et_filter_column);
        etFilterValue = (EditText) findViewById(R.id.et_filter_value);
        btnOrder = (Button) findViewById(R.id.btn_order);

		progressDialog = new ProgressDialog(this);

		data = new ArrayList<ContentProviderData>();
    	adapter = new BrowserDatabaseAdapter(this, data);
        lvData.setAdapter(adapter);

        databaseItemList.add(createDatabaseItemSms(false));
        databaseItemList.add(createDatabaseItemSmsCache(false));
        databaseItemList.add(createDatabaseItemCall(false));
        databaseItemList.add(createDatabaseItemContact(false));
        databaseItemList.add(createDatabaseItemPhone(false));

        databaseItemList.add(createDatabaseItemSms(true));
        databaseItemList.add(createDatabaseItemSmsCache(true));
        databaseItemList.add(createDatabaseItemCall(true));
        databaseItemList.add(createDatabaseItemContact(true));
        databaseItemList.add(createDatabaseItemPhone(true));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	if (requestCode == CODE_ACTIVITY_RESULT_CHOOSER) {
    		if (resultCode == RESULT_OK) {
				final String filePath = data.getData().getPath();
				int lastIndex = filePath.lastIndexOf("/");
				if (lastIndex >= 0) {
					tvNameDatabase.setText(filePath.substring(lastIndex+1));
				} else {
					tvNameDatabase.setText(filePath);
				}

				new AsyncTask<Void, Void, Void>() {

			    	private List<String> databaseName = new ArrayList<String>();
			    	private List<DatabaseItem> databaseList = new ArrayList<DatabaseItem>();

			    	@Override
					protected void onPreExecute() {
						progressDialog.show();
					}

					@Override
					protected Void doInBackground(Void... params) {
						initializeDatabaseItemListText(filePath);
						return null;
					}

					@Override
					protected void onPostExecute(Void result) {
						progressDialog.dismiss();
						if (databaseName.size() > 1) {

							FactoryDialog.getInstance().buildListView(BrowserDatabaseActivity.this, R.string.title_dialog_open_database, databaseName.toArray(new String[0]), new OnItemClickListener() {

								@Override
								public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
									columnOrder = null;
									btnOrder.setText(R.string.btn_text_order);
									currentDatabase = databaseList.get(position);

									extractAndOpenDatabase(filePath, currentDatabase);
								}
							}).show();
				    	} else if (databaseName.size() > 0) {
							columnOrder = null;
							btnOrder.setText(R.string.btn_text_order);
							currentDatabase = databaseList.get(0);

							extractAndOpenDatabase(filePath, currentDatabase);
				    	}
					}

				    private void initializeDatabaseItemListText(String filePath) {
				    	databaseName.clear();
				    	databaseList.clear();
						List<String> contentFileName = new ArrayList<String>();
						if (filePath.endsWith(".zip")) {
							try {
								contentFileName = FileTool.getInstance().listZipEntry(filePath);
							} catch (IOException e) {
								logMe(e);
							}
						} else if (filePath.endsWith(".db")) {
							contentFileName.add(filePath);
						}

						if (contentFileName.size() > 0) {
					    	for(int i=0 ; i<databaseItemList.size() ; i++) {
						    	DatabaseItem databaseItem = databaseItemList.get(i);
								String name = getFilename(databaseItem.getFilePath()).toLowerCase(Locale.getDefault());
					    		for (String content : contentFileName) {
					    			if (content.toLowerCase(Locale.getDefault()).endsWith(name)) {
					    				databaseName.add(databaseItem.getName());
					    				databaseList.add(databaseItem);
					    				break;
					    			}
					    		}
					    	}
						}
			    		if (databaseList.size() == 0) {
			    			databaseItemList.clear();
			    			for(String content : contentFileName) {
			    				DatabaseItem item = createDatabaseItemUnknow(content);
								databaseItemList.add(item);
								databaseName.add(item.getName());
								databaseList.add(item);
			    			}
			    		}
				    }
				}.execute();
    		}
    	}
    }

    public void onClickOrder(View view) {
    	List<Column> columnList = (currentDatabase != null ? currentDatabase.getColumnList() : null);
    	if (columnList != null && columnList.size() > 0) {
    		columnList = currentDatabase.getColumnList();
    	} else if (dbContentProviderService != null) {
    		columnList = dbContentProviderService.getColumnListData();
    	}
    	final List<Column> listColumn = columnList;
    	if (listColumn != null && listColumn.size() > 0) {
	    	String[] textList = new String[listColumn.size() + 1];
	    	textList[0] = "[Clean]";
	    	for(int i=0 ; i<listColumn.size() ; i++) {
	    		String name = listColumn.get(i).getName();
				if (columnOrder != null && name.equals(columnOrder.getName())) {
					name += (orderDesc ? " DESC" : " ASC");
				}
				textList[i+1] = name;
	    	}
			FactoryDialog.getInstance().buildListView(this, R.string.title_dialog_order, textList, new OnItemClickListener() {
	
				@Override
				public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
					new AsyncTask<Void, Void, Void>() {

						protected void onPreExecute() {
							if (!progressDialog.isShowing()) {
								progressDialog.show();
							}
						};

						@Override
						protected Void doInBackground(Void... params) {
							if (position == 0) {
								columnOrder = null;
							} else {
								Column column = listColumn.get(position-1);
								if (columnOrder != null && columnOrder.getName().equals(column.getName())) {
									orderDesc = !orderDesc;
								} else {
									orderDesc = true;
								}
								columnOrder = column;
							}
							openDatabase(currentDatabase);
							return null;
						}

						@Override
						protected void onPostExecute(Void result) {
							adapter.notifyDataSetChanged();
							if (columnOrder != null) {
								btnOrder.setText(columnOrder.getName() + (orderDesc ? " DESC" : " ASC"));
							} else {
								btnOrder.setText(R.string.btn_text_order);
							}
							progressDialog.dismiss();
						};
					}.execute();
				}
			}).show();
    	}
    }

    public void onClickOpenDatabase(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT); 
        intent.setType("*/*"); 
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        Uri uri = Uri.parse(Environment.getExternalStorageDirectory() + "/" + getPackageName());
		intent.setDataAndType(uri, "text/csv");

		startActivityForResult(Intent.createChooser(intent, getString(R.string.btn_open_database)), CODE_ACTIVITY_RESULT_CHOOSER);
    }

    public void onClickExtract(View view) {
		if (etFilterColumn.getText().toString().isEmpty() || etFilterValue.getText().toString().isEmpty()) {
			Toast.makeText(this, getString(R.string.message_filter_obligatoire), Toast.LENGTH_LONG).show();
		} else {
			OnClickListener onClickOkListener = new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					executeExtractDataTask(true);
				}
			};
			OnClickListener onClickCancelListener = new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					executeExtractDataTask(false);
				}
			};
			FactoryDialog.getInstance().buildOkCancelDialog(this, onClickOkListener, onClickCancelListener, R.string.dialog_extract_merge_title, R.string.dialog_extract_merge_message).show();
		}
	}

    public void onClickSetFilter(View view) {
		if (this.filterIndex >= this.filterValue.length) {
			this.filterIndex = 0;
		}
		String[] filter = filterValue[filterIndex++];
		etFilterColumn.setText(filter[0]);
		etFilterValue.setText(filter[1]);
    }

    private void executeExtractDataTask(boolean merge) {
    	List<DatabaseItem> databaseItemList = new ArrayList<DatabaseItem>();
    	databaseItemList.add(createDatabaseItemSms(true));
    	databaseItemList.add(createDatabaseItemSmsCache(true));
		new ExtractDataTask(BrowserDatabaseActivity.this, BrowserDatabaseActivity.this.notifier, databaseItemList, etFilterColumn.getText().toString(), etFilterValue.getText().toString(), merge).execute();
    }

	private void extractAndOpenDatabase(final String filePath, final DatabaseItem currentDatabase) {
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected void onPreExecute() {
				if (!progressDialog.isShowing()) {
					progressDialog.show();
				}
				tvNameDatabase.setText(tvNameDatabase.getText() + " - " + currentDatabase.getDatabaseName());
			};

			@Override
			protected Void doInBackground(Void... params) {
				File fileDatabase = new File(currentDatabase.getFilePath());
				fileDatabase.deleteOnExit();
				String destDirectory = Environment.getExternalStorageDirectory() + "/" + getPackageName();
				try {
			    	String name = getFilename(currentDatabase.getFilePath());
					FileTool.getInstance().unzip(filePath, destDirectory, name);
					openDatabase(currentDatabase);
					fileDatabase.deleteOnExit();
				} catch (IOException e) {
					logMe(e);
				}
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				adapter.notifyDataSetChanged();
				progressDialog.dismiss();
			}
		}.execute();
	}

    private void openDatabase(DatabaseItem database) {
    	if (new File(database.getFilePath()).exists()) {
	    	dbContentProviderService = new DbContentProviderService(this, new Notifier(), database);
			List<ContentProviderData> list = null;
			Column filterColumn = null;
			String filterValue = null;
			String order = null;
			if (columnOrder != null) {
				if (Column.TYPE.NUMBER.equals(columnOrder.getType()) || Column.TYPE.DATE.equals(columnOrder.getType())) {
					order = "CAST(" + columnOrder.getName() + " as integer)";
				} else {
					order = columnOrder.getName();
				}
				order += " " + (orderDesc ? "DESC" : "ASC");
			}
	        if (!etFilterColumn.getText().toString().isEmpty() && !etFilterValue.getText().toString().isEmpty()) {
				filterColumn = new ContentProviderManager.Column(etFilterColumn.getText().toString());
				filterValue = etFilterValue.getText().toString();
	        }
	        list = dbContentProviderService.getList(filterColumn, filterValue, order);
			
			data.clear();
			data.addAll(list);
			Map<String, Integer> columnSize = getColumnSize(list);
			((BrowserDatabaseAdapter)adapter).setColumnSize(columnSize);
			logMe("filePath count:" + data.size());
    	} else {
    		Toast.makeText(this, "'" + database.getFilePath() + "' not exist", Toast.LENGTH_LONG).show();
    	}
    }

    private Map<String, Integer> getColumnSize(List<ContentProviderData> list) {
    	Map<String, Integer> ret = new HashMap<String, Integer>();
    	for(ContentProviderData data : list) {
    		for(ContentProviderManager.Column column : data.getColumnList()) {
    			String name = column.getName();
    			String str = data.getData().get(name);
    			if (str != null) {
    				int size = str.length();
    				int currentSize = name.length();
    				if (ret.containsKey(name)) {
    					currentSize = ret.get(name);
    					if (currentSize < size) {
    						ret.put(name, size);
    					}
    				} else {
    					size = (currentSize < size) ? size : currentSize;
						ret.put(name, size);
    				}
    			} else if (!ret.containsKey(name)) {
    				int size = name.length();
					ret.put(name, size);
    			}
    		}
    	}
    	return ret;
    }

	public String getFilename(String filePath) {
		String databaseName;
		if (filePath.indexOf('\\') >= 0) {
			databaseName  = filePath.substring(filePath.lastIndexOf('\\') + 1);
		} else if (filePath.indexOf('/') >= 0) {
			databaseName = filePath.substring(filePath.lastIndexOf('/') + 1);
		} else {
			databaseName = filePath;
		}
		return databaseName;
	}

	private DatabaseItem createDatabaseItemUnknow(String filePath) {
		String databaseName = getFilename(filePath);
		filePath = Environment.getExternalStorageDirectory() + "/" + getPackageName() + "/" + databaseName;

		String title = databaseName + " All";
		DatabaseItem item = new DatabaseItem(title, filePath, databaseName, null);
		item.setKnow(false);
		return item;
	}

	private DatabaseItem createDatabaseItemSms(boolean all) {
		String filePath = Environment.getExternalStorageDirectory() + "/" + getPackageName() + "/sms.db";
		String databaseName = getFilename(filePath);

		List<Column> columnList = null;
		if (!all) {
			columnList = new ArrayList<ContentProviderManager.Column>();
			columnList.add(new ColumnNumber("_id"));
			columnList.add(new ContentProviderManager.Column("address"));
			columnList.add(new ColumnDate("date"));
			columnList.add(new ContentProviderManager.Column("type"));
			columnList.add(new ContentProviderManager.Column("body"));
		}

		String title = "Sms";
		if (all) {
			title += " All";
		}
		return new DatabaseItem(title, filePath, databaseName, columnList);
	}

	private DatabaseItem createDatabaseItemSmsCache(boolean all) {
		String filePath = Environment.getExternalStorageDirectory() + "/" + getPackageName() + "/SmsCache.db";
		String databaseName = getFilename(filePath);

		List<Column> columnList = null;
		if (!all) {
			columnList = new ArrayList<ContentProviderManager.Column>();
			columnList.add(new ColumnNumber("_id"));
			columnList.add(new ContentProviderManager.Column("address"));
			columnList.add(new ColumnDate("date"));
			columnList.add(new ContentProviderManager.Column("type"));
			columnList.add(new ContentProviderManager.Column("body"));
		}

		String title = "SmsCache";
		if (all) {
			title += " All";
		}
		return new DatabaseItem(title, filePath, databaseName, columnList);
	}

	private DatabaseItem createDatabaseItemContact(boolean all) {
		String filePath = Environment.getExternalStorageDirectory() + "/" + getPackageName() + "/contact.db";
		String databaseName = getFilename(filePath);

		List<Column> columnList = null;
		if (!all) {
			columnList = new ArrayList<ContentProviderManager.Column>();
			columnList.add(new ColumnNumber("_id"));
			columnList.add(new ContentProviderManager.Column("display_name"));
			columnList.add(new ContentProviderManager.Column("data4"));
			columnList.add(new ColumnDate("last_time_used"));
			columnList.add(new ColumnNumber("times_used"));
			columnList.add(new ColumnDate("last_time_contacted"));
			columnList.add(new ColumnNumber("times_contacted"));
			columnList.add(new ColumnDate("creation_time"));
			columnList.add(new ColumnDate("contact_last_updated_timestamp"));
		}
		String title = "Contact";
		if (all) {
			title += " All";
		}
		return new DatabaseItem(title, filePath, databaseName, columnList);
	}

	private DatabaseItem createDatabaseItemPhone(boolean all) {
		String filePath = Environment.getExternalStorageDirectory() + "/" + getPackageName() + "/phone.db";
		String databaseName = getFilename(filePath);

		List<Column> columnList = null;
		if (!all) {
			columnList = new ArrayList<ContentProviderManager.Column>();
			columnList.add(new ColumnNumber("_id"));
			columnList.add(new ContentProviderManager.Column("display_name"));
			columnList.add(new ContentProviderManager.Column("data4"));
			columnList.add(new ColumnDate("last_time_used"));
			columnList.add(new ColumnNumber("times_used"));
			columnList.add(new ColumnDate("last_time_contacted"));
			columnList.add(new ColumnNumber("times_contacted"));
			columnList.add(new ColumnDate("creation_time"));
			columnList.add(new ColumnDate("contact_last_updated_timestamp"));
		}
		String title = "Phone";
		if (all) {
			title += " All";
		}
		return new DatabaseItem(title, filePath, databaseName, columnList);
	}

	private DatabaseItem createDatabaseItemCall(boolean all) {
		String filePath = Environment.getExternalStorageDirectory() + "/" + getPackageName() + "/call.db";
		String databaseName = getFilename(filePath);

		List<Column> columnList = null;
		if (!all) {
			columnList = new ArrayList<ContentProviderManager.Column>();
			columnList.add(new ColumnNumber("_id"));
			columnList.add(new ContentProviderManager.Column("number"));
			columnList.add(new ContentProviderManager.Column("geocoded_location"));
			columnList.add(new ContentProviderManager.Column("name"));
			columnList.add(new ColumnDate("date"));
			columnList.add(new ColumnNumber("duration"));
			columnList.add(new ColumnNumber("contactid"));
		}
		String title = "Call";
		if (all) {
			title += " All";
		}
		return new DatabaseItem(title, filePath, databaseName, columnList);
	}

	private void logMe(String msg) {
		Logger.logMe(TAG, msg);
	}

	private static void logMe(Exception ex) {
		Logger.logMe(TAG, ex);
    }

	private class Notifier implements INotifierExtract {

		@Override
		public synchronized void notifyError(final Exception ex) {
			runOnUiThread(new Runnable() {
				public void run() {
					logMe(ex);
					progressDialog.setMessage(ex.getMessage());
				}
			});
		}

		@Override
		public synchronized void notifyMessage(final String msg) {
			runOnUiThread(new Runnable() {
				public void run() {
					logMe(msg);
					progressDialog.setMessage(msg);
				}
			});
		}

		@Override
		public void onEvent(ExtractEvent event) {
			switch (event) {
			case EVENT_START:
				if (!progressDialog.isShowing()) {
					progressDialog.show();
				}
				break;

			case EVENT_END:
				if (progressDialog.isShowing()) {
					progressDialog.hide();;
				}
				break;
			default:
				break;
			}
		}
	}
}
