package com.teachingassistant.Support.Net;

import com.teachingassistant.Support.Bean.Course;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 郭振阳 on 2016/12/4.
 * Update by 郭振阳 on 2017/01/25.
 */

public class Jxjson {


    /**
     * 获取个人信息
     * 课表信息的对象中包含个人信息的一个对象xsxx，提取并解析
     * @param kbsj 课表原始数据
     * @return 解析后的课表原始数据
     * @throws JSONException
     */
    public  Map<String, String> praseJSon_xsxx(String kbsj) throws JSONException
    {
        Map<String,String> xsxx = new HashMap<>();
        JSONObject jsonObject = new JSONObject(kbsj);
        JSONObject json_xsxx = jsonObject.getJSONObject("xsxx");
        xsxx.put("学号",json_xsxx.getString("XH"));
        xsxx.put("学年",json_xsxx.getString("XNMC"));
        xsxx.put("学期",json_xsxx.getString("XQMMC"));
        xsxx.put("姓名",json_xsxx.getString("XM"));
        return xsxx;
    }

    /**
     * 获取课表信息
     * 课表信息中包含课表数组kbList，数组中包含了课表对象，每个时间段都是一个对象，提取并解析
     * @param kbData 课表原始数据
     * @return 解析后的课表数据
     * @throws JSONException
     */
    public List<Course> praseJSon_kb(String kbData) throws JSONException
    {
        JSONObject jsonObject = new JSONObject(kbData);
        JSONArray json_kb = jsonObject.getJSONArray("kbList");
        List<Course> kb = new ArrayList<>();
        for(int i=0;i<json_kb.length();i++)
        {
            JSONObject jsonObject1 = json_kb.getJSONObject(i);
            Course course = new Course();
            course.setCourseName(jsonObject1.getString("kcmc"));
            course.setCourseDay(jsonObject1.getString("xqj"));
            course.setCourseTeacher(jsonObject1.getString("xm")+"("
                                    +jsonObject1.getString("zcmc")+")");
            course.setCourseJC(jsonObject1.getString("jc"));
            course.setCourseLocal(jsonObject1.getString("cdmc"));
            //上课周次解析为数组
            int week[] = new int[25];
            for(int j=0;j<25;j++)
                week[j]=0;
            String weeks = jsonObject1.getString("zcd");
            for(int j=0;j<weeks.length();j++)
            {
                for(int k=j;k<weeks.length();k++)
                {
                    if(weeks.charAt(k)=='-')
                    {
                        String str = weeks.substring(j,k);
                        int a = Integer.parseInt(str),b=0,l=k+1;
                        for(;l<weeks.length();l++)
                        {
                            if(weeks.charAt(l)=='周')
                            {
                                str = weeks.substring(k+1,l);
                                b=Integer.parseInt(str);
                                break;
                            }
                        }
                        for(int m=a-1;m<b;m++)
                        {
                            week[m]=1;
                        }
                        j=l+1;
                        break;
                    }
                    if(weeks.charAt(k)=='周')
                    {
                        String str = weeks.substring(j,k);
                        int a = Integer.parseInt(str);
                        week[a-1]=1;
                        j=k+1;
                        break;
                    }
                }
            }
            course.setCourseWeek(week);
            kb.add(course);
        }
        return kb;
    }

    /**
     * 成绩信息是一个对象，包含成绩数组items
     * @param cjData 成绩原始数据
     * @return 解析后的成绩数据
     * @throws JSONException
     */
    public List<Map<String,Object>> praseJSon_cj(String cjData) throws JSONException
    {
        JSONObject jsonObject = new JSONObject(cjData);
        JSONArray json_cj = jsonObject.getJSONArray("items");
        List<Map<String,Object>> cjd = new ArrayList<>();
        double sumGrade=0.0,sumJD=0.0,sumXF1=0.0,sumXF = 0.0;
        int sum=0,sum1=0;
        for(int i=0;i<json_cj.length();i++)
        {
            Map<String,Object> cj = new HashMap<>();
            JSONObject jsonObject1 = json_cj.getJSONObject(i);
            cj.put("course_name",jsonObject1.getString("kcmc"));
            if(jsonObject1.getString("kcxzmc").equals("通识教育公选课"))
            {
                cj.put("course_description",jsonObject1.getString("kcxzmc")
                        +"-"+jsonObject1.getString("kcgsmc"));
                sumXF1+=Double.parseDouble(jsonObject1.getString("xf"));
            }
            else
            {
                cj.put("course_description",jsonObject1.getString("kcxzmc"));
                if(jsonObject1.getString("cj").equals("优秀"))
                {
                    sumGrade+=(90.0
                            *Double.parseDouble(jsonObject1.getString("xf")));
                }else if(jsonObject1.getString("cj").equals("良好"))
                {
                    sumGrade+=(80.0
                            *Double.parseDouble(jsonObject1.getString("xf")));
                }else if(jsonObject1.getString("cj").equals("及格"))
                {
                    sumGrade+=(60.0
                            *Double.parseDouble(jsonObject1.getString("xf")));
                }else if(jsonObject1.getString("cj").equals("不及格"))
                {
                    sumGrade+=(50.0
                            *Double.parseDouble(jsonObject1.getString("xf")));
                }else{
                    sumGrade+=(Double.parseDouble(jsonObject1.getString("cj"))
                            *Double.parseDouble(jsonObject1.getString("xf")));
                }
                sumXF+=Double.parseDouble(jsonObject1.getString("xf"));
                //学校教务系统出现100分没绩点的情况的补丁
                if(jsonObject1.getString("cj").equals("100"))
                {
                    sumJD+=4.0;
                }else{
                    sumJD+=Double.parseDouble(jsonObject1.getString("jd"));
                }
                sum1++;
            }
            cj.put("course_id",jsonObject1.getString("kch"));
            cj.put("jxb_id",jsonObject1.getString("jxb_id"));
            cj.put("course_grade",jsonObject1.getString("cj"));
            //学校教务系统出现100分没绩点的情况的补丁
            if(jsonObject1.getString("cj").equals("100"))
            {
                cj.put("course_point","4");
            }else{
                cj.put("course_point",jsonObject1.getString("jd"));
            }
            cj.put("course_credit",jsonObject1.getString("xf"));
            sum++;
            cjd.add(cj);
        }
        Map<String,Object> zj = new HashMap<>();
        zj.put("科目-学分",String.valueOf(sum)+"-"+String.valueOf(sumXF1+sumXF));
        zj.put("加权平均学分(不含公选课)",String.valueOf(sumGrade/sumXF));
        zj.put("平均绩点(不含公选课)",String.valueOf(sumJD/sum1));
        cjd.add(zj);
        return cjd;
    }

    /**
     * 解析考试信息，考试信息对象包含有items数组，每一项都是一门考试
     * @param ksData 考试信息
     * @return 解析后的考试信息
     * @throws JSONException 抛出异常
     */
    public List<Map<String,Object>> praseJSon_ks(String ksData) throws Exception
    {
        JSONObject jsonObject = new JSONObject(ksData);
        JSONArray jsonArray = jsonObject.getJSONArray("items");
        List<Map<String,Object>> ksd = new ArrayList<>();
        for(int i=0;i<jsonArray.length();i++)
        {
            Map<String,Object> ks = new HashMap<>();
            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
            ks.put("exam_name",jsonObject1.getString("kcmc"));
            ks.put("exam_time",jsonObject1.getString("kssj"));
            //比较当前时间和考试时间，得出考试状态
            String starttime = jsonObject1.getString("kssj").substring(0,16);
            String endtime = jsonObject1.getString("kssj").substring(0,11)+
                            jsonObject1.getString("kssj").substring(17,22);
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm");
            Date start = df.parse(starttime);
            Date end = df.parse(endtime);
            Date now = new Date();
            if(now.compareTo(start)<0) {
                ks.put("exam_status","未开始");
            }else if(now.compareTo(end)>0) {
                ks.put("exam_status","已结束");
            }else{
                ks.put("exam_status","进行中");
            }
            ks.put("exam_local",jsonObject1.getString("cdmc"));
            ksd.add(ks);
        }
        return ksd;
    }
}
