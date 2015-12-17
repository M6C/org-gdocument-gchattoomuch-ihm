package org.gdocument.gchattoomuch.ihm.browser.db.service;

import java.util.List;

import org.gdocument.gchattoomuch.ihm.browser.db.datasource.DBContentProviderDataSource;
import org.gdocument.gchattoomuch.ihm.browser.db.manager.ContentProviderManager.Column;
import org.gdocument.gchattoomuch.ihm.browser.db.model.ContentProviderData;
import org.gdocument.gchattoomuch.ihm.browser.db.model.DatabaseItem;

import android.content.Context;

import com.cameleon.common.android.db.sqlite.service.GenericService;
import com.cameleon.common.android.inotifier.INotifierMessage;

public class DbContentProviderService extends GenericService<ContentProviderData> {

	public DbContentProviderService(Context context, INotifierMessage notificationMessage, String path, String databaseName) {
		super(context, new DBContentProviderDataSource(context, notificationMessage, path, databaseName), notificationMessage);
	}

	public DbContentProviderService(Context context, INotifierMessage notificationMessage, String path, String databaseName, List<Column> columnList) {
		super(context, new DBContentProviderDataSource(context, notificationMessage, path, databaseName, columnList), notificationMessage);
	}

	public DbContentProviderService(Context context, INotifierMessage notificationMessage, DatabaseItem database) {
		super(context, new DBContentProviderDataSource(context, notificationMessage, database), notificationMessage);
	}

	public String[] getColumnList() {
		return ((DBContentProviderDataSource)dbDataSource).getColumnList();
	}

	public List<Column> getColumnListData() {
		return ((DBContentProviderDataSource)dbDataSource).getColumnListData();
	}

	public void deleteAll() {
    	try {
    		dbDataSource.open();
    		// Delete in database
			dbDataSource.delete((String)null);
    	}
    	finally {
    		dbDataSource.close();
    	}
	}

	public List<ContentProviderData> getList(Column column, String filter, String order) {
    	try {
    		dbDataSource.open();
			return ((DBContentProviderDataSource)dbDataSource).getAll(column, filter, order);
    	}
    	finally {
    		dbDataSource.close();
    	}
	}
}