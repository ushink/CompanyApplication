package com.publish.monitorsystem.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;

public class USBBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals("android.hardware.usb.action.USB_STATE")) {
			if (intent.getExtras().getBoolean("connected")) {
				// usb 插入
				Intent intent1 = new Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS);  
				intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(intent1);
			} else {
			}
		}
	}
}
