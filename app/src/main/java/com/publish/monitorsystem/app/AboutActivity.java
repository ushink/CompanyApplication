package com.publish.monitorsystem.app;

import android.app.Application;
import android.content.Context;
import android.os.SystemClock;
import android.view.View;
import android.widget.ImageView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import com.msystemlib.base.BaseActivity;
import com.msystemlib.img.ImgLoad;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.publish.monitorsystem.R;
import com.publish.monitorsystem.api.db.dao.InventoryEqptDao;
import com.publish.monitorsystem.api.db.dao.UploadInventoryEqptDao;
import com.publish.monitorsystem.application.SysApplication;

public class AboutActivity extends BaseActivity {
private InventoryEqptDao eqptDao;
private UploadInventoryEqptDao uploadInventoryEqptDao;
	@InjectView(R.id.iv)
	ImageView iv;
private SysApplication myapp;
	@Override
	public int bindLayout() {
		return R.layout.activity_about;
	}

	@Override
	public void initView(View view) {
		
		ButterKnife.inject(this);
		ImageLoader imageLoader = ImgLoad.initImageLoader(this);
		imageLoader.displayImage("drawable://" + R.drawable.logo,iv);

	}
	
	@OnClick(R.id.btn_click)
	public void onClick(){
		 SystemClock.setCurrentTimeMillis(5435454L);
	}

	@Override
	public void doBusiness(Context mContext) {
		Application app=getApplication();
		myapp=(SysApplication)app;
		eqptDao = InventoryEqptDao.getInstance(this);
		uploadInventoryEqptDao = UploadInventoryEqptDao.getInstance(this);
		eqptDao.updateInventoryEqpt();
		uploadInventoryEqptDao.deleteAllUploadInventoryEqpt();
		uploadInventoryEqptDao.deleteAllUploadInventory();
//		myapp.Devaddrs.clear();
	}

	@Override
	public void resume() {

	}

	@Override
	public void destroy() {

	}

}
