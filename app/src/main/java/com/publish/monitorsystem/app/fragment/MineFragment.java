package com.publish.monitorsystem.app.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import butterknife.ButterKnife;
import butterknife.InjectView;

import com.msystemlib.base.BaseFragment;
import com.msystemlib.utils.AlertUtils;
import com.msystemlib.utils.ThreadUtils;
import com.publish.monitorsystem.R;
import com.publish.monitorsystem.api.utils.DialogUtils;
import com.publish.monitorsystem.app.AboutActivity;
import com.publish.monitorsystem.app.FeedBackActivity;
import com.publish.monitorsystem.app.LoginActivity;

public class MineFragment extends BaseFragment implements OnClickListener {
	
	
	@InjectView(R.id.rl_checkversion)
	RelativeLayout rlCheckversion;
	@InjectView(R.id.rl_feedback)
	RelativeLayout rlFeedback;
	@InjectView(R.id.rl_about)
	RelativeLayout rlAbout;
	@InjectView(R.id.rl_logout)
	RelativeLayout rlLogout;
	
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			loadingDialog.dismiss();
			AlertUtils.dialog1(getActivity(), "温馨提示", "当前版本已是最新版本！",new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			}, null);
		};
	};
	private Dialog loadingDialog;
	@Override
	public View initView() {
		View view = View.inflate(getActivity(), R.layout.fragment_mine, null);
		ButterKnife.inject(this,view);
		rlCheckversion.setOnClickListener(this);
		rlFeedback.setOnClickListener(this);
		rlAbout.setOnClickListener(this);
		rlLogout.setOnClickListener(this);
		return view;
	}

	@Override
	public void initData() {
		
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_checkversion:
			loadingDialog = DialogUtils.createLoadingDialog(getActivity(), "正在检查，请稍后...");
			loadingDialog.show();
			ThreadUtils.runInBackground(new Runnable() {
				
				@Override
				public void run() {
					SystemClock.sleep(2000);
					handler.sendEmptyMessage(100);
				}
			}); 
			break;
		case R.id.rl_feedback:
			Intent intent = new Intent(getActivity(),FeedBackActivity.class);
			startActivity(intent);
			getActivity().overridePendingTransition(R.anim.base_slide_right_in, R.anim.base_slide_remain);
			break;
		case R.id.rl_about:
			Intent intent1 = new Intent(getActivity(),AboutActivity.class);
			startActivity(intent1);
			getActivity().overridePendingTransition(R.anim.base_slide_right_in, R.anim.base_slide_remain);
			break;
		case R.id.rl_logout:
			AlertUtils.dialog1(getActivity(), "退出", "是否退出当前账号?",new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Intent intent2 = new Intent(getActivity(),LoginActivity.class);
					startActivity(intent2);
					getActivity().finish();
					getActivity().overridePendingTransition(R.anim.base_slide_right_in, R.anim.base_slide_remain);
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
}
