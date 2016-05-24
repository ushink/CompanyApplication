package com.msystemlib.common.adapter;

import java.util.List;

import com.msystemlib.base.BaseFragment;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class ComFragmentAdapter extends FragmentStatePagerAdapter {

	protected List<BaseFragment> items;
	
	public ComFragmentAdapter(FragmentManager fm) {
		super(fm);
	}
	public ComFragmentAdapter(FragmentManager fm,List<BaseFragment> items) {
		super(fm);
		this.items = items;
	}

	@Override
	public Fragment getItem(int position) {
		return items.get(position);
	}

	@Override
	public int getCount() {
		return items.size();
	}
}
