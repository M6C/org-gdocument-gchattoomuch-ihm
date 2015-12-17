package org.gdocument.gchattoomuch.ihm.browser.db.adapter;

import java.util.List;
import java.util.Map;

import org.gdocument.gchattoomuch.ihm.browser.db.activity.BrowserDatabaseActivity;
import org.gdocument.gchattoomuch.ihm.browser.db.manager.ContentProviderManager;
import org.gdocument.gchattoomuch.ihm.browser.db.model.ContentProviderData;
import org.gdocument.gchattoomuch.lib.log.Logger;

import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class BrowserDatabaseAdapter extends BaseAdapter {

	private static final String TAG = BrowserDatabaseAdapter.class.getName();
	private static final int CAR_SIZE = 35;

	private BrowserDatabaseActivity context;
	private List<ContentProviderData> data = null;
	private Map<String, Integer> columnSize;

	public BrowserDatabaseAdapter(BrowserDatabaseActivity context, List<ContentProviderData> data) {
		this.context = context;
		this.data = data;
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		int sumColumnSize = sumColumnSize();
		int padH = 1;
		int padV = 5;

		LinearLayout ll = new LinearLayout(context);
		ll.setBackgroundColor(Color.GREEN);
		ll.setOrientation(LinearLayout.VERTICAL);
		ll.setPadding(padH, padV, padH, padV);
		ll.setLayoutParams(new AbsListView.LayoutParams(sumColumnSize, LayoutParams.WRAP_CONTENT));

		if (position < data.size()) {
			ContentProviderData d = data.get(position);
			List<ContentProviderManager.Column> columnList = d.getColumnList();
			if (position==0) {
				LinearLayout llColumn = new LinearLayout(context);
				llColumn.setBackgroundColor(Color.GRAY);
				llColumn.setOrientation(LinearLayout.HORIZONTAL);
				llColumn.setPadding(padH, padV, padH, padV);
				llColumn.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
				ll.addView(llColumn);

				for(ContentProviderManager.Column column : columnList) {
					TextView tv = new TextView(context);
					tv.setLayoutParams(new LinearLayout.LayoutParams(columnSize(column.getName()), LayoutParams.WRAP_CONTENT));
					tv.setPadding(padH, padV, padH, padV);
					tv.setText(column.getName());
					llColumn.addView(tv);
				}
			}

			LinearLayout llData = new LinearLayout(context);
			llData.setBackgroundColor(Color.BLUE);
			llData.setOrientation(LinearLayout.HORIZONTAL);
			llData.setPadding(padH, padV, padH, padV);
			llData.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			ll.addView(llData);

			for(ContentProviderManager.Column column : columnList) {
				TextView tv = new TextView(context);
				tv.setPadding(padH, padV, padH, padV);
				tv.setLayoutParams(new LayoutParams(columnSize(column.getName()), LayoutParams.WRAP_CONTENT));
				tv.setText(d.getData().get(column.getName()));
				llData.addView(tv);
			}
		}
		return ll;
	}

	private int sumColumnSize() {
		int ret = 2000;
		if (columnSize != null) {
			ret = 0;
			for(int size : columnSize.values()) {
				ret += (size * CAR_SIZE);
			}
		}
		return ret;
	}

	private int columnSize(String name) {
		int ret = 20;
		if (columnSize != null && columnSize.containsKey(name)) {
			ret = (columnSize.get(name) * CAR_SIZE);
		}
		return ret;
	}

	public void setColumnSize(Map<String, Integer> columnSize) {
		this.columnSize = columnSize;
	}

	private void logMe(String msg) {
		Logger.logMe(TAG, msg);
	}

	@SuppressWarnings("unused")
	private static void logMe(Exception ex) {
		Logger.logMe(TAG, ex);
    }
}