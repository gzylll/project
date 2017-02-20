package com.teachingassistant.Activity.Search;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.teachingassistant.Presenter.CourseListViewAdapter;
import com.teachingassistant.R;
import com.teachingassistant.Support.Bean.MyApplication;
import com.teachingassistant.Support.Bean.Time;
import com.teachingassistant.Support.Bean.dbHelper;
import com.teachingassistant.Support.View.Dialog.ChooseWeekDialog;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 课程显示activity
 */
public class CourseActivity extends AppCompatActivity {

    private ActionBar actionBar;
    private TextView chooseWeek;
    private TextView courseTime;
    private PopupWindow window;
    private MyApplication app;
    private ListView partlist;
    private List<Map<String,Object>> week,part;
    /**
     * 课程表最低布局，运行时向该布局中添加课表格子
     */
    private RelativeLayout courseTable;
    /**
     * 课程表颜色变化
     */
    private int colorchoose=0;

    /**
     * 课程表中每一节课的宽度和高度，随屏幕尺度变化，由onWindowFocusChanged函数赋值
     */
    public static int itemHeight,itemWidth;

    /**
     * 课程周数选择点击事件处理
     */
    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Map<String,Object> map = week.get((int)id);
            String s = map.get("week").toString();
            chooseWeek.setText(s);
            int offset = Integer.parseInt(s.substring(1,s.length()-1))-app.readDefaultWeek();
            setTime(offset);
            showCourse();
            if(window!=null)
            {
                window.dismiss();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);
        initialCourse();
        //初始化时间，加载
        Time.initialWeek();
        setTime(0);
    }

    /**
     * 主要部件初始化
     */
    private void initialCourse(){
        actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setCustomView(R.layout.actionbar_course);
        }

        chooseWeek = (TextView)findViewById(R.id.choose_week);
        courseTime = (TextView)findViewById(R.id.course_time);
        partlist = (ListView)findViewById(R.id.part_list);
        app = new MyApplication();

        chooseWeek.setText("第"+app.readDefaultWeek()+"周");

        week = new ArrayList<>();
        for(int i=0;i<25;i++)
        {
            Map<String,Object> wk = new HashMap<>();
            wk.put("week","第"+(i+1)+"周");
            week.add(wk);
        }

        part = new ArrayList<>();
        for(int i=0;i<12;i++)
        {
            Map<String,Object> pt = new HashMap<>();
            pt.put("part",String.valueOf(i+1));
            part.add(pt);
        }

        courseTable = (RelativeLayout) findViewById(R.id.course_table);
    }

    /**
     * 时间设置
     * 根据传入的周偏移量和保存的量计算并显示时间
     * 示例：setTime(0)设置当前周的时间
     * @param week 周偏移量
     */
    private void setTime(int week) {
        //周一到周日的布局
        int layout[] = new int[]{R.id.l1, R.id.l2, R.id.l3, R.id.l4, R.id.l5, R.id.l6,R.id.l7};
        //实例化时间显示组件
        TextView month = (TextView) findViewById(R.id.month);
        TextView monday = (TextView) findViewById(R.id.monday);
        TextView tuesday = (TextView) findViewById(R.id.tuesday);
        TextView wednesday = (TextView) findViewById(R.id.wednesday);
        TextView thursday = (TextView) findViewById(R.id.thursday);
        TextView friday = (TextView) findViewById(R.id.friday);
        TextView saturday = (TextView) findViewById(R.id.saturday);
        TextView sunday = (TextView) findViewById(R.id.sunday);
        //根据time支持类得到时间列表
        List<String> list = Time.getDayList(Time.getDate(), week);
        //显示
        monday.setText(list.get(0));
        tuesday.setText(list.get(1));
        wednesday.setText(list.get(2));
        thursday.setText(list.get(3));
        friday.setText(list.get(4));
        saturday.setText(list.get(5));
        sunday.setText(list.get(6));
        month.setText(list.get(7));
        //设置今天的背景
        LinearLayout layout1 = (LinearLayout) findViewById(layout[Time.getXQJ(new Date()) - 1]);
        if (layout1 != null){
            if(chooseWeek.getText().toString().equals("第"+app.readDefaultWeek()+"周")){
                layout1.setBackgroundColor(getResources().getColor(R.color.color1));
            }else{
                layout1.setBackgroundColor(0);
            }
        }
    }

    /**
     * 弹出窗口显示
     * @param parent 弹出窗口父组件
     */
    private void showPopWindow(View parent) {
        LayoutInflater inflater = LayoutInflater.from(this);
        if (parent.getId() == R.id.choose_week) {
            View view = inflater.inflate(R.layout.popwindow_choose_week, null);
            //此处的listview所在布局加载完成之后,通过加载的布局对象才能实例化
            ListView weeklist = (ListView) view.findViewById(R.id.week_list);
            SimpleAdapter adapter = new SimpleAdapter(this,
                    week,
                    R.layout.list_items_week,
                    new String[]{"week"},
                    new int[]{R.id.week});
            weeklist.setAdapter(adapter);
            weeklist.setOnItemClickListener(onItemClickListener);
            window = new PopupWindow(view, 300, 400);
        } else {
            View view = inflater.inflate(R.layout.popwindow_course_more, null);
            window = new PopupWindow(view, 300, 300);
        }
        //设置弹出背景
        ColorDrawable dw = new ColorDrawable(getResources().getColor(R.color.popwindow_color));
        window.setBackgroundDrawable(dw);
        //获取焦点
        window.setFocusable(true);
        window.setOutsideTouchable(true);

        //屏幕变暗
        backGroundAlpha(0.5f);
        //设置显示位置
        int x = 0;
        if (parent.getId() == R.id.choose_week) {
            x = parent.getWidth() / 2 - window.getWidth() / 2;
        } else {
            x = parent.getWidth() - window.getWidth();
        }
        window.showAsDropDown(parent, x, actionBar.getHeight() - parent.getHeight());
        //设置popwindow消失后恢复
        window.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                backGroundAlpha(1f);
            }
        });
    }

    /**
     * actionbar点击事件
     * 1.返回：返回
     * 2.周数选择：弹出窗口
     * 3.+：弹出窗口
     * @param v 被点击的view
     */
    public void courseOnClick(View v){
        Intent intent;
        switch (v.getId())
        {
            //返回键
            case R.id.course_back:
                finish();
                break;
            //选择周数
            case R.id.choose_week:
                showPopWindow(v);
                break;
            //导入按钮
            case R.id.kb_loadin:
                showPopWindow(v);
                break;
            //导入按钮中的两个选项
            //导入新学期
            case R.id.new_course:
                window.dismiss();
                intent = new Intent(CourseActivity.this, CourseSearchActivity.class);
                startActivity(intent);
                break;
            //设置当前周
            case R.id.week_now:
                window.dismiss();
                ChooseWeekDialog dialog = new ChooseWeekDialog(this);
                dialog.show();
                //回调函数，如果选择的和当前的不同，则重画课表
                dialog.setOnWeekCLister(new ChooseWeekDialog.OnWeekCLister() {
                    @Override
                    public void onClick(String weekchoosed) {
                        if(!weekchoosed.equals(chooseWeek.getText().toString())){
                            //更新周数显示
                            chooseWeek.setText(weekchoosed);
                            //更新时间
                            setTime(0);
                            //更新课表
                            showCourse();
                        }
                    }
                });
                break;
        }
    }

    /**
     * 背景设置
     * @param alpha 背景透明度（0.0f-1.0f）
     */
    public void backGroundAlpha(float alpha){
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = alpha;
        getWindow().setAttributes(lp);
    }

    /**
     * activity一加载完毕就执行该函数
     * 实现：1.根据屏幕绘制view，获得对应的长宽比例
     *      2.初次加载课表
     * @param hasFocus 获得焦点时执行
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus){
            //获得屏幕高度
            DisplayMetrics dm = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(dm);
            int mscreenHeight = dm.heightPixels;
            //获得屏幕宽度
            int mscreenWidth = dm.widthPixels;
            //获得状态栏和actionBar的宽度
            int statusBarHeight = getResources().getDimensionPixelSize(
                        getResources().getIdentifier("status_bar_height", "dimen", "android"));
            int actionbarheight = actionBar.getHeight();
            //获得第一行的高度
            int lineHeight =(int)getResources().getDimension(R.dimen.course_Hang1_height);
            //获得第一行的宽度
            int lineWidth = (int)getResources().getDimension(R.dimen.course_Lie1_width);
            //计算listview高度
            itemHeight=(mscreenHeight-statusBarHeight-actionbarheight-lineHeight)/10-2;
            itemWidth=(mscreenWidth-lineWidth-1)/7;
            //为listview设定adapter
            CourseListViewAdapter partAdapter = new CourseListViewAdapter(
                    this,
                    part,
                    R.layout.list_items_part,
                    new String[]{"part"},
                    new int[]{R.id.course_part},
                    itemHeight);
            partlist.setAdapter(partAdapter);
            //加载课表
            showCourse();
        }
    }

    /**
     * 从数据库中读取对应周数的课表，并显示
     */
    public void showCourse(){
        //先清空所有控件
        courseTable.removeAllViews();
        colorchoose=0;
        //获得所要求的课表信息
        String string = chooseWeek.getText().toString();
        String time = app.readDefaultCourseTableName();
        int weektime = Integer.parseInt(string.substring(1,string.length()-1));
        //根据信息查询
        dbHelper dbHelper = new dbHelper(this,time);
        List<Map<String,String>> course = dbHelper.query(weektime);
        //逐个添加
        for(int i=0;i<course.size();i++){
            int day = Integer.parseInt(course.get(i).get("courseday"));
            int start=0,end=0;
            String jc = course.get(i).get("coursejc");
            for(int j=0;j<jc.length();j++){
                if(jc.charAt(j)=='-'){
                    start=Integer.parseInt(jc.substring(0,j));
                    end=Integer.parseInt(jc.substring(j+1,jc.length()-1));
                    break;
                }
            }
            String courseInfo = course.get(i).get("coursename")+"\n@"
                                +course.get(i).get("courselocal");
            showCourseAt(courseInfo,day,start,end);
            //换颜色
            colorchoose++;
        }
        //更新时间显示
        courseTime.setText(time.substring(0,9)+"学期 第"+time.substring(10,11)+"学期");
    }

    /**
     * 根据课程信息添加课程到对应位置，在showcourse中被调用
     * @param courseInfo 课程信息
     * @param day 课程周几
     * @param start 课程开始节数
     * @param end 课程节数节数
     */
    public void showCourseAt(String courseInfo,int day,int start,int end){
        //课表背景色
        int color[] = new int[]{R.color.course_color1,R.color.course_color2,
                                R.color.course_color3,R.color.course_color4,
                                R.color.course_color5};
        //加载课程布局
        View courseView = LayoutInflater.from(this).inflate(R.layout.course_table_items,null);
        //设置课程显示信息
        TextView textView = (TextView)courseView.findViewById(R.id.course_table_item_text);
        textView.setText(courseInfo);
        textView.setBackgroundColor(getResources().getColor(color[colorchoose%4]));
        //设置课程显示位置，长宽属性
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams
                (itemWidth, (2+itemHeight)*(end-start+1));
        params.setMargins((day-1)*itemWidth,(start-1)*(itemHeight+1),0,0);
        //加载设置信息
        courseView.setLayoutParams(params);
        //添加课程
        courseTable.addView(courseView);
    }
}
