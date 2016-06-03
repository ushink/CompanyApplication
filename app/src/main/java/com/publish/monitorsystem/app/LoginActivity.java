package com.publish.monitorsystem.app;

import android.app.Dialog;
import android.content.Context;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.msystemlib.base.BaseActivity;
import com.msystemlib.utils.LogUtils;
import com.msystemlib.utils.SPUtils;
import com.msystemlib.utils.ThreadUtils;
import com.msystemlib.utils.ToastUtils;
import com.publish.monitorsystem.R;
import com.publish.monitorsystem.api.Const;
import com.publish.monitorsystem.api.bean.UserBean;
import com.publish.monitorsystem.api.db.dao.UserDao;
import com.publish.monitorsystem.api.utils.DialogUtils;
import com.publish.monitorsystem.application.SysApplication;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class LoginActivity extends BaseActivity{

    @InjectView(R.id.etLoginName)
    EditText etLoginName;
    @InjectView(R.id.etPassword)
    EditText etPassword;
    @InjectView(R.id.cb_rember)
    CheckBox cbRember;

    private UserDao userDao;
    @Override
    public int bindLayout() {
        return R.layout.activity_login;
    }

    @Override
    public void initView(View view) {
        ButterKnife.inject(this);
    }

    @OnClick(R.id.btnLogin)
    public void onLogin(){
        final String userName = etLoginName.getText().toString().trim();
        final String pwd = etPassword.getText().toString().trim();
        if(TextUtils.isEmpty(userName) || TextUtils.isEmpty(pwd)){
            ToastUtils.showToast(this, "帐号或密码不能为空");
            return;
        }
        if(cbRember.isChecked()){
            SPUtils.saveString(LoginActivity.this, Const.USERNAME, userName);
            SPUtils.saveString(LoginActivity.this, Const.PASSWORD, pwd);
        }else{
            SPUtils.clear(LoginActivity.this);
        }
        loadingDialog = DialogUtils.createLoadingDialog(this, "正在登陆，请稍后...");
        loadingDialog.show();
        ThreadUtils.runInBackground(new Runnable() {

            @Override
            public void run() {
                SystemClock.sleep(1500);

                if(Const.ADMIN.equalsIgnoreCase(userName) && Const.ADMINPWD.equals(pwd)){
                    LogUtils.d(SysApplication.TAG,"管理员登录,时间：" + SystemClock.currentThreadTimeMillis());
					jump2Activity(LoginActivity.this,LocalActivity.class, null, false);
                }else{
                    matchUser(userName,pwd);
                }
            }
        });
    }

    /**
     * 登录匹配用户
     * @param userName
     * @param pwd
     */
    private void matchUser(final String userName, final String pwd) {
        UserBean.User user = userDao.getUserInfo(userName);
        SysApplication.assignData(Const.USERNAME, userName);
        if(user != null && !"".equals(user.TypeID)){
//			SysApplication.assignData(Const.TYPEID, Integer.parseInt(user.TypeID.trim()) - 2);
            SysApplication.assignData(Const.TYPEID, user.TypeID);
            LogUtils.d("ckj",user.TypeID);
        }
        boolean avaiLogin = userDao.avaiLogin(userName, pwd);
        if(avaiLogin){
            LogUtils.d(SysApplication.TAG,userName + "用户登录,时间：" + SystemClock.currentThreadTimeMillis());
			jump2Activity(LoginActivity.this, MainActivity.class, null , false);
        }else{
            loadingDialog.dismiss();
            ToastUtils.showToast(LoginActivity.this, "账号或者密码错误");
        }
    }

    @Override
    public void doBusiness(Context mContext) {
        etLoginName.setText(SPUtils.getString(this, Const.USERNAME, ""));
        etPassword.setText(SPUtils.getString(this, Const.PASSWORD, ""));
        userDao = UserDao.getInstance(mContext);
    }

    @Override
    public void resume() {
        etLoginName.setText(SPUtils.getString(this, Const.USERNAME, ""));
        etPassword.setText(SPUtils.getString(this, Const.PASSWORD, ""));
        if(loadingDialog != null){
            loadingDialog.dismiss();
        }
    }

    @Override
    public void destroy() {
    }
    private long exitTime = 0;
    private Dialog loadingDialog;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(getApplicationContext(), "再按一次退出应用",
                        Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                mApplication.removeAll();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
