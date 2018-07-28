package com.example.wuk.emotiondiary;

import org.litepal.crud.LitePalSupport;

import java.io.Serializable;
import java.util.Calendar;

public class Day extends LitePalSupport implements Serializable {
    private int year;
    private int month;
    private int day;
    private String content;
    private int state = state_null;
    Calendar calendar;

    final public static String[] week ={"SUN","MON","TUES","WED","THUR","FRI","SAT"};
    final public static int state_null = 0;
    final public static int state_angry= 1;
    final public static int state_gray = 2;
    final public static int state_calm = 3;
    final public static int state_happy = 4;
    final public static int state_VeryHappy = 5;


    public void Day(int year,int month ,int day ,int state,String content){
        this.year = year;
        this.month  = month;
        this.day = day;
        this.content = content;
        this.state = state;
    }
    public void setYear(int year) {
        this.year = year;
    }
    public void setMonth(int month){
        this.month = month;
    }
    public void setDay(int day){
        this.day = day;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public void setState(int state) {
        this.state = state;
    }

    public int getYear() {
        return year;
    }
    public int getMonth() {
        return month;
    }
    public int getDay() {
        return day;
    }
    public String getContent() {
        return content;
    }
    public int getState() {
        return state;
    }
}
