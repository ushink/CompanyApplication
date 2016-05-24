package com.msystemlib.http;

import org.ksoap2.serialization.SoapObject;
/**
 * WebService回调接口
 */
public interface IWebServiceCallBack {
	
	public void onSucced (SoapObject result);
	
	public void onFailure (String result);
}
