package org.gdocument.gchattoomuch.p2p.task;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.gdocument.gchattoomuch.ihm.browser.db.activity.BrowserDatabaseActivity;
import org.gdocument.gchattoomuch.ihm.browser.db.manager.ContentProviderManager;
import org.gdocument.gchattoomuch.ihm.browser.db.manager.ContentProviderManager.Column;
import org.gdocument.gchattoomuch.ihm.browser.db.model.ContentProviderData;
import org.gdocument.gchattoomuch.ihm.browser.db.model.DatabaseItem;
import org.gdocument.gchattoomuch.ihm.browser.db.service.DbContentProviderService;
import org.gdocument.gchattoomuch.lib.log.Logger;

import android.os.AsyncTask;
import android.os.Environment;

import com.cameleon.common.android.inotifier.INotifierMessage;
import com.cameleon.common.tool.FileTool;

public class ExtractDataTask extends AsyncTask<Void, Void, Void> {

	private static final String TAG = ExtractDataTask.class.getName();
	private static final String FILENAME_DATABASE_END_DB = ".db";
	private static final String FILENAME_DATABASE_END_ZIP = ".zip";
	private static final String FILENAME_DATABASE_START = "database-";

	private BrowserDatabaseActivity context;
	private INotifierMessage notifier;
	private List<DatabaseItem> databaseItemList = new ArrayList<DatabaseItem>();
	private String filterValue;
	private String filterColumn;
	private List<ContentProviderData> data;
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
	private String unzipDirectory;
	private String extractDirectory;
	private String destinationDirectory;
	private String databaseLocation;
	private boolean mergeDB = false;
	private DbContentProviderService dbContentProviderService;
	private List<String> listDatabaseName = new ArrayList<String>();

	private DatabaseItem currentDatabase;

	public ExtractDataTask(BrowserDatabaseActivity context, INotifierMessage notifier, DatabaseItem databaseItem, String filterColumn, String filterValue, boolean mergeDB) {
		this.context = context;
		this.notifier = notifier;
		this.filterColumn = filterColumn;
		this.filterValue = filterValue;
        this.databaseItemList.add(databaseItem);
        this.databaseLocation = "/data/data/" + this.context.getPackageName() + "/databases";
        this.destinationDirectory = Environment.getExternalStorageDirectory() + "/" + this.context.getPackageName();
		this.unzipDirectory = destinationDirectory + "/unzip";
		this.extractDirectory = destinationDirectory + "/extract";
		this.mergeDB = mergeDB;
	}

	@Override
	protected Void doInBackground(Void... params) {
		logMe("doInBackground");
		try {
			initialize();
	        File path = new File(destinationDirectory);
	        File[] fileList = path.listFiles();
	        for(File file : fileList) {
	        	String filename = file.getName().toLowerCase(Locale.getDefault());
	        	if (file.isFile() && filename.startsWith(FILENAME_DATABASE_START) && filename.endsWith(FILENAME_DATABASE_END_ZIP)) {
					filename = renameZipFilename(filename);
	        		for(DatabaseItem currentDatabase : databaseItemList) {
	        			unzipDatabase(file.getAbsolutePath(), currentDatabase);
	        			extractData(currentDatabase);

	        			if (this.data.size() > 0) {
		        			createDatabase(currentDatabase, filename);
		        			insertData();
	        			} else {
	        				notify("No data found file '" + filename + "'");
	        			}
	        		}
	        	}
	        }
	        extractDatabase();
		} catch(RuntimeException e) {
			logMe(e);
		} finally {
		}
		return null;
	}

	protected Void doInBackground2(Void... params) {
		logMe("doInBackground");
		try {
			initialize();
			File path = new File(destinationDirectory);
			File[] fileList = path.listFiles();
			for (File file : fileList) {
				String filename = file.getName().toLowerCase(Locale.getDefault());
				if (file.isFile()
					&& filename.startsWith(FILENAME_DATABASE_START)
					&& filename.endsWith(FILENAME_DATABASE_END_ZIP)) {
					filename = renameZipFilename(filename);
					for (DatabaseItem currentDatabase : databaseItemList) {
						unzipDatabase(file.getAbsolutePath(), currentDatabase);
						extractData(currentDatabase);

						if (this.data.size() > 0) {
							createDatabase(currentDatabase, filename);
							insertData();
						} else {
							notify("No data found file '" + filename + "'");
						}
					}
				}
			}
			extractDatabase();
		} catch (RuntimeException e) {
			logMe(e);
		} finally {
		}
		return null;
	}

	private void initialize() {
		File fileUnzip = new File(this.unzipDirectory);
		File fileExtract = new File(this.extractDirectory);

		if (!fileUnzip.exists()) {
			fileUnzip.mkdirs();
		}

		if (!fileExtract.exists()) {
			fileExtract.mkdirs();
		}
	}

	private void createDatabase(DatabaseItem databaseItem, String filename) {
//		String date = filename.substring("database-".length(), filename.lastIndexOf(".zip"));
//		String databaseName = databaseItem.getDatabaseName().substring(0, databaseItem.getDatabaseName().lastIndexOf(".db")) + "-" + date + ".db";
//		String databaseFilename = this.extractDirectory + "/" + databaseName; //= destinationDatabaseFilename;
//		this.dbContentProviderService = new DbContentProviderService(this.context, this.notifier, databaseFilename, databaseName);

		if (!this.mergeDB || this.dbContentProviderService == null) {
			String date = filename.substring("database-".length(), filename.lastIndexOf(".zip"));
			String databaseName = databaseItem.getDatabaseName().substring(0, databaseItem.getDatabaseName().lastIndexOf(".db")) + "-" + date + ".db";
			this.currentDatabase = new DatabaseItem(databaseName, databaseItem.getFilePath(), databaseName, null);
			this.dbContentProviderService = new DbContentProviderService(this.context, this.notifier, currentDatabase);
			this.dbContentProviderService.deleteAll();
	
			this.listDatabaseName.add(this.databaseLocation + "/" + databaseName);
	
			notify("Create Database Destination-file:'" + databaseName + "' DBName:" + databaseItem.getDatabaseName());
		}
	}

	private void unzipDatabase(final String filePath, final DatabaseItem currentDatabase) {
		File fileDatabase = new File(currentDatabase.getFilePath());
		fileDatabase.deleteOnExit();
		try {
	    	String name = ExtractDataTask.this.context.getFilename(currentDatabase.getFilePath());
			FileTool.getInstance().unzip(filePath, unzipDirectory, name);
		} catch (IOException e) {
			logMe(e);
		}
	}

    private void extractData(DatabaseItem database) {
    	String path = unzipDirectory + "/" + new File(database.getFilePath()).getName();
		File fileDatabase = new File(path);
    	if (fileDatabase.exists()) {
    		database.setFilePath(path);
    		try {
	    		DbContentProviderService dbContentProviderService = new DbContentProviderService(this.context, this.notifier, database);
				Column filterColumn = null;
				String filterValue = null;
				String order = null;
		        if (!this.filterColumn.isEmpty() && !this.filterValue.isEmpty()) {
					filterColumn = new ContentProviderManager.Column(this.filterColumn);
					filterValue = this.filterValue;
		        }
		        this.data = dbContentProviderService.getList(filterColumn, filterValue, order);
    		} finally {
    			fileDatabase.deleteOnExit();
    		}
    	} else {
    		notify("'" + database.getFilePath() + "' not exist");
    	}
    }

	private void insertData() {
		int i=0;
		try {
			for(ContentProviderData pojo : this.data) {
				if (this.mergeDB) {
					ContentProviderData o = null;
					try {
						o = dbContentProviderService.find(Long.parseLong(pojo.getData().get("_id")));
					} catch (RuntimeException e) {
						notify(e);
					}
					if (o == null) {
						dbContentProviderService.createOrUpdate(pojo);
						i++;
					}
				} else {
					pojo.setId(null);
					pojo.getData().remove("_id");
					dbContentProviderService.createOrUpdate(pojo);
					i++;
				}
			}
		} finally {
			notify("Insert " + i + " raw in '" + this.currentDatabase.getFilePath() + "'");
		}
	}

	private void extractDatabase() {
		String destinationZipName = this.extractDirectory + "/[database-" + sdf.format(new Date()) + "].zip";
		notify("Extrat database to Zip file '" + destinationZipName + "'\r\n");
		for(String name : this.listDatabaseName) {
			notify("-'" + name + "'\r\n");
		}
		FileTool.createZip(this.listDatabaseName, destinationZipName);
		notify("Zip file '" + destinationZipName + "' created");
	}

	private String renameZipFilename(String filename) {
		String ret = filename;

		String start = FILENAME_DATABASE_START;
		String end = FILENAME_DATABASE_END_ZIP;
		String date = filename.substring(start.length(), filename.lastIndexOf(end));
		if (date.length() == 13) {
			try {
				File root = new File(destinationDirectory);
				ret = filename.substring(0, filename.lastIndexOf(start) + start.length()) + sdf.format(new Date(Long.parseLong(date))) + end;
				boolean done = new File(root, filename).renameTo(new File(root, ret));
				notify("Rename Zip From:'" + filename + "' To:'" + ret + "' done:" + done);
			} catch (RuntimeException e) {
				notify("Erreur rename zip message:" + e.getMessage());
			}
		} else {
			notify("Rename Zip not needed filename:'" + filename + "' date:'" + date + "'");
		}

		return ret;
	}

	@Override
	protected void onPostExecute(Void param) {
	}

	@Override
	protected void onCancelled() {
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