package com.teachingassistant.Activity.Search;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.teachingassistant.Presenter.MyHandler;
import com.teachingassistant.R;
import com.teachingassistant.Support.Bean.Course;
import com.teachingassistant.Support.Bean.MyApplication;
import com.teachingassistant.Support.Bean.dbHelper;
import com.teachingassistant.Support.Bean.okHttpUilts;
import com.teachingassistant.Support.Net.Jxjson;
import com.teachingassistant.Support.View.Dialog.ChooseTimeDialog;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 课程显示activity
 */
public class CourseSearchActivity extends AppCompatActivity {

    private  Jxjson jxjson;
    private MyHandler handler;
    private MyApplication app;
    private String strxn, strxq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_search);
        initialKBCX();
        loadCourseTable();
    }

    /**
     * 初始化，实例化控件
     */
    private void initialKBCX() {

        app = new MyApplication();
        jxjson = new Jxjson();
        handler = new MyHandler(this,"CourseSearchActivity");

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setTitle("选择课表");
            actionBar.show();
        }
    }

    /**
     * 加载我的课表列表
     */
    private void loadCourseTable() {
        ListView myCourseTable = (ListView) findViewById(R.id.my_course_table);
        String username = app.readAccount();
        final List<String> list = app.readCourseTableNameById(username);
        List<Map<String, String>> list1 = new ArrayList<>();
        if (list.size() != 0) {
            TextView num = (TextView) findViewById(R.id.course_table_num);
            String s = num.getText().toString().substring(0, 5) + list.size();
            num.setText(s);
            for (int i = 0; i < list.size(); i++) {
                String xn = list.get(i).substring(0, 9) + "学年";
                String xq = "第" + list.get(i).substring(10, 11) + "学期";
                Map<String, String> map = new HashMap<>();
                map.put("ctName", xn + " " + xq);
                list1.add(map);
            }
        }
        final SimpleAdapter adapter = new SimpleAdapter(this,
                                                  list1,
                                                  R.layout.list_items_my_course_table,
                                                  new String[]{"ctName"},
                                                  new int[]{R.id.course_table_name});
        myCourseTable.setAdapter(adapter);
        myCourseTable.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                app.writeDefaultCourseTableName(list.get((int)id));
                Intent intent = new Intent(CourseSearchActivity.this,CourseActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }
    /**
     * 课表查询的点击事件
     * 1.获取RequestBody
     * 2.生成Request
     * 3.获取结果
     * @param v 点击的对象
     */
    public void kbOnClick(View v) {
        switch (v.getId()) {
            //点击选择时间
            case R.id.course_time_choose:
                //弹窗
                ChooseTimeDialog dialog = new ChooseTimeDialog(this);
                dialog.show();
                //根据弹窗回调，获得选中的选项
                dialog.setOnTimeCListener(new ChooseTimeDialog.OnTimeCListener() {
                    @Override
                    public void OnClick(String xn, String xq) {
                        strxn=xn;
                        strxq=xq;
                        TextView textView = (TextView)findViewById(R.id.kbtime_show);
                        textView.setText(xn+""+xq);
                    }
                });
                break;
            case R.id.kbsearch:
                //点击查询，非空才可
                if(strxq==null||strxn==null)
                    Toast.makeText(this,"请选择时间",Toast.LENGTH_SHORT).show();
                else
                    requestForKB();
        }
    }

    /**
     * 通过网络获取课表的函数
     */
    public void requestForKB(){
        final RequestBody requestBody = getRequestBody();
        new Thread() {
            @Override
            public void run() {
                super.run();
                final Request request = okHttpUilts.getRequest(
                        okHttpUilts.getKbcxurl() + app.readAccount(), requestBody);
                okHttpUilts.getOkHttpClient().newCall(request).enqueue(new Callback() {

                    /**
                     * 请求失败
                     * 标志：what:0x03
                     * @param call Call对象
                     * @param e IO异常
                     */
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Message message = Message.obtain();
                        message.what = 0x03;
                        handler.sendMessage(message);
                    }

                    /**
                     * 请求成功
                     * 标志：what:0x04
                     *      arg1:0:查询成功，返回200
                     *           1:返回不是200
                     * @param call Call对象
                     * @param response 请求返回
                     * @throws IOException
                     */
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Message message = Message.obtain();
                        message.what = 0x04;
                        if (response.code() == 200) {
                            message.arg1 = 0;
                            Bundle bundle = new Bundle();
                            bundle.putString("return", response.body().string());
                            message.setData(bundle);
                        } else {
                            message.arg1 = 1;
                        }
                        handler.sendMessage(message);
                    }
                });
            }
        }.start();
    }


    /**
     * 获取选择的学年和学期，返回对应的RequestBody
     * 规则：学年为选择项目的前4个字符
     *      学期1为12，2为3
     * @return RequestBody
     */

    private RequestBody getRequestBody() {
        String y = strxn.substring(0,4);
        String s = strxq.substring(1,2);
        if(s.equals("1"))
            s="3";
        else
            s="12";
        return new FormBody.Builder()
                .add("xnm",y)
                .add("xqm",s).build();
    }

    /**
     * 二次解析课表数据，并存储课表到数据库
     * @param kc 课表数据
     */
    public void saveKB(String kc) {
        Map<String,String> studentInfo = new HashMap<>();
        List<Course> kb = new ArrayList<>();
        try {
            studentInfo= jxjson.praseJSon_xsxx(kc);
            kb=jxjson.praseJSon_kb(kc);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(kb.size()==0){
            Toast.makeText(this,"没有查询到课程信息",Toast.LENGTH_LONG).show();
        }else {
            //获得数据库
            String dbName = studentInfo.get("学年") + '-' + studentInfo.get("学期") + '-' +
                    studentInfo.get("学号");
            //没有重复的话，记录在本地
            if (app.writeCourseTableName(dbName)) {
                com.teachingassistant.Support.Bean.dbHelper dbHelper = new dbHelper(this, dbName);
                //解析数据为对象并传入数据库插入
                for (int i = 0; i < kb.size(); i++) {
                    dbHelper.instert(kb.get(i));
                }
            }
            app.writeDefaultCourseTableName(dbName);

            //存储完毕，跳转
            Intent intent = new Intent(this,CourseActivity.class);
            startActivity(intent);
            finish();
        }
    }

}