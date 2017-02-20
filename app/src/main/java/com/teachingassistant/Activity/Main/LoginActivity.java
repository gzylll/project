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
import android.widget.TextView;
import android.widget.Toast;

import com.teachingassistant.Activity.Search.SearchMainActivity;
import com.teachingassistant.R;
import com.teachingassistant.Support.Bean.MyApplication;
import com.teachingassistant.Support.Bean.okHttpUilts;
import com.teachingassistant.Support.Net.Jxhtml;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 登陆处理activity
 */
public class LoginActivity extends AppCompatActivity {

    private TextView username, password;
    private Button login;
    private CheckBox rem_pwn, auto_login;
    private Handler handler;

    MyApplication app = new MyApplication();
    ActionBar actionBar;

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
                        //跳转
                        Toast.makeText(LoginActivity.this, "欢迎您！"+app.readAccount(),
                                Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(LoginActivity.this, SearchMainActivity.class);
                        LoginActivity.this.startActivity(intent);
                        LoginActivity.this.finish();
                    }else if(msg.arg1 == 1){
                        Toast.makeText(LoginActivity.this,msg.getData().getString("status"),
                                Toast.LENGTH_LONG).show();
                    }else if (msg.arg1 == 2) {
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
        login = (Button) findViewById(R.id.login);
        rem_pwn = (CheckBox) findViewById(R.id.rempwn);
        auto_login = (CheckBox) findViewById(R.id.autologin);

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

