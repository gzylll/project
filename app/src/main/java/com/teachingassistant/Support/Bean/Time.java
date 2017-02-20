package com.teachingassistant.Support.Bean;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 时间支持类，提供一些关于时间的计算方法
 * Created by 郭振阳 on 2017/2/5.
 */

public class Time {

    /**
     * 由日期获得星期几
     * @param date 日期
     * @return 星期几
     */
    public static int getXQJ(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int a = calendar.get(Calendar.DAY_OF_WEEK)-1;
        if(a==0)
            return 7;
        return a;
    }

    /**
     * 获得当前时间
     * @return 时间
     */
    public static Date getDate(){
        return new Date();
    }

    public static Date getMondayDate(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_YEAR,
                     calendar.get(Calendar.DAY_OF_YEAR)-(getXQJ(date)-1));
        return calendar.getTime();
    }
    /**
     * 由日期和周偏移量计算周日期列表以及该周第一天的月份
     * 例如某个日期date前3周的周列表，（date,-3）
     * @param date 日期
     * @param week 周偏移量
     * @return 周日期列表:前七位日期列表，最后一位月份
     */
    public static List<String> getDayList(Date date, int week){
        Calendar calender = Calendar.getInstance();
        List<String> dayList = new ArrayList<>();
        int offset = week*7-(getXQJ(getDate())-1);
        calender.setTime(date);
        calender.set(Calendar.DAY_OF_YEAR,calender.get(Calendar.DAY_OF_YEAR)+offset);
        String month = calender.get(Calendar.MONTH)+1+"月";
        dayList.add(String.valueOf(calender.get(Calendar.DAY_OF_MONTH)));
        for(int i =0;i<6;i++){
            calender.set(Calendar.DAY_OF_YEAR,calender.get(Calendar.DAY_OF_YEAR)+1);
            dayList.add(String.valueOf(calender.get(Calendar.DAY_OF_MONTH)));
        }
        dayList.add(month);
        return dayList;
    }

    /**
     * 初始化当前周的周数
     * 根据记录的周数，当前日期，和记录的日期计算，并更新
     */
    public static void initialWeek(){
        MyApplication application = new MyApplication();
        Date date1 = new Date();
        Date date2 = application.readDateDefault();
        Calendar fromCalendar = Calendar.getInstance();
        fromCalendar.setTime(date2);
        fromCalendar.set(Calendar.HOUR_OF_DAY, 0);
        fromCalendar.set(Calendar.MINUTE, 0);
        fromCalendar.set(Calendar.SECOND, 0);
        fromCalendar.set(Calendar.MILLISECOND, 0);

        Calendar toCalendar = Calendar.getInstance();
        toCalendar.setTime(date1);
        toCalendar.set(Calendar.HOUR_OF_DAY, 0);
        toCalendar.set(Calendar.MINUTE, 0);
        toCalendar.set(Calendar.SECOND, 0);
        toCalendar.set(Calendar.MILLISECOND, 0);

        int week = ((int)((toCalendar.getTime().getTime() - fromCalendar.getTime().getTime())
                / (1000 * 60 * 60 * 24)))/7;
        //更新周数
        application.writeDefaultWeek(application.readDefaultWeek()+week);
        //更新日期
        application.writeDateDefault(getMondayDate(getDate()));
    }
}
