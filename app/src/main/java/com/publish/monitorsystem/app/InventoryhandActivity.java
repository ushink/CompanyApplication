package com.publish.monitorsystem.app;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.os.SystemClock;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;

import com.msystemlib.base.BaseActivity;
import com.msystemlib.utils.FileUtils;
import com.publish.monitorsystem.R;
import com.publish.monitorsystem.api.Const;
import com.publish.monitorsystem.api.bean.UploadInventoryEqpt;
import com.publish.monitorsystem.api.bean.EqptBean.Eqpt;
import com.publish.monitorsystem.api.db.dao.EqptDao;
import com.publish.monitorsystem.api.db.dao.InventoryEqptDao;
import com.publish.monitorsystem.api.db.dao.UploadInventoryDao;
import com.publish.monitorsystem.api.db.dao.UploadInventoryEqptDao;
import com.publish.monitorsystem.api.readrfid.IRunneableReaderListener;
import com.publish.monitorsystem.api.readrfid.Runnable_Reader;
import com.publish.monitorsystem.application.SysApplication;
import com.uhf.api.cls.Reader.READER_ERR;
import com.uhf.api.cls.Reader.TAGINFO;
import com.publish.monitorsystem.utils.ImageUtils;

public class InventoryhandActivity extends BaseActivity{
	@InjectView(R.id.btn_start)
	Button btnStart;
	@InjectView(R.id.btn_stop)
	Button btnStop;
	@InjectView(R.id.tv_title)
	TextView tv_title;
	@InjectView(R.id.tv_readstatue)
	TextView tv_readstatue;
	@InjectView(R.id.tv_eqpt_code)
	TextView tv_eqpt_code;
	@InjectView(R.id.tv_eqpt_name)
	TextView tv_eqpt_name;
	@InjectView(R.id.tv_eqpt_type)
	TextView tv_eqpt_type;
	@InjectView(R.id.tv_eqpt_OutFactoryNum)
	TextView tv_eqpt_OutFactoryNum;
	@InjectView(R.id.tv_eqpt_InitialValue)
	TextView tv_eqpt_InitialValue;
	@InjectView(R.id.tv_eqpt_ProjectName)
	TextView tv_eqpt_ProjectName;
	@InjectView(R.id.tv_eqpt_EquipmentPosition)
	TextView tv_eqpt_EquipmentPosition;
	@InjectView(R.id.tv_eqpt_DepartmentName)
	TextView tv_eqpt_DepartmentName;
	@InjectView(R.id.tv_eqpt_ManagePerson)
	TextView tv_eqpt_ManagePerson;
	@InjectView(R.id.tv_eqpt_UsePerson)
	TextView tv_eqpt_UsePerson;
	@InjectView(R.id.tv_eqpt_IsSecret)
	TextView tv_eqpt_IsSecret;
	@InjectView(R.id.tv_desc_departmentName)
	TextView tv_desc_departmentName;

	@InjectView(R.id.ll_eqpt_InitialValue)
	LinearLayout ll_eqpt_InitialValue;
	@InjectView(R.id.ll_eqpt_ManagePerson)
	LinearLayout ll_eqpt_ManagePerson;
	@InjectView(R.id.ll_eqpt_OutFactoryNum)
	LinearLayout ll_eqpt_OutFactoryNum;
	@InjectView(R.id.ll_eqpt_ProjectName)
	LinearLayout ll_eqpt_ProjectName;
	@InjectView(R.id.ll_eqpt_type)
	LinearLayout ll_eqpt_type;
	@InjectView(R.id.ll_eqpt_IsSecret)
	LinearLayout ll_eqpt_IsSecret;
	@InjectView(R.id.ll_eqpt_uesPerson)
	LinearLayout ll_eqpt_uesPerson;
	@InjectView(R.id.iv)
	ImageView iv;
	
	private SoundPool soundPool;
	private SysApplication myapp;
	private Runnable_Reader runnable;
	Map<String,TAGINFO> Devaddrs=new LinkedHashMap<String,TAGINFO>();//有序
	private Handler handler = new Handler( );
	
	private InventoryEqptDao inventoryEqptDao;
	private EqptDao eqptDao;
	private UploadInventoryEqptDao uploadInventoryeqptDao;
	private UploadInventoryDao uploadInventoryDao;
	
	private String roomID;
	private Eqpt eqpt;
	private ImageUtils imageUtils = ImageUtils.getInstance();
	private String planID;
	private String functionID;
	private String InventoryFlag;

	@Override
	public int bindLayout() {
		return R.layout.activity_hand;
	}

	@Override
	public void initView(View view) {
		ButterKnife.inject(this);
		tv_title.setText("手动");
		if("2".equals(SysApplication.gainData(Const.TYPEID).toString().trim())){
			iv.setVisibility(View.VISIBLE);
			ll_eqpt_type.setVisibility(View.GONE);
			ll_eqpt_OutFactoryNum.setVisibility(View.GONE);
			ll_eqpt_InitialValue.setVisibility(View.GONE);
			ll_eqpt_ProjectName.setVisibility(View.GONE);
			ll_eqpt_ManagePerson.setVisibility(View.GONE);
			ll_eqpt_IsSecret.setVisibility(View.GONE);
		}else if("3".equals(SysApplication.gainData(Const.TYPEID).toString().trim())){
			ll_eqpt_type.setVisibility(View.GONE);
			ll_eqpt_OutFactoryNum.setVisibility(View.GONE);
			ll_eqpt_InitialValue.setVisibility(View.GONE);
			ll_eqpt_ProjectName.setVisibility(View.GONE);
			ll_eqpt_ManagePerson.setVisibility(View.GONE);
			ll_eqpt_IsSecret.setVisibility(View.GONE);
			ll_eqpt_uesPerson.setVisibility(View.GONE);
			tv_desc_departmentName.setText("档号：");
		}
	}

	@Override
	public void doBusiness(Context mContext) {

		//数据处理层初始化
		inventoryEqptDao = InventoryEqptDao.getInstance(this);
		eqptDao = EqptDao.getInstance(this);
		uploadInventoryDao = UploadInventoryDao.getInstance(this);
		uploadInventoryeqptDao = UploadInventoryEqptDao.getInstance(this);
		//变量初始化
		Intent intent = getIntent();
		eqpt = (Eqpt) intent.getSerializableExtra("eqpt");
		planID = intent.getStringExtra("planID");
		roomID = intent.getStringExtra("roomID");
		functionID = intent.getStringExtra("functionID");
		InventoryFlag = intent.getStringExtra("InventoryFlag");
		//view层初始化

		if("1".equals(InventoryFlag)){
			tv_readstatue.setText("已读取到");
			tv_readstatue.setTextColor(Color.BLACK);
		}else if("0".equals(InventoryFlag)){
			tv_readstatue.setText("未读取到");
			tv_readstatue.setTextColor(Color.RED);
		}

		if("1".equals(SysApplication.gainData(Const.TYPEID).toString().trim())){
			tv_eqpt_code.setText(eqpt.EquipmentCode);
			tv_eqpt_name.setText(eqpt.EquipmentName);
			tv_eqpt_type.setText(eqpt.Specification);
			tv_eqpt_OutFactoryNum.setText(eqpt.OutFactoryNum);
			tv_eqpt_InitialValue.setText(eqpt.InitialValue);
			tv_eqpt_ProjectName.setText(eqpt.ProjectName);
			tv_eqpt_EquipmentPosition.setText(eqpt.EquipmentPosition);
			tv_eqpt_DepartmentName.setText(eqpt.DepartmentName);
			tv_eqpt_ManagePerson.setText(eqpt.ManagePerson);
			tv_eqpt_UsePerson.setText(eqpt.UsePerson);
			if(null != eqpt.IsSecret && !"".equals(eqpt.IsSecret)){
				tv_eqpt_IsSecret.setText(Integer.parseInt(eqpt.IsSecret) == 0?"非涉密":"涉密");
			}
		}else if("2".equals(SysApplication.gainData(Const.TYPEID).toString().trim())){
			imageUtils.loadImage(getApplicationContext(), "file://" + FileUtils.gainSDCardPath() +"/IMGcache/"+eqpt.ImageName,iv);
			tv_eqpt_code.setText(eqpt.EquipmentCode);
			tv_eqpt_name.setText(eqpt.EquipmentName);
			tv_eqpt_EquipmentPosition.setText(eqpt.EquipmentPosition);
			tv_eqpt_DepartmentName.setText(eqpt.DepartmentName);
			tv_eqpt_UsePerson.setText(eqpt.UsePerson);
		}else if("3".equals(SysApplication.gainData(Const.TYPEID).toString().trim())){
			tv_eqpt_code.setText(eqpt.EquipmentCode);
			tv_eqpt_name.setText(eqpt.EquipmentName);
			tv_eqpt_EquipmentPosition.setText(eqpt.EquipmentPosition);
			tv_eqpt_DepartmentName.setText(eqpt.FileCode);
		}

		
		soundPool= new SoundPool(10,AudioManager.STREAM_SYSTEM,5);
		soundPool.load(this,R.raw.beep,1);
		Application app=getApplication();
		myapp=(SysApplication)app;
		runnable = new Runnable_Reader(myapp);
		myapp.Rparams=myapp.new ReaderParams();
		btnStop.setEnabled(false);
		btnStart.setOnClickListener(new OnClickListener()
		{

			@SuppressWarnings("unused")
			@Override
			public void onClick(View arg0) {
				try{
					handler.postDelayed(runnable,0);
					runnable.setOnReadListener(new IRunneableReaderListener() {
						
						@Override
						public void setView() {
							
						}
						
						@Override
						public void setTagInventory_Raw(READER_ERR er) {
							 handler.postDelayed(runnable,myapp.Rparams.sleep);
						}
						
						@Override
						public void setReadoncecnt(int i) {
							
						}
						
						@Override
						public void setReaderSound(String tag, TAGINFO tfs) {
							if(!Devaddrs.containsKey(tag)){
								soundPool.play(1,0.2f, 0.8f, 0, 0, 1);
								
								if(eqpt.EPC.equals(tag)){
									soundPool.play(1,0.2f, 0.2f, 0, 0, 1);
									SystemClock.sleep(100);
									soundPool.play(1,0.5f, 0.5f, 0, 0, 1);
									SystemClock.sleep(100);
									soundPool.play(1,0.8f, 0.8f, 0, 0, 1);
									tv_readstatue.setText("已读取到");
									tv_readstatue.setTextColor(Color.BLACK);
									Eqpt eqptByEPC = eqptDao.getEqptByEPC(tag);
									if(eqptByEPC != null) {
										inventoryEqptDao.updateInventoryEqpt(eqptByEPC.EquipmentID, planID);

										// 保存盘点设备信息
										Date date = new Date();// 创建一个时间对象，获取到当前的时间
										SimpleDateFormat sdf = new SimpleDateFormat(
												"yyyy/MM/dd HH:mm");// 设置时间显示格式
										String str = sdf.format(date);// 将当前时间格式化为需要的类型
										eqptDao.updateEqpt(eqptByEPC.EPC,str);
										if (inventoryEqptDao.isInventoryEqptID(eqptByEPC.EquipmentID, planID) && "1".equals(functionID)) {
											UploadInventoryEqpt eqpt = new UploadInventoryEqpt();
											eqpt.InfoID = UUID.randomUUID().toString();
											eqpt.RoomID = roomID;
											eqpt.PlanID = planID;
											eqpt.InventoryID = uploadInventoryDao.getAllUploadInventory().get(0).InventoryID;
											eqpt.ParentPlanID = inventoryEqptDao.getParentPlanID(planID);
											eqpt.EquipmentID = eqptByEPC.EquipmentID;
											eqpt.InventoryTime = str;
											uploadInventoryeqptDao.addUploadInventoryEqpt(eqpt);
										}
									}
								}
								Devaddrs.put(tag,tfs);
							}
						}
						
						@Override
						public void setReaderError(READER_ERR er) {
							handler.removeCallbacks(runnable); 

							btnStart.setText("读");
						}
						
						@Override
						public void setReadNum() {
							handler.postDelayed(runnable,myapp.Rparams.sleep);
						}
					});
					ReadHandleUI();
				}catch(Exception ex)
				{
					Toast.makeText(InventoryhandActivity.this, "开始盘点失败："+ex.getMessage(),
							Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		btnStop.setOnClickListener(new OnClickListener()
		{

			@SuppressWarnings("unused")
			@Override
			public void onClick(View arg0) {
				handler.removeCallbacks(runnable);
				if(false)
				{
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				StopHandleUI();
			}
			
		});
	}
	
	/**
	 * 读取按钮及结束按钮控制
	 */
 	private void ReadHandleUI()
	{
 		this.btnStart.setEnabled(false);
 		this.btnStop.setEnabled(true);
	}
	private void StopHandleUI()
	{
		this.btnStart.setEnabled(true);
		this.btnStop.setEnabled(false);
	}

	@Override
	public void resume() {
		
	}

	@Override
	public void destroy() {
		
	}
	
	@Override    
	public boolean onKeyDown(int keyCode, KeyEvent event) { 
		/**
		 * 没有停止读取不能退出该页面
		 */
	if(keyCode == KeyEvent.KEYCODE_BACK){ 
		if(btnStop.isEnabled()){
			return  true;
		}
	}  
	return  super.onKeyDown(keyCode, event);     

	}

}
