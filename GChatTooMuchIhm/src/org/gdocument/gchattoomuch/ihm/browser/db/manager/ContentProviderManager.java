package org.gdocument.gchattoomuch.ihm.browser.db.manager;

import java.io.File;
import java.text.Format;
import java.util.ArrayList;
import java.util.List;

import org.gdocument.gchattoomuch.lib.log.Logger;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ContentProviderManager {

	private static final String TAG = ContentProviderManager.class.getName();

	private List<String> tableList = new ArrayList<String>();
	private List<String> columnList = new ArrayList<String>();
	private List<Column> columnListData = new ArrayList<Column>();
	private String tableName = null;
	private SQLiteOpenHelper dbHelper;

	public ContentProviderManager(Context context, SQLiteOpenHelper dbHelper) {
		this(context, dbHelper, null);
	}

	public ContentProviderManager(Context context, SQLiteOpenHelper dbHelper, List<Column> columnList) {
		this.dbHelper = dbHelper;
		initializeTableList();
		if (columnList == null || columnList.size() == 0) {
			initializeColumnList(context);
		} else {
			for(Column column : columnList) {
				this.columnList.add(column.getName());
			}
		}
	}

	public List<String> getColumnList() {
		return columnList;
	}

	public List<Column> getColumnListData() {
		return columnListData;
	}

	public String getTableName() {
		return tableName;
	}

	private void initializeColumnList(Context context) {
		if (columnList.size() == 0 && tableName != null) {
			columnListData.clear();
			Cursor cursor = null;
			try {
				SQLiteDatabase db = dbHelper.getReadableDatabase();
				cursor = db.query(tableName, null, null, null, null, null, null, "1");
				if (cursor!=null) {
					int columnsQty = cursor.getColumnCount();
					for(int i=0 ; i<columnsQty ; i++) {
						String name = cursor.getColumnName(i);
						columnList.add(name);
						columnListData.add(new Column(name));
					}
				}
			} finally {
				if (cursor!=null) {
					cursor.close();
				}
			}
		}
	}

	private void initializeTableList() {
		if (tableList.size() == 0) {
			Cursor cursor = null;
			try {
				SQLiteDatabase db = dbHelper.getReadableDatabase();
				File dbFile = new File(db.getPath());
				logMe("database path:" + dbFile.getPath() + " size:" + dbFile.length());
				cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type = 'table' and name != 'android_metadata'", null);
				if (cursor!=null && cursor.moveToFirst()) {
					do {
						String name = cursor.getString(0);
						logMe("tableName:" + name);
						tableList.add(name);
					} while (cursor.moveToNext());
				}
			} finally {
				if (cursor!=null) {
					cursor.close();
				}
				switch (tableList.size()) {
					case 0:
						logMe("No table found");
						break;
					case 1:
						tableName = tableList.get(0);
						break;
					default:
						logMe("Multiple table not supported");
				}
			}
		}
	}

	private void logMe(String msg) {
		Logger.logMe(TAG, msg);
	}

	@SuppressWarnings("unused")
	private static void logMe(Exception ex) {
		Logger.logMe(TAG, ex);
    }

	public static class Column {
		public enum TYPE {
			STRING, NUMBER, DATE
		};

		private String name;
		private Format format;
		private TYPE type = TYPE.STRING;

		public Column(String name) {
			this.name = name;
		}

		public Column(String name, Format format) {
			this.name = name;
			this.format = format;
		}

		public Column(String name, Format format, TYPE type) {
			this.name = name;
			this.format = format;
			this.type = type;
		}

		public String toString(String data) {
			return data;
		}
		
		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Format getFormat() {
			return format;
		}

		public void setFormat(Format format) {
			this.format = format;
		}

		public TYPE getType() {
			return type;
		}

		public void setType(TYPE type) {
			this.type = type;
		}
	}
}