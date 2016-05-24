package com.msystemlib.utils;

import android.os.Handler;
import android.os.Looper;

public class ThreadUtils {
	public static Handler handler = new Handler(Looper.getMainLooper());

	/**
	 * 运行在子线程
	 */
	public static void runInBackground(Runnable runnable) {
		new Thread(runnable).start();
	}
	
	/**
	 * 运行在主线程
	 */
	public static void runInMainThread(Runnable runnable) {
		handler.post(runnable);
	}
}
