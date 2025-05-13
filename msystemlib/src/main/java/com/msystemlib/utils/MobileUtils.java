package com.msystemlib.utils;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.annotation.SuppressLint;

public class MobileUtils {
	/**
	 * 得到手机MAC地址
	 */
	public static String getMacAddress(Context context) {
		String result = "";
		WifiManager wifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		result = wifiInfo.getMacAddress();
		return result;
	}

	/**
	 * 得到手机IMEI
	 * Note: getDeviceId() requires READ_PRIVILEGED_PHONE_STATE, which is only available to system apps.
	 * For regular apps, this will not work on Android 10+.
	 */
	@SuppressLint("MissingPermission")
	public static String getIMEI(Context context) {
		String result = "";
		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		result = tm.getDeviceId();
		return result;
	}
}
