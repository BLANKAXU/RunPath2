package com.example.blanka.runpath.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.TextView;

import com.example.blanka.runpath.R;
import com.example.blanka.runpath.activity.Running_Activity;
import com.example.blanka.runpath.util.unitConversion;

/**
 * Created by BLANKA on 2017/4/25 0025.
 */

public class Running_Fragment extends mFragment implements View.OnClickListener {
    private View view;
    private TextView tv_pause;
    private TextView tv_distance_f;
    private TextView tv_speed;
    private TextView tv_stop;
    private Chronometer chronometerTimer;
    private Running_Activity runningActivity;
    private long recordTime;//记录下来的总时间
    private Boolean IS_PAUSED = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_runnig, container, false);

        chronometerTimer = (Chronometer) view.findViewById(R.id.chronometer_timer);
        tv_pause = (TextView) view.findViewById(R.id.tv_pause);
        tv_stop = (TextView) view.findViewById(R.id.tv_stop);
        tv_distance_f = (TextView) view.findViewById(R.id.tv_distance_f);
        tv_speed = (TextView) view.findViewById(R.id.tv_speed);
        tv_pause.setOnClickListener(this);
        tv_stop.setOnClickListener(this);

        runningActivity = (Running_Activity)getActivity();
        recordTime = 0;
        startTiming();
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_stop:
                runningActivity.stopRunning();
                tv_pause.setText("继续");
                tv_pause.setBackgroundColor(Color.parseColor("#EEEEEE"));
                tv_pause.setTextColor(Color.parseColor("#de000000"));
                runningActivity.setRecordPause();
                IS_PAUSED = true;
                stopTiming();
                break;
            case R.id.tv_pause:
                if (IS_PAUSED) {
                    tv_pause.setText("暂停");
                    tv_pause.setBackgroundColor(Color.parseColor("#707070"));
                    tv_pause.setTextColor(Color.parseColor("#EEEEEE"));
                    runningActivity.setRecordStart();
                    IS_PAUSED = false;
                    startTiming();
                } else {
                    tv_pause.setText("继续");
                    tv_pause.setBackgroundColor(Color.parseColor("#EEEEEE"));
                    tv_pause.setTextColor(Color.parseColor("#de000000"));
                    runningActivity.setRecordPause();
                    IS_PAUSED = true;
                    stopTiming();
                }
                break;
        }
    }

    /**
     * 开始计时和停止计时 使用官方 Chronometer
     */
    public void startTiming() {
        chronometerTimer.setBase(SystemClock.elapsedRealtime() - recordTime);//计时器清零
        int hour = (int) ((SystemClock.elapsedRealtime() - chronometerTimer.getBase()) / 1000 / 60 / 60);
        chronometerTimer.setFormat("0" + String.valueOf(hour) + ":%s");
        chronometerTimer.start();
    }

    public void stopTiming() {
        chronometerTimer.stop();
        recordTime = SystemClock.elapsedRealtime() - chronometerTimer.getBase();//保存这次记录的时间
    }

    public void setDistance(String distance) {
        tv_distance_f.setText(distance);
    }
    public void setSpeed(float distance) {tv_speed.setText(unitConversion.speed(distance, recordTime) + "'");}
    public Long getRecordTime(){
        return recordTime;
    }

}
