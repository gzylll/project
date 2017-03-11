package com.teachingassistant.Support.Bean;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 课程表存储类
 * Created by 郭振阳 on 2016/12/4.
 */

public class dbHelper {

    private String dbName;
    private String tableName = "course";
    private String ID = "_id";
    private int dbVersion = 1;
    private SQLiteDatabase db;
    private dbOpenHelper helper;

    /**
     * 内部类实现打开数据库
     */
    private class dbOpenHelper extends SQLiteOpenHelper{

        //建表语句
        private String createTable = "create table "+ tableName +
                "(" + ID + " integer primary key autoincrement," + //ID
                "coursename text not null,"                        //课程名
                +"courseteacher text not null,"                    //教师
                +"courselocal text not null,"                      //教室
                +"coursejc text not null,"                         //节次
                +"courseday text not null,";                       //星期

        public dbOpenHelper(Context context){
            super(context,dbName,null,dbVersion);
        }

        /**
         * 程序初次使用数据库的时候调用
         * @param db 数据库对象
         */
        @Override
        public void onCreate(SQLiteDatabase db) {
            for(int i=0;i<24;i++)
            {
                createTable+=("week"+(i+1)+" integer,");
            }
            createTable+="week25 integer)";
            db.execSQL(createTable);
        }

        /**
         * 程序升级数据库时调用，这里只是简单的删除旧版数据库重新建立新版数据库
         * @param db 数据库对象
         * @param oldVersion 旧的版本
         * @param newVersion 新的版本
         */
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS"+tableName);
            onCreate(db);
        }
    }

    /**
     * 构造函数，私有成员赋值，获得可写数据库
     * @param context Context对象
     * @param dbName 传入数据库名称
     */
    public dbHelper(Context context,String dbName){
        this.dbName = dbName;
        helper = new dbOpenHelper(context);
        db = helper.getWritableDatabase();
    }

    /**
     * 向数据库插入数据
     */
    public void instert(Course course) {
        ContentValues values = new ContentValues();
        values.put("coursename",course.getCourseName());
        values.put("courseteacher",course.getCourseTeacher());
        values.put("courselocal",course.getCourseLocal());
        values.put("coursejc",course.getCourseJC());
        values.put("courseday",course.getCourseDay());
        int a[] = course.getCourseWeek();
        for(int i = 1;i<26;i++)
        {
            values.put("week"+i,a[i-1]);
        }
        db.insert(tableName,null,values);
    }

    /**
     * 按周查询数据库，返回对应的课程
     * @param week 查询的周数
     * @return 返回对应周的课程信息
     */
    public List<Map<String,String>> query(int week){
        Cursor cursor = db.query(tableName,
                new String[]{"coursename","courseteacher","courselocal", "coursejc","courseday"},
                "week"+week+"=1",
                null,null,null,null);
        cursor.moveToFirst();
        List<Map<String,String>> courses = new ArrayList<>();
        for(int i=0;i<cursor.getCount();i++)
        {
            Map<String,String> course = new HashMap<>();
            course.put("coursename",cursor.getString(0));
            course.put("courseteacher",cursor.getString(1));
            course.put("courselocal",cursor.getString(2));
            course.put("coursejc",cursor.getString(3));
            course.put("courseday",cursor.getString(4));
            courses.add(course);
            cursor.moveToNext();
        }
        cursor.close();
        return courses;
    }

    /**
     * 查询所有的课程名称及教师名字
     */
    public List<Map<String,Object>> query()
    {
        Cursor cursor = db.query(tableName,
                new String[]{"coursename","courseteacher"},
                null,
                null,null,null,null);
        cursor.moveToFirst();
        List<Map<String,Object>> courses = new ArrayList<>();
        for(int i=0;i<cursor.getCount();i++)
        {
            Map<String,Object> course = new HashMap<>();
            course.put("groupName",cursor.getString(0)+"-"+cursor.getString(1));
            courses.add(course);
            cursor.moveToNext();
        }
        cursor.close();
        return courses;
    }
}
