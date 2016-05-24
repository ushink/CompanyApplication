package com.publish.monitorsystem.app.fragment;

import android.app.Application;
import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;

import com.msystemlib.base.BaseFragment;
import com.msystemlib.common.adapter.GirdViewAdapter;
import com.msystemlib.utils.ThreadUtils;
import com.msystemlib.utils.ToastUtils;
import com.publish.monitorsystem.R;
import com.publish.monitorsystem.api.Const;
import com.publish.monitorsystem.api.db.dao.BuildingDao;
import com.publish.monitorsystem.api.db.dao.EqptDao;
import com.publish.monitorsystem.api.db.dao.InventoryDao;
import com.publish.monitorsystem.api.db.dao.InventoryEqptDao;
import com.publish.monitorsystem.api.db.dao.RoomDao;
import com.publish.monitorsystem.api.db.dao.UploadInventoryDao;
import com.publish.monitorsystem.api.readrfid.ReadRFID;
import com.publish.monitorsystem.api.utils.SPconfig;
import com.publish.monitorsystem.app.EqptReadActivity;
import com.publish.monitorsystem.app.InventorySelActivity;
import com.publish.monitorsystem.application.SysApplication;

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
	SysApplication myapp;
	private ReadRFID readRFID;
	
	private final int INVENTORY = 100;
	private final int READ = 101;
	
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case INVENTORY:
				Intent intent1 = new Intent(getActivity(),
						InventorySelActivity.class);
				startActivity(intent1);
				getActivity().overridePendingTransition(
						R.anim.base_slide_right_in, R.anim.base_slide_remain);
				break;
			case READ:
				Intent intent = new Intent(getActivity(),
						EqptReadActivity.class);
				startActivity(intent);
				getActivity().overridePendingTransition(
						R.anim.base_slide_right_in, R.anim.base_slide_remain);
				break;
			}
		};
	};
	private String typeID;

	@Override
	public View initView() {
		View view = View.inflate(getActivity(), R.layout.fragment_business,
				null);
		ButterKnife.inject(this, view);
		tvTitle.setText("业务操作");

		typeID = SysApplication.gainData(Const.TYPEID).toString().trim();
		if ("1".equals(typeID) || "2".equals(typeID)) {
			names = new String[] { "盘点", "读取" };
			imageIds = new int[] { R.drawable.check, R.drawable.read };
		} else if ("3".equals(typeID)) {
			names = new String[] { "盘点", "读取", "查找"};
//			, "核检"  , R.drawable.again
			imageIds = new int[] { R.drawable.check, R.drawable.read,
					R.drawable.search };
		}

		adapter = new GirdViewAdapter(names) {

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View view = null;
				if (convertView == null) {
					view = View.inflate(getActivity(), R.layout.gird_item_home,
							null);
				} else {
					view = convertView;
				}

				TextView name = (TextView) view
						.findViewById(R.id.tv_name_grid_item);
				name.setText(names[position]);
				name.setTextColor(getActivity().getResources().getColor(
						R.color.title));

				ImageView image = (ImageView) view
						.findViewById(R.id.iv_icon_gird_item);
				image.setBackgroundResource(imageIds[position]);
				return view;
			}
		};
		gvItems.setAdapter(adapter);
		gvItems.setOnItemClickListener(this);
		return view;
	}

	@Override
	public void initData() {
		eqptDao = EqptDao.getInstance(getActivity());
		inventoryEqptDao = InventoryEqptDao.getInstance(getActivity());
		buildingDao = BuildingDao.getInstance(getActivity());
		roomDao = RoomDao.getInstance(getActivity());
		inventoryDao = InventoryDao.getInstance(getActivity());
		uploadInventoryDao = UploadInventoryDao.getInstance(getActivity());
		Application app = getActivity().getApplication();
		myapp = (SysApplication) app;
		myapp.spf = new SPconfig(getActivity());
		readRFID = new ReadRFID(myapp);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		switch (position) {
		case 0: // 盘点
			if ("1".equals(typeID)){
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
				ThreadUtils.runInBackground(new Runnable() {
					
					@Override
					public void run() {
						readRFID.initReader();
						handler.sendEmptyMessage(INVENTORY);
					}
				});
			}
			break;
		case 1: // 读取
			if ("1".equals(typeID)){
				if (eqptDao.getSize() < 0 || eqptDao.getSize() == 0) {
					ToastUtils.showToast(getActivity(), "请先下载数据");
					return;
				}
				ThreadUtils.runInBackground(new Runnable() {
					
					@Override
					public void run() {
						readRFID.initReader();
						handler.sendEmptyMessage(READ);
					}
				});
			}
			break;
		case 2: // 查找

			break;
//		case 3: // 核检
//
//			break;

		}
	}
}
