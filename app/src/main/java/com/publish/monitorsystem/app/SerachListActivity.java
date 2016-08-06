package com.publish.monitorsystem.app;

import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.msystemlib.base.BaseActivity;
import com.msystemlib.utils.AlertUtils;
import com.msystemlib.utils.ThreadUtils;
import com.publish.monitorsystem.R;
import com.publish.monitorsystem.api.bean.InventoryBean;
import com.publish.monitorsystem.api.db.dao.InventoryDao;
import com.publish.monitorsystem.api.db.dao.InventoryEqptDao;
import com.publish.monitorsystem.api.readrfid.ReadRFID;
import com.publish.monitorsystem.application.SysApplication;

import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class SerachListActivity extends BaseActivity{


    @InjectView(R.id.tv_title)
    TextView tvTitle;
    @InjectView(R.id.lv_searchList)
    ListView lv_searchList;
    private ReadRFID readRFID;
    private SysApplication myapp;

    private InventoryDao inventoryDao;
    private InventoryEqptDao inventoryEqptDao;
    private  List<InventoryBean.Inventory> allInventoryList;
    private final int LOAD = 80;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage (Message msg) {
            switch (msg.what){
                case LOAD:
                    frushAdapter();
                    break;
            }
        }
    };
    private CommonAdapter<InventoryBean.Inventory> adapter;

    /**
     * listView刷新
     */
    private void frushAdapter () {
        if(adapter != null){
            adapter.notifyDataSetChanged();
        }else{
            adapter =  new CommonAdapter<InventoryBean.Inventory>(allInventoryList) {
                @Override
                public View getView (int position, View convertView, ViewGroup parent) {
                    View view;
                    if (convertView == null) {
                        view = getLayoutInflater()
                                .inflate(R.layout.listitemview_searchlist,
                                        parent, false);
                    } else {
                        view = convertView;
                    }
                    TextView tv_search = (TextView) view.findViewById(R.id.tv_search);
                    String planName = allInventoryList.get(position).PlanName;
                    tv_search.setText(planName);
                    return view;
                }
            };
            lv_searchList.setAdapter(adapter);
        }
    }

    @Override
    public int bindLayout () {
        return R.layout.activity_searchlist;
    }

    @Override
    public void initView (View view) {
        ButterKnife.inject(this);
        tvTitle.setText("档案查找");
        lv_searchList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick (AdapterView<?> parent, View view, int position, long id) {
                HashMap<String,String> map = new HashMap<String, String>();
                map.put("planID",allInventoryList.get(position).PlanID);
                jump2Activity(SerachListActivity.this,SearchActivity.class,map,false);
            }
        });
        lv_searchList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick (AdapterView<?> parent, View view, final int position, long id) {

                AlertUtils.dialog1(SerachListActivity.this, "提示", "是否删除计划？",new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick (DialogInterface dialog, int which) {
                        dialog.dismiss();
                        //长按删除刷新listView
                        inventoryEqptDao.deleteInventoryEqptByParentPlan(allInventoryList.get(position).ParentPlanID);
                        inventoryDao.deleteAllInventory(allInventoryList.get(position).PlanID);
                        allInventoryList.remove(allInventoryList.get(position));
                        adapter.notifyDataSetChanged();
                    }
                },new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                return true;
            }
        });
    }

    @Override
    public void doBusiness (Context mContext) {
        //变量初始化
        Application app=getApplication();
        myapp=(SysApplication)app;
        readRFID = new ReadRFID(myapp);
        //数据层初始化
        inventoryEqptDao = InventoryEqptDao.getInstance(this);
        inventoryDao = InventoryDao.getInstance(this);
        ThreadUtils.runInBackground(new Runnable() {
            @Override
            public void run () {
                readRFID.initReader();
                allInventoryList  = inventoryDao.getAllInventoryList(2);
                handler.sendEmptyMessage(LOAD);
            }
        });


    }

    @Override
    public void resume () {

    }

    @Override
    public void destroy () {
        readRFID.colseReader();
    }
}
