package com.msystemlib.base;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;

import com.msystemlib.MApplication;
import com.msystemlib.R;
import com.msystemlib.utils.StatuesUtils;

/**
 * android 系统中的四大组件之一Activity基类
 * 
 * @version 1.0
 */
public abstract class BaseActivity extends Activity implements IBaseActivity {

	/*** 整个应用Applicaiton **/
	protected MApplication mApplication = null;
	/** 当前Activity的弱引用，防止内存泄露 **/
	private WeakReference<Activity> context = null;
	/** 当前Activity渲染的视图View **/
	private View mContextView = null;
	/** 共通操作 **/
	/** 日志输出标志 **/
	protected final String TAG = this.getClass().getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "BaseActivity-->onCreate()");
		// 无标题栏
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// //开启沉浸式状态栏
		StatuesUtils.openImmerseStatasBarMode(this);
		// 设置渲染视图View
		mContextView = LayoutInflater.from(this).inflate(bindLayout(), null);
		setContentView(mContextView);

		// 获取应用Application
		mApplication = (MApplication) getApplicationContext();

		// 将当前Activity压入栈
		context = new WeakReference<Activity>(this);
		mApplication.pushTask(context);
		// 初始化控件
		initView(mContextView);

		// 业务操作
		doBusiness(this);

		// 显示VoerFlowMenu
		// displayOverflowMenu(getContext()); // <--- commented out
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		Log.d(TAG, "BaseActivity-->onRestart()");
	}

	@Override
	protected void onStart() {
		super.onStart();
		Log.d(TAG, "BaseActivity-->onStart()");
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.d(TAG, "BaseActivity-->onResume()");
		resume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.d(TAG, "BaseActivity-->onPause()");
	}

	@Override
	protected void onStop() {
		super.onStop();
		Log.d(TAG, "BaseActivity-->onStop()");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "BaseActivity-->onDestroy()");

		destroy();
		mApplication.removeTask(context);
	}

	/**
	 * 显示Actionbar菜单图标
	 */
	@Override
	public boolean onMenuOpened(int featureId, Menu menu) {
		if (featureId == Window.FEATURE_ACTION_BAR && menu != null) {
			if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
				try {
					Method m = menu.getClass().getDeclaredMethod(
							"setOptionalIconsVisible", Boolean.TYPE);
					m.setAccessible(true);
					m.invoke(menu, true);// 显示
				} catch (Exception e) {
					Log.e(TAG, "onMenuOpened-->" + e.getMessage());
				}
			}
		}
		return super.onMenuOpened(featureId, menu);
	}

	/**
	 * 获取当前Activity
	 * 
	 * @return
	 */
	protected Activity getContext() {
		if (null != context)
			return context.get();
		else
			return null;
	}

	/**
	 * Actionbar点击返回键关闭事件
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			overridePendingTransition(0, R.anim.base_slide_right_out);
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(0, R.anim.base_slide_right_out);
	}

	/**
	 * 跳转到指定的activity
	 * @param self 当前activity
	 * @param clazz 要调转的activity
	 * @param map 页面调整传递的数据
	 * @param b	是否关闭当前activity
	 */
	public void jump2Activity(Activity self,Class<? extends Activity> clazz,HashMap<String,String> map,boolean b) {
		Intent intent = new Intent(self, clazz);
		if (map != null) {
			for (Iterator<Map.Entry<String, String>> it = map.entrySet().iterator(); it.hasNext();) {
				Map.Entry<String, String> entry = it.next();
				intent.putExtra(entry.getKey(), entry.getValue());
			}
		}
		if(b){
			self.finish();
		}
		startActivity(intent);
		overridePendingTransition(R.anim.base_slide_right_in,
				R.anim.base_slide_remain);
	}

	/**
	 * 关闭当前activity
	 */
	public void finishActivity(Activity self) {
		self.finish();
		overridePendingTransition(0, R.anim.base_slide_right_out);
	}

}
