package com.msystemlib.http;

import com.google.gson.Gson;

public class JsonToBean {

	/**
	 * json转成对象
	 * @param jsonString
	 * @param cls
	 * @return
	 */
	public static <T> T getJsonBean(String jsonString, Class<T> cls) {
		T t = new Gson().fromJson(jsonString, cls);
		return t;
	}
	
	/**
	 * 将对象转成json
	 * @param t
	 * @return
	 */
	public static <T> String toJsonArray(T t){
		return new Gson().toJson(t);
	}
}
