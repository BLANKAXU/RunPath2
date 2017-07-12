package com.example.blanka.runpath.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.services.weather.LocalWeatherForecastResult;
import com.amap.api.services.weather.LocalWeatherLive;
import com.amap.api.services.weather.LocalWeatherLiveResult;
import com.amap.api.services.weather.WeatherSearch;
import com.amap.api.services.weather.WeatherSearchQuery;
import com.example.blanka.runpath.R;
import com.example.blanka.runpath.activity.CountDown_Activity;
import com.example.blanka.runpath.activity.History_Activity;
import com.example.blanka.runpath.activity.Main_Activity;
import com.example.blanka.runpath.db.RunningRecord;
import com.example.blanka.runpath.util.unitConversion;
import com.example.liangmutian.randomtextviewlibrary.RandomTextView;

import net.qiujuer.genius.blur.StackBlur;

import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * Created by BLANKA on 2017/4/24 0024.
 */

public class Main_Fragment extends mFragment implements WeatherSearch.OnWeatherSearchListener, View.OnClickListener {
    private List<RunningRecord> RunningRecordList;
    private View view;
    private WeatherSearchQuery mquery;
    private WeatherSearch mweathersearch;
    private LocalWeatherLive mweatherlive;
    private Button button;
    private RandomTextView distanceCountA;
    private RandomTextView distanceCountB;
    private TextView liveWeather;
    private TextView Temperature;
    private TextView wind;
    private TextView humidity;
    private ImageView blurBackg;
    private ImageView close;
    private ImageView run;
    private ImageView walk;
    private ImageView bick;
    private ImageView runImage;
    private PopupWindow mwPopWindow;
    private PopupWindow msPopWindow;
    private LinearLayout Ly_distanceCount;
    private boolean mwPopWindowShow = false;
    private boolean mwPopWindow_fistshow = true;
    private Bitmap bitmapBlur;
    private String userName;
    private Float mdistanceCount = Float.valueOf(0);


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_main, container, false);
        button = (Button) view.findViewById(R.id.startButton);
        liveWeather = (TextView) view.findViewById(R.id.liveWeather);
        runImage = (ImageView) view.findViewById(R.id.runImage);
        distanceCountA = (RandomTextView) view.findViewById(R.id.tv_distanceCountA);
        distanceCountB = (RandomTextView) view.findViewById(R.id.tv_distanceCountB);
        Ly_distanceCount = (LinearLayout) view.findViewById(R.id.linearL_distanceCount);
        button.setOnClickListener(this);
        liveWeather.setOnClickListener(this);
        runImage.setOnClickListener(this);
        Ly_distanceCount.setOnClickListener(this);
        searchliveweather();//实时天气
        initRandomTV();
        return view;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.startButton: {
                if (!userName.equals("")) {
                    Intent intent = new Intent(getContext(), CountDown_Activity.class);
                    startActivity(intent);
                } else {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                    dialog.setMessage("未登录，本次运动将只保存在本地哦");
                    dialog.setCancelable(true);
                    dialog.setPositiveButton("继续跑步", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(getContext(), CountDown_Activity.class);
                            startActivity(intent);
                        }
                    });
                    dialog.setNegativeButton("去登陆", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(view.getContext(), History_Activity.class);
                            startActivity(intent);
                        }
                    });
                    dialog.show();
                }
            }
            break;
            case R.id.liveWeather: {
                if (mwPopWindowShow) {
                    mwPopWindow.dismiss();
                    mwPopWindowShow = false;
                } else {
                    searchliveweather();//实时天气
                    showPopupWindow();
                    mwPopWindowShow = true;
                }
            }
            break;
            case R.id.runImage: {
                showSPopupWindow();
            }
            break;
            case R.id.pop_close: {
                msPopWindow.dismiss();
            }
            break;
            case R.id.pop_run: {
                runImage.setImageResource(R.drawable.paobu2);
                msPopWindow.dismiss();
            }
            break;
            case R.id.pop_walk: {
                runImage.setImageResource(R.drawable.walk);
                msPopWindow.dismiss();
            }
            break;
            case R.id.pop_bick: {
                runImage.setImageResource(R.drawable.bick);
                msPopWindow.dismiss();
            }
            break;
            case R.id.linearL_distanceCount: {
                Intent intent = new Intent(getContext(), History_Activity.class);
                startActivity(intent);
            }
            break;

        }
    }

    /**
     * 载入运动总路程
     */
    public void loadDistanceCount() {
        SharedPreferences pref = getActivity().getSharedPreferences("data", Context.MODE_PRIVATE);
        userName = pref.getString("user", "");
        if (userName.equals("")) {
            RunningRecordList = DataSupport.where("user = ?", "").find(RunningRecord.class);
        } else {
            RunningRecordList = DataSupport.where("user = ? or user = ?", userName, "").find(RunningRecord.class);
        }
        mdistanceCount = Float.valueOf(0);
        for (RunningRecord runningRecord : RunningRecordList) {
            mdistanceCount = mdistanceCount + runningRecord.getMetre();
        }
        String data[] = unitConversion.distance(mdistanceCount).split("\\.");
        distanceCountA.setText(data[0]);
        distanceCountB.setText(data[1]);
        distanceCountA.start();
        distanceCountB.start();
    }

    /**
     * 实现数字滚动
     */
    private void initRandomTV() {
        int pianyiliangA[] = new int[]{40, 36};
        int pianyiliangB[] = new int[]{32, 30};
        distanceCountA.setPianyilian(pianyiliangA);
        distanceCountA.setMaxLine(7);
        distanceCountB.setPianyilian(pianyiliangB);
        distanceCountB.setMaxLine(7);
    }


    @Override
    public void onResume() {
        super.onResume();
        loadDistanceCount();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        distanceCountA.destroy();
        distanceCountB.destroy();
    }

    /**
     * 搜索实时天气
     */
    private void searchliveweather() {
        mquery = new WeatherSearchQuery("杭州市", WeatherSearchQuery.WEATHER_TYPE_LIVE);
        mweathersearch = new WeatherSearch(getContext());
        mweathersearch.setOnWeatherSearchListener(this);
        mweathersearch.setQuery(mquery);
        mweathersearch.searchWeatherAsyn(); //异步搜索
    }

    @Override
    public void onWeatherLiveSearched(LocalWeatherLiveResult weatherLiveResult, int rCode) {
        if (rCode == 1000) {
            if (weatherLiveResult != null && weatherLiveResult.getLiveResult() != null) {
                mweatherlive = weatherLiveResult.getLiveResult();
                liveWeather.setText(mweatherlive.getWeather());
            } else {
                Toast.makeText(getContext(), "没有相关数据", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getContext(), "没有相城市", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onWeatherForecastSearched(LocalWeatherForecastResult localWeatherForecastResult, int rCode) {

    }

    /**
     * 天气弹出
     */
    private void showPopupWindow() {
        View contentView = LayoutInflater.from(getContext()).inflate(R.layout.popuplayout, null);
        mwPopWindow = new PopupWindow(contentView);
        mwPopWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        mwPopWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);

        Temperature = (TextView) contentView.findViewById(R.id.Temperature);
        wind = (TextView) contentView.findViewById(R.id.wind);
        humidity = (TextView) contentView.findViewById(R.id.humidity);

        if (mweatherlive != null) {
            Temperature.setText(mweatherlive.getTemperature() + "°");
            wind.setText(mweatherlive.getWindDirection() + "风:" + mweatherlive.getWindPower() + "级");
            humidity.setText(mweatherlive.getHumidity() + "%");
        }

        mwPopWindow.showAsDropDown(liveWeather);

    }

    /**
     * 运动类型弹出
     */
    private void showSPopupWindow() {

        View contentView = LayoutInflater.from(getContext()).inflate(R.layout.spopuplayout, null);
        msPopWindow = new PopupWindow(contentView);
        msPopWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        msPopWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);

        close = (ImageView) contentView.findViewById(R.id.pop_close);
        run = (ImageView) contentView.findViewById(R.id.pop_run);
        walk = (ImageView) contentView.findViewById(R.id.pop_walk);
        bick = (ImageView) contentView.findViewById(R.id.pop_bick);
        blurBackg = (ImageView) contentView.findViewById(R.id.blurBackg);

        close.setOnClickListener(this);
        run.setOnClickListener(this);
        walk.setOnClickListener(this);
        bick.setOnClickListener(this);

        //外部是否可以点击
        msPopWindow.setBackgroundDrawable(new ColorDrawable());
        msPopWindow.setOutsideTouchable(true);

        if (mwPopWindow_fistshow) {
            View nowView = getActivity().getWindow().getDecorView();
            nowView.setDrawingCacheEnabled(true);
            nowView.buildDrawingCache(true);
            Bitmap bmp1 = nowView.getDrawingCache();//获取当前窗口快照，相当于截屏
            Bitmap bmp2 = Bitmap.createBitmap(bmp1, 0, 0, bmp1.getWidth(), bmp1.getHeight());
            bitmapBlur = blur(bmp2, 70);
            mwPopWindow_fistshow = false;
        }
        blurBackg.setImageBitmap(bitmapBlur);
        msPopWindow.setAnimationStyle(R.style.contextMenuAnim);
        msPopWindow.showAtLocation(contentView, Gravity.CENTER, 0, 0);

    }

    /**
     * 背景虚化
     */
    public Bitmap blur(Bitmap bitmap, int radius) {
        return StackBlur.blurNativelyPixels(bitmap, radius, true);
    }
}
