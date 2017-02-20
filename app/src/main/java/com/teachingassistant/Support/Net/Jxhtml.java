package com.teachingassistant.Support.Net;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by 郭振阳 on 2016/12/1.
 */

public class Jxhtml {

    public static String isLogin(String s)
    {
        Document document = Jsoup.parse(s);
        Elements elements = document.select("[class=error_text0 hidden]");
        if(elements.size()==0)
        {
            return "success";
        }
        else
        {
            String ss = elements.get(0).text();
            return ss;
        }
    }

    public static Map<String,Object> praseGrade(String s)
    {
        Map<String,Object> grade = new HashMap<>();
        Document document = Jsoup.parse(s);
        Elements elements = document.select("[valign=middle]");
        if(elements.size()==3)
        {
            grade.put("平时比例","");
            grade.put("平时成绩","");
            grade.put("期末比例","");
            grade.put("期末成绩","");
            grade.put("总评",elements.get(2).text());
        }
        else if(elements.size()==9)
        {
            grade.put("平时比例",elements.get(1).text());
            grade.put("平时成绩",elements.get(2).text());
            grade.put("期末比例",elements.get(4).text());
            grade.put("期末成绩",elements.get(5).text());
            grade.put("总评",elements.get(8).text());
        }
        else
        {
            grade.put("平时比例","");
            grade.put("平时成绩","");
            grade.put("期末比例","");
            grade.put("期末成绩","");
            grade.put("总评","");
        }
        return grade;
    }

}
