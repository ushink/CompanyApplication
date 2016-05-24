package com.publish.monitorsystem.api.utils;

import java.util.List;

import com.msystemlib.http.JsonToBean;

public class FormatJsonUtils {

	public static String formatJson(List list) {
		String str = "{" + "\"" + "ds" + "\"" + ":[";
		int count = 0;
		for (int i = 0; i < list.size(); i++) {
			str += JsonToBean.toJsonArray(list.get(i));
			count++;
			if (count != list.size()) {
				str += ",";
			}
		}
		str += "]}";
		return str;
	}
}
