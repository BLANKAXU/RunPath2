package com.example.blanka.runpath.activity;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.PolygonOptions;
import com.amap.api.maps.model.PolylineOptions;
import com.example.blanka.runpath.R;
import com.example.blanka.runpath.db.RunningRecord;
import com.example.blanka.runpath.util.unitConversion;

import java.util.ArrayList;
import java.util.List;

public class Record_Activity extends AppCompatActivity {
    public static final LatLng SHANGHAI = new LatLng(31.238068, 121.501654);// 上海市经纬度
    private MapView mMapView = null;
    private AMap aMap;
    private String runPath;
    private String runTime;
    private String runDistance;
    private String runSpeed;
    private RunningRecord mRunningRecord;
    private TextView tv_time;
    private TextView tv_distance;
    private TextView tv_speed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        setTitle("运动记录");
        mRunningRecord = (RunningRecord) getIntent().getSerializableExtra("RunningData");
        tv_time = (TextView) findViewById(R.id.time);
        tv_distance = (TextView) findViewById(R.id.distance);
        tv_speed = (TextView) findViewById(R.id.speed);
        mMapView = (MapView) findViewById(R.id.map2);
        dataInit();//数据初始化
        mMapView.onCreate(savedInstanceState);// 此方法必须重写
        aMap = mMapView.getMap();
        aMap.getUiSettings().setZoomControlsEnabled(false);
        aMap.getUiSettings().setMyLocationButtonEnabled(false);// 设置默认定位按钮是否显示
        aMap.moveCamera(CameraUpdateFactory.zoomTo(18));
        analyseRunPath(runPath);
        setUpMap3();
    }

    private void dataInit() {
        runPath = mRunningRecord.getLine();
        runTime = unitConversion.time(mRunningRecord.getTime());
        runDistance = unitConversion.distance(mRunningRecord.getMetre());
        runSpeed = unitConversion.speed(mRunningRecord.getMetre(), mRunningRecord.getTime());

        tv_time.setText("时间:" + runTime);
        tv_distance.setText("距离(公里):" + runDistance);
        tv_speed.setText("速度" + runSpeed);
    }

    private void analyseRunPath(String runPath) {
        String[] piontArrey = runPath.split(";");
        for (int i = 0; i < piontArrey.length - 2; i++) {
            String[] piontOlder = piontArrey[i].split(",");
            String[] piontNewer = piontArrey[i + 1].split(",");

                LatLng older = new LatLng(Double.valueOf(piontOlder[0]), Double.valueOf(piontOlder[1]));
                LatLng newer = new LatLng(Double.valueOf(piontNewer[0]), Double.valueOf(piontNewer[1]));

            if(i == 0){
                aMap.animateCamera(CameraUpdateFactory.changeLatLng(older));
            }

            if(AMapUtils.calculateLineDistance(older, newer) < 40){
                setUpMap(older,newer);
            }else{
                setUpMap2(older,newer);
            }

        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);//保存地图当前的状态
        mMapView.onSaveInstanceState(outState);
    }
    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();//在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();//在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();//在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
    }

    private void setUpMap(LatLng oldLatLng, LatLng newLatLng) {  // 绘制正常运动曲线
        aMap.addPolyline((new PolylineOptions())
                .add(oldLatLng, newLatLng)
                .geodesic(true).color(Color.GREEN));
    }

    private void setUpMap2(LatLng oldLatLng, LatLng newLatLng) { // 绘制非正常运动曲线
        aMap.addPolyline((new PolylineOptions())
                .add(oldLatLng, newLatLng)
                .setDottedLine(true)
                .geodesic(true).color(Color.GRAY));
    }

    /**
     * 加深背影颜色
     */
    private void setUpMap3() {
        // 绘制一个长方形
        aMap.addPolygon(new PolygonOptions()
                .addAll(createRectangle(SHANGHAI, 50, 50))
                .fillColor(Color.argb(90, 1, 1, 1)).strokeColor(Color.RED).strokeWidth(1));

    }

    /**
     * 生成一个长方形的四个坐标点 背景
     */
    private List<LatLng> createRectangle(LatLng center, double halfWidth,
                                         double halfHeight) {
        List<LatLng> latLngs = new ArrayList<LatLng>();
        latLngs.add(new LatLng(center.latitude - halfHeight, center.longitude - halfWidth));
        latLngs.add(new LatLng(center.latitude - halfHeight, center.longitude + halfWidth));
        latLngs.add(new LatLng(center.latitude + halfHeight, center.longitude + halfWidth));
        latLngs.add(new LatLng(center.latitude + halfHeight, center.longitude - halfWidth));
        return latLngs;
    }

}
