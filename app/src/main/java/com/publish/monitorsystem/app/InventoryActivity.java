package com.publish.monitorsystem.app;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;

import com.msystemlib.base.BaseActivity;
import com.msystemlib.img.ImgLoad;
import com.msystemlib.utils.FileUtils;
import com.msystemlib.utils.LogUtils;
import com.msystemlib.utils.SPUtils;
import com.msystemlib.utils.ThreadUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.PauseOnScrollListener;
import com.pow.api.cls.RfidPower.PDATYPE;
import com.publish.monitorsystem.R;
import com.publish.monitorsystem.api.Const;
import com.publish.monitorsystem.api.bean.UploadInventoryEqpt;
import com.publish.monitorsystem.api.bean.EqptBean.Eqpt;
import com.publish.monitorsystem.api.db.dao.EqptDao;
import com.publish.monitorsystem.api.db.dao.InventoryDao;
import com.publish.monitorsystem.api.db.dao.InventoryEqptDao;
import com.publish.monitorsystem.api.db.dao.RoomDao;
import com.publish.monitorsystem.api.db.dao.UploadInventoryDao;
import com.publish.monitorsystem.api.db.dao.UploadInventoryEqptDao;
import com.publish.monitorsystem.api.readrfid.IRunneableReaderListener;
import com.publish.monitorsystem.api.readrfid.Runnable_Reader;
import com.publish.monitorsystem.api.utils.MyUtils;
import com.publish.monitorsystem.application.SysApplication;
import com.uhf.api.cls.Reader.READER_ERR;
import com.uhf.api.cls.Reader.TAGINFO;

public class InventoryActivity extends BaseActivity {
	@InjectView(R.id.btn_start)
	Button btnStart;
	@InjectView(R.id.btn_stop)
	Button btnStop;
	@InjectView(R.id.tv_title)
	TextView tv_title;
	@InjectView(R.id.lv_inventoryeqpt)
	ListView listView;
	
	private InventoryEqptDao inventoryEqptDao;
	private UploadInventoryEqptDao uploadInventoryeqptDao;
	private EqptDao eqptDao;
	private UploadInventoryDao uploadInventoryDao;
	private InventoryDao inventoryDao;
	private RoomDao roomDao;

	private Timer mTimer;
	private TimerTask mTimerTask;
	private List<String> sqls = new ArrayList<>();

	private List<Eqpt> inventoryEqptlist = new ArrayList<Eqpt>();

	private final int CLICK = 80;
	private final int FRUSH = 81;

	private int size;

	Map<String, TAGINFO> Devaddrs = new LinkedHashMap<String, TAGINFO>();// 有序
	private SoundPool soundPool;
	private SysApplication myapp;
	private Runnable_Reader runnable;
	private ImageLoader imageLoader;
	private String planIDparam;
	private String roomIDparam;
	private String planID;
	private String roomID;
	private Handler handler = new Handler(){
		@Override
		public void handleMessage (Message msg) {
			switch (msg.what){
				case CLICK:
					initClick();
					break;
				case FRUSH:
						frushAdapter();
					break;
			}
		}
	};
	private String inventoryID;
	private String parentPlanID;

	private void initClick () {
		if(inventoryEqptlist == null || inventoryEqptlist.size() == 0){
			btnStop.setEnabled(false);
			btnStart.setEnabled(false);
		}else{
			btnStop.setEnabled(false);
			btnStart.setOnClickListener(new OnClickListener() {

				@SuppressWarnings("unused")
				@Override
				public void onClick(View arg0) {

					try {
						startTimer();
						if (myapp.ThreadMODE == 0){
							handler.postDelayed(runnable, 0);
							runnable.setOnReadListener(new IRunneableReaderListener() {

								@Override
								public void setView() {

								}

								@Override
								public void setTagInventory_Raw(READER_ERR er) {
									handler.postDelayed(runnable, myapp.Rparams.sleep);
								}

								@Override
								public void setReadoncecnt(int i) {

								}

								@Override
								public void setReaderSound(String tag, TAGINFO tfs) {
									if (!myapp.Devaddrs.containsKey(planID + tag)
											&& !Devaddrs.containsKey(planID+ tag)
											&& inventoryEqptDao.gettagplanSize(planID,tag)>0) {
										//读取到标签响两声
										soundPool.play(1, 0.2f, 0.8f, 0, 0, 1);
										SystemClock.sleep(100);
										soundPool.play(1, 0.8f, 0.2f, 0, 0, 1);
										Devaddrs.put(planID + tag, tfs);
										Eqpt eqptByEPC = eqptDao.getEqptByEPC(tag);
										if(eqptByEPC != null){
											inventoryEqptDao.updateInventoryEqpt(eqptByEPC.EquipmentID, planID);
//										sqls.add("update inventoryeqpt set IsInventory = 1  where EquipmentID like'%"+eqptByEPC.EquipmentID+"%' and PlanID like '%"+planID+"%'");
											// 保存盘点设备信息
											Date date = new Date();// 创建一个时间对象，获取到当前的时间
											SimpleDateFormat sdf = new SimpleDateFormat(
													"yyyy/MM/dd HH:mm");// 设置时间显示格式
											String str = sdf.format(date);// 将当前时间格式化为需要的类型
											eqptDao.updateEqpt(eqptByEPC.EPC,str);
											if(inventoryEqptDao.isInventoryEqptID(eqptByEPC.EquipmentID,planID)){
												UploadInventoryEqpt eqpt = new UploadInventoryEqpt();
												eqpt.InfoID = UUID.randomUUID().toString();
												if(null==roomID){
													eqpt.RoomID = "";
												}else{
													eqpt.RoomID = roomID;
												}
												eqpt.PlanID = planID;
												eqpt.InventoryID = inventoryID;
												eqpt.ParentPlanID = parentPlanID;
												eqpt.EquipmentID = eqptByEPC.EquipmentID;
												eqpt.InventoryTime = str;
												uploadInventoryeqptDao.addUploadInventoryEqpt(eqpt);
											}
										}
									}
								}

								@Override
								public void setReaderError(READER_ERR er) {
									handler.removeCallbacks(runnable);

									btnStart.setText("读");
								}

								@Override
								public void setReadNum() {
									handler.postDelayed(runnable, myapp.Rparams.sleep);
								}
							});
						}
						ReadHandleUI();
					} catch (Exception ex) {
						Toast.makeText(InventoryActivity.this,
								"开始盘点失败：" + ex.getMessage(), Toast.LENGTH_SHORT)
								.show();
					}
				}
			});

			btnStop.setOnClickListener(new OnClickListener() {

				@SuppressWarnings("unused")
				@Override
				public void onClick(View arg0) {
					stopTimer();
					if (myapp.ThreadMODE == 0)
						handler.removeCallbacks(runnable);

					if (myapp.Rpower.GetType() == PDATYPE.SCAN_ALPS_ANDROID_CUIUS2) {
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					myapp.Devaddrs.putAll(Devaddrs);
					StopHandleUI();
				}

			});
		}
	}

	private CommonAdapter<Eqpt> adapter;

	@Override
	public int bindLayout() {
		return R.layout.activity_inventory;
	}

	@Override
	public void initView(View view) {
		ButterKnife.inject(this);
		tv_title.setText("自动盘点");
	}

	@Override
	public void doBusiness(Context mContext) {
		//数据处理层初始化
		inventoryEqptDao = InventoryEqptDao.getInstance(this);
		uploadInventoryDao = UploadInventoryDao.getInstance(this);
		eqptDao = EqptDao.getInstance(this);
		roomDao = RoomDao.getInstance(this);
		inventoryDao = InventoryDao.getInstance(this);
		uploadInventoryeqptDao = UploadInventoryEqptDao.getInstance(this);
		//变量初始化
		imageLoader = ImgLoad.initImageLoader(InventoryActivity.this);
		soundPool = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);
		soundPool.load(this, R.raw.beep, 1);
		Application app = getApplication();
		myapp = (SysApplication) app;
		runnable = new Runnable_Reader(myapp);
		myapp.Rparams = myapp.new ReaderParams();


		Intent intent = getIntent();
		planIDparam = intent.getStringExtra("planIDparam");
		roomIDparam = intent.getStringExtra("roomIDparam");

		ThreadUtils.runInBackground(new Runnable() {
			@Override
			public void run () {
				planID = inventoryDao.getInventoryPlanID1(planIDparam);
				roomID = roomDao.getRoomID(roomIDparam);
				parentPlanID = inventoryEqptDao.getParentPlanID(planID);
				inventoryID = uploadInventoryDao.getAllUploadInventory().get(0).InventoryID;
				SPUtils.saveString(InventoryActivity.this, "ParentPlanID", parentPlanID);
				inventoryEqptlist = inventoryEqptDao.getInventoryEqptList1(planID);
				handler.sendEmptyMessage(FRUSH);
				handler.sendEmptyMessage(CLICK);
			}
		});
	}

	/**
	 * listView刷新
	 */
	private void frushAdapter() {
		size = inventoryEqptDao.getinventoryeqptSize(planID,0);

		adapter = new CommonAdapter<Eqpt>(inventoryEqptlist) {

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View view = null;
				if("1".equals(SysApplication.gainData(Const.TYPEID).toString().trim())){
					ViewHolder vh;
					if (convertView == null) {
						view = getLayoutInflater()
								.inflate(R.layout.listitemview_inven,
										parent, false);
						vh = new ViewHolder(view);
						view.setTag(vh);
					} else {
						view = convertView;
						vh = (ViewHolder) view.getTag();
					}
					Eqpt eqpt = inventoryEqptlist.get(position);
					vh.tv_equipmentCode.setText(MyUtils.ToDBC("资产编号：\n" + eqpt.EquipmentCode));
					vh.tv_equipmentName
							.setText(MyUtils.ToDBC("资产名称：\n" + eqpt.EquipmentName));
					vh.tv_equipmentPosition.setText(MyUtils.ToDBC("物理位置：\n" + eqpt.EquipmentPosition));
					vh.tv_departmentName.setText(MyUtils.ToDBC("使用部门：\n" + eqpt.DepartmentName));
					if ((position + 1) > size) {
						vh.tv_equipmentCode.setTextColor(Color.BLACK);
						vh.tv_equipmentName.setTextColor(Color.BLACK);
						vh.tv_equipmentPosition.setTextColor(Color.BLACK);
						vh.tv_departmentName.setTextColor(Color.BLACK);
						view.setClickable(false);
					}else{
						vh.tv_equipmentCode.setTextColor(Color.RED);
						vh.tv_equipmentName.setTextColor(Color.RED);
						vh.tv_equipmentPosition.setTextColor(Color.RED);
						vh.tv_departmentName.setTextColor(Color.RED);
					}
				}else if("2".equals(SysApplication.gainData(Const.TYPEID).toString().trim())){
					ViewHolder vh;
					if (convertView == null) {
						view = getLayoutInflater()
								.inflate(R.layout.listitemview_inven,
										parent, false);
						vh = new ViewHolder(view);
						view.setTag(vh);
					} else {
						view = convertView;
						vh = (ViewHolder) view.getTag();
					}
					Eqpt eqpt = inventoryEqptlist.get(position);
					vh.tv_equipmentCode.setText(MyUtils.ToDBC("资产编号：\n" + eqpt.EquipmentCode));
					vh.tv_equipmentName
							.setText(MyUtils.ToDBC("资产名称：\n" + eqpt.EquipmentName));
					vh.tv_equipmentPosition.setText(MyUtils.ToDBC("物理位置：\n" + eqpt.EquipmentPosition));
					vh.tv_departmentName.setText(MyUtils.ToDBC("使用部门：\n" + eqpt.DepartmentName));
					vh.iv.setVisibility(View.VISIBLE);
					vh.ll_LC.setVisibility(View.VISIBLE);
					vh.iv_line.setVisibility(View.VISIBLE);
					vh.tv_LCCode.setText(MyUtils.ToDBC("浪潮编号：\n" + eqpt.LangChaoBianHao));
					vh.tv_contractName.setText(MyUtils.ToDBC("合同名称：\n" + eqpt.ContractName));
					imageLoader.displayImage("file://" + FileUtils.gainSDCardPath() +"/IMGcache/"+eqpt.ImageName,vh.iv);
					if ((position + 1) > size) {
						vh.tv_equipmentCode.setTextColor(Color.BLACK);
						vh.tv_equipmentName.setTextColor(Color.BLACK);
						vh.tv_equipmentPosition.setTextColor(Color.BLACK);
						vh.tv_departmentName.setTextColor(Color.BLACK);
						view.setClickable(false);
					}else{
						vh.tv_equipmentCode.setTextColor(Color.RED);
						vh.tv_equipmentName.setTextColor(Color.RED);
						vh.tv_equipmentPosition.setTextColor(Color.RED);
						vh.tv_departmentName.setTextColor(Color.RED);
					}
				}else if("3".equals(SysApplication.gainData(Const.TYPEID).toString().trim())){
					ViewHolder vh;
					if (convertView == null) {
						view = getLayoutInflater()
								.inflate(R.layout.listitemview_inven,
										parent, false);
						vh = new ViewHolder(view);
						view.setTag(vh);
					} else {
						view = convertView;
						vh = (ViewHolder) view.getTag();
					}
					Eqpt eqpt = inventoryEqptlist.get(position);
					vh.tv_equipmentCode.setText(MyUtils.ToDBC("资产编号：\n" + eqpt.EquipmentCode));
					vh.tv_equipmentName
							.setText(MyUtils.ToDBC("资产名称：\n" + eqpt.EquipmentName));
					vh.tv_equipmentPosition.setText(MyUtils.ToDBC("物理位置：\n" + eqpt.EquipmentPosition));
					vh.tv_departmentName.setText(MyUtils.ToDBC("档号：\n" + eqpt.FileCode));
					if ((position + 1) > size) {
						vh.tv_equipmentCode.setTextColor(Color.BLACK);
						vh.tv_equipmentName.setTextColor(Color.BLACK);
						vh.tv_equipmentPosition.setTextColor(Color.BLACK);
						vh.tv_departmentName.setTextColor(Color.BLACK);
						view.setClickable(false);
					}else{
						vh.tv_equipmentCode.setTextColor(Color.RED);
						vh.tv_equipmentName.setTextColor(Color.RED);
						vh.tv_equipmentPosition.setTextColor(Color.RED);
						vh.tv_departmentName.setTextColor(Color.RED);
					}
				}
				return view;
			}
		};
		listView.setAdapter(adapter);
		listView.setOnScrollListener(new PauseOnScrollListener(imageLoader, true, true)); // 设置滚动时不加载图片
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {

				if(!btnStop.isEnabled()){
//					if ((arg2 + 1) < size
//							|| (arg2 + 1) == size) {
						Eqpt eqpt = inventoryEqptlist.get(arg2);
						Intent intent = new Intent(InventoryActivity.this,
								InventoryhandActivity.class);
						intent.putExtra("eqpt", eqpt);
						intent.putExtra("planID", planID);
						intent.putExtra("roomID", roomID);
						intent.putExtra("functionID", "1");
						startActivity(intent);
						overridePendingTransition(R.anim.base_slide_right_in,
								R.anim.base_slide_remain);
//					}
				}
			}
		});
	}

//	@Override
//	protected void onActivityResult (int requestCode, int resultCode, Intent data) {
//		super.onActivityResult(requestCode, resultCode, data);
//		if(data != null){
//			if (resultCode == 55) {
//				int position = data.getIntExtra("position",0);
//				LinearLayout childAt = (LinearLayout) listView.getChildAt(position);
//				childAt.getChildAt()
//			}
//
//		}
//	}

	static class ViewHolder {
		@InjectView(R.id.tv_equipmentCode)
		TextView tv_equipmentCode;
		@InjectView(R.id.tv_equipmentName)
		TextView tv_equipmentName;

		@InjectView(R.id.tv_equipmentPosition)
		TextView tv_equipmentPosition;
		@InjectView(R.id.tv_departmentName)
		TextView tv_departmentName;
		@InjectView(R.id.tv_LCCode)
		TextView tv_LCCode;
		@InjectView(R.id.ll_LC)
		LinearLayout ll_LC;

		@InjectView(R.id.tv_contractName)
		TextView tv_contractName;
		@InjectView(R.id.iv_line)
		ImageView iv_line;
		@InjectView(R.id.iv)
		ImageView iv;

		public ViewHolder(View view) {
			ButterKnife.inject(this, view);
		}
	}
	private void ReadHandleUI() {
		this.btnStart.setEnabled(false);
		this.btnStop.setEnabled(true);
	}


	private void StopHandleUI() {
		ThreadUtils.runInBackground(new Runnable() {
			@Override
			public void run () {
				SystemClock.sleep(2000);
				do{
					inventoryEqptlist = inventoryEqptDao.getInventoryEqptList1(planID);
				}while(inventoryEqptlist ==null || inventoryEqptlist.size() == 0);
				handler.sendEmptyMessage(FRUSH);
			}
		});
		this.btnStart.setEnabled(true);
		this.btnStop.setEnabled(false);
	}


	private void startTimer(){
		if (mTimer == null) {
			mTimer = new Timer();
		}

		if (mTimerTask == null) {
			mTimerTask = new TimerTask() {
				@Override
				public void run() {
//					inventoryEqptDao.updateInventoryEqpts(sqls);
////					sqls.clear();
//					SystemClock.sleep(1000);
					do{
						inventoryEqptlist = inventoryEqptDao.getInventoryEqptList1(planID);
					}while(inventoryEqptlist ==null || inventoryEqptlist.size() == 0);
					handler.sendEmptyMessage(FRUSH);
				}
			};
		}

		if(mTimer != null && mTimerTask != null )
			mTimer.schedule(mTimerTask, 10 * 1000, 10 *1000);

	}

	private void stopTimer(){

		if (mTimer != null) {
			mTimer.cancel();
			mTimer = null;
		}

		if (mTimerTask != null) {
			mTimerTask.cancel();
			mTimerTask = null;
		}
	}

	@Override
	public void resume() {
	}

//	private List<Eqpt> temp = new ArrayList<>();
//	protected List<Eqpt> getData(){
//		temp  = inventoryEqptDao.getInventoryEqptList1(planID);
//		if(temp.size() == 0){
//			getData();
//		}else {
//			temp =getData();
//		}
//		return null;
//	}

	@Override
	public void destroy() {
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (btnStop.isEnabled()) {
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);

	}
}
