package com.msystemlib.common;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;
import org.ksoap2.serialization.SoapObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.msystemlib.http.HttpConn;
import com.msystemlib.http.IWebServiceCallBack;
import com.msystemlib.utils.AlertUtils;
import com.msystemlib.utils.DateTimeUtils;
import com.msystemlib.utils.FileUtils;

/**
 * 共通机能-版本检测/更新
 */
public class VersionChecker {

	public static final String SERVER_VERSION = "version";
	public static final String CHANGE_LOG = "changeLog";
	public static final String APK_URL = "apkURL";
	public final static String SD_FOLDER = FileUtils.gainSDCardPath()+"/VersionChecker/";
	private static VersionChecker mChecker = new VersionChecker();
	private static final String TAG = VersionChecker.class.getSimpleName();
	
	public static void requestCheck(Context mContext,String strURL, String namespace,String methodName){
		mChecker.getLatestVersion(mContext, strURL,namespace,methodName);
	}
	
	/**
	 * 获取最新版本
	 * @param mContext
	 * @param strURL
	 * @param namespace 
	 * @param methodName 
	 * @return
	 */
	private JSONObject getLatestVersion(final Context mContext,String strURL, String namespace, String methodName){
		
		
		HttpConn.callService(strURL, namespace, methodName, null, new IWebServiceCallBack() {
			
			@Override
			public void onSucced(SoapObject result) {
				final String string = result.getProperty(0).toString();
				AlertUtils.dialog(mContext, "版本升级", "检测到有新版本，是否马上更新？",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,int which) {
								try {
									downLoadApk(mContext,string);
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,int which) {
								dialog.dismiss();
							}
						});
			}
			
			@Override
			public void onFailure(String result) {
				
			}
		});
		return null;
	}
	
	/**
	 * 从服务器中下载APK
	 */
	private void downLoadApk(final Context mContext,final String downURL) {
		final ProgressDialog pd; // 进度条对话框
		pd = new ProgressDialog(mContext);
		pd.setCancelable(false);// 必须一直下载完，不可取消
		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pd.setMessage("正在下载");
		pd.show();
		new Thread() {
			@Override
			public void run() {
				try {
					File file = downloadFile(downURL, pd);
					sleep(3000);
					installApk(mContext,file);
					// 结束掉进度条对话框
					pd.dismiss();
				} catch (Exception e) {
					pd.dismiss();
					Log.e(TAG, "下载新版本失败，原因：" + e.getMessage());
				}
			}
		}.start();
	}
	
	/**
	 * 从服务器下载最新更新文件
	 * 
	 * @param path
	 *            下载路径
	 * @param pd
	 *            进度条
	 * @return
	 * @throws Exception
	 */
	private File downloadFile(String path, ProgressDialog pd) throws Exception {
		// 如果相等的话表示当前的sdcard挂载在手机上并且是可用的
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			URL url = new URL(path);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5000);
			// 获取到文件的大小
			pd.setMax(conn.getContentLength());
			InputStream is = conn.getInputStream();
			String fileName = SD_FOLDER+DateTimeUtils.formatDateTime(new java.util.Date(),"yyyyMMddHHmm") + ".apk";
			File file = new File(fileName);
			// 目录不存在创建目录
			if (!file.getParentFile().exists())
				file.getParentFile().mkdirs();
			FileOutputStream fos = new FileOutputStream(file);
			BufferedInputStream bis = new BufferedInputStream(is);
			byte[] buffer = new byte[1024];
			int len;
			int total = 0;
			while ((len = bis.read(buffer)) != -1) {
				fos.write(buffer, 0, len);
				total += len;
				// 获取当前下载量
				pd.setProgress(total);
			}
			fos.close();
			bis.close();
			is.close();
			return file;
		} else {
			throw new IOException("未发现有SD卡");
		}
	}

	/**
	 * 安装apk
	 */
	private void installApk(Context mContext,File file) {
		Uri fileUri = Uri.fromFile(file);
		Intent it = new Intent();
		it.setAction(Intent.ACTION_VIEW);
		it.setDataAndType(fileUri, "application/vnd.android.package-archive");
		it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);// 防止打不开应用
		mContext.startActivity(it);
	}
	
	/**
	 * 获取应用程序版本（versionName）
	 * @return 当前应用的版本号
	 */
	private static double getLocalVersion(Context context) {
		PackageManager manager = context.getPackageManager();
		PackageInfo info = null;
		try {
			info = manager.getPackageInfo(context.getPackageName(), 0);
		} catch (NameNotFoundException e) {
			Log.e(TAG, "获取应用程序版本失败，原因："+e.getMessage());
			return 0.0;
		}
		
		return Double.valueOf(info.versionName);
	}
}
