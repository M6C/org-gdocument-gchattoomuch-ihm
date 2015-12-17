package org.gdocument.gchattoomuch.ihm.browser.db.helper;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.gdocument.gchattoomuch.ihm.browser.db.manager.ContentProviderManager;
import org.gdocument.gchattoomuch.ihm.browser.db.manager.ContentProviderManager.Column;
import org.gdocument.gchattoomuch.ihm.browser.db.model.DatabaseItem;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.cameleon.common.android.db.sqlite.helper.GenericDBHelper;
import com.cameleon.common.android.inotifier.INotifierMessage;

public class DBContentProviderHelper extends GenericDBHelper {

	@SuppressLint("SdCardPath")
	private static final String DATABASE_DIRECTORY = "/data/data/org.gdocument.gchattoomuch.ihm/databases";

	private static final String TAG = DBContentProviderHelper.class.getCanonicalName();

	public static final int DATABASE_VERSION = 1;

	private String path;
	private ContentProviderManager contentProviderManager;

	public DBContentProviderHelper(Context context, INotifierMessage notificationMessage, String path, String databaseName) {
		super(context, notificationMessage, databaseName, DATABASE_VERSION);
		this.path = path;

		contentProviderManager = new ContentProviderManager(context, this);
	}

	public DBContentProviderHelper(Context context, INotifierMessage notificationMessage, String path, String databaseName, List<Column> columnList) {
		super(context, notificationMessage, databaseName, DATABASE_VERSION);
		this.path = path;
		copyDatabase();

		contentProviderManager = new ContentProviderManager(context, this, columnList);
	}

	public DBContentProviderHelper(Context context, INotifierMessage notificationMessage, DatabaseItem database) {
		this(context, notificationMessage, database.getFilePath(), database.getDatabaseName(), database.getColumnList());
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
	}

	private void copyDatabase() {
		String currentPath = DATABASE_DIRECTORY;
		File dir = new File(currentPath);
		if (!dir.exists()) {
			logMe("create directory:" + currentPath);
			dir.mkdirs();
		}
        File backupDB = new File(dir, databaseName);
        if (backupDB.exists()) {
        	try {
				logMe("delete database:" + backupDB.getPath());
	        	backupDB.delete();
    		} catch (RuntimeException e) {
    			logMe(e);
    		}
        }
    	try {
    		File currentDB = new File(path);
			logMe("create database:" + backupDB.getPath() + " size:" + currentDB.length());
			backupDB.createNewFile();
			copyDb(currentDB, backupDB);
		} catch (IOException e) {
			logMe(e);
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
	}

	@Override
	public String getTag() {
		return TAG;
	}

	@Override
	public String getTableName() {
		return contentProviderManager.getTableName();
	}

	@Override
	public String getDatabaseCreate() {
		return null;
	}

	public List<String> getColumnList() {
		return contentProviderManager.getColumnList();
	}

	public List<Column> getColumnListData() {
		return contentProviderManager.getColumnListData();
	}

	@Override
	protected void logMe(String msg) {
		notify("****" + msg);
    }
	@Override
	protected void logMe(Exception ex) {
		notify(ex);
    }
}