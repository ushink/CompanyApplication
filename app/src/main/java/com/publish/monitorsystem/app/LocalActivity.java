package com.publish.monitorsystem.app;

import java.util.HashMap;
import java.util.List;

import org.ksoap2.serialization.SoapObject;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Handler;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;

import com.msystemlib.base.BaseActivity;
import com.msystemlib.http.HttpConn;
import com.msystemlib.http.IWebServiceCallBack;
import com.msystemlib.http.JsonToBean;
import com.msystemlib.utils.AlertUtils;
import com.msystemlib.utils.LogUtils;
import com.msystemlib.utils.MobileUtils;
import com.msystemlib.utils.ThreadUtils;
import com.msystemlib.utils.ToastUtils;
import com.publish.monitorsystem.R;
import com.publish.monitorsystem.api.Const;
import com.publish.monitorsystem.api.bean.UserBean;
import com.publish.monitorsystem.api.bean.UserBean.User;
import com.publish.monitorsystem.api.db.dao.UserDao;
import com.publish.monitorsystem.api.utils.DialogUtils;
import com.publish.monitorsystem.view.MyProgressBar;

public class LocalActivity extends BaseActivity implements OnClickListener {

	@InjectView(R.id.rl_loaduser)
	RelativeLayout rlLoaduser;
	@InjectView(R.id.rl_checkversion)
	RelativeLayout rlCheckversion;
	@InjectView(R.id.rl_feedback)
	RelativeLayout rlFeedback;
	@InjectView(R.id.rl_about)
	RelativeLayout rlAbout;
	@InjectView(R.id.rl_logout)
	RelativeLayout rlLogout;
	@InjectView(R.id.tv_title)
	TextView tvTitle;
	@InjectView(R.id.tv_testconn)
	TextView tvTestconn;
	
	private final int VERSION = 100;//版本检查
	private final int UPDATE_USER = 101;//同步登录人员
	private final int FLUSH = 102;//测试连接刷新状态
	private boolean isRunning = false;//是否在测试连接
	private int firstIn = 0;//第一次执行onResume
	private int addCount;//人员下载进度参数
	
	private UserDao userDao;
	private List<User> userInfoList; 
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case VERSION:
				loadingDialog.dismiss();
				AlertUtils.dialog1(LocalActivity.this, "温馨提示", "当前版本已是最新版本！",new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}, null);
				break;
			case UPDATE_USER:
				pro.setProgress(i);
				tvPro.setText(i + "/100");
				if(i >= 100){
					downloadDialog.dismiss();
					i = 0;
					ToastUtils.showToast(LocalActivity.this, "下载成功");
				}
				break;
			case FLUSH:
				
				if(isRunning){
					//测试连接
					TestConn();
					handler.sendEmptyMessageDelayed(FLUSH, 5000);
				}
				break;
			}
			
		};
	};
	private Dialog loadingDialog;
	private int i;
	private MyProgressBar pro;
	private Dialog downloadDialog;
	private TextView tvPro;
	private String mobileIMEI;
	@Override
	public int bindLayout() {
		return R.layout.activity_local;
	}

	@Override
	public void initView(View view) {
		ButterKnife.inject(this);
		tvTitle.setText("后台管理");
		rlLoaduser.setOnClickListener(this);
		rlCheckversion.setOnClickListener(this);
		rlFeedback.setOnClickListener(this);
//		rlAbout.setOnClickListener(this);
		rlAbout.setVisibility(View.GONE);
		rlLogout.setOnClickListener(this);
		tvTestconn.setText("测试连接中...");
	}

	@Override
	public void doBusiness(Context mContext) {
		mobileIMEI = MobileUtils.getIMEI(this);
		userDao = UserDao.getInstance(this);
		TestConn();
		isRunning = true;
		handler.sendEmptyMessageDelayed(FLUSH, 5000);
		
	}

	@Override
	public void resume() {
		firstIn++;
		//activity第一次创建时不测试连接
		if(firstIn > 1){
			tvTestconn.setText("测试连接中...");
			tvTestconn.setTextColor(getResources().getColor(R.color.brown));
			TestConn();
			isRunning = true;
			handler.sendEmptyMessageDelayed(FLUSH, 5000);	
		}
		
	}
	@Override
	protected void onPause() {
		isRunning = false;
		super.onPause();
	}

	@Override
	public void destroy() {
		isRunning = false;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_loaduser://同步登录人员
			downloadDialog = showDownloadDialog(this, "正在准备下载...", "0/0", i);
			downloadDialog.show();
			HashMap<String, String> properties = new HashMap<String, String>();
			properties.put("IMEI", mobileIMEI);
			HttpConn.callService(Const.URL, Const.NAMESPACE, Const.GETLOGINUSERLIST, properties , new IWebServiceCallBack() {
				
				@Override
				public void onSucced(SoapObject result) {
					if(result != null){
						final String string = result.getProperty(0).toString();
						LogUtils.d("ckj", string);
						if(!"404".equals(string)){
							ThreadUtils.runInBackground(new Runnable() {

								@Override
								public void run() {
									saveUserInfo(string);
								}
							});
						}else{
							ToastUtils.showToast(LocalActivity.this, "没有人员信息");
							downloadDialog.dismiss();
						}

					}else{
						ToastUtils.showToast(LocalActivity.this, "联网失败");
						downloadDialog.dismiss();
					}
				}
				
				@Override
				public void onFailure(String result) {
					ToastUtils.showToast(LocalActivity.this, "联网失败");
					downloadDialog.dismiss();
				}
			});
			
			break;
		case R.id.rl_checkversion://版本更新检查
			loadingDialog = DialogUtils.createLoadingDialog(this, "正在检查，请稍后...");
			loadingDialog.show();
			ThreadUtils.runInBackground(new Runnable() {
				
				@Override
				public void run() {
					SystemClock.sleep(1500);
					handler.sendEmptyMessage(VERSION);
				}
			});
			break;
		case R.id.rl_feedback://意见反馈
			jump2Activity(LocalActivity.this,FeedBackActivity.class, null, false);
			break;
//		case R.id.rl_about://关于
//			jump2Activity(LocalActivity.this,AboutActivity.class, null, false);
//			break;
		case R.id.rl_logout://用户退出
			AlertUtils.dialog1(this, "退出", "是否退出当前账号?",new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					jump2Activity(LocalActivity.this,LoginActivity.class, null, true);
				}
			},new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			break;
		}
	}
	
	
	/**
	 * 保存用户信息
	 * @param msgObj
	 */
	protected void saveUserInfo(String msgObj) {
		userDao.deleteAllUserLog();
		addCount = 0;
		UserBean userInfoBean = JsonToBean.getJsonBean(msgObj,UserBean.class);
		userInfoList = userInfoBean.ds;
		for (User user : userInfoList) {
			userDao.addUserLog(user);
			addCount++;
			handler.sendEmptyMessage(UPDATE_USER);
		}
		if (addCount == userInfoList.size()) {
			downloadDialog.dismiss();
			ThreadUtils.runInMainThread(new Runnable() {
				@Override
				public void run() {
					AlertUtils.dialog1(LocalActivity.this, "提示", "人员同步成功！", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					}, null);
				}
			});
		} else {
			ThreadUtils.runInMainThread(new Runnable() {
				@Override
				public void run() {
					AlertUtils.dialog1(LocalActivity.this, "提示", "同步全部人员失败，请重新同步...", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						} 
					}, null);
				}
			});
		}
	}
	
	/**
	 * 下载提示对话框
	 * @param context 上下文
	 * @param msg 提示信息
	 * @param text 下载进度文字
	 * @param progress 下载进度
	 * @return
	 */
	private Dialog showDownloadDialog(Context context, String msg, String text, int progress) {  
		  
        LayoutInflater inflater = LayoutInflater.from(context);  
        View v = inflater.inflate(R.layout.download_dialog, null);// 得到加载view  
        LinearLayout layout = (LinearLayout) v.findViewById(R.id.dialog_view);// 加载布局  
        pro = (MyProgressBar) v.findViewById(R.id.pro);
        tvPro = (TextView) v.findViewById(R.id.tv_pro);  
        TextView tipTextView = (TextView) v.findViewById(R.id.tipTextView);// 提示文字  
        tipTextView.setText(msg);// 设置加载信息  
        tvPro.setText(text);//设置下载进度提示
        pro.setProgress(progress);
        pro.setMax(100);
        Dialog downloadDialog = new Dialog(context, R.style.loading_dialog);// 创建自定义样式dialog  
  
        downloadDialog.setCancelable(false);// 不可以用“返回键”取消  
        downloadDialog.setContentView(layout, new LinearLayout.LayoutParams(  
                LinearLayout.LayoutParams.MATCH_PARENT,  
                LinearLayout.LayoutParams.MATCH_PARENT));// 设置布局  
        return downloadDialog;  
  
    }  
	
	/**
	 * 测试连接
	 */
	public void TestConn(){
		HttpConn.callService(Const.URL, Const.NAMESPACE, Const.TESTCONNECT, null, new IWebServiceCallBack() {
			
			@Override
			public void onSucced(SoapObject result) {
				if(result != null){
					final String string = result.getProperty(0).toString();
					if("true".equals(string)){
						tvTestconn.setText("连接成功");
						tvTestconn.setTextColor(Color.GREEN);
					}else{
						tvTestconn.setText("连接失败");
						tvTestconn.setTextColor(Color.RED);
					}
				}
			}
			
			@Override
			public void onFailure(String result) {
				tvTestconn.setText("连接失败");
				tvTestconn.setTextColor(Color.RED);
			}
		});
	}

}
