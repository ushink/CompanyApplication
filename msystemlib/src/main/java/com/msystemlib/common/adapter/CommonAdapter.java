package com.msystemlib.common.adapter;

import java.util.List;

import android.widget.BaseAdapter;

public abstract class CommonAdapter<T> extends BaseAdapter {

	
	protected List<T> items;

	public CommonAdapter(List<T> items){
		this.items = items;
	}
	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

}
