package com.teachingassistant.Support.Bean;

/**
 * 课程实体
 * Created by 郭振阳 on 2017/2/2.
 */

public class Course {
    /**
     * 课程名
     */
    private String courseName;
    /**
     * 课程教师
     */
    private String courseTeacher;
    /**
     * 课程地点
     */
    private String courseLocal;
    /**
     * 课程星期几
     */
    private String courseDay;
    /**
     * 课程节次
     */
    private String courseJC;
    /**
     * 课程周次
     */
    private int courseWeek[];

    public int[] getCourseWeek() {
        return courseWeek;
    }

    public String getCourseDay() {
        return courseDay;
    }

    public String getCourseJC() {
        return courseJC;
    }

    public String getCourseLocal() {
        return courseLocal;
    }

    public String getCourseName() {
        return courseName;
    }

    public String getCourseTeacher() {
        return courseTeacher;
    }

    public void setCourseDay(String courseDay) {
        this.courseDay = courseDay;
    }

    public void setCourseJC(String courseJC) {
        this.courseJC = courseJC;
    }

    public void setCourseLocal(String courseLocal) {
        this.courseLocal = courseLocal;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public void setCourseTeacher(String courseTeacher) {
        this.courseTeacher = courseTeacher;
    }

    public void setCourseWeek(int[] courseWeek) {
        this.courseWeek = courseWeek;
    }
}

