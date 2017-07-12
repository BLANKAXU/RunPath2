package com.example.blanka.runpath.activity;

import android.app.Application;
import android.content.Context;

import org.litepal.LitePalApplication;

/**
 * Created by BLANKA on 2017/5/21 0021.
 * 实现全局获取context
 */

public class mApplication extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        context = getApplicationContext();
        LitePalApplication.initialize(context);
    }
    /**
     * 一个全局获取context的方法
     * */
    public static Context getContext(){
        return context;
    }
}
