package com.publish.monitorsystem.app.fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.publish.monitorsystem.R;
import com.msystemlib.base.BaseFragment;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MineFragment extends BaseFragment implements OnClickListener {
	@InjectView(R.id.tv_title)
	TextView tvTitle;
	@InjectView(R.id.rl_checkversion)
	RelativeLayout rlCheckversion;
	@InjectView(R.id.rl_feedback)
	RelativeLayout rlFeedback;
	@InjectView(R.id.rl_logout)
	RelativeLayout rlLogout;

	@Override
	public View initView() {
		View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_mine, null);
		ButterKnife.inject(this, view);
		tvTitle.setText("我的");
		rlCheckversion.setOnClickListener(this);
		rlFeedback.setOnClickListener(this);
		rlLogout.setOnClickListener(this);
		return view;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_checkversion:
			// TODO: Implement version check
			break;
		case R.id.rl_feedback:
			// TODO: Implement feedback
			break;
		case R.id.rl_logout:
			// TODO: Implement logout
			break;
		}
	}

	@Override
	public void initData() {
		// Реализация по необходимости
	}
}
