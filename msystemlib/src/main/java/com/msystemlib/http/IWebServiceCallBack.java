package com.msystemlib.http;

import org.json.JSONObject;
/**
 * WebService回调接口
 */
public interface IWebServiceCallBack {
	
	public void onSucced (JSONObject result);
	
	public void onFailure (String result);
}
