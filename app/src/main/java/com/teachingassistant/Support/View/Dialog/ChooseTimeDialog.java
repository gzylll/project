package com.teachingassistant.Support.View.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.teachingassistant.Presenter.TextWheelAdapter;
import com.teachingassistant.R;
import com.teachingassistant.Support.View.WheelView.view.OnWheelChangedListener;
import com.teachingassistant.Support.View.WheelView.view.OnWheelScrollListener;
import com.teachingassistant.Support.View.WheelView.view.WheelView;

import java.util.ArrayList;

/**
 * 选择时间弹窗类
 * Created by 郭振阳 on 2017/2/8.
 */

public class ChooseTimeDialog extends Dialog implements View.OnClickListener {

    private Context context;

    private ArrayList<String> xnlist = new ArrayList<>();
    private ArrayList<String> xqlist = new ArrayList<>();

    private String strXn;
    private String strXq;
    //字体最大最小设置
    private int maxTextSize = 24;
    private int minTextSize = 14;

    private OnTimeCListener onTimeCListener;

    TextWheelAdapter xnAdapter,xqAdapter;

    public ChooseTimeDialog(Context context) {
        super(context);
        this.context=context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_choose_time);

        WheelView xn = (WheelView) findViewById(R.id.choose_xuennian_wheel);
        WheelView xq = (WheelView) findViewById(R.id.choose_xueqi_wheel);
        TextView btn_sure = (TextView) findViewById(R.id.btn_choose_time_sure);
        TextView btn_cacel = (TextView) findViewById(R.id.btn_choose_time_cancel);
        btn_sure.setOnClickListener(this);
        btn_cacel.setOnClickListener(this);

        iniData();

        xnAdapter = new TextWheelAdapter(context,xnlist,0,maxTextSize,minTextSize);
        xn.setVisibleItems(5);
        xn.setCurrentItem(getXnIndex(strXn));
        xn.setViewAdapter(xnAdapter);
        xn.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                String currentText = (String)xnAdapter.getItemText(wheel.getCurrentItem());
                strXn=currentText;
                setTextSize(currentText,xnAdapter);
            }
        });
        xn.addScrollingListener(new OnWheelScrollListener() {
            @Override
            public void onScrollingStarted(WheelView wheel) {

            }

            @Override
            public void onScrollingFinished(WheelView wheel) {
                String currentText = (String) xnAdapter.getItemText(wheel.getCurrentItem());
                setTextSize(currentText, xnAdapter);
            }
        });

        xqAdapter = new TextWheelAdapter(context,xqlist,0,maxTextSize,minTextSize);
        xq.setVisibleItems(5);
        xq.setCurrentItem(getXqIndex(strXq));
        xq.setViewAdapter(xqAdapter);
        xq.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                String currentText = (String)xqAdapter.getItemText(wheel.getCurrentItem());
                strXq=currentText;
                setTextSize(currentText,xqAdapter);
            }
        });
        xq.addScrollingListener(new OnWheelScrollListener() {
            @Override
            public void onScrollingStarted(WheelView wheel) {

            }

            @Override
            public void onScrollingFinished(WheelView wheel) {
                String currentText = (String) xqAdapter.getItemText(wheel.getCurrentItem());
                setTextSize(currentText, xqAdapter);
            }
        });

    }

    private void iniData(){
        xqlist.clear();
        xqlist.add("第1学期");
        xqlist.add("第2学期");
        strXq=xqlist.get(0);

        xnlist.clear();
        xnlist.add("2013-2014学年");
        xnlist.add("2014-2015学年");
        xnlist.add("2015-2016学年");
        xnlist.add("2016-2017学年");
        xnlist.add("2017-2018学年");
        xnlist.add("2018-2019学年");
        strXn=xnlist.get(0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_choose_time_sure:
                if(onTimeCListener!=null){
                    onTimeCListener.OnClick(strXn,strXq);
                }
                dismiss();
                break;
            case R.id.btn_choose_time_cancel:
                dismiss();
        }
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
                textvew.setTextSize(maxTextSize);
            } else {
                textvew.setTextSize(minTextSize);
            }
        }
    }

    public int getXnIndex(String xn){
        int size = xnlist.size();
        for(int i=0;i<size;i++){
            if(xnlist.get(i).equals(xn)){
                return i;
            }
        }
        return 0;
    }

    public int getXqIndex(String xq){
        int size = xqlist.size();
        for(int i=0;i<size;i++){
            if(xqlist.get(i).equals(xq)){
                return i;
            }
        }
        return 0;
    }

    public void setOnTimeCListener(OnTimeCListener onTimeCListener){
        this.onTimeCListener=onTimeCListener;
    }
    /**
     * 回调接口
     */
    public interface OnTimeCListener{
        public void OnClick(String xn,String xq);
    }

}
