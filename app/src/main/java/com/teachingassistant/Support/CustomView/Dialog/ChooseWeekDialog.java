package com.teachingassistant.Support.CustomView.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.teachingassistant.Presentation.Adapter.TextWheelAdapter;
import com.teachingassistant.R;
import com.teachingassistant.MyApplication;
import com.teachingassistant.Support.CustomView.WheelView.view.OnWheelChangedListener;
import com.teachingassistant.Support.CustomView.WheelView.view.OnWheelScrollListener;
import com.teachingassistant.Support.CustomView.WheelView.view.WheelView;

import java.util.ArrayList;


/**
 * 当前周设置弹窗类
 * Created by 郭振阳 on 2017/2/8.
 */

public class ChooseWeekDialog extends Dialog implements View.OnClickListener{

    private Context context;

    private ArrayList<String> weeks = new ArrayList<>();
    private Boolean issetData = false;
    private String weekDefault;

    //字体最大最小设置
    private int maxTextSize = 24;
    private int minTextSize = 14;

    TextWheelAdapter madapter;
    private OnWeekCLister onWeekCLister;

    private MyApplication application = new MyApplication();

    public ChooseWeekDialog(Context context){
        super(context);
        this.context=context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_set_week);
        WheelView week = (WheelView) findViewById(R.id.choose_week_wheel);
        TextView btn_sure = (TextView) findViewById(R.id.btn_choose_week_sure);
        TextView btn_cancel = (TextView) findViewById(R.id.btn_choose_week_cancel);
        btn_sure.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);
        weekDefault="第"+application.readDefaultWeek()+"周";

        if(!issetData)
            setWeeekList();

        madapter = new TextWheelAdapter(context,weeks,getWeekItem(weekDefault),
                maxTextSize,minTextSize);
        week.setVisibleItems(5);
        week.setViewAdapter(madapter);
        week.setCurrentItem(getWeekItem(weekDefault));
        week.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                String currentText = (String)madapter.getItemText(wheel.getCurrentItem());
                weekDefault = currentText;
                setTextSize(currentText,madapter);
            }
        });

        week.addScrollingListener(new OnWheelScrollListener() {
            @Override
            public void onScrollingStarted(WheelView wheel) {

            }

            @Override
            public void onScrollingFinished(WheelView wheel) {
                String currentText = (String) madapter.getItemText(wheel.getCurrentItem());
                setTextSize(currentText, madapter);
            }
        });

    }

    /**
     * 初始化现实的数组
     */
    private void setWeeekList(){
        for(int i=0;i<25;i++){
            weeks.add("第"+(i+1)+"周");
        }
        issetData=true;
    }

    /**
     * 返回对应周的索引，找不到获得默认的索引
     * @param week 所查询的周
     * @return 查询的索引
     */
    private int getWeekItem(String week){
        int size = weeks.size();
        for(int i=0;i<size;i++){
            if(weeks.get(i).equals(week)){
                return i;
            }
        }
        return getWeekItem(weekDefault);
    }

    /**
     * 更改字体大小
     * @param curriteItemText 当前字体
     * @param adapter 当前WheelView的Adapter
     */
    private void setTextSize(String curriteItemText, TextWheelAdapter adapter) {
        //获得view数组
        ArrayList<View> arrayList = adapter.getTestViews();
        int size = arrayList.size();
        String currentText;
        for (int i = 0; i < size; i++) {
            TextView textvew = (TextView) arrayList.get(i);
            currentText = textvew.getText().toString();
            //是当前项目放大
            if (curriteItemText.equals(currentText)) {
                textvew.setTextSize(24);
            } else {
                textvew.setTextSize(14);
            }
        }
    }

    /**
     * 设置回调
     * @param onWeekCLister 回调监听
     */
    public void setOnWeekCLister(OnWeekCLister onWeekCLister){
        this.onWeekCLister = onWeekCLister;
    }

    //回调监听接口
    public interface OnWeekCLister{
        public void onClick(String weekchoosed);
    }

    /**
     * 类的点击函数
     * @param v 被点击的view
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //点击确定
            case R.id.btn_choose_week_sure:
                //写入默认周
                application.writeDefaultWeek(Integer.parseInt(
                        weekDefault.substring(1,weekDefault.length()-1)));
                if(onWeekCLister!=null){
                    //调用回调，将选择的返回
                    onWeekCLister.onClick(weekDefault);
                }
                dismiss();
                break;
            case R.id.btn_choose_week_cancel:
                dismiss();
                break;
        }
    }


}
