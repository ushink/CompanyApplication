package com.msystemlib.view;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.msystemlib.R;

public class MyToast extends Toast {
	 
	public MyToast(Context context) {
		super(context);
	}
	public static Toast makeText(Context context, CharSequence text, int duration) {
        Toast result = new Toast(context);
 
        LayoutInflater inflate = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflate.inflate(R.layout.my_toast, null);
        TextView tv = (TextView)v.findViewById(R.id.my_toast);
        tv.setText(text);
        
        result.setView(v);
        //setGravity方法用于设置位置，此处为垂直居中
        result.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        result.setDuration(duration);
        return result;
    }
	
}
