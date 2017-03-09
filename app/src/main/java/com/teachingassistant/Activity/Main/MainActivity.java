package com.teachingassistant.Activity.Main;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.teachingassistant.Fragement.ConversationFragment;
import com.teachingassistant.Fragement.CourseFragment;
import com.teachingassistant.Fragement.DynamicFragment;
import com.teachingassistant.Fragement.EducationFragment;
import com.teachingassistant.R;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends FragmentActivity implements
        ViewPager.OnPageChangeListener,FragmentTabHost.OnTabChangeListener{

    private LayoutInflater inflater;
    private FragmentTabHost tabHost;
    //fragment界面
    private final Class FragmentClass[]={
        EducationFragment.class, DynamicFragment.class,CourseFragment.class,ConversationFragment.class};
    //fragmnet文字
    private final String titleArray[]={
        "教务","动态","课程","消息"};
    //fragment图片
    private final int ImageArray[]={
            R.drawable.bg_tab_person,R.drawable.bg_tab_person,
            R.drawable.bg_tab_person,R.drawable.bg_tab_person};
    //fragment tab实例名字
    private String mTextviewArray[] = {"education", "dynamics", "course","conversation"};
    //viewpager
    private ViewPager vp;
    private List<Fragment> list = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initPage();
    }

    private void initView()
    {
        //加载布局管理器
        inflater = LayoutInflater.from(this);
        //viewpage初始化
        vp = (ViewPager)findViewById(R.id.viewPager);
        //设置页面滑动监听
        vp.addOnPageChangeListener(this);
        //tabhost
        tabHost = (FragmentTabHost)findViewById(R.id.tabhost);
        tabHost.setup(this,getSupportFragmentManager(),R.id.viewPager);//绑定tabHost和viewPager
        tabHost.setOnTabChangedListener(this);

        int length = FragmentClass.length;
        for(int i=0;i<length;i++)
        {
            //get tab
            TabHost.TabSpec tabSpec = tabHost.newTabSpec(mTextviewArray[i]).setIndicator(getTabItemView(i));
            //set tab
            tabHost.addTab(tabSpec,FragmentClass[i],null);
            tabHost.getTabWidget().setDividerDrawable(null);
        }
    }

    //初始化fragement
    private void initPage()
    {
        ConversationFragment csf = new ConversationFragment();
        CourseFragment cf = new CourseFragment();
        DynamicFragment df = new DynamicFragment();
        EducationFragment ef = new EducationFragment();

        list.add(ef);
        list.add(df);
        list.add(cf);
        list.add(csf);

        vp.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return list.get(position);
            }

            @Override
            public int getCount() {
                return list.size();
            }
        });
    }

    //set tab view
    private View getTabItemView(int index) {
        View view = inflater.inflate(R.layout.activity_main_tab, null);
        ImageView icon = (ImageView) view.findViewById(R.id.tabicon);
        icon.setImageResource(ImageArray[index]);
        TextView title = (TextView) view.findViewById(R.id.tabtitle);
        title.setText(titleArray[index]);
        return view;
    }

    /**
     *  当页面在滑动的时候会调用此方法，在滑动被停止之前，此方法一直得到调用。
     * @param position 当前页面
     * @param positionOffset 当前页面偏移的百分比
     * @param positionOffsetPixels 当前页面偏移的像素位置
     */
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    /**
     * 状态改变函数
     * @param state 当前状态
     *              0：无操作
     *              1：正在滑动
     *              2：滑动完毕
     */
    @Override
    public void onPageScrollStateChanged(int state) {

    }


    /**
     * 此方法是页面跳转完后得到调用
     * 重写此方法为了使页面滑动时下面tab也会随之改变
     * @param position 当前选中的页面的Position
     */
    @Override
    public void onPageSelected(int position) {
        tabHost.setCurrentTab(position);
    }

    /**
     * tab改变
     * 实现点击下面tab上面页面跳转
     * @param tabId 当前选中的tab
     */
    @Override
    public void onTabChanged(String tabId) {
        int position = tabHost.getCurrentTab();
        vp.setCurrentItem(position);
    }
}
