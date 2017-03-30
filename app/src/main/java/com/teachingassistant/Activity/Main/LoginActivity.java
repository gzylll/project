package com.teachingassistant.Activity.Main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.teachingassistant.MyApplication;
import com.teachingassistant.R;
import com.teachingassistant.Bean.okHttpUilts;
import com.teachingassistant.Support.Net.Jxhtml;
import com.tencent.TIMCallBack;
import com.tencent.TIMManager;
import com.tencent.TIMUser;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import tencent.tls.platform.TLSAccountHelper;
import tencent.tls.platform.TLSErrInfo;
import tencent.tls.platform.TLSLoginHelper;
import tencent.tls.platform.TLSPwdLoginListener;
import tencent.tls.platform.TLSStrAccRegListener;
import tencent.tls.platform.TLSUserInfo;

/**
 * 登陆处理activity
 * 流程:1.用户输入账户密码，非空后进入2
 *     2.开线程POST账户密码到教务系统验证，成功后进入3
 *     3.在腾讯服务器端登陆该账户（TLS登陆),登陆成功获取UserSig，进入5，否则4
 *     4.账号不存在则注册账户（TLS注册），注册成功返回3，否则失败
 *     5.继续登陆获取服务（TIM登陆）
 * 以上流程全部完成后跳转界面，完成登陆过程。
 */
public class LoginActivity extends AppCompatActivity {

    private TextView username, password,status;
    private Button login;
    private CheckBox rem_pwn, auto_login;
    private Handler handler;
    private TLSAccountHelper accountHelper;
    private TLSLoginHelper loginHelper;
    private MyApplication app = new MyApplication();
    private ActionBar actionBar;
    private String TIMaccount,TIMpassword;
    private ProgressBar pb;

    /**
     * 注册监听器
     */
    private TLSStrAccRegListener tlsStrAccRegListener = new TLSStrAccRegListener() {
        @Override
        public void OnStrAccRegSuccess(TLSUserInfo tlsUserInfo) {
            Log.d("LoginActivity","TLS注册账号成功");
            //注册成功即登陆
            status.setText("TLS注册");
            loginHelper.TLSPwdLogin(TIMaccount,TIMpassword.getBytes(),loginListener);
        }

        @Override
        public void OnStrAccRegFail(TLSErrInfo tlsErrInfo) {
                Toast.makeText(LoginActivity.this,"ErrCode:"+tlsErrInfo.ErrCode+"Msg:"+tlsErrInfo.Msg,
                        Toast.LENGTH_SHORT).show();
        }

        @Override
        public void OnStrAccRegTimeout(TLSErrInfo tlsErrInfo) {
            Toast.makeText(LoginActivity.this,"超时:"+"ErrCode:"+tlsErrInfo.ErrCode+"Msg:"+tlsErrInfo.Msg,
                    Toast.LENGTH_LONG).show();
        }
    };

    /**
     * 登陆监听器
     */
    private TLSPwdLoginListener loginListener = new TLSPwdLoginListener() {
        @Override
        public void OnPwdLoginSuccess(TLSUserInfo tlsUserInfo) {
            Log.d("LoginActivity","TLS登陆成功");
            status.setText("TIM登陆");
            //初始化TIMUser对象
            TIMUser timUser = new TIMUser();
            timUser.setIdentifier(TIMaccount);
            timUser.setAccountType(String.valueOf(MyApplication.accountType));
            timUser.setAppIdAt3rd(String.valueOf(MyApplication.sdkAPPID));
            //TIMSDK登陆
            TIMManager.getInstance().login(
                    MyApplication.sdkAPPID,              //APPID
                    timUser,                             //TIMUser
                    loginHelper.getUserSig(TIMaccount),  //UserSig
                    new TIMCallBack() {                  //回调
                        @Override
                        public void onError(int i, String s) {

                        }

                        @Override
                        public void onSuccess() {
                            Log.d("LoginActivity","TIM登陆成功");
                            pb.setVisibility(ProgressBar.GONE);
                            status.setVisibility(View.GONE);
                            //登陆成功跳转
                            Toast.makeText(LoginActivity.this, "欢迎您！"+app.readAccount(),
                                    Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            LoginActivity.this.startActivity(intent);
                            LoginActivity.this.finish();
                        }
                    });
        }

        @Override
        public void OnPwdLoginReaskImgcodeSuccess(byte[] bytes) {
        }

        @Override
        public void OnPwdLoginNeedImgcode(byte[] bytes, TLSErrInfo tlsErrInfo) {

        }

        @Override
        public void OnPwdLoginFail(TLSErrInfo tlsErrInfo) {
            if(tlsErrInfo.ErrCode==229) {
                int result = accountHelper.TLSStrAccReg(TIMaccount,
                                                        TIMpassword,
                                                        tlsStrAccRegListener);
                if (result == TLSErrInfo.INPUT_INVALID) {
                    Toast.makeText(LoginActivity.this,TIMaccount,Toast.LENGTH_SHORT).show();
                    Toast.makeText(LoginActivity.this, "acount invalid", Toast.LENGTH_SHORT).show();
                }
            }
            else{
                Toast.makeText(LoginActivity.this,"登陆腾讯服务器失败:"+tlsErrInfo.ErrCode+tlsErrInfo.Msg,
                        Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void OnPwdLoginTimeout(TLSErrInfo tlsErrInfo) {
            Toast.makeText(LoginActivity.this,"登陆腾讯服务器超时", Toast.LENGTH_SHORT).show();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginInitial();

        handler = new Handler() {
            /**
             * 处理登陆线程的消息
             * @param msg
             */
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 0x00) {
                    Toast.makeText(LoginActivity.this, "发送请求失败,请检查网络连接", Toast.LENGTH_SHORT).show();
                } else if (msg.what == 0x01) {
                    if (msg.arg1 == 0 ){
                        //验证矿大身份正确之后登陆腾讯服务器
                        loginHelper.TLSPwdLogin(TIMaccount,TIMpassword.getBytes(),loginListener);
                    }else if(msg.arg1 == 1){
                        pb.setVisibility(ProgressBar.GONE);
                        status.setVisibility(View.GONE);
                        Toast.makeText(LoginActivity.this,msg.getData().getString("status"),
                                Toast.LENGTH_LONG).show();
                    }else if (msg.arg1 == 2) {
                        pb.setVisibility(ProgressBar.GONE);
                        status.setVisibility(View.GONE);
                        Toast.makeText(LoginActivity.this, " net error ", Toast.LENGTH_LONG).show();
                    }
                }

            }
        };
    }

    /**
     * 登陆初始化
     * 1.对象实例化
     * 2.记住密码的实现
     */
    private void loginInitial() {
        username = (TextView) findViewById(R.id.username);
        password = (TextView) findViewById(R.id.password);
        status = (TextView)findViewById(R.id.login_status);
        login = (Button) findViewById(R.id.login);
        rem_pwn = (CheckBox) findViewById(R.id.rempwn);
        auto_login = (CheckBox) findViewById(R.id.autologin);
        pb = (ProgressBar)findViewById(R.id.login_progress);

        actionBar = getSupportActionBar();
        actionBar.setTitle("登陆");
        actionBar.show();

        if (app.isRememberPWN()) {
            username.setText(app.readAccount());
            password.setText(app.readPassword());
            rem_pwn.setChecked(true);
        }
        if(app.isAutoLogin())
        {
            auto_login.setChecked(true);
            login.performClick();
        }

        accountHelper = TLSAccountHelper.getInstance().init(MyApplication.getContext(),
                                                            MyApplication.sdkAPPID,
                                                            MyApplication.accountType,
                                                            MyApplication.appVer);

        loginHelper = TLSLoginHelper.getInstance().init(MyApplication.getContext(),
                                                        MyApplication.sdkAPPID,
                                                        MyApplication.accountType,
                                                        MyApplication.appVer);
    }

    /**
     * 登陆按钮的点击事件
     * 1.判断输入框的内容，不为空则更新，为空报错
     * 2.开线程发送登陆请求
     */
    public void loginOnClick(View v) {
        //判断空
        if (username.getText().toString().equals("") || password.getText().toString().equals("")) {
            Toast.makeText(LoginActivity.this, "用户名和密码不能为空", Toast.LENGTH_SHORT).show();
        } else {
            app.writeAccount(username.getText().toString());
            if (rem_pwn.isChecked()) {
                app.writeIsRememberPWN(true);
                app.writePassword(password.getText().toString());
            } else {
                app.writeIsRememberPWN(false);
            }
            if(auto_login.isChecked())
            {
                app.writeIsAutoLogin(true);
            }else{
                app.writeIsAutoLogin(false);
            }

            pb.setVisibility(ProgressBar.VISIBLE);
            status.setVisibility(View.VISIBLE);
            status.setText("验证矿大身份");

            TIMaccount = "cumt_"+username.getText().toString();
            MyApplication.TIMAccount = TIMaccount;
            TIMpassword = password.getText().toString();
            //POST请求
            final RequestBody requestBody = new FormBody.Builder()
                    .add("yhm", username.getText().toString())
                    .add("mm", password.getText().toString())
                    .add("yzm", "").build();
            //判断响应
            new Thread() {
                /**
                 * 请求登陆
                 */
                public void run() {
                    Request request = okHttpUilts.getRequest(okHttpUilts.getLoginurl(), requestBody);
                    okHttpUilts.getOkHttpClient().newCall(request).enqueue(new Callback() {
                        /**
                         * 请求失败
                         * 标志：what:0x00
                         * @param call
                         * @param e
                         */
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Message m = handler.obtainMessage();
                            m.what = 0x00;
                            handler.sendMessage(m);
                        }

                        /**
                         * 请求成功
                         * 标志：what:0x01
                         *      arg1:0:返回值200，登陆成功，返回success
                         *           1:返回值200,登录失败，返回错误信息
                         *           2:返回值不是200，网络问题
                         * @param call
                         * @param response
                         * @throws IOException
                         */
                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            Log.i("login", "Response");
                            Message m = handler.obtainMessage();
                            m.what = 0x01;
                            if (response.code() == 200) {
                                String string = response.body().string();
                                if (Jxhtml.isLogin(string).equals("success")) {
                                    Bundle bundle = new Bundle();
                                    bundle.putString("return", string);
                                    bundle.putString("status", "success");
                                    m.arg1 = 0;
                                    m.setData(bundle);
                                } else {
                                    Bundle bundle = new Bundle();
                                    bundle.putString("return", string);
                                    bundle.putString("status", Jxhtml.isLogin(string));
                                    m.arg1 = 1;
                                    m.setData(bundle);
                                }
                            } else {
                                m.arg1 = 2;
                            }
                            handler.sendMessage(m);
                        }
                    });
                }
            }.start();
        }
    }
}

