package com.publish.monitorsystem.app;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import com.msystemlib.base.BaseActivity;
import com.msystemlib.utils.ToastUtils;
import com.publish.monitorsystem.R;

public class FeedBackActivity extends BaseActivity {
	@InjectView(R.id.et_feedback)
	EditText etFeedBack;
	@InjectView(R.id.et_num)
	EditText etNum;
	@InjectView(R.id.tv_title)
	TextView tvTitle;
	@Override
	public int bindLayout() {
		return R.layout.activity_feedback;
	}

	@Override
	public void initView(View view) {
		ButterKnife.inject(this);
		tvTitle.setText("意见反馈");
	}

	@Override
	public void doBusiness(Context mContext) {
		
	}
	
	@OnClick(R.id.btn_submit)
	public void onSubmit(){
		String feedBack = etFeedBack.getText().toString().trim();
		String num = etNum.getText().toString().trim();
		if(TextUtils.isEmpty(feedBack) || TextUtils.isEmpty(num)){
			Toast.makeText(this, "请完善信息",Toast.LENGTH_SHORT).show();
			return;
		}
		ToastUtils.showToast(this, "已提交");
		finishActivity(this);
	}

	@Override
	public void resume() {

	}

	@Override
	public void destroy() {
	}

}
