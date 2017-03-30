package com.teachingassistant.Bean;

import android.content.res.AssetManager;
import android.util.Log;

import com.teachingassistant.MyApplication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 教师信息类
 * Created by 11650 on 2017/3/23.
 */

public class TeacherInfo{

    private static Map<String,List<String>> tInfo;

    public static void init() throws IOException {
        if(tInfo==null) {
            tInfo = new HashMap<>();
            AssetManager am = MyApplication.getContext().getAssets();
            InputStream is = am.open("teacherinfo/teacherinfo.txt");
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                String[] info = line.split(",");
                List<String> list = new ArrayList<>();
                list.add(info[0]);
                for(int i=0;i<info.length-2;i++){
                    list.add(info[i+2]);
                }
                tInfo.put(info[1],list);
            }
        }
    }

    public static String findTeacherIDByName(String name) {
        String[] classInfo = name.split("-");
        List<String> list = tInfo.get(classInfo[1]);
        String s = list.get(0)+"#";
        for(int i=1;i<list.size();i++){
            if(list.get(i).equals(classInfo[0])){
                s+=String.valueOf(i-1);
                return s;
            }
        }
        return null;
    }
}
