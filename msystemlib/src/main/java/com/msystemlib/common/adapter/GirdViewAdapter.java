package com.msystemlib.common.adapter;

import android.widget.BaseAdapter;

public abstract class GirdViewAdapter extends BaseAdapter{

	private String[] items;
	public GirdViewAdapter(String[] items){
		this.items = items;
	}
	
	@Override
	public int getCount() {
		return items.length;
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
