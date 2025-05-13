package com.msystemlib.common;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.msystemlib.http.HttpConn;
import com.msystemlib.http.IWebServiceCallBack;
import com.msystemlib.utils.AlertUtils;
import com.msystemlib.utils.FileUtils;
import com.msystemlib.utils.SDUtils;
import com.msystemlib.utils.ToastUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 共通机能-版本检测/更新
 */
public class VersionChecker {

	private static String SD_FOLDER = FileUtils.gainSDCardPath()+"/VersionChecker/";
	private static VersionChecker mChecker = new VersionChecker();
	private static final String TAG = VersionChecker.class.getSimpleName();

	public static void requestCheck(Activity self, Class<? extends Activity> clazz, String strURL, String apkURL, String namespace, String methodName, HashMap<String,String> properties, List<String> colums) {
		mChecker.getLatestVersion(self, clazz, strURL, apkURL, namespace, methodName, properties, colums);
	}

	/**
	 * 获取最新版本
	 */
	private JSONObject getLatestVersion(final Activity self, final Class<? extends Activity> clazz, final String strURL, final String apkURL, String namespace, String methodName, HashMap<String,String> properties, final List<String> colums) {
		final double localVersion = getLocalVersion(self);

		HttpConn.callService(strURL, namespace, methodName, properties, new IWebServiceCallBack() {
			@Override
			public void onSucced(JSONObject result) {
				if(result != null) {
					try {
						String string = result.optString("result");
						JSONObject jsonObject = new JSONObject(string);
						JSONArray jsonArray = jsonObject.getJSONArray("ds");
						JSONObject jsonObject1 = new JSONObject(jsonArray.toString().replace("[", "").replace("]", ""));
						String VersionName = jsonObject1.getString(colums.get(0));
						final String updateUrl = jsonObject1.getString(colums.get(1));
						String VersionFileName = jsonObject1.getString(colums.get(2));
						File file = new File(SD_FOLDER);
						if(!file.exists()) {
							SD_FOLDER = SD_FOLDER + VersionFileName;
						}
						if(localVersion > Double.parseDouble(VersionName) || localVersion == Double.parseDouble(VersionName)) {
							gotoMainActivity(self, clazz);
						} else {
							//弹出更新提示
							AlertUtils.dialog(self, "版本升级", "检测到有新版本，是否马上更新？", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									try {
										downLoadApk(self, apkURL + updateUrl);
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							}, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									dialog.dismiss();
								}
							});
						}
					} catch (NumberFormatException e) {
						e.printStackTrace();
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}

			@Override
			public void onFailure(String result) {
				ToastUtils.showToast(self, "请检查网络连接");
			}
		});
		return null;
	}

	/**
	 * 从服务器中下载APK
	 */
	private void downLoadApk(final Activity activity, final String downURL) {
		OkHttpClient client = new OkHttpClient();
		Request request = new Request.Builder()
				.url(downURL)
				.build();

		client.newCall(request).enqueue(new Callback() {
			@Override
			public void onFailure(Call call, IOException e) {
				activity.runOnUiThread(() -> ToastUtils.showToast(activity, "下载失败"));
			}

			@Override
			public void onResponse(Call call, Response response) throws IOException {
				if (!response.isSuccessful()) {
					activity.runOnUiThread(() -> ToastUtils.showToast(activity, "下载失败"));
					return;
				}

				try {
					File file = new File(SD_FOLDER);
					if (!file.exists()) {
						file.createNewFile();
					}
					
					FileOutputStream fos = new FileOutputStream(file);
					fos.write(response.body().bytes());
					fos.close();
					
					installApk(activity, file);
				} catch (Exception e) {
					activity.runOnUiThread(() -> ToastUtils.showToast(activity, "下载失败"));
				}
			}
		});
	}

	/**
	 * 安装apk
	 */
	private void installApk(Context context, File file) {
		Uri fileUri = Uri.fromFile(file);
		Intent it = new Intent();
		it.setAction(Intent.ACTION_VIEW);
		it.setDataAndType(fileUri, "application/vnd.android.package-archive");
		it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);// 防止打不开应用
		context.startActivity(it);
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

	protected void gotoMainActivity(Activity self, Class<? extends Activity> clazz) {
		Intent intent = new Intent(self, clazz);
		self.startActivity(intent);
		// 如果不想再跳回来，就finish
		self.finish();
	}
}
