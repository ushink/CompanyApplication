package com.msystemlib.http;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.os.Handler;
import android.os.Message;

import com.msystemlib.utils.SoapUtils;
import org.json.JSONObject;

public class HttpConn {
	
	// 含有5个线程的线程池
	private static final ExecutorService executorService = Executors.newFixedThreadPool(5);

	/**
	 * 
	 * @param url WebService服务器地址
	 * @param namespace 命名空间
	 * @param methodName WebService的调用方法名
	 * @param properties WebService的参数
	 * @param webServiceCallBack 返回结果回调接口
	 */
	public static void callService(String url, final String namespace, final String methodName, HashMap<String,String> properties, final IWebServiceCallBack webServiceCallBack) {
		// 用于子线程与主线程通信的Handler
		final Handler mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				if(msg.what == 0){
					webServiceCallBack.onSucced((JSONObject) msg.obj);
				}else{
					webServiceCallBack.onFailure((String)msg.obj);
				}
			}
		};

		// 开启线程去访问WebService
		executorService.submit(new Runnable() {
			@Override
			public void run() {
				Message mgs = mHandler.obtainMessage();
				try {
					// TODO: Сформировать soapEnvelope из properties
					SoapUtils.getInstance().callSoapService(url, namespace + methodName, "<soapEnvelope>", new SoapUtils.SoapCallback() {
						@Override
						public void onSuccess(JSONObject result) {
							mgs.what = 0;
							mgs.obj = result;
							mHandler.sendMessage(mgs);
						}

						@Override
						public void onFailure(String error) {
							mgs.what = 1;
							mgs.obj = error;
							mHandler.sendMessage(mgs);
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
					mgs.what = 1;
					mgs.obj = e.getMessage();
					mHandler.sendMessage(mgs);
				}
			}
		});
	}


}