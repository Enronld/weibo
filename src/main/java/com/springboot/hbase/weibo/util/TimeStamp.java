package com.springboot.hbase.weibo.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeStamp {

    //将时间转化成倒序时间戳
    public static String timeToTimeStamp(String time){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            String timestamp = Long.toString(Long.MAX_VALUE - sdf.parse(time).getTime());
            return timestamp;

        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    //将倒序时间戳转化为时间
    public static String timeStampToTime(String timeStamp){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {
            Date date = sdf.parse(sdf.format(new Date(new Long(timeStamp))));
            String time = Long.toString(Long.MAX_VALUE - sdf.parse(sdf.format(date)).getTime());    //获取正序时间戳
            return sdf.format(sdf.parse(sdf.format(new Date(new Long(time)))));

        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

}
