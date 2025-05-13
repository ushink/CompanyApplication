package com.msystemlib.common.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import java.util.ArrayList;
import java.util.List;

public class ComFragmentAdapter extends FragmentPagerAdapter {
	private List<Fragment> fragments = new ArrayList<>();

	public ComFragmentAdapter(FragmentManager fm) {
		super(fm);
	}

	public void addFragment(Fragment fragment) {
		fragments.add(fragment);
	}

	@Override
	public Fragment getItem(int position) {
		return fragments.get(position);
	}

	@Override
	public int getCount() {
		return fragments.size();
	}
}
