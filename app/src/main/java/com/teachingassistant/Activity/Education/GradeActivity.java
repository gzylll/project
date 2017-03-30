package com.teachingassistant.Activity.Education;

import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.teachingassistant.Presentation.Adapter.MyHandler;
import com.teachingassistant.R;
import com.teachingassistant.MyApplication;
import com.teachingassistant.Bean.okHttpUilts;
import com.teachingassistant.Support.Net.Jxjson;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 成绩显示activity
 */
public class GradeActivity extends AppCompatActivity {

    private Spinner year,semester;
    private TextView grade_sum,grade_adv,grade_padv;
    private ListView gradeList;
    private MyApplication app;
    private MyHandler handler;
    private Jxjson jxjson;
    private List<Map<String,Object>> gradeInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_grade);
        initialCJCX();
    }

    /**
     * listview点击事件
     */
    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Map<String,Object> clickItem = gradeInfo.get((int)id);
            String y = year.getSelectedItem().toString().substring(0,4);
            String s = semester.getSelectedItem().toString();
            if(s.equals("1"))
            {
                s = "3";
            }else{
                s = "12";
            }
            final RequestBody requestBody = new FormBody.Builder()
                    .add("xh_id",app.readAccount())
                    .add("jxb_id",clickItem.get("jxb_id").toString())
                    .add("xnm",y)
                    .add("xqm",s)
                    .add("kcmc",clickItem.get("course_name").toString())
                    .build();
            /**
             * 请求科目详细信息
             */
            new Thread(){
                @Override
                public void run() {
                    super.run();
                    Request request = okHttpUilts.getRequest(
                            okHttpUilts.getCjcxUrl_1()+app.readAccount(),requestBody);
                    okHttpUilts.getOkHttpClient().newCall(request).enqueue(new Callback() {
                        /**
                         * 请求科目详细信息失败
                         * 标志：what:0x09
                         * @param call Call
                         * @param e IO异常
                         */
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Message message = Message.obtain();
                            message.what = 0x09;
                            handler.sendMessage(message);
                        }

                        /**
                         * 请求成功
                         * 标志：what:0x0A
                         *      arg1:0:200
                         *      arg1:1:不是200
                         * @param call Call
                         * @param response 返回
                         * @throws IOException
                         */
                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            Message message = Message.obtain();
                            message.what = 0x0A;
                            if(response.code()==200) {
                                message.arg1 = 0;
                                Bundle bundle = new Bundle();
                                bundle.putString("详细成绩",response.body().string());
                                message.setData(bundle);
                            }else{
                                message.arg1 = 1;
                            }
                            handler.sendMessage(message);
                        }
                    });
                }
            }.start();
        }
    };

    /**
     * 初始化
     */
    private void initialCJCX() {
        year = (Spinner)findViewById(R.id.Year_grade);
        semester = (Spinner)findViewById(R.id.semester_grade);
        grade_adv = (TextView)findViewById(R.id.course_adv);
        grade_padv = (TextView)findViewById(R.id.course_padv);
        grade_sum = (TextView)findViewById(R.id.course_sum);
        gradeList = (ListView)findViewById(R.id.gradeList);
        if(gradeList!=null)
            gradeList.setOnItemClickListener(onItemClickListener);

        app = new MyApplication();
        jxjson = new Jxjson();
        handler = new MyHandler(this,"GradeActivity");
        gradeInfo = new ArrayList<>();

        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setTitle("成绩查询");
            actionBar.show();
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * 成绩查询点击事件
     * @param v 点击对象
     */
    public void cjOnClick(View v) {
        final RequestBody requestBody = getRequestBody();
        new Thread()
        {
            @Override
            public void run() {
                super.run();
                Request request = okHttpUilts.getRequest(
                        okHttpUilts.getCjcxUrl()+app.readAccount(),requestBody);
                okHttpUilts.getOkHttpClient().newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.i("Grade","Failure");
                        Message message = Message.obtain();
                        message.what = 0x05;
                        handler.sendMessage(message);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Log.i("Grade","Success");
                        Message message = Message.obtain();
                        message.what = 0x06;
                        if(response.code()==200)
                        {
                            String string = response.body().string();
                            Log.e("grade",string);
                            message.arg1=0;
                            Bundle bundle = new Bundle();
                            bundle.putString("grade",string);
                            message.setData(bundle);
                        }
                        else
                        {
                            message.arg1=1;
                        }
                        handler.sendMessage(message);
                    }
                });
            }
        }.start();
    }

    /**
     * 获得请求体
     * @return RequestBody
     */
    private RequestBody getRequestBody() {
        String y = year.getSelectedItem().toString().substring(0,4);
        String s = semester.getSelectedItem().toString();
        if(s.equals("1"))
        {
            s = "3";
        }else{
            s = "12";
        }
        return new FormBody.Builder()
                .add("xnm",y)
                .add("xqm",s)
                .add("_search","false")
                .add("queryModel.showCount","100").build();
    }

    /**
     * 显示成绩
     * @param cj  原始成绩数据
     */
    public void showCJ(String cj) {
        try {
            gradeInfo = jxjson.praseJSon_cj(cj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (gradeInfo.size() == 1) {
            Toast.makeText(this,"没有成绩信息",Toast.LENGTH_SHORT).show();
        }
        Map<String, Object> sum = gradeInfo.get(gradeInfo.size() - 1);
        grade_sum.setText(sum.get("科目-学分").toString());
        grade_adv.setText(sum.get("加权平均学分(不含公选课)").toString().length() > 5
                ? sum.get("加权平均学分(不含公选课)").toString().substring(0, 5)
                : sum.get("加权平均学分(不含公选课)").toString());
        grade_padv.setText(sum.get("平均绩点(不含公选课)").toString().length() > 4
                ? sum.get("平均绩点(不含公选课)").toString().substring(0, 4)
                : sum.get("平均绩点(不含公选课)").toString());
        gradeInfo.remove(gradeInfo.size() - 1);
        //加载listview
        String[] grade_string = {"course_name", "course_id", "course_description", "course_grade",
                "course_point", "course_credit"};
        int[] grade_int = {R.id.course_name, R.id.course_id, R.id.course_description, R.id.course_grade,
                R.id.course_point, R.id.course_credit};
        SimpleAdapter simpleAdapter = new SimpleAdapter(this,
                gradeInfo,
                R.layout.list_items_grade,
                grade_string,
                grade_int);
        gradeList.setAdapter(simpleAdapter);
    }
}
