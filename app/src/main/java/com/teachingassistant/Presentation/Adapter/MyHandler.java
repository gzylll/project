package com.teachingassistant.Presentation.Adapter;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.teachingassistant.Activity.Education.CourseSearchActivity;
import com.teachingassistant.Activity.Education.ExamActivity;
import com.teachingassistant.Activity.Education.GradeActivity;
import com.teachingassistant.R;
import com.teachingassistant.Support.Net.Jxhtml;

import java.lang.ref.WeakReference;
import java.util.Map;

/**
 * Handler类
 * 更新：使用静态内部类持有弱引用对象取代原来匿名内部类的方式解决内存泄露
 * 更新：剥离为单独的类，减少重复代码
 * Created by 郭振阳 on 2017/2/2.
 */

public class MyHandler extends Handler {

    private WeakReference<Activity> mActivity;

    private String className;

    public MyHandler(Activity activity,String className)
    {
        mActivity = new WeakReference<>(activity);
        this.className = className;
    }
    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);

        switch(className)
        {
            case "CourseSearchActivity":
                CourseSearchActivity courseSearchActivity = (CourseSearchActivity) mActivity.get();
                if(msg.what==0x03)
                {
                    Toast.makeText(courseSearchActivity," Request Error ",Toast.LENGTH_SHORT)
                            .show();
                }
                else if(msg.what==0x04)
                {
                    if(msg.arg1==0)
                    {
                        courseSearchActivity.saveKB(msg.getData().getString("return"));
                    }
                    else
                    {
                        Toast.makeText(courseSearchActivity," Return Error ", Toast.LENGTH_SHORT)
                                .show();
                    }
                }
                break;

            case "ExamActivity":
                ExamActivity examActivity = (ExamActivity) mActivity.get();
                if(msg.what==0x07) {
                    Toast.makeText(examActivity," Request Error ",Toast.LENGTH_LONG)
                            .show();
                }
                else if(msg.what==0x08){
                    if(msg.arg1==0){
                        examActivity.showKS(msg.getData().getString("exam"));
                    }else{
                        Toast.makeText(examActivity," ReturnData Error",Toast.LENGTH_LONG)
                                .show();
                    }
                }
                break;

            case "GradeActivity":
                GradeActivity gradeActivity = (GradeActivity) mActivity.get();
                if(msg.what == 0x05)
                {
                    Toast.makeText(gradeActivity," Request Failed ",Toast.LENGTH_LONG).show();
                }
                else if(msg.what == 0x06)
                {
                    if(msg.arg1==0)
                    {
                        Log.i("Grade"," receive data ");
                        gradeActivity.showCJ(msg.getData().getString("grade"));
                    }
                    else
                    {
                        Toast.makeText(gradeActivity,"Net Failed",Toast.LENGTH_LONG).show();
                    }
                }
                else if(msg.what == 0x09)
                {
                    Toast.makeText(gradeActivity," Request Details Error ",Toast.LENGTH_LONG).show();
                }
                else if(msg.what == 0x0A)
                {
                    //弹出对话框显示成绩
                    if(msg.arg1 == 0)
                    {
                        //获得弹出框布局的实例化对象
                        LayoutInflater inflater = LayoutInflater.from(gradeActivity);
                        View layout = inflater.inflate(R.layout.dialog_exam,null);

                        //获取解析后的结果
                        Map<String,Object> grade = Jxhtml.praseGrade(
                                msg.getData().getString("详细成绩").toString());
                        //对应位置赋值
                        TextView textView;
                        textView = (TextView)layout.findViewById(R.id.grade_normal_ratio);
                        textView.setText(grade.get("平时比例").toString());
                        textView = (TextView)layout.findViewById(R.id.grade_normal_grade);
                        textView.setText(grade.get("平时成绩").toString());
                        textView = (TextView)layout.findViewById(R.id.grade_final_ratio);
                        textView.setText(grade.get("期末比例").toString());
                        textView = (TextView)layout.findViewById(R.id.grade_final_grade);
                        textView.setText(grade.get("期末成绩").toString());
                        textView = (TextView)layout.findViewById(R.id.grade_sum_ratio);
                        textView.setText("100%");
                        textView = (TextView)layout.findViewById(R.id.grade_sum_grade);
                        textView.setText(grade.get("总评").toString());

                        //构建弹窗
                        AlertDialog.Builder builder = new AlertDialog.Builder(gradeActivity);
                        builder.setView(layout);
                        builder.setCancelable(true);
                        builder.create().show();
                    }
                }
                break;
            default:
                break;
        }
    }
}
