package com.publish.monitorsystem.app;

import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager.widget.ViewPager.OnPageChangeListener;
import com.msystemlib.common.adapter.ComFragmentAdapter;
import com.publish.monitorsystem.utils.SPUtils;
import com.publish.monitorsystem.R;
import com.publish.monitorsystem.app.fragment.BusinessFragment;
import com.publish.monitorsystem.app.fragment.MineFragment;
import com.publish.monitorsystem.app.fragment.UpdateFragment;

public class MainActivity extends FragmentActivity implements OnCheckedChangeListener {
	private RadioGroup radioGroup;
	private ViewPager viewPager;
	private ComFragmentAdapter fragmentAdapter;
	private BusinessFragment businessFragment;
	private UpdateFragment updateFragment;
	private MineFragment mineFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initView();
		initData();
	}

	private void initView() {
		radioGroup = findViewById(R.id.rg_main);
		viewPager = findViewById(R.id.view_pager);
		radioGroup.setOnCheckedChangeListener(this);
	}

	private void initData() {
		businessFragment = new BusinessFragment();
		updateFragment = new UpdateFragment();
		mineFragment = new MineFragment();
		fragmentAdapter = new ComFragmentAdapter(getSupportFragmentManager());
		fragmentAdapter.addFragment(businessFragment);
		fragmentAdapter.addFragment(updateFragment);
		fragmentAdapter.addFragment(mineFragment);
		viewPager.setAdapter(fragmentAdapter);
		viewPager.addOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				RadioButton radioButton = (RadioButton) radioGroup.getChildAt(position);
				radioButton.setChecked(true);
			}

			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
			}

			@Override
			public void onPageScrollStateChanged(int state) {
			}
		});
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch (checkedId) {
			case R.id.rb_business:
				viewPager.setCurrentItem(0);
				break;
			case R.id.rb_update:
				viewPager.setCurrentItem(1);
				break;
			case R.id.rb_mine:
				viewPager.setCurrentItem(2);
				break;
		}
	}
}
