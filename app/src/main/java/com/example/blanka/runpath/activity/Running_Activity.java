package com.example.blanka.runpath.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.PolygonOptions;
import com.amap.api.maps.model.PolylineOptions;
import com.example.blanka.runpath.R;
import com.example.blanka.runpath.db.RunningRecord;
import com.example.blanka.runpath.fragment.Running_Fragment;
import com.example.blanka.runpath.util.unitConversion;

import org.litepal.tablemanager.Connector;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class Running_Activity extends AppCompatActivity implements LocationSource, AMapLocationListener, View.OnClickListener {
    public static final LatLng SHANGHAI = new LatLng(31.238068, 121.501654);// 上海市经纬度
    private boolean IS_FRAG_SHOWED = false;
    private boolean isFirstLatLng;
    private boolean IS_RECORD_PAUSE = false;

    private MapView mMapView = null;
    private OnLocationChangedListener mListener;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;
    private AMap aMap;

    private Button stopButton = null;
    private TextView distanceTextView;
    private TextView tv_gps;
    private FragmentManager fragmentManager;
    private Running_Fragment runningFragment;
    private Fragment currentFragment = new Fragment();
    private float distance = 0;
    private LatLng oldLatLng;

    private StringBuilder runPath = new StringBuilder();
    private String userName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_running);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();//隐藏标题栏
        }
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);//去状态栏
        }
        mMapView = (MapView) findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);// 此方法必须重写
        aMap = mMapView.getMap();
        aMap.setLocationSource(this); // 设置定位监听
        aMap.getUiSettings().setMyLocationButtonEnabled(false);// 设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        aMap.moveCamera(CameraUpdateFactory.zoomTo(18));
        aMap.setMyLocationType(AMap.LOCATION_TYPE_MAP_FOLLOW);
        //使用 aMap.setMapTextZIndex(2) 可以将地图底图文字设置在添加的覆盖物之上

        fragmentManager = getSupportFragmentManager();
        runningFragment = new Running_Fragment();
        showFragment(runningFragment);

        stopButton = (Button) findViewById(R.id.stopButton);
        distanceTextView = (TextView) findViewById(R.id.distanceTextView);
        tv_gps = (TextView) findViewById(R.id.tv_gps);
        stopButton.setOnClickListener(this);
        tv_gps.setOnClickListener(this);
        setUpMap3();
        isFirstLatLng = true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.stopButton:
                stopRunning();
                break;
            case R.id.tv_gps:
                if (IS_FRAG_SHOWED) {
                    tv_gps.setBackgroundDrawable(getResources().getDrawable(R.drawable.mapcancel));
                    hideFragment();
                } else {
                    tv_gps.setBackgroundDrawable(getResources().getDrawable(R.drawable.mapbuttom));
                    showFragment(currentFragment);
                }
                break;
        }
    }

    /**
     * 结束函数
     */
    public void stopRunning() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(Running_Activity.this);
        dialog.setTitle("确认结束运动");
        dialog.setMessage("运动够了吗？再跑一会把");
        dialog.setCancelable(false);
        dialog.setPositiveButton("够了", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deactivate();
                runningFragment.stopTiming();
                AlertDialog.Builder dialog2 = new AlertDialog.Builder(Running_Activity.this);
                dialog2.setTitle("是否保存记录");
                dialog2.setMessage("保存了的记录可以随时查看哦");
                dialog2.setCancelable(false);
                dialog2.setPositiveButton("保存", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        saveTodb();
                        Toast.makeText(Running_Activity.this, "本次运动" + String.valueOf(distance) + "米，记录保存成功", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
                dialog2.setNegativeButton("不用啦", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(Running_Activity.this, "本次运动" + String.valueOf(distance) + "米", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
                dialog2.show();
            }
        });
        dialog.setNegativeButton("在跑会", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        dialog.show();

    }

    /**
     * 使用show()显示fragment
     */
    private void showFragment(Fragment fragment) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        //如果之前没有添加过
        if (!fragment.isAdded()) {
            transaction.add(R.id.data_content, fragment);
        } else {
            transaction.show(fragment);
        }
        transaction.commit();
        //全局变量，记录当前显示的fragment
        currentFragment = fragment;
        IS_FRAG_SHOWED = true;
    }

    /**
     * 使用hide()隐藏fragment
     */
    private void hideFragment() {
        FragmentTransaction mBeginTransaction = fragmentManager.beginTransaction();
        mBeginTransaction.hide(currentFragment);
        mBeginTransaction.commit();
        IS_FRAG_SHOWED = false;
    }


    /**
     * 激活定位
     */
    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;
        if (mlocationClient == null) {
            mlocationClient = new AMapLocationClient(this);//初始化定位
            mLocationOption = new AMapLocationClientOption();//初始化定位参数
            mlocationClient.setLocationListener(this);  //设置定位回调监听
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy); //设置为高精度定位模式
            /**
             * 设置是否优先返回GPS定位结果，如果30秒内GPS没有返回定位结果则进行网络定位
             * 注意：只有在高精度模式下的单次定位有效，其他方式无效
             */
            mLocationOption.setOnceLocation(false);
            mLocationOption.setGpsFirst(true);
            mLocationOption.setInterval(1000);// 设置发送定位请求的时间间隔,最小值为1000ms,1秒更新一次定位信息
            mLocationOption.setSensorEnable(true);//可选，设置是否使用传感器。默认是false
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mlocationClient.setLocationOption(mLocationOption);//设置定位参数
            mlocationClient.startLocation();//启动定位
        }
    }

    /**
     * 停止定位
     */
    @Override
    public void deactivate() {
        mListener = null;
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);//在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mMapView.onSaveInstanceState(outState);
    }

    /**
     * 定位成功后回调函数
     */
    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (mListener != null && amapLocation != null) {
            if (amapLocation != null
                    && amapLocation.getErrorCode() == 0) {
                mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
                LatLng newLatLng = new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude());
                //判断第一次定位
                if (isFirstLatLng) {
                    oldLatLng = newLatLng;
                    runPath.append(newLatLng.latitude);
                    runPath.append(",");
                    runPath.append(newLatLng.longitude);
                    runPath.append(";");
                    isFirstLatLng = false;
                }
                //判断位置是否改变
                if (!newLatLng.toString().equals(oldLatLng.toString())) {
                    float newDistance = AMapUtils.calculateLineDistance(oldLatLng, newLatLng);
                    if (!IS_RECORD_PAUSE) {
                        if (newDistance < 40) {
                            //画运动轨迹并记录距离
                            setUpMap(oldLatLng, newLatLng);
                            distance = distance + newDistance;
                            runPath.append(newLatLng.latitude);
                            runPath.append(",");
                            runPath.append(newLatLng.longitude);
                            runPath.append(";");
                            distanceTextView.setText("你已运动" + String.valueOf(distance) + "米");
                            runningFragment.setDistance(unitConversion.distance(distance));
                            runningFragment.setSpeed(distance);
                        } else {
                            //偏移轨迹不记录距离
                            setUpMap2(oldLatLng, newLatLng);
                            runPath.append(newLatLng.latitude);
                            runPath.append(",");
                            runPath.append(newLatLng.longitude);
                            runPath.append(";");
                        }
                    }
                    oldLatLng = newLatLng;
                }
            } else {
                String errText = "定位失败," + amapLocation.getErrorCode() + ": " + amapLocation.getErrorInfo();
                Log.e("AmapErr", errText);
            }
        }
    }

    /**
     * 绘制正常运动曲线
     */
    private void setUpMap(LatLng oldLatLng, LatLng newLatLng) {
        aMap.addPolyline((new PolylineOptions())
                .add(oldLatLng, newLatLng)
                .geodesic(true).color(Color.GREEN));
    }

    /**
     * 绘制非正常运动曲线
     */
    private void setUpMap2(LatLng oldLatLng, LatLng newLatLng) {
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

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
        if (null != mlocationClient) {
            mlocationClient.onDestroy();
        }
        runPath.append("end");
        Toast.makeText(Running_Activity.this, "你已运动" + String.valueOf(distance) + "米", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(Running_Activity.this);
        dialog.setTitle("确定要退出吗");
        dialog.setMessage("退出后会停止记录运动，记录不会保留");
        dialog.setCancelable(false);
        dialog.setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        dialog.show();
    }

    /**
     * 保存到数据库
     */
    private void saveTodb() {
        SharedPreferences pref = getSharedPreferences("data", Context.MODE_PRIVATE);
        userName = pref.getString("user", "");
        Connector.getDatabase();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd;HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());
        String date = formatter.format(curDate);
        String dateAll[] = date.split(";");
        RunningRecord NEW = new RunningRecord(dateAll[0], dateAll[1], userName, distance, runningFragment.getRecordTime(), runPath.toString());
        NEW.save();
    }


    /**
     * 生成一个长方形的四个坐标点
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

    /**
     * 控制记录暂停开始
     */
    public void setRecordStart() {
        IS_RECORD_PAUSE = false;
    }

    public void setRecordPause() {
        IS_RECORD_PAUSE = true;
    }

}
