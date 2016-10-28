package com.publish.monitorsystem.app;

import android.app.Application;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.msystemlib.base.BaseActivity;
import com.msystemlib.img.ImgLoad;
import com.msystemlib.utils.FileUtils;
import com.msystemlib.utils.LogUtils;
import com.msystemlib.utils.ThreadUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.PauseOnScrollListener;
import com.pow.api.cls.RfidPower.PDATYPE;
import com.publish.monitorsystem.R;
import com.publish.monitorsystem.api.Const;
import com.publish.monitorsystem.api.bean.EqptBean.Eqpt;
import com.publish.monitorsystem.api.db.dao.EqptDao;
import com.publish.monitorsystem.api.readrfid.IRunneableReaderListener;
import com.publish.monitorsystem.api.readrfid.ReadRFID;
import com.publish.monitorsystem.api.readrfid.Runnable_Reader;
import com.publish.monitorsystem.api.utils.MyUtils;
import com.publish.monitorsystem.application.SysApplication;
import com.uhf.api.cls.Reader;
import com.uhf.api.cls.Reader.READER_ERR;
import com.uhf.api.cls.Reader.TAGINFO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static com.publish.monitorsystem.R.layout.activity_eqptread;

public class EqptReadActivity extends BaseActivity {


	@InjectView(R.id.btn_start)
	Button btnStart;
	@InjectView(R.id.btn_stop)
	Button btnStop;
	@InjectView(R.id.tv_title)
	TextView tv_title;

	@InjectView(R.id.textView_readoncecnt)
	TextView tv_once;
	@InjectView(R.id.textView_invstate)
	TextView tv_state;
	@InjectView(R.id.textView_readallcnt)
	TextView tv_tags;

	@InjectView(R.id.lv_eqptread)
	ListView listView;
	private Timer mTimer;
	private TimerTask mTimerTask;
	private ImageLoader imageLoader;
	private EqptDao eqptDao;
	Map<String, TAGINFO> Devaddrs = new LinkedHashMap<String, TAGINFO>();// 有序
	private SoundPool soundPool;
	private final int CLICK = 80;
	private final int FRUSH = 81;
	private SysApplication myapp;
	private Handler handler = new Handler(){
		@Override
		public void handleMessage (Message msg) {
			switch (msg.what){
				case CLICK:
					initClick();
					break;
				case FRUSH:
					showlist();
					break;
			}
		}
	};

	private void initClick () {
		//view层初始化
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
								tv_state.setText("error:" + String.valueOf(er.value())
										+ " " + er.toString());
								handler.postDelayed(runnable, myapp.Rparams.sleep);
							}

							@Override
							public void setReadoncecnt(int i) {
								tv_once.setText(String.valueOf(i));
							}

							@Override
							public void setReaderSound(String tag,TAGINFO tfs) {
								if (!Devaddrs.containsKey(tag)) {
									//读到标签响两次
									soundPool.play(1, 0.2f, 0.8f, 0, 0, 1);
									SystemClock.sleep(100);
									soundPool.play(1, 0.8f, 0.2f, 0, 0, 1);
									Devaddrs.put(tag, tfs);
								}
							}

							@Override
							public void setReaderError(READER_ERR er) {
								handler.removeCallbacks(runnable);

								tv_state.setText("error:"
										+ String.valueOf(er.value())
										+ er.toString());
								btnStart.setText("读");
							}

							@Override
							public void setReadNum() {
								tv_tags.setText(String.valueOf(listView.getCount()));
								handler.postDelayed(runnable, myapp.Rparams.sleep);
							}
						});
					}
					ReadHandleUI();
				} catch (Exception ex) {
					Toast.makeText(EqptReadActivity.this,
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

	private ReadRFID readRFID;
	private CommonAdapter adapter;
	private Runnable_Reader runnable;

	@Override
	public int bindLayout() {
		return R.layout.activity_eqptread;
	}

	@Override
	public void initView(View view) {
		ButterKnife.inject(this);
		tv_title.setText("读取");
	}

	@Override
	public void doBusiness(Context mContext) {
		//声音池初始化
		soundPool = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);
		soundPool.load(this, R.raw.beep, 1);
		//变量初始化
		Application app = getApplication();
		myapp = (SysApplication) app;
		runnable = new Runnable_Reader(myapp);
		readRFID = new ReadRFID(myapp);
		myapp.Rparams = myapp.new ReaderParams();
		imageLoader = ImgLoad.initImageLoader(EqptReadActivity.this);
		//数据处理层初始化
		eqptDao = EqptDao.getInstance(this);
		ThreadUtils.runInBackground(new Runnable() {

			@Override
			public void run() {
				readRFID.initReader();
				handler.sendEmptyMessage(CLICK);
			}
		});


	}
	private List<Eqpt> eqptList = new ArrayList<>();
	/**
	 * 显示读取列表
	 */
	private void showlist() {
		List<Map<String, ?>> list = new ArrayList<Map<String, ?>>();
		Iterator<Entry<String, TAGINFO>> iesb;
		synchronized (this) {
			Map<String, TAGINFO> Devaddrs2 = new HashMap<String, TAGINFO>();
			Devaddrs2.putAll(Devaddrs);
			iesb = Devaddrs2.entrySet().iterator();
		}
		while (iesb.hasNext()) {
			TAGINFO bd = iesb.next().getValue();
			String epcstr = Reader.bytes_Hexstr(bd.EpcId);
			if (epcstr.length() < 24)
				epcstr = String.format("%-24s", epcstr);
			Eqpt eqpt = eqptDao.getEqptByEPC(epcstr);
			if(eqpt != null){
				if(!eqptList.contains(eqpt)){
					eqptList.add(eqpt);
				}
			}
		}
		if(eqptList == null || eqptList.size() == 0){
			return;
		}
//		MyUtils.removeDuplicate(eqptList);
		if ("1".equals(SysApplication.gainData(Const.TYPEID).toString().trim())) {
			//typeId为1时为设备系统
			if(adapter != null){
				adapter.notifyDataSetChanged();
			}else{
				adapter =  new CommonAdapter<Eqpt>(eqptList) {
					@Override
					public View getView (int position, View convertView, ViewGroup parent) {
						View view;
						ViewHolder vh;
						if (convertView == null) {
							view = getLayoutInflater()
									.inflate(R.layout.listitemview_inv,
											parent, false);
							vh = new ViewHolder(view);
							view.setTag(vh);
						} else {
							view = convertView;
							vh = (ViewHolder) view.getTag();
						}
						Eqpt eqpt = eqptList.get(position);
						vh.tv_equipmentCode.setText(MyUtils.ToDBC("资产编号：\n" + eqpt.EquipmentCode));
						vh.tv_equipmentName
								.setText(MyUtils.ToDBC("资产名称：\n" + eqpt.EquipmentName));
						vh.tv_specification.setText(MyUtils.ToDBC("型号：\n" + eqpt.Specification));
						vh.tv_outFactoryNum.setText(MyUtils.ToDBC("出厂编号：\n" + eqpt.OutFactoryNum));
						vh.tv_initialValue.setText(MyUtils.ToDBC("资产原值：\n" + eqpt.InitialValue));
						vh.tv_projectName.setText(MyUtils.ToDBC("资产属性：" + eqpt.ProjectName));
						vh.tv_equipmentPosition.setText(MyUtils.ToDBC("物理位置：\n" + eqpt.EquipmentPosition));
						vh.tv_departmentName.setText(MyUtils.ToDBC("使用部门：\n" + eqpt.DepartmentName));
						vh.tv_managePerson.setText(MyUtils.ToDBC("责任人：\n" + eqpt.ManagePerson));
						vh.tv_usePerson.setText(MyUtils.ToDBC("使用人：\n" + eqpt.UsePerson));
						if (null != eqpt.IsSecret && !"".equals(eqpt.IsSecret)) {
							vh.tv_isSecret.setText(MyUtils.ToDBC("保密等级：\n" + ((Integer.parseInt(eqpt.IsSecret) == 0) ? "非涉密" : "涉密")));
						}else{
							vh.tv_isSecret.setText("保密等级：");
						}
						return view;
					}
				};
				listView.setAdapter(adapter);
			}
		}else if("2".equals(SysApplication.gainData(Const.TYPEID).toString().trim())){
			//typeId为2时为营具系统
			if(adapter != null){
				adapter.notifyDataSetChanged();
			}else {
				adapter = new CommonAdapter<Eqpt>(eqptList) {
					@Override
					public View getView (int position, View convertView, ViewGroup parent) {
						View view;
						ViewHolder1 vh;
						if (convertView == null) {
							view = getLayoutInflater()
									.inflate(R.layout.listitemview_inv_2,
											parent, false);
							vh = new ViewHolder1(view);
							view.setTag(vh);
						} else {
							view = convertView;
							vh = (ViewHolder1) view.getTag();
						}
						Eqpt eqpt = eqptList.get(position);
						vh.tv_equipmentCode.setText(MyUtils.ToDBC("资产编号：\n" + eqpt.EquipmentCode));
						vh.tv_equipmentName
								.setText(MyUtils.ToDBC("资产名称：\n" + eqpt.EquipmentName));
						vh.tv_equipmentPosition.setText(MyUtils.ToDBC("物理位置：" + eqpt.EquipmentPosition));
						vh.tv_departmentName.setText(MyUtils.ToDBC("使用部门：\n" + eqpt.DepartmentName));
						vh.tv_usePerson.setText(MyUtils.ToDBC("使用人：\n" + eqpt.UsePerson));

						vh.tv_LCCode.setText(MyUtils.ToDBC("浪潮编号：\n" + eqpt.LangChaoBianHao));
						vh.tv_contractName.setText(MyUtils.ToDBC("合同名称：\n" + eqpt.ContractName));
						imageLoader.displayImage("file://" + FileUtils.gainSDCardPath() + "/IMGcache/" + eqpt.ImageName, vh.iv);
						return view;
					}
				};
				listView.setAdapter(adapter);
				listView.setOnScrollListener(new PauseOnScrollListener(imageLoader, true, true)); // 设置滚动时不加载图片
			}
		}else if("3".equals(SysApplication.gainData(Const.TYPEID).toString().trim())){
			//typeId为3时为档案系统
			if(adapter != null){
				adapter.notifyDataSetChanged();
			}else{
			adapter =  new CommonAdapter<Eqpt>(eqptList) {
				@Override
				public View getView (int position, View convertView, ViewGroup parent) {
					View view;
					ViewHolder2 vh;
					if (convertView == null) {
						view = getLayoutInflater()
								.inflate(R.layout.listitemview_inv_1,
										parent, false);
						vh = new ViewHolder2(view);
						view.setTag(vh);
					} else {
						view = convertView;
						vh = (ViewHolder2) view.getTag();
					}
					Eqpt eqpt = eqptList.get(position);
					vh.tv_equipmentCode.setText(MyUtils.ToDBC("条码编号：\n" + eqpt.EquipmentCode));
					vh.tv_equipmentName
							.setText(MyUtils.ToDBC("资产名称：\n" + eqpt.EquipmentName));
					vh.tv_departmentName.setText(MyUtils.ToDBC("物理位置：\n" + eqpt.EquipmentPosition));
					vh.tv_usePerson.setText(MyUtils.ToDBC("档号：\n" + eqpt.FileCode));
					vh.tv_equipmentPosition.setVisibility(View.GONE);
					vh.iv.setVisibility(View.GONE);
					vh.iv_line.setVisibility(View.GONE);
					vh.iv_line1.setVisibility(View.GONE);
					return view;
				}
			};
			listView.setAdapter(adapter);
		}
		}
	}

	static class ViewHolder {
		@InjectView(R.id.tv_equipmentCode)
		TextView tv_equipmentCode;
		@InjectView(R.id.tv_equipmentName)
		TextView tv_equipmentName;
		@InjectView(R.id.tv_specification)
		TextView tv_specification;
		@InjectView(R.id.tv_outFactoryNum)
		TextView tv_outFactoryNum;
		@InjectView(R.id.tv_initialValue)
		TextView tv_initialValue;
		@InjectView(R.id.tv_projectName)
		TextView tv_projectName;
		@InjectView(R.id.tv_equipmentPosition)
		TextView tv_equipmentPosition;
		@InjectView(R.id.tv_departmentName)
		TextView tv_departmentName;
		@InjectView(R.id.tv_managePerson)
		TextView tv_managePerson;
		@InjectView(R.id.tv_usePerson)
		TextView tv_usePerson;
		@InjectView(R.id.tv_isSecret)
		TextView tv_isSecret;

		public ViewHolder(View view) {
			ButterKnife.inject(this, view);
		}
	}

	static class ViewHolder1 {
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
		@InjectView(R.id.tv_contractName)
		TextView tv_contractName;
		@InjectView(R.id.tv_usePerson)
		TextView tv_usePerson;
		@InjectView(R.id.iv)
		ImageView iv;
		@InjectView(R.id.iv_line1)
		ImageView iv_line1;
		@InjectView(R.id.iv_line)
		ImageView iv_line;

		public ViewHolder1(View view) {
			ButterKnife.inject(this, view);
		}
	}

	static class ViewHolder2 {
		@InjectView(R.id.tv_equipmentCode)
		TextView tv_equipmentCode;
		@InjectView(R.id.tv_equipmentName)
		TextView tv_equipmentName;

		@InjectView(R.id.tv_equipmentPosition)
		TextView tv_equipmentPosition;
		@InjectView(R.id.tv_departmentName)
		TextView tv_departmentName;

		@InjectView(R.id.tv_usePerson)
		TextView tv_usePerson;
		@InjectView(R.id.iv)
		ImageView iv;
		@InjectView(R.id.iv_line1)
		ImageView iv_line1;
		@InjectView(R.id.iv_line)
		ImageView iv_line;

		public ViewHolder2(View view) {
			ButterKnife.inject(this, view);
		}
	}

	/**
	 * 读取时按钮切换
	 */
	private void ReadHandleUI() {
		this.btnStart.setEnabled(false);
		this.btnStop.setEnabled(true);
	}
	/**
	 * 停止时按钮切换
	 */
	private void StopHandleUI() {
		this.btnStart.setEnabled(true);
		this.btnStop.setEnabled(false);
		ThreadUtils.runInBackground(new Runnable() {
			@Override
			public void run () {
				SystemClock.sleep(2000);
				handler.sendEmptyMessage(FRUSH);
			}
		});
	}

	@Override
	public void resume() {

	}
	private void startTimer(){
		if (mTimer == null) {
			mTimer = new Timer();
		}

		if (mTimerTask == null) {
			mTimerTask = new TimerTask() {
				@Override
				public void run() {
					handler.sendEmptyMessage(FRUSH);
				}
			};
		}

		if(mTimer != null && mTimerTask != null )
			mTimer.schedule(mTimerTask, 3 * 1000, 3 *1000);

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
	public void destroy() {
		readRFID.colseReader();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (btnStop.isEnabled()) {
				//不停止不能返回
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);

	}
}
