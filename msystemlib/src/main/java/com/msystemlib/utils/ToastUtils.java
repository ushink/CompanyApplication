package com.msystemlib.utils;

import android.app.Activity;
import android.widget.Toast;

public class ToastUtils {
	//系统Toast
	public static void showToast(final Activity act,final String str){
		if("main".equals(Thread.currentThread().getName())){
			Toast.makeText(act, str, Toast.LENGTH_SHORT).show();
		}else{
			act.runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					Toast.makeText(act, str, Toast.LENGTH_SHORT).show();
				}
			});
		}
	}
}
