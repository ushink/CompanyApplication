package com.publish.monitorsystem.app.fragment;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import org.ksoap2.serialization.SoapObject;

import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.InjectView;

import com.msystemlib.base.BaseFragment;
import com.msystemlib.common.adapter.GirdViewAdapter;
import com.msystemlib.http.HttpConn;
import com.msystemlib.http.IWebServiceCallBack;
import com.msystemlib.http.JsonToBean;
import com.msystemlib.utils.AlertUtils;
import com.msystemlib.utils.FileUtils;
import com.msystemlib.utils.LogUtils;
import com.msystemlib.utils.SDUtils;
import com.msystemlib.utils.SPUtils;
import com.msystemlib.utils.ThreadUtils;
import com.msystemlib.utils.ToastUtils;
import com.publish.monitorsystem.R;
import com.publish.monitorsystem.api.Const;
import com.publish.monitorsystem.api.bean.BuildingBean;
import com.publish.monitorsystem.api.bean.EqptBean;
import com.publish.monitorsystem.api.bean.InventoryBean;
import com.publish.monitorsystem.api.bean.InventoryEqptBean;
import com.publish.monitorsystem.api.bean.RoomBean;
import com.publish.monitorsystem.api.bean.UploadInventory;
import com.publish.monitorsystem.api.bean.UploadInventoryEqpt;
import com.publish.monitorsystem.api.bean.BuildingBean.Building;
import com.publish.monitorsystem.api.bean.EqptBean.Eqpt;
import com.publish.monitorsystem.api.bean.InventoryBean.Inventory;
import com.publish.monitorsystem.api.bean.InventoryEqptBean.InventoryEqpt;
import com.publish.monitorsystem.api.bean.RoomBean.Room;
import com.publish.monitorsystem.api.db.dao.BuildingDao;
import com.publish.monitorsystem.api.db.dao.EqptDao;
import com.publish.monitorsystem.api.db.dao.InventoryDao;
import com.publish.monitorsystem.api.db.dao.InventoryEqptDao;
import com.publish.monitorsystem.api.db.dao.RoomDao;
import com.publish.monitorsystem.api.db.dao.UploadInventoryDao;
import com.publish.monitorsystem.api.db.dao.UploadInventoryEqptDao;
import com.publish.monitorsystem.api.utils.FormatJsonUtils;
import com.publish.monitorsystem.app.LocalActivity;
import com.publish.monitorsystem.application.SysApplication;
import com.publish.monitorsystem.view.MyProgressBar;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;

public class UpdateFragment extends BaseFragment implements OnItemClickListener {

	@InjectView(R.id.tv_title)
	TextView tvTitle;
	@InjectView(R.id.tv_testconn)
	TextView tvTestconn;
	@InjectView(R.id.gv_items)
	GridView gvItems;
	private Dialog downloadDialog;
	private MyProgressBar pro;
	private TextView tvPro;
	private String[] names;
	private int [] imageIds;
	private int i;
	
	private EqptDao eqptDao;
	private InventoryDao inventoryDao;
	private InventoryEqptDao inventoryEqptDao;
	private BuildingDao buildingDao;
	private RoomDao roomDao;
	private UploadInventoryDao uploadInventoryDao;
	private UploadInventoryEqptDao uploadInventoryEqptDao;
	
	private GirdViewAdapter adapter;
	private List<Eqpt> eqptBeanList;
	private SysApplication myapp;

//	private static String SD_FOLDER = ;
	private ArrayList<String> temp = new ArrayList<>();
	
	private int pageIndexeqpt;//设备信息分页索引
	private int pageIndexInventoryEqpt;//设备信息分页索引
	private int addCount;//设备信息下载进度参数
	private int inventoryCount;//盘点计划下载进度参数
	private int equipmentSize;//设备信息总数
	private int inventoryEqutSize;//盘点设备信息总数
	private int pageSize = 200;//每页的个数
	private String jsonString;//每页的json
	
	private boolean isRunning = false;//是否在测试连接
	private int firstIn = 0;//第一次执行onResume
	
	private List<UploadInventoryEqpt> pageInventoryEqpt;//上传子表
	private int size;//上传数据总数
	private int uploadCount;//上传进度参数
	private int index;//总页数
	private int in;
	
	private final int FLUSH = 102;//测试连接刷新状态
	private final int DOWNLOADEQPT = 80;//下载设备信息
	private final int DOWNLOADEND = 81;//分页每次下载完成
	private final int DOWNLOADINVENTORY = 83;//下载盘点计划
	private final int DOWNLOADEINVENTORYND = 82;//下载盘点对话框数据量总数
	private final int DOWNLOADPLANEND = 85;//下载盘点对话框数据量总数
	private final int UPLOADINVENTORY = 86;//上传盘点设备信息表
	private final int UPLOADINVENTORYEND = 87;//上传盘点设备信息表结束
	private final int DOWNLOADIMG = 88;//上传盘点设备信息表结束
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case DOWNLOADEQPT:
				pro.setProgress(addCount*100/equipmentSize);
				tvPro.setText("已下载 ：" + addCount + "/ 共" + equipmentSize + "条");
				break;
			case DOWNLOADEND:
				pageIndexeqpt++;
				if(pageIndexeqpt < equipmentSize / pageSize + 2){
					properties.put("PageIndex", pageIndexeqpt + "");
					getData(Const.GETEQUIPMENTTAGINFO);
				}
				break;
			case DOWNLOADINVENTORY:
				pro.setProgress(inventoryCount*100/inventoryEqutSize);
				tvPro.setText("已下载 ：" + inventoryCount + "/ 共" + inventoryEqutSize + "条");
				break;
			case DOWNLOADEINVENTORYND:
				pageIndexInventoryEqpt++;
				if(pageIndexInventoryEqpt < inventoryEqutSize / pageSize + 2){
					properties.put("PageIndex", pageIndexInventoryEqpt + "");
					getData(Const.GETINVENTORYEQPT);
				}
				break;
			case FLUSH:
				if(isRunning){
					//测试连接
					TestConn();
					handler.sendEmptyMessageDelayed(FLUSH, 5000);
				}
				break;
			case DOWNLOADPLANEND:
				getInventoryEqptNum();
				break;
			case UPLOADINVENTORY:
				uploadCount += pageSize;
				uploadInventoryInfo(uploadCount);
				break;
			case UPLOADINVENTORYEND:
				uploadCount += size - pageSize*(index - 1);
				uploadInventoryInfo(uploadCount);
				break;
			case DOWNLOADIMG:
				pro.setProgress(addCount*100/temp.size());
				tvPro.setText("已下载 ：" + addCount + "/ 共" + temp.size() + "条");
				break;
			}

			
		};
	};
	private HashMap<String, String> properties;
	private HashMap<String, String> properties1;
	private String typeID;
	private List<Inventory> inventoryList;
	private List<Building> buildingList;
	private List<Room> roomList;
	private List<InventoryEqpt> inventoryEqptList;
	@Override
	public View initView() {
		View view = View.inflate(getActivity(), R.layout.fragment_update, null);
		ButterKnife.inject(this,view);
		tvTitle.setText("更新数据");
		tvTestconn.setText("测试连接中...");
		
		typeID = SysApplication.gainData(Const.TYPEID).toString().trim();
		if("1".equals(typeID) || "2".equals(typeID)){
			//设备及营具系统
			names = new String[]{"更新最新信息","获取盘点计划","上传最新信息"};
			imageIds = new int[]{R.drawable.getdevice,R.drawable.getcheck,R.drawable.upload };
		}else if("3".equals(typeID)){
			//档案系统
			names  = new String[]{"更新最新信息","获取盘点计划","上传最新信息",
					"获取查找计划"};
//			,"获取核检计划"  ,R.drawable.getagain
			imageIds = new int[]{R.drawable.getdevice,R.drawable.getcheck, R.drawable.upload
					,R.drawable.getsearch};
		}
		
		
		adapter = new GirdViewAdapter(names) {
			
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View view = null;
				if(convertView == null){
					view =  View.inflate(getActivity(), R.layout.gird_item_home, null);
				}else{
					view = convertView;
				}
				
				TextView name = (TextView) view.findViewById(R.id.tv_name_grid_item);
				name.setText(names[position]);
				name.setTextColor(getActivity().getResources().getColor(R.color.title));
				
				ImageView image = (ImageView) view.findViewById(R.id.iv_icon_gird_item);
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
		//数据处理层初始化
		eqptDao = EqptDao.getInstance(getActivity());
		inventoryDao = InventoryDao.getInstance(getActivity());
		inventoryEqptDao = InventoryEqptDao.getInstance(getActivity());
		buildingDao = BuildingDao.getInstance(getActivity());
		roomDao = RoomDao.getInstance(getActivity());
		uploadInventoryDao = UploadInventoryDao.getInstance(getActivity());
		uploadInventoryEqptDao = UploadInventoryEqptDao.getInstance(getActivity());
		//变量初始化
		Application app=getActivity().getApplication();
		myapp=(SysApplication)app;

		//开启联网判断
		TestConn();
		isRunning = true;
		handler.sendEmptyMessageDelayed(FLUSH, 5000);
	}
	
	
	@Override
	public void onResume() {
		super.onResume();
		firstIn++;
		//activity第一次创建时不测试连接
		if(firstIn > 1){
			tvTestconn.setText("测试连接中...");
			tvTestconn.setTextColor(getResources().getColor(R.color.brown));
			TestConn();
			isRunning = true;
			handler.sendEmptyMessageDelayed(FLUSH, 5000);	
		}
	}
	@Override
	public void onPause() {
		super.onPause();
		isRunning = false;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		isRunning = false;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		switch (position) {
		case 0: //更新最新信息
				AlertUtils.dialog1(getActivity(), "提示", "是否清空及更新最新信息？",new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						downloadDialog = showDownloadDialog(getActivity(), "正在准备下载...", "已下载 ：0/ 共0条", i);
						downloadDialog.show();
						properties = new HashMap<String, String>();
						properties.put("TypeID", SysApplication.gainData(Const.TYPEID).toString().trim());
						HttpConn.callService(Const.URL, Const.NAMESPACE, Const.GETEQUIPMENTTAGNUMBER, properties, new IWebServiceCallBack() {
							@Override
							public void onSucced(SoapObject result) {
								if(result != null){
									//联网成功后删除所有之前缓存信息
									eqptDao.deleteAllEqpt();
									String string = result.getProperty(0).toString();
									if(!"404".equals(string)){
										equipmentSize = Integer.parseInt(string);
										tvPro.setText("已下载 ：0/ 共"+ equipmentSize +"条");
										properties.put("PageSize", pageSize + "");
										pageIndexeqpt = 1;
										properties.put("PageIndex", pageIndexeqpt + "");
										getData(Const.GETEQUIPMENTTAGINFO);
									}else{
										if("1".equals(typeID)){
											ToastUtils.showToast(getActivity(), "没有设备信息");
										}else if("2".equals(typeID)){
											ToastUtils.showToast(getActivity(), "没有营具信息");
										}else if("3".equals(typeID)){
											ToastUtils.showToast(getActivity(), "没有档案信息");
										}
										downloadDialog.dismiss();
									}
								}
							}

							@Override
							public void onFailure(String result) {
								ToastUtils.showToast(getActivity(), "联网失败");
								downloadDialog.dismiss();
							}
						});
					}
				} , new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
			break;
		case 1: //获取盘点计划
			//FunctionID 1盘点2查找3核检
				AlertUtils.dialog1(getActivity(), "提示", "是否清空及更新最新信息？",new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						if(uploadInventoryEqptDao.getSize() > 0){
							ToastUtils.showToast(getActivity(), "请先上传完盘点信息，再更新新的盘点计划");
							return;
						}
						downloadDialog = showDownloadDialog(getActivity(), "正在准备下载...", "已下载 ：0/ 共0条", i);
						downloadDialog.show();
						//联网成功后删除所有之前缓存信息
						uploadInventoryDao.deleteAllUploadInventory();
						UploadInventory inventory = new UploadInventory();
						inventory.InventoryID = UUID.randomUUID().toString();
						inventory.ParentPlanID = "";
						inventory.UploadTime = "";
						//下载盘点计划时创建此次盘点的主表
						uploadInventoryDao.addUploadInventory(inventory);

						properties = new HashMap<String, String>();
						properties.put("TypeID", SysApplication.gainData(Const.TYPEID).toString().trim());
						properties.put("FunctionID", 1+"");
						HttpConn.callService(Const.URL, Const.NAMESPACE, Const.GETINVENTORYPLAN, properties, new IWebServiceCallBack() {

							@Override
							public void onSucced(SoapObject result) {

								if(result != null){
									//联网成功后删除所有之前缓存信息
									inventoryDao.deleteAllInventory();
									buildingDao.deleteAllBuilding();
									roomDao.deleteAllRoom();
									myapp.Devaddrs.clear();
									inventoryEqptDao.deleteAllInventoryEqpt();
									String string = result.getProperty(0).toString();
									if(!"404".equals(string) && !"405".equals(string)){
										getBuildingAndRoom(string);
									}else{
										downloadDialog.dismiss();
										ToastUtils.showToast(getActivity(), "未制定盘点计划");
									}
								}
							}

							@Override
							public void onFailure(String result) {
								ToastUtils.showToast(getActivity(), "联网失败");
								downloadDialog.dismiss();
							}
						});
					}
				},new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
			break;
		case 2: //上传最新信息
				if(uploadInventoryDao.getSize() < 0 || uploadInventoryDao.getSize() == 0
						|| uploadInventoryEqptDao.getSize() < 0 
						|| uploadInventoryEqptDao.getSize() == 0){
					ToastUtils.showToast(getActivity(), "请先下载并进行盘点");
					return;
				}
				downloadDialog = showDownloadDialog(getActivity(), "正在准备上传...", "已上传 ：0/ 共0条", i);
				downloadDialog.show();
				Date date = new Date();// 创建一个时间对象，获取到当前的时间
				SimpleDateFormat sdf = new SimpleDateFormat(
						"yyyy/MM/dd HH:mm");// 设置时间显示格式
				String str = sdf.format(date);// 将当前时间格式化为需要的类型
				uploadInventoryDao.updateUploadInventory(SPUtils.getString(getActivity(), "ParentPlanID", ""), str);
				List<UploadInventory> uploadInventory = uploadInventoryDao.getAllUploadInventory();
				String jsonArray = FormatJsonUtils.formatJson(uploadInventory);
				size = uploadInventoryEqptDao.getSize();
				pageInventoryEqpt = uploadInventoryEqptDao.getPageInventoryEqpt(pageSize, 0);
				index = size % pageSize != 0 ? (size / pageSize + 1) : size / pageSize;
				properties = new HashMap<String, String>();
				properties.put("strJson", jsonArray);
				tvPro.setText("已下载 ：0/ 共"+ size +"条");
				HttpConn.callService(Const.URL, Const.NAMESPACE, Const.UPLOADINVENTORY, properties, new IWebServiceCallBack() {
					
					@Override
					public void onSucced(SoapObject result) {
						if(result != null){
							String string = result.getProperty(0).toString();
							if("true".equals(string) ){
								getInventoryInfo();
							}
						}
					}
					
					@Override
					public void onFailure(String result) {
						ToastUtils.showToast(getActivity(), "联网失败");
						downloadDialog.dismiss();
					}
				});
				//{"InventoryID":"29702702-b9f7-44d0-a5c4-d1ecd7cf90b7","ParentPlanID":"bc8c2190-8ea6-4178-9ef5-a392b6b4041a","UploadTime":"2016/03/31 11:33"}

			break;
		case 3: //获取查找计划
			properties.put("TypeID", SysApplication.gainData(Const.TYPEID).toString().trim());
			properties.put("FunctionID", 2+"");
			HttpConn.callService(Const.URL, Const.NAMESPACE, Const.GETINVENTORYPLAN, properties, new IWebServiceCallBack() {
				@Override
				public void onSucced(SoapObject result) {
					if(result != null){
						String string = result.getProperty(0).toString();
						LogUtils.d("ckj", string);
					}
				}
				
				@Override
				public void onFailure(String result) {
					ToastUtils.showToast(getActivity(), "联网失败");
				}
			});
			break;
//		case 4: //获取核检计划
//			properties.put("TypeID", SysApplication.gainData(Const.TYPEID).toString().trim());
//			properties.put("FunctionID", 3+"");
//			HttpConn.callService(Const.URL, Const.NAMESPACE, Const.GETINVENTORYPLAN, properties, new IWebServiceCallBack() {
//				@Override
//				public void onSucced(SoapObject result) {
//					if(result != null){
//						String string = result.getProperty(0).toString();
//						LogUtils.d("ckj", string);
//					}
//				}
//
//				@Override
//				public void onFailure(String result) {
//					ToastUtils.showToast(getActivity(), "联网失败");
//				}
//			});
//			break;
		}
	}
	
	/**
	 * 获取上传数据
	 */
	protected void getInventoryInfo() {
		ThreadUtils.runInBackground(new Runnable() {

			@Override
			public void run() {
				if(in < index){
					pageInventoryEqpt = uploadInventoryEqptDao.getPageInventoryEqpt(pageSize, in);
					if(in == index - 1){
						in ++;
						handler.sendEmptyMessage(UPLOADINVENTORYEND);
					}else{
						in++;
						handler.sendEmptyMessage(UPLOADINVENTORY);
					}
				}
			}
		});
	}

	/**
	 * 上传盘点信息
	 * @param count
	 */
	protected void uploadInventoryInfo(final int count) {
		String json = FormatJsonUtils.formatJson(pageInventoryEqpt);
		properties.put("strJson", json);
		HttpConn.callService(Const.URL, Const.NAMESPACE, Const.UPLOADINVENTORYINFO, properties, new IWebServiceCallBack() {

			@Override
			public void onSucced(SoapObject result) {
				if(result != null){
					String string = result.getProperty(0).toString();
					if("true".equals(string) ){
						pro.setProgress(count*100/size);
						tvPro.setText("已上传 ：" + count + "/ 共" + size + "条");
						if(in < index){
							getInventoryInfo();
						}else{
							in = 0;
							downloadDialog.dismiss();
							AlertUtils.dialog1(getActivity(), "提示", "上传成功！", new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog, int which) {
									uploadInventoryEqptDao.deleteAllUploadInventoryEqpt();
									uploadInventoryEqptDao.deleteAllUploadInventory();
									myapp.Devaddrs.clear();
									dialog.dismiss();
								}
							}, null);
						}
					}
				}
			}

			@Override
			public void onFailure(String result) {
				ToastUtils.showToast(getActivity(), "联网失败");
				downloadDialog.dismiss();
			}
		});
	}

	/**
	 * 获取建筑和房间列表
	 * @param str
	 */
	protected void getBuildingAndRoom(final String str) {
		HttpConn.callService(Const.URL, Const.NAMESPACE, Const.GETBUILDINGLIST, null, new IWebServiceCallBack() {
			@Override
			public void onSucced(SoapObject result) {
				if(result != null){
					final String buildStr = result.getProperty(0).toString();
					HttpConn.callService(Const.URL, Const.NAMESPACE, Const.GETROOMLIST, null, new IWebServiceCallBack() {
						@Override
						public void onSucced(SoapObject result) {
							if(result != null){
								final String roomStr = result.getProperty(0).toString();
								ThreadUtils.runInBackground(new Runnable() {

									@Override
									public void run() {
										saveInventory(str + "&" + buildStr + "&" + roomStr);
									}
								});
							}
						}
						
						@Override
						public void onFailure(String result) {
							ToastUtils.showToast(getActivity(), "联网失败");
						}
					});
				}
			}
			
			@Override
			public void onFailure(String result) {
				ToastUtils.showToast(getActivity(), "联网失败");
			}
		});
	}

	/**
	 * 保存盘点计划
	 * @param jsonStr
	 */
	protected void saveInventory(String jsonStr) {
		String[] strs = jsonStr.split("&");
		InventoryBean inventoryBean = JsonToBean.getJsonBean(strs[0], InventoryBean.class);
		BuildingBean buildingBean = JsonToBean.getJsonBean(strs[1], BuildingBean.class);
		RoomBean roomBean = JsonToBean.getJsonBean(strs[2], RoomBean.class);
		inventoryList = inventoryBean.ds;
		buildingList = buildingBean.ds;
		roomList = roomBean.ds;
		for (Inventory inventory : inventoryList) {
			inventoryDao.addInventory(inventory);
		}
		
		for (Building building : buildingList) {
			buildingDao.addBuilding(building);
		}
		for (Room room : roomList) {
			roomDao.addRoom(room);
		}
		handler.sendEmptyMessage(DOWNLOADPLANEND);
		
	}
	
	
	/**
	 * 得到盘点设备数量
	 */
	protected void getInventoryEqptNum(){
		properties1 = new HashMap<String, String>();
		properties1.put("ParentPlanID", inventoryDao.getInventory(SysApplication.gainData(Const.TYPEID).toString().trim()).ParentPlanID);
		HttpConn.callService(Const.URL, Const.NAMESPACE, Const.GETINVENTORYEQPTNUM, properties1 , new IWebServiceCallBack() {
			
			@Override
			public void onSucced(SoapObject result) {
				if(result != null){
					String string = result.getProperty(0).toString();
					inventoryEqutSize = Integer.parseInt(string);
					LogUtils.d("ckj", inventoryEqutSize+"");
					tvPro.setText("已下载 ：0/ 共"+ inventoryEqutSize +"条");
					properties1.put("PageSize", pageSize + "");
					pageIndexInventoryEqpt = 1;
					properties1.put("PageIndex", pageIndexInventoryEqpt + "");
					getInventoryData(Const.GETINVENTORYEQPT);
				}
			}
			
			@Override
			public void onFailure(String result) {
				
			}
		});
	}
	
	/**
	 * 联网得到盘点设备信息
	 * @param getInventoryData
	 */
	protected void getInventoryData(String getInventoryData) {
		HttpConn.callService(Const.URL, Const.NAMESPACE, getInventoryData, properties1 , new IWebServiceCallBack() {
			
			@Override
			public void onSucced(SoapObject result) {
				if(result != null){
					jsonString = result.getProperty(0).toString();
					ThreadUtils.runInBackground(new Runnable() {

						@Override
						public void run() {
							saveInventoryEqpt(jsonString);
							LogUtils.d("ckj", jsonString);
						}
					});
				}
			}
			
			@Override
			public void onFailure(String result) {
				ToastUtils.showToast(getActivity(), "联网失败");
				downloadDialog.dismiss();
			}
		});
	}

	/**
	 * 保存盘点设备信息
	 * @param msgObj
	 */
	protected void saveInventoryEqpt(String msgObj) {
		InventoryEqptBean inventoryEqptBean = JsonToBean.getJsonBean(msgObj,InventoryEqptBean.class);
		inventoryEqptList = inventoryEqptBean.ds;
		for (InventoryEqpt inventoryEqpt : inventoryEqptList) {
			inventoryEqptDao.addInventoryEqpt(inventoryEqpt);
			inventoryCount++;
			handler.sendEmptyMessage(DOWNLOADINVENTORY);
		}
		handler.sendEmptyMessage(DOWNLOADEINVENTORYND);
		if (inventoryCount >= inventoryEqutSize) {
			downloadDialog.dismiss();
			inventoryCount = 0;
			i = 0;
			ThreadUtils.runInMainThread(new Runnable() {
				@Override
				public void run() {
					AlertUtils.dialog1(getActivity(), "提示", "盘点计划同步成功！", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					}, null);
				}
			});
		
		}
	}

	/**
	 * 联网得到设备信息
	 * @param getequipmenttaginfo
	 */
	protected void getData(String getequipmenttaginfo) {
		HttpConn.callService(Const.URL, Const.NAMESPACE, getequipmenttaginfo, properties , new IWebServiceCallBack() {
			
			@Override
			public void onSucced(SoapObject result) {
				if(result != null){
					jsonString = result.getProperty(0).toString();
					ThreadUtils.runInBackground(new Runnable() {

						@Override
						public void run() {
							saveEqptInfo(jsonString);
						}
					});
				}
			}
			
			@Override
			public void onFailure(String result) {
				ToastUtils.showToast(getActivity(), "联网失败");
				downloadDialog.dismiss();
			}
		});
	}
	

	/**
	 * 保存设备信息
	 * @param msgObj
	 */
	protected void saveEqptInfo(String msgObj) {
		EqptBean eqptBean = JsonToBean.getJsonBean(msgObj,EqptBean.class);
		eqptBeanList = eqptBean.ds;
		for (Eqpt eqpt : eqptBeanList) {
			eqptDao.addEqpt(eqpt);
			addCount++;
			handler.sendEmptyMessage(DOWNLOADEQPT);
		}
		handler.sendEmptyMessage(DOWNLOADEND);
		if (addCount >= equipmentSize) {
			downloadDialog.dismiss();
			addCount = 0;
			i = 0;
			if(!"".equals(eqptBeanList.get(0).ImageName)){//判断是否为营具系统

				for (Eqpt eqpt : eqptDao.getAllEqptList()){
					temp.add(eqpt.ImageName);
				}
				removeDuplicate(temp);
				ThreadUtils.runInMainThread(new Runnable() {
					@Override
					public void run() {
						downloadDialog = showDownloadDialog(getActivity(), "正在下载图片", "已下载 ：0/ 共"+temp.size()+"条", i);
						downloadDialog.show();
						downLoadFile(getActivity(),Const.URL_IMG +"/" + temp.get(addCount),temp.get(addCount));
					}
				});

			}else{
				ThreadUtils.runInMainThread(new Runnable() {
					@Override
					public void run() {
						if("1".equals(typeID)){
							AlertUtils.dialog1(getActivity(), "提示", "设备同步成功！", new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog, int which) {
									dialog.dismiss();
								}
							}, null);
						}else if("3".equals(typeID)){
							AlertUtils.dialog1(getActivity(), "提示", "档案同步成功！", new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog, int which) {
									dialog.dismiss();
								}
							}, null);
						}

					}
				});

			}



		}
	}

	/**
	 * 去除list重复数据
	 * @param list
     */
	public   static   void  removeDuplicate(List list)   {
		HashSet h  =   new  HashSet(list);
		list.clear();
		list.addAll(h);
	}
	/**
	 * 从服务器中下载文件
	 */
	private void downLoadFile(final Context mContext,final String downURL,String name) {
		FinalHttp fh = new FinalHttp();
		String status = Environment.getExternalStorageState();
		if (status.equals(Environment.MEDIA_MOUNTED)) {
			if(SDUtils.getSDFreeSize()>10){//判断内存卡大小
				File file=new File(FileUtils.gainSDCardPath() +"/IMGcache/" + name);
				if(file.exists()){
					addCount ++;
					handler.sendEmptyMessage(DOWNLOADIMG);
					if(addCount < temp.size()){//迭代下载每一张图片
						downLoadFile(getActivity(),Const.URL_IMG +"/" + temp.get(addCount),temp.get(addCount));
					}else{
						downloadDialog.dismiss();
						addCount = 0;
						i = 0;
						AlertUtils.dialog1(getActivity(), "提示", "营具同步成功！", new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								dialog.dismiss();
							}
						}, null);
					}
				}else{
					fh.download(downURL, FileUtils.gainSDCardPath() +"/IMGcache/" + name,new AjaxCallBack<File>() {
						@Override
						public void onLoading(long count, long current) {
						}

						@Override
						public void onSuccess(File t) {
							addCount ++;
							handler.sendEmptyMessage(DOWNLOADIMG);//下载成功修改下载进度
							if(addCount < temp.size()){
								downLoadFile(getActivity(),Const.URL_IMG +"/" + temp.get(addCount),temp.get(addCount));
							}else{
								downloadDialog.dismiss();
								addCount = 0;
								i = 0;
								AlertUtils.dialog1(getActivity(), "提示", "营具同步成功！", new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog, int which) {
										dialog.dismiss();
									}
								}, null);
							}
						}
					});
				}
			}else{
				Toast.makeText(mContext, "内存卡空间不足",Toast.LENGTH_LONG).show();
			}
		}else{
			Toast.makeText(mContext, "没有储存卡",Toast.LENGTH_LONG).show();
		}
	}
	
	/**
	 * 下载提示对话框
	 * @param context 上下文
	 * @param msg 提示信息
	 * @param text 下载进度文字
	 * @param progress 下载进度
	 * @return
	 */
	private Dialog showDownloadDialog(Context context, String msg, String text, int progress) {  
		  
        LayoutInflater inflater = LayoutInflater.from(context);  
        View v = inflater.inflate(R.layout.download_dialog, null);// 得到加载view  
        LinearLayout layout = (LinearLayout) v.findViewById(R.id.dialog_view);// 加载布局  
        pro = (MyProgressBar) v.findViewById(R.id.pro);  
        tvPro = (TextView) v.findViewById(R.id.tv_pro);  
        TextView tipTextView = (TextView) v.findViewById(R.id.tipTextView);// 提示文字  
        tipTextView.setText(msg);// 设置加载信息  
        tvPro.setText(text);//设置下载进度提示
        pro.setProgress(progress);
        pro.setMax(100);
        Dialog downloadDialog = new Dialog(context, R.style.loading_dialog);// 创建自定义样式dialog  
  
        downloadDialog.setCancelable(false);// 不可以用“返回键”取消  
        downloadDialog.setContentView(layout, new LinearLayout.LayoutParams(  
                LinearLayout.LayoutParams.MATCH_PARENT,  
                LinearLayout.LayoutParams.MATCH_PARENT));// 设置布局  
        return downloadDialog;  
  
    }  
	
	/**
	 * 测试连接
	 */
	public void TestConn(){
		HttpConn.callService(Const.URL, Const.NAMESPACE, Const.TESTCONNECT, null, new IWebServiceCallBack() {
			
			@Override
			public void onSucced(SoapObject result) {
				if(result != null){
					final String string = result.getProperty(0).toString();
					if("true".equals(string)){
						tvTestconn.setText("连接成功");
						tvTestconn.setTextColor(Color.GREEN);
					}else{
						tvTestconn.setText("连接失败");
						tvTestconn.setTextColor(Color.RED);
					}
				}
			}
			
			@Override
			public void onFailure(String result) {
				tvTestconn.setText("连接失败");
				tvTestconn.setTextColor(Color.RED);
			}
		});
	}
}
