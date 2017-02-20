package com.teachingassistant.Activity.Search;

import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.teachingassistant.Presenter.MyHandler;
import com.teachingassistant.R;
import com.teachingassistant.Support.Bean.MyApplication;
import com.teachingassistant.Support.Bean.okHttpUilts;
import com.teachingassistant.Support.Net.Jxjson;

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
 * 考试显示activity
 */
public class ExamActivity extends AppCompatActivity {

    private Spinner year,semester;
    private TextView exam;
    private ActionBar actionBar;
    private ListView examlist;
    private SimpleAdapter simpleAdapter;
    private MyApplication app;
    private MyHandler handler;
    private Jxjson jxjson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam);
        initialKSCX();
    }

    /**
     * 实例化对象
     */
    private void initialKSCX() {
        year = (Spinner)findViewById(R.id.Year_exam);
        semester = (Spinner)findViewById(R.id.semester_exam);
        exam = (TextView)findViewById(R.id.re_exam);
        examlist = (ListView)findViewById(R.id.examList);

        app = new MyApplication();
        jxjson = new Jxjson();
        handler = new MyHandler(this,"ExamActivity");

        actionBar = getSupportActionBar();
        actionBar.setTitle("考试查询");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.show();
    }

    /**
     * 获得查询的请求体
     * @return 请求体
     */
    private RequestBody getRequestBody() {
        String y = year.getSelectedItem().toString().substring(0,4);
        String s = semester.getSelectedItem().toString();
        if(s.equals("1")) {
            s = "3";
        }else {
            s = "12";
        }
        return new FormBody.Builder()
                .add("xnm",y)
                .add("xqm",s)
                .add("_search","false")
                .add("queryModel.showCount","100").build();
    }

    /**
     * 查询点击事件
     * @param v 点击对象
     */
    public void ksOnClick(View v) {
        new Thread(){
            @Override
            public void run() {
                super.run();
                Request request = okHttpUilts.getRequest(
                        okHttpUilts.getKscxUrl()+app.readAccount(),getRequestBody());
                okHttpUilts.getOkHttpClient().newCall(request).enqueue(new Callback() {

                    /**
                     * 失败
                     * 标志：what:0x07
                     * @param call Call对象
                     * @param e IO异常
                     */
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.i("Exam","onFailure");
                        Message message = Message.obtain();
                        message.what = 0x07;
                        handler.sendMessage(message);
                    }

                    /**
                     * 请求成功
                     * 标志：what:0x08
                     *      arg1:0:返回200，成功
                     *           1：返回不是200
                     * @param call Call对象
                     * @param response 返回数据对象
                     * @throws IOException IO异常
                     */
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Log.i("Exam","onResponse");
                        Message message = Message.obtain();
                        message.what = 0x08;
                        if(response.code()==200) {
                            message.arg1 = 0;
                            Bundle bundle = new Bundle();
                            bundle.putString("exam",response.body().string());
                            message.setData(bundle);
                        }else{
                            message.arg1=1;
                        }
                        handler.sendMessage(message);
                    }
                });
            }
        }.start();
    }

    /**
     * 显示考试数据
     * @param ksData 原始考试数据
     */
    public void showKS(String ksData) {
        List<Map<String,Object>> examList = new ArrayList<>();
        try{
            examList = jxjson.praseJSon_ks(ksData);
        }catch (Exception e){
            e.printStackTrace();
        }
        if(examList.size()==0) {
            exam.setText("没有找到考试信息！");
        }
        else{
            exam.setText("共找到"+examList.size()+"项考试");
        }
        String[] examListString = {"exam_name","exam_time","exam_local","exam_status"};
        int[] examListInt = {R.id.exam_name,R.id.exam_time,R.id.exam_local,R.id.exam_status};
        simpleAdapter = new SimpleAdapter(this,
                                        examList,
                                        R.layout.list_items_exam,
                                        examListString,
                                        examListInt);
        examlist.setAdapter(simpleAdapter);
    }
}
