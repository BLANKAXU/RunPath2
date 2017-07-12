package com.example.blanka.runpath.util;

import java.text.DecimalFormat;

/**
 * Created by BLANKA on 2017/5/17 0017.
 */

public class unitConversion {
    /**
     * 距离单位转化
     */
    public static String distance(float distanceM)
    {
        distanceM = distanceM/1000;
        DecimalFormat df   =   new   DecimalFormat("#####0.00");
        String distanceK=df.format(distanceM);//返回的是String类型的数据
        return distanceK;
    }
    /**
     * 时间单位转化
     */
    public static String time(Long time)
    {
        String T;
        long Htime = time /1000 /60 /60;
        long Mtime = time /1000 /60 ;
        long Stime = time /1000 % 60 ;
        if( Htime == 0) {
            T = Mtime + ":" + Stime;
        }else {
            T = Htime + ":" + Mtime + ":" + Stime;
        }
        return  T;
    }
    /**
    * 速度计算
    * */
    public static String speed(float distance, Long time){
        float speed =  distance / (time *60);
        DecimalFormat df   =   new   DecimalFormat("#####0.00");
        String speedF = df.format(speed);
        return speedF;
    }
}
