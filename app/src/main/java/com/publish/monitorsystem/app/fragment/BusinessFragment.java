package com.publish.monitorsystem.app.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import butterknife.ButterKnife;
import butterknife.InjectView;

import com.msystemlib.base.BaseFragment;
import com.msystemlib.common.adapter.GirdViewAdapter;
import com.msystemlib.utils.LogUtils;
import com.msystemlib.utils.ThreadUtils;
import com.publish.monitorsystem.R;
import com.publish.monitorsystem.api.Const;
import com.publish.monitorsystem.api.db.dao.BuildingDao;
import com.publish.monitorsystem.api.db.dao.EqptDao;
import com.publish.monitorsystem.api.db.dao.InventoryDao;
import com.publish.monitorsystem.api.db.dao.InventoryEqptDao;
import com.publish.monitorsystem.api.db.dao.RoomDao;
import com.publish.monitorsystem.api.db.dao.UploadInventoryDao;
import com.publish.monitorsystem.api.readrfid.ReadRFID;
import com.publish.monitorsystem.api.utils.MyUtils;
import com.publish.monitorsystem.api.utils.SPconfig;
import com.publish.monitorsystem.app.EqptReadActivity;
import com.publish.monitorsystem.app.InventorySelActivity;
import com.publish.monitorsystem.app.SerachListActivity;
import com.publish.monitorsystem.application.SysApplication;
import com.publish.monitorsystem.utils.ToastUtils;

public class BusinessFragment extends BaseFragment implements
		OnItemClickListener {
	@InjectView(R.id.tv_title)
	TextView tvTitle;
	@InjectView(R.id.gv_items)
	GridView gvItems;

	private EqptDao eqptDao;
	private BuildingDao buildingDao;
	private RoomDao roomDao;
	private InventoryDao inventoryDao;
	private InventoryEqptDao inventoryEqptDao;
	private UploadInventoryDao uploadInventoryDao;
	private String[] names;
	private int[] imageIds;
	private GirdViewAdapter adapter;

	private String typeID;

	@Override
	public View initView() {
		View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_business, null);
		ButterKnife.inject(this, view);
		tvTitle.setText("业务操作");
		gvItems.setOnItemClickListener(this);
		return view;
	}

	@Override
	public void initData() {
		//数据处理层初始化
		eqptDao = EqptDao.getInstance(getActivity());
		inventoryEqptDao = InventoryEqptDao.getInstance(getActivity());
		buildingDao = BuildingDao.getInstance(getActivity());
		roomDao = RoomDao.getInstance(getActivity());
		inventoryDao = InventoryDao.getInstance(getActivity());
		uploadInventoryDao = UploadInventoryDao.getInstance(getActivity());
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		switch (position) {
		case 0: // 盘点
			if (eqptDao.getSize() < 0 || eqptDao.getSize() == 0
					|| inventoryEqptDao.getSize() < 0
					|| inventoryEqptDao.getSize() == 0
					|| buildingDao.getSize() < 0 || buildingDao.getSize() == 0
					|| roomDao.getSize() < 0 || roomDao.getSize() == 0
					|| inventoryDao.getSize() < 0
					|| inventoryDao.getSize() == 0
					|| uploadInventoryDao.getSize() < 0
					|| uploadInventoryDao.getSize() == 0) {
				ToastUtils.showToast(getActivity(), "请先下载数据");
				return;
			}
			if(!MyUtils.isFastDoubleClick()) {
				Intent intent1 = new Intent(getActivity(),
						InventorySelActivity.class);
				startActivity(intent1);
				getActivity().overridePendingTransition(
						R.anim.base_slide_right_in, R.anim.base_slide_remain);
			}
			break;
		case 1: // 读取
			if (eqptDao.getSize() < 0 || eqptDao.getSize() == 0) {
				ToastUtils.showToast(getActivity(), "请先下载数据");
				return;
			}
			if(!MyUtils.isFastDoubleClick()){
				Intent intent = new Intent(getActivity(),
						EqptReadActivity.class);
				startActivity(intent);
				getActivity().overridePendingTransition(
						R.anim.base_slide_right_in, R.anim.base_slide_remain);
			}
			break;
		case 2: // 查找
			if(!MyUtils.isFastDoubleClick()){
				Intent intent = new Intent(getActivity(),
						SerachListActivity.class);
				startActivity(intent);
				getActivity().overridePendingTransition(
						R.anim.base_slide_right_in, R.anim.base_slide_remain);
			}
			break;
//		case 3: // 核检
//
//			break;

		}
	}
}
