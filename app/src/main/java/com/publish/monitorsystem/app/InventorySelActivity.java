package com.publish.monitorsystem.app;

import java.util.HashMap;
import java.util.List;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;

import com.msystemlib.base.BaseActivity;
import com.msystemlib.utils.SPUtils;
import com.msystemlib.utils.ThreadUtils;
import com.publish.monitorsystem.R;
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
		adapter_rooms = new ArrayAdapter<String>(InventorySelActivity.this,R.layout.simple_spinner_item,rooms); 
		adapter_rooms.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);    
		spinner_rooms.setAdapter(adapter_rooms);
		btn_inventory.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				int inventoryPosition = spinner_inventorys.getSelectedItemPosition();
				int buildingPosition = spinner_buildings.getSelectedItemPosition();
				int roomPosition = spinner_rooms.getSelectedItemPosition();
				String planID = inventoryDao.getInventoryPlanID(inventorys[inventoryPosition]);
				
				String parentPlanID = inventoryEqptDao.getParentPlanID(planID);
				SPUtils.saveString(InventorySelActivity.this, "ParentPlanID", parentPlanID);
				String roomID = roomDao.getRoomID(rooms[roomPosition]);
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("planID", planID);
				map.put("roomID", roomID);
				jump2Activity(InventorySelActivity.this, InventoryActivity.class, map , false);
			}
		});		
	}

	@Override
	public void initView(View view) {
		ButterKnife.inject(this);
	}

	@Override
	public void doBusiness(Context mContext) {
		inventoryDao = InventoryDao.getInstance(this);
		buildingDao = BuildingDao.getInstance(this);
		roomDao = RoomDao.getInstance(this);
		inventoryEqptDao = InventoryEqptDao.getInstance(this);
		Application app=getApplication();
		myapp=(SysApplication)app;
		readRFID = new ReadRFID(myapp);
		ThreadUtils.runInBackground(new Runnable() {

			@Override
			public void run() {
				List<Inventory> allInventoryList = inventoryDao.getAllInventoryList();
				inventorys = new String[allInventoryList.size() - 1];
				for (int i = 0; i < allInventoryList.size(); i++) {
					if(!allInventoryList.get(i).PlanID.equals(allInventoryList.get(i).ParentPlanID)){
						inventorys[i] = allInventoryList.get(i).PlanName;
					}
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
