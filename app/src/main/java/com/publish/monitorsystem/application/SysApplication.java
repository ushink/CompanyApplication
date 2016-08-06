package com.publish.monitorsystem.application;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.widget.TabHost;

import com.msystemlib.MApplication;
import com.pow.api.cls.RfidPower;
import com.publish.monitorsystem.api.utils.SPconfig;
import com.publish.monitorsystem.app.LoginActivity;
import com.uhf.api.cls.Reader;

public class SysApplication extends MApplication {

	/**对外提供整个应用生命周期的Context**/
	private static Context instance;
	private static final String LOG_DIR = Environment
            .getExternalStorageDirectory().getAbsolutePath() + "/monitorsystem/log/";
	private static final String LOG_NAME = getCurrentDateString() + ".txt";
	public static final String TAG = "jack";
	/**
	 * 对外提供Application Context
	 * @return
	 */
	public static Context gainContext() {
		return instance;
	}

	public void onCreate() {
		super.onCreate();
		instance = this;
		Thread.setDefaultUncaughtExceptionHandler(handler);
	}
	UncaughtExceptionHandler handler = new UncaughtExceptionHandler() {
		 
        @Override
        public void uncaughtException(Thread thread, Throwable ex) {
        	writeErrorLog(ex);
			Intent intent = new Intent();
			intent.setClass(instance, LoginActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			instance.startActivity(intent);
			android.os.Process.killProcess(android.os.Process.myPid());
        }
	};

	/**
     * 打印错误日志
     * 
     * @param ex
     */
    protected void writeErrorLog(Throwable ex) {
         FileOutputStream fileOutputStream = null;
        PrintStream printStream = null;
        try {
        	File dir = new File(LOG_DIR);
        	if (!dir.exists()) {
        		dir.mkdirs();
        	}
        	File file = new File(dir, LOG_NAME);
        	if (!file.exists()) {
        		file.createNewFile();
        	}
        	fileOutputStream = new FileOutputStream(file, true);
            printStream = new PrintStream(fileOutputStream);

			// 先写入手机的信息
			Class<?> clazz = Class.forName("android.os.Build");
			Field[] fields = clazz.getFields();// 获得所有的字员变量
			for (Field field : fields) {
				String name = field.getName(); // 获得成员变量的名称
				Object value = field.get(null); // 获得成员变量的值
				printStream.println(name+" : "+value); // 将成员变量的信息，写出日志文件
			}
			printStream.println("=============我是一条分隔线=====================");
            ex.printStackTrace(printStream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (printStream != null) {
                	printStream.flush();
                    printStream.close();
                }
                if(fileOutputStream != null){
                	 fileOutputStream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
	
    /**
     * 获取当前日期
     * 
     * @return    
     */
    private static String getCurrentDateString() {
        String result = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
                Locale.getDefault());
        Date nowDate = new Date();
        result = sdf.format(nowDate);
        return result;
    }
	/*******************************************Application中存放的Activity操作（压栈/出栈）API（结束）*****************************************/
    /*
	 * 公共变量   
	 */
	    public Map<String,Reader.TAGINFO> Devaddrs=new LinkedHashMap<String,Reader.TAGINFO>();//有序
		public String path;
		public String planID;
	    public int ThreadMODE=0;
		public int refreshtime=1000;
		public int Mode;
		public Map<String, String> m;
		public TabHost tabHost;
		public long exittime;

	    public Reader Mreader;
	    public int antportc;
	    public String Curepc;
		public int Bank;
		public int BackResult;

	    public SPconfig spf;
		public RfidPower Rpower;

		public ReaderParams Rparams = new ReaderParams();


		public class ReaderParams
		{
			public String Address;

			//save param
			public int opant;

			public List<String> invpro;
			public String opro;
			public int[] uants;
			public int readtime;
			public int sleep;

			public int checkant;
			public int[] rpow;
			public int[] wpow;

			public int region;
			public int[] frecys;
			public int frelen;

			public int session;
			public int qv;
			public int wmode;
			public int blf;
			public int maxlen;
			public int target;
			public int gen2code;
			public int gen2tari;

			public String fildata;
			public int filadr;
			public int filbank;
			public int filisinver;
			public int filenable;

			public int emdadr;
			public int emdbytec;
			public int emdbank;
			public int emdenable;

			public int antq;
			public int adataq;
			public int rhssi;
			public int invw;
			public int iso6bdeep;
			public int iso6bdel;
			public int iso6bblf;

			//other params

			public String password;
			public int optime;
			public ReaderParams()
			{
				opant=1;
				invpro=new ArrayList<String>();
				invpro.add("GEN2");
				uants=new int[1];
				uants[0]=1;
				sleep=0;
				readtime=200;
				optime=1000;
				opro="GEN2";
				checkant=1;
				rpow=new int[]{3000,500,500,500};
				wpow=new int[]{3000,500,500,500};
				region=1;
				frelen=0;
				session=0;
				qv=-1;
				wmode=0;
				blf=0;
				maxlen=0;
				target=0;
				gen2code=2;
				gen2tari=0;

				fildata="";
				filadr=32;
				filbank=1;
				filisinver=0;
				filenable=0;

				emdadr=0;
				emdbytec=0;
				emdbank=1;
				emdenable=0;

				adataq=0;
				rhssi=1;
				invw=0;
				iso6bdeep=0;
				iso6bdel=0;
				iso6bblf=0;
			}
		}
}
