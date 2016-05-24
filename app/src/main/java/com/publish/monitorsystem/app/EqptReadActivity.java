package com.publish.monitorsystem.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.app.Application;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.os.SystemClock;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;

import com.msystemlib.base.BaseActivity;
import com.pow.api.cls.RfidPower.PDATYPE;
import com.publish.monitorsystem.R;
import com.publish.monitorsystem.api.bean.EqptBean.Eqpt;
import com.publish.monitorsystem.api.db.dao.EqptDao;
import com.publish.monitorsystem.api.readrfid.IRunneableReaderListener;
import com.publish.monitorsystem.api.readrfid.ReadRFID;
import com.publish.monitorsystem.api.readrfid.Runnable_Reader;
import com.publish.monitorsystem.application.SysApplication;
import com.publish.monitorsystem.view.MyAdapter;
import com.uhf.api.cls.Reader;
import com.uhf.api.cls.Reader.READER_ERR;
import com.uhf.api.cls.Reader.TAGINFO;

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

	private EqptDao eqptDao;
	Map<String, TAGINFO> Devaddrs = new LinkedHashMap<String, TAGINFO>();// 有序
	private SoundPool soundPool;
	private SysApplication myapp;
	private Handler handler = new Handler();
	private ReadRFID readRFID;
	private Runnable_Reader runnable; 
	@Override
	public int bindLayout() {
		return activity_eqptread;
	}

	@Override
	public void initView(View view) {
		ButterKnife.inject(this);
		tv_title.setText("设备读取");
	}

	@Override
	public void doBusiness(Context mContext) {
		soundPool = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);
		soundPool.load(this, R.raw.beep, 1);
		Application app = getApplication();
		myapp = (SysApplication) app;
		runnable = new Runnable_Reader(myapp);
		readRFID = new ReadRFID(myapp);
		myapp.Rparams = myapp.new ReaderParams();
		eqptDao = EqptDao.getInstance(this);
		btnStop.setEnabled(false);
		
		btnStart.setOnClickListener(new OnClickListener() {

			@SuppressWarnings("unused")
			@Override
			public void onClick(View arg0) {
				try {

					if (myapp.ThreadMODE == 0){
						handler.postDelayed(runnable, 0);
						runnable.setOnReadListener(new IRunneableReaderListener() {
							
							@Override
							public void setView() {
								showlist();
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
								tv_tags.setText(String.valueOf(listView.getCount() - 1));
								handler.postDelayed(runnable, myapp.Rparams.sleep);
							}
						});
					}
					myapp.Devaddrs.clear();
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

	String[] Coname = new String[] { "序号", "资产编号", "资产名称", "型号", "出厂编号",
			"资产原值", "资产属性", "物理位置", "使用部门", "责任人", "使用人", "保密等级" };
	/**
	 * 显示读取列表
	 */
	private void showlist() {

		List<Map<String, ?>> list = new ArrayList<Map<String, ?>>();
		Iterator<Entry<String, TAGINFO>> iesb;
		synchronized (this) {
			Map<String, TAGINFO> Devaddrs2 = new LinkedHashMap<String, TAGINFO>();
			Devaddrs2.putAll(Devaddrs);
			iesb = Devaddrs2.entrySet().iterator();
		}
		int j = 1;

		Map<String, String> h = new HashMap<String, String>();
		for (int i = 0; i < Coname.length; i++)
			h.put(Coname[i], Coname[i]);
		list.add(h);

		while (iesb.hasNext()) {
			TAGINFO bd = iesb.next().getValue();
			Map<String, String> m = new HashMap<String, String>();
			m.put(Coname[0], String.valueOf(j));
			j++;
			String epcstr = Reader.bytes_Hexstr(bd.EpcId);
			if (epcstr.length() < 24)
				epcstr = String.format("%-24s", epcstr);

			Eqpt eqpt = eqptDao.getEqptByEPC(epcstr);

			m.put(Coname[1], eqpt.EquipmentCode);
			m.put(Coname[2], eqpt.EquipmentName);
			m.put(Coname[3], eqpt.Specification);
			m.put(Coname[4], eqpt.OutFactoryNum);
			m.put(Coname[5], eqpt.InitialValue);
			m.put(Coname[6], eqpt.ProjectName);
			m.put(Coname[7], eqpt.EquipmentPosition);
			m.put(Coname[8], eqpt.DepartmentName);
			m.put(Coname[9], eqpt.ManagePerson);
			m.put(Coname[10], eqpt.UsePerson);
			if (null != eqpt.IsSecret) {
				m.put(Coname[11],
						(Integer.parseInt(eqpt.IsSecret) == 0) ? "非涉密" : "涉密");
			}
			list.add(m);
		}

		// /*
		ListAdapter adapter = new MyAdapter(this, list,
				R.layout.listitemview_inv, Coname, new int[] {
						R.id.tv_readsort, R.id.tv_equipmentCode,
						R.id.tv_equipmentName, R.id.tv_specification,
						R.id.tv_outFactoryNum, R.id.tv_initialValue,
						R.id.tv_projectName, R.id.tv_equipmentPosition,
						R.id.tv_departmentName, R.id.tv_managePerson,
						R.id.tv_usePerson, R.id.tv_isSecret });

		// layout为listView的布局文件，包括三个TextView，用来显示三个列名所对应的值
		// ColumnNames为数据库的表的列名
		// 最后一个参数是int[]类型的，为view类型的id，用来显示ColumnNames列名所对应的值。view的类型为TextView
		listView.setAdapter(adapter);
		// */
	}

	private void ReadHandleUI() {
		this.btnStart.setEnabled(false);
		this.btnStop.setEnabled(true);
	}

	private void StopHandleUI() {
		this.btnStart.setEnabled(true);
		this.btnStop.setEnabled(false);
	}

	@Override
	public void resume() {

	}

	@Override
	public void destroy() {
		readRFID.colseReader();
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
