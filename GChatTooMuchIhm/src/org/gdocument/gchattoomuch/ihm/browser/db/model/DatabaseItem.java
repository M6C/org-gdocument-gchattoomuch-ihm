package org.gdocument.gchattoomuch.ihm.browser.db.model;

import java.util.ArrayList;
import java.util.List;

import org.gdocument.gchattoomuch.ihm.browser.db.manager.ContentProviderManager;
import org.gdocument.gchattoomuch.ihm.browser.db.manager.ContentProviderManager.Column;

public class DatabaseItem {

	private String name = null;
	private String filePath = null;
	private String databaseName = null;
	private List<ContentProviderManager.Column> columnList = new ArrayList<ContentProviderManager.Column>();

	public DatabaseItem(String name, String filePath, String databaseName, List<Column> columnList) {
		super();
		this.name = name;
		this.filePath = filePath;
		this.databaseName = databaseName;
		this.columnList = columnList;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public String getDatabaseName() {
		return databaseName;
	}
	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}
	public List<ContentProviderManager.Column> getColumnList() {
		return columnList;
	}
	public void setColumnList(List<ContentProviderManager.Column> columnList) {
		this.columnList = columnList;
	}
}