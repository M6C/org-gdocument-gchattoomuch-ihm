package org.gdocument.gchattoomuch.ihm.browser.db.datasource;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.gdocument.gchattoomuch.ihm.browser.db.helper.DBContentProviderHelper;
import org.gdocument.gchattoomuch.ihm.browser.db.manager.ContentProviderManager;
import org.gdocument.gchattoomuch.ihm.browser.db.manager.ContentProviderManager.Column;
import org.gdocument.gchattoomuch.ihm.browser.db.manager.ContentProviderManager.ColumnDate;
import org.gdocument.gchattoomuch.ihm.browser.db.model.ContentProviderData;
import org.gdocument.gchattoomuch.ihm.browser.db.model.DatabaseItem;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.cameleon.common.android.db.sqlite.datasource.GenericDBDataSource;
import com.cameleon.common.android.inotifier.INotifierMessage;
import com.cameleon.common.tool.DbTool;

public class DBContentProviderDataSource extends GenericDBDataSource<ContentProviderData> {

	private static final String TAG = DBContentProviderDataSource.class.getCanonicalName();

	private String[] columnList = null;
	private List<Column> columnListData = null;

	public DBContentProviderDataSource(Context context, INotifierMessage notificationMessage, String path, String databaseName) {
		super(new DBContentProviderHelper(context, notificationMessage, path, databaseName), notificationMessage);
		columnList = ((DBContentProviderHelper)dbHelper).getColumnList().toArray(new String[0]);
	}

	public DBContentProviderDataSource(Context context, INotifierMessage notificationMessage, String path, String databaseName, List<Column> columnList) {
		super(new DBContentProviderHelper(context, notificationMessage, path, databaseName, columnList), notificationMessage);
		this.columnList = ((DBContentProviderHelper)dbHelper).getColumnList().toArray(new String[0]);
		this.columnListData = columnList;
	}

	public DBContentProviderDataSource(Context context, INotifierMessage notificationMessage, DatabaseItem database) {
		super(new DBContentProviderHelper(context, notificationMessage, database), notificationMessage);
		this.columnList = ((DBContentProviderHelper)dbHelper).getColumnList().toArray(new String[0]);
		this.columnListData = database.getColumnList() != null ? database.getColumnList() : ((DBContentProviderHelper)dbHelper).getColumnListData();
	}

	@Override
	protected String[] getAllColumns() {
		return columnList;
	}

	public List<ContentProviderData> getAll(Column column, String filter, String order) {
		Date dateStart = new Date();
		List<ContentProviderData> pojos = new ArrayList<ContentProviderData>();
		
		String where = null;
		if (column != null && filter != null) {
			where = column.getName() + " like '%" + filter +"%'";
		}
		where = customizeWhere(where);
		Cursor cursor = db.query(dbHelper.getTableName(), getAllColumns(), where, null, null, null, order);
		
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			ContentProviderData pojo = cursorToPojo(cursor);
			pojos.add(pojo);
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		
		logMe("getAllLike()", dateStart);
		return pojos;
	}

	@Override
	protected void putContentValue(ContentValues values, ContentProviderData content) {
		Map<String, String> data = content.getData();
		for(String name : columnList) {
			String d = data.get(name);
			if (d != null) {
				values.put(name, d);
			}
		}
	}

	@Override
	protected ContentProviderData cursorToPojo(Cursor cursor) {
		ContentProviderData ret = new ContentProviderData();
		HashMap<String, String> data = new HashMap<String, String>();
		List<ContentProviderManager.Column> listColumn = new ArrayList<ContentProviderManager.Column>();
		for(int i=0 ; i < columnList.length ; i++) {
			String name = columnList[i];
			String d = DbTool.getInstance().toString(cursor, i);
			Column nameToColumn = null;
			if (columnListData == null) {
				if (name.toLowerCase(Locale.getDefault()).contains("date")) {
					nameToColumn = new ColumnDate(name);
				} else {
					nameToColumn = new Column(name);
				}
			} else {
				nameToColumn = nameToColumn(name);
			}
			listColumn.add(nameToColumn);
			if (d != null) {
				data.put(name, nameToColumn.toString(d));
			}
		}
		ret.setData(data);
		ret.setColumnList(listColumn);
		return ret;
	}

	private Column nameToColumn(String name) {
		Column ret = null;
		for (ContentProviderManager.Column column : columnListData) {
			if (column.getName().equalsIgnoreCase(name)) {
				ret = column;
			}
		}
		if ((ret == null || ret instanceof Column) && name.toLowerCase(Locale.getDefault()).contains("date")) {
			ret = new ColumnDate(name);
		}
		return ret;
	}

	@Override
	protected String getTag() {
		return TAG;
	}

	public String[] getColumnList() {
		return columnList;
	}

	public List<Column> getColumnListData() {
		return columnListData;
	}
}