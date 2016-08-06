package com.publish.monitorsystem.api.utils;

import java.util.HashSet;
import java.util.List;

/**
 * Created by jack on 2016/6/2.
 */
public class MyUtils {

    /**
     * 去除list重复数据
     * @param list
     */
    public   static   void  removeDuplicate(List list)   {
        HashSet h  =   new  HashSet(list);
        list.clear();
        list.addAll(h);
    }

    /**
     * 文字、数字、字符转全角
     * @param input
     * @return
     */
    public static String ToDBC(String input) {
        char[] c = input.toCharArray();
        for (int i = 0; i< c.length; i++) {
            if (c[i] == 12288) {
                c[i] = (char) 32;
                continue;
            }if (c[i]> 65280&& c[i]< 65375)
                c[i] = (char) (c[i] - 65248);
        }
        return new String(c);
    }

    /**
     *重复点击判断
     */
    private static long lastClickTime;
    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if ( 0 < timeD && timeD < 1000) {
            return true;
        }
        lastClickTime = time;
        return false;
    }
}
