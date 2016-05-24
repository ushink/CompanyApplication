package com.publish.monitorsystem.app;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.Window;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import butterknife.ButterKnife;
import butterknife.InjectView;

import com.msystemlib.base.BaseFragment;
import com.msystemlib.common.adapter.ComFragmentAdapter;
import com.msystemlib.utils.StatuesUtils;
import com.publish.monitorsystem.R;
import com.publish.monitorsystem.app.fragment.BusinessFragment;
import com.publish.monitorsystem.app.fragment.MineFragment;
import com.publish.monitorsystem.app.fragment.UpdateFragment;

public class MainActivity extends FragmentActivity implements OnCheckedChangeListener {

	@InjectView(R.id.view_pager)
	ViewPager viewPager;
	@InjectView(R.id.rg_main)
	RadioGroup rgMain;
	@InjectView(R.id.rb_update)
	RadioButton rbUpdate;
	@InjectView(R.id.rb_business)
	RadioButton rbBusiness;
	@InjectView(R.id.rb_mine)
	RadioButton rbMine;

	private List<BaseFragment> pagerList;
	private ComFragmentAdapter adapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		StatuesUtils.openImmerseStatasBarMode(this);
		setContentView(R.layout.activity_main);
		ButterKnife.inject(this);
		Drawable[] drawables = rbUpdate.getCompoundDrawables();
		drawables[1].setBounds(0,0,drawables[1].getMinimumWidth() - 25,drawables[1].getMinimumHeight()- 25);
		rbUpdate.setCompoundDrawables(null,drawables[1],null,null);

		Drawable[] drawables1 = rbBusiness.getCompoundDrawables();
		drawables1[1].setBounds(0,0,drawables1[1].getMinimumWidth() - 25,drawables1[1].getMinimumHeight()- 25);
		rbBusiness.setCompoundDrawables(null,drawables1[1],null,null);

		Drawable[] drawables2 = rbMine.getCompoundDrawables();
		drawables2[1].setBounds(0,0,drawables2[1].getMinimumWidth() - 25,drawables2[1].getMinimumHeight()- 25);
		rbMine.setCompoundDrawables(null,drawables2[1],null,null);
		doBusiness(this);
	}

	/**
	 * 得到pagerList
	 * @return
	 */
	public List<BaseFragment> getPageList(){
		return pagerList;
	}

	private void doBusiness(Context mContext) {
		rgMain.setOnCheckedChangeListener(this);
		rgMain.check(R.id.rb_update);
		pagerList = new ArrayList<BaseFragment>();
		//添加3个子页面
		pagerList.add(new UpdateFragment());
		pagerList.add(new BusinessFragment());
		pagerList.add(new MineFragment());
		adapter = new ComFragmentAdapter(getSupportFragmentManager(),pagerList);
		viewPager.setAdapter(adapter);
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				if(arg0 == 0){
					rgMain.check(R.id.rb_update);
				}else if(arg0 == 1){
					rgMain.check(R.id.rb_business);
				}else if(arg0 == 2){
					rgMain.check(R.id.rb_mine);
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		int index = 0;

		switch (checkedId) {
		case R.id.rb_update://更新
			index = 0;
			rbUpdate.setTextColor(this.getResources().getColor(R.color.bottomcolor));
			rbBusiness.setTextColor(this.getResources().getColor(R.color.darkblack));
			rbMine.setTextColor(this.getResources().getColor(R.color.darkblack));
			break;
		case R.id.rb_business://业务
			index = 1;
			rbBusiness.setTextColor(this.getResources().getColor(R.color.bottomcolor));
			rbUpdate.setTextColor(this.getResources().getColor(R.color.darkblack));
			rbMine.setTextColor(this.getResources().getColor(R.color.darkblack));
			break;
		case R.id.rb_mine://我的
			index = 2;
			rbMine.setTextColor(this.getResources().getColor(R.color.bottomcolor));
			rbBusiness.setTextColor(this.getResources().getColor(R.color.darkblack));
			rbUpdate.setTextColor(this.getResources().getColor(R.color.darkblack));
			break;
		}
		//将页面设置到index下标的页面
		viewPager.setCurrentItem(index);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		/* 返回键 */
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			MainActivity.this.finish();
			overridePendingTransition(0, R.anim.base_slide_right_out);
		}
		return false;
	}

}
