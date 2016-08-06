package com.publish.monitorsystem.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;

import com.msystemlib.base.BaseActivity;
import com.msystemlib.utils.LogUtils;
import com.msystemlib.utils.SPUtils;
import com.msystemlib.utils.ThreadUtils;
import com.publish.monitorsystem.R;
import com.publish.monitorsystem.api.Const;
import com.publish.monitorsystem.api.bean.BuildingBean.Building;
import com.publish.monitorsystem.api.bean.InventoryBean.Inventory;
import com.publish.monitorsystem.api.bean.RoomBean.Room;
import com.publish.monitorsystem.api.db.dao.BuildingDao;
import com.publish.monitorsystem.api.db.dao.InventoryDao;
import com.publish.monitorsystem.api.db.dao.InventoryEqptDao;
import com.publish.monitorsystem.api.db.dao.RoomDao;
import com.publish.monitorsystem.api.readrfid.ReadRFID;
import com.publish.monitorsystem.application.SysApplication;

public class InventorySelActivity extends BaseActivity {
	@InjectView(R.id.tv_title)
	TextView tvTitle;
	@InjectView(R.id.spinner_inventorys)
	Spinner spinner_inventorys;
	@InjectView(R.id.spinner_rooms)
	Spinner spinner_rooms;
	@InjectView(R.id.ll_rooms)
	LinearLayout ll_rooms;
	@InjectView(R.id.spinner_buildings)
	Spinner spinner_buildings;
	@InjectView(R.id.btn_inventory)
	Button btn_inventory;
	private SysApplication myapp;
	private ReadRFID readRFID;
	
	private InventoryDao inventoryDao;
	private BuildingDao buildingDao;
	private RoomDao roomDao;
	private InventoryEqptDao inventoryEqptDao;
	
	private String[] inventorys,rooms;
	private String[] Buildings;

	private int roomPosition;

	private ArrayAdapter<String> adapter_inventorys;
	private ArrayAdapter<String> adapter_rooms;
	private ArrayAdapter<String> adapter_buildings;
	private Handler handler = new Handler(){

		public void handleMessage(android.os.Message msg) {
			init();
		};
	};
	
	@Override
	public int bindLayout() {
		return R.layout.activity_inventory_sel;
	}

	protected void init() {

		adapter_inventorys = new ArrayAdapter<String>(InventorySelActivity.this,R.layout.simple_spinner_item,inventorys);
		adapter_inventorys.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner_inventorys.setAdapter(adapter_inventorys);
		adapter_buildings = new ArrayAdapter<String>(InventorySelActivity.this,R.layout.simple_spinner_item,Buildings);
		adapter_buildings.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner_buildings.setAdapter(adapter_buildings);
		if("1".equals(SysApplication.gainData(Const.TYPEID).toString().trim()) || "2".equals(SysApplication.gainData(Const.TYPEID).toString().trim())){
			adapter_rooms = new ArrayAdapter<String>(InventorySelActivity.this,R.layout.simple_spinner_item,rooms);
			adapter_rooms.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinner_rooms.setAdapter(adapter_rooms);
		}
		btn_inventory.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				int inventoryPosition = spinner_inventorys.getSelectedItemPosition();
				int buildingPosition = spinner_buildings.getSelectedItemPosition();
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("planIDparam", inventorys[inventoryPosition]);
				if("1".equals(SysApplication.gainData(Const.TYPEID).toString().trim()) || "2".equals(SysApplication.gainData(Const.TYPEID).toString().trim())){
					roomPosition = spinner_rooms.getSelectedItemPosition();
					map.put("roomIDparam", rooms[roomPosition]);
				}else{
					map.put("roomIDparam", "-1");
				}
				jump2Activity(InventorySelActivity.this, InventoryActivity.class, map , false);
			}
		});		
	}

	@Override
	public void initView(View view) {
		ButterKnife.inject(this);
		tvTitle.setText("盘点选择");
		if(!"1".equals(SysApplication.gainData(Const.TYPEID).toString().trim()) && !"2".equals(SysApplication.gainData(Const.TYPEID).toString().trim())){
			ll_rooms.setVisibility(View.GONE);
		}
	}

	@Override
	public void doBusiness(Context mContext) {
//数据处理层初始化
		inventoryDao = InventoryDao.getInstance(this);
		buildingDao = BuildingDao.getInstance(this);
		roomDao = RoomDao.getInstance(this);
		inventoryEqptDao = InventoryEqptDao.getInstance(this);

		//变量初始化
		Application app=getApplication();
		myapp=(SysApplication)app;
		readRFID = new ReadRFID(myapp);
		ThreadUtils.runInBackground(new Runnable() {

			@Override
			public void run() {
				readRFID.initReader();
				List<Inventory> allInventoryList = inventoryDao.getAllInventoryList(1);
				List<String> temp = new ArrayList<String>();
				for (int i = 0; i < allInventoryList.size(); i++) {
					if(!allInventoryList.get(i).PlanID.equals(allInventoryList.get(i).ParentPlanID)){
						temp.add(allInventoryList.get(i).PlanName);
					}
				}
				inventorys = new String[temp.size()];
				for (int i = 0; i < temp.size(); i++) {
					inventorys[i] = temp.get(i);
				}
				List<Room> allRoomList = roomDao.getAllRoomList();
				rooms = new String[allRoomList.size()];
				for (int i = 0; i < allRoomList.size(); i++) {
					rooms[i] = allRoomList.get(i).RoomName;
				}
				List<Building> allBuildingList = buildingDao.getAllBuildingList();
				Buildings = new String[allBuildingList.size()];
				for (int i = 0; i < allBuildingList.size(); i++) {
					Buildings[i] = allBuildingList.get(i).BuildingName;
				}
				handler.sendEmptyMessage(100);
			}
		});
	}

	@Override
	public void resume() {

	}

	@Override
	public void destroy() {
		readRFID.colseReader();
	}

}
