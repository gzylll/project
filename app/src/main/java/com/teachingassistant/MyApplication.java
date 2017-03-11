package com.teachingassistant;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.teachingassistant.Support.Bean.Time;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Created by 郭振阳 on 2016/12/3.
 */

public class MyApplication extends Application {

    /**
     * 全局上下文
     */
    private static Context context;

    /**
     * 个人信息持久化
     * 1.对象名：personinfo
     * 2.成员：account:账号:String
     *        password:密码:String
     *        remeber_pwn:记住密码:Boolean
     *        auto_login:自动登陆:Boolean
     *        course_table_default:默认显示的课表：String
     *        course_table:已下载的课表列表，末尾加：隔开：String
     *        week_default:当前默认第几周：int
     *        date_default:当前默认周的周一日期
     */
    private static SharedPreferences personInfo;
    private static SharedPreferences.Editor editor;

    public static int sdkAPPID = 1400026165;
    public static int accountType = 11118;
    public static String appVer = "1.0";

    public static String TIMAccount;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        personInfo = context.getSharedPreferences("personinfo",0);
        editor = personInfo.edit();
    }

    /**
     * 获得context
     * @return
     */
    public static Context getContext()
    {
        return context;
    }

    /**
     * 修改个人信息
     */
    public void writeAccount(String account) {
        editor.putString("account",account);
        editor.commit();
    }

    public void writePassword(String password) {
        editor.putString("password",password);
        editor.commit();
    }

    public void writeIsRememberPWN(Boolean is) {
        editor.putBoolean("remember_pwn",is);
        editor.commit();
    }

    public void writeIsAutoLogin(Boolean is) {
        editor.putBoolean("auto_login",is);
        editor.commit();
    }

    /**
     * 写入课表列表
     * @param str 课表名称：学年-学期-学号
     * @return true：写入成功
     *          false：存在重复，失败
     */
    public Boolean writeCourseTableName(String str) {
        List<String> list = readCourseTableName();
        Boolean is=false;
        for(int i=0;i<list.size();i++) {
            if(list.get(i).equals(str)) {
                is=true;
                break;
            }
        }
        if(is){
            return false;
        }else{
            String string = personInfo.getString("course_table","")+str+":";
            editor.putString("course_table",string);
            editor.commit();
            return true;
        }
    }

    public void writeDefaultCourseTableName(String str){
        editor.putString("course_table_default",str);
        editor.commit();
    }

    public void writeDefaultWeek(int i){
        editor.putInt("week_default",i);
        editor.commit();
    }

    public void writeDateDefault(Date date){
        editor.putString("date_default",date.toString());
        editor.commit();
    }

    public void clear() {
        editor.clear();
        editor.commit();
    }

    /**
     * 获得个人信息
     */
    public String readAccount()
    {
        return personInfo.getString("account","");
    }

    public String readPassword()
    {
        return personInfo.getString("password","");
    }

    public Boolean isRememberPWN()
    {
        return personInfo.getBoolean("remember_pwn",false);
    }

    public Boolean isAutoLogin(){
        return personInfo.getBoolean("auto_login",false);
    }

    /**
     * 读取所有已经下载的课表
     * @return 课表列表
     */
    public List<String> readCourseTableName(){
        List<String> list = new ArrayList<>();
        String str = personInfo.getString("course_table","");
        int j=0;
        for(int i=0;i<str.length();i++)
        {
            if(str.charAt(i)==':'){
                list.add(str.substring(j,i));
                j=i+1;
            }
        }
        return list;
    }

    /**
     * 读取当前用户的课表列表
     * @param string 用户ID
     * @return 课表列表
     */
    public List<String> readCourseTableNameById(String string){
        List<String> list = readCourseTableName();
        for(int i=0;i<list.size();i++){
            int length = list.get(i).length();
            String s = list.get(i).substring(length-8,length);
            if(!s.equals(string)){
                list.remove(i);
            }
        }
        return list;
    }

    /**
     * 查询是否存在某个课表
     * @param string 课表名称
     * @return bool
     */
    public boolean IsExistsCourseTable(String string) {
        List<String> list = readCourseTableName();
        for(int i=0;i<list.size();i++)
        {
            if(list.get(i).equals(string))
                return true;
        }
        return false;
    }

    /**
     * 读取当前应该显示的课表名称
     * @return 课表名称
     */
    public String readDefaultCourseTableName(){
        return personInfo.getString("course_table_default","");
    }

    //默认本周为第一周
    public int readDefaultWeek(){
        return personInfo.getInt("week_default",1);
    }

    public Date readDateDefault(){
        return new Date(personInfo.getString("date_default",
                Time.getMondayDate(new Date()).toString()));
    }
}
