package org.gdocument.gchattoomuch.ihm.browser.db.model;

import java.util.List;
import java.util.Map;

import org.gdocument.gchattoomuch.ihm.browser.db.manager.ContentProviderManager;

import com.cameleon.common.android.model.GenericDBPojo;

public class ContentProviderData extends GenericDBPojo<Long> {

	private static final long serialVersionUID = 1L;

	private List<ContentProviderManager.Column> columnList;
	private Map<String, String> data = null;

	public ContentProviderData() {
		super();
	}

	public ContentProviderData(Long id) {
		super(id);
	}

	public ContentProviderData(Map<String, String> data) {
		this.data = data;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public List<ContentProviderManager.Column> getColumnList() {
		return columnList;
	}

	public void setColumnList(List<ContentProviderManager.Column> columnList) {
		this.columnList = columnList;
	}

	public Map<String, String> getData() {
		return data;
	}

	public void setData(Map<String, String> data) {
		this.data = data;
	}
}