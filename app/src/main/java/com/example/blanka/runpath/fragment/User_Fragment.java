package com.example.blanka.runpath.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.blanka.runpath.R;
import com.example.blanka.runpath.activity.History_Activity;
import com.example.blanka.runpath.activity.Login_Activity;
import com.example.blanka.runpath.db.RunningRecord;
import com.example.blanka.runpath.util.HttpUtil;
import com.example.blanka.runpath.util.unitConversion;
import com.hrules.charter.CharterBar;
import com.hrules.charter.CharterXLabels;

import org.json.JSONArray;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;
import org.w3c.dom.Text;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by BLANKA on 2017/4/24 0024.
 */

public class User_Fragment extends mFragment implements View.OnClickListener {
    private List<RunningRecord> LocalRunningRecordList;

    private boolean IS_USER_LOGINED = false;
    private static String userName;
    private View view;
    private Button btn_login;
    private Button btn_logout;
    private Button btn_upload;
    private TextView tv_info;
    private ImageView imgb_personal;
    private ProgressDialog progressDialog;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private CharterBar charter_bar;
    private CharterXLabels charter_bar_XLabel;
    private float[] values;
    private float[] dateValues;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_user, container, false);
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar_personal);
        btn_login = (Button) view.findViewById(R.id.btn_login);
        btn_logout = (Button) view.findViewById(R.id.btn_logout);
        btn_upload = (Button) view.findViewById(R.id.btn_upload);
        imgb_personal = (ImageView) view.findViewById(R.id.imgb_personal);
        collapsingToolbarLayout = (CollapsingToolbarLayout) view.findViewById(R.id.collaosing_toolbar);
        charter_bar = (CharterBar) view.findViewById(R.id.charter_bar);
        charter_bar_XLabel = (CharterXLabels) view.findViewById(R.id.charter_bar_XLabel);
        tv_info = (TextView) view.findViewById(R.id.tv_info);
        charter_barshow();
        btn_login.setOnClickListener(this);
        btn_logout.setOnClickListener(this);
        btn_upload.setOnClickListener(this);
        tv_info.setOnClickListener(this);
        loadUserName();
        Glide.with(this).load(R.drawable.img_back2).into(imgb_personal);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                Intent intent = new Intent(view.getContext(), Login_Activity.class);
                startActivity(intent);
                break;
            case R.id.btn_upload:
                upload_localdb();
                break;
            case R.id.btn_logout:
                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                dialog.setTitle("确认退出用户吗");
                dialog.setMessage("无法使用联网同步");
                dialog.setCancelable(false);
                dialog.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences.Editor editor = getActivity().getSharedPreferences("data", getActivity().MODE_PRIVATE).edit();
                        editor.putString("user", "");
                        editor.apply();
                        loadUserName();
                    }
                });
                dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                dialog.show();
                break;
            case R.id.tv_info:
                Intent intent1 = new Intent(view.getContext(), History_Activity.class);
                startActivity(intent1);
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        loadUserName();
        loadDistanceCount();
    }

    /**
     * 载入用户信息 显示欢迎信息
     */
    private void loadUserName() {
        SharedPreferences pref = getActivity().getSharedPreferences("data", Context.MODE_PRIVATE);
        userName = pref.getString("user", "");
        if (userName.equals("")) {
            collapsingToolbarLayout.setTitle("HI, " + "请登录");
            IS_USER_LOGINED = false;
            refreshUIStyle();
        } else {
            collapsingToolbarLayout.setTitle("HI, " + userName);
            IS_USER_LOGINED = true;
            refreshUIStyle();
        }

    }

    /**
     * 载入运动总路程
     * */
    public void loadDistanceCount() {
        List<RunningRecord> RunningRecordList;
        if(userName.equals("")) {
            RunningRecordList = DataSupport.where("user = ? ", userName ).find(RunningRecord.class);
        }else {
            RunningRecordList = DataSupport.where("user = ? or user = ?", userName ,"").find(RunningRecord.class);
        }
        Float mdistanceCount = Float.valueOf(0);
        for(RunningRecord runningRecord: RunningRecordList){
            mdistanceCount = mdistanceCount + runningRecord.getMetre();
        }
        tv_info.setText("已运动"+ unitConversion.distance(Float.valueOf(mdistanceCount))+ "公里，查看历史记录");
    }

    /**
     *设置图表信息
     * */
    private void charter_barshow() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd");
        Date curDate = new Date(System.currentTimeMillis());
        String date = formatter.format(curDate);
        dateValues = new float[]{1,2,3,4,5,6,7};
        for(int i = 0 ; i < 7 ;i++){
            if( Float.valueOf(date) - i <=  0) {
                dateValues[i] = Float.valueOf(date) + 31 - i;
            }else {
                dateValues[i] = Float.valueOf(date) - i;
            }
        }
        Resources res = getResources();
        int[] barColors = new int[] {
                res.getColor(R.color.lightBlue500),
                res.getColor(R.color.lightBlue400),
                res.getColor(R.color.lightBlue300)};

        // charter_bar_XLabel
        charter_bar_XLabel.setStickyEdges(false);
        charter_bar_XLabel.setValues(dateValues);
        // charter_bar
        charter_bar.setValues(values);
        charter_bar.setColors(barColors);
        charter_bar.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                values = new float[]{(float) 2.1, (float) 1.5, 10, 0, (float) 2.5, (float) 1.0, (float) 0.9};
                charter_bar.setValues(values);
                charter_bar.show();
            }
        });
    }

    /**
     * 根据登录状态刷新按钮
     */
    private void refreshUIStyle() {
        if (IS_USER_LOGINED) {
            btn_login.setVisibility(View.GONE);
            btn_logout.setVisibility(View.VISIBLE);
            btn_upload.setVisibility(View.VISIBLE);
        } else {
            btn_logout.setVisibility(View.GONE);
            btn_upload.setVisibility(View.GONE);
            btn_login.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 用户上传运动记录模块
     */
    private void upload_localdb() {
        showProgressDialog();

        LocalRunningRecordList = DataSupport.where("user = ?", userName).find(RunningRecord.class);//读取本地数据库数据
        if(LocalRunningRecordList.size() > 0){
            StringBuilder dateString = new StringBuilder();
            StringBuilder clockString = new StringBuilder();
            StringBuilder userString = new StringBuilder();
            StringBuilder metreString = new StringBuilder();
            StringBuilder timeString = new StringBuilder();
            StringBuilder lineString = new StringBuilder();
            for (int i = 0; i < LocalRunningRecordList.size(); i++) {
                dateString.append(LocalRunningRecordList.get(i).getDate() + "#");
                clockString.append(LocalRunningRecordList.get(i).getClock() + "#");
                userString.append(LocalRunningRecordList.get(i).getUser() + "#");
                metreString.append(LocalRunningRecordList.get(i).getMetre() + "#");
                timeString.append(LocalRunningRecordList.get(i).getTime() + "#");
                lineString.append(LocalRunningRecordList.get(i).getLine() + " #");
            }

            String address = HttpUtil.GetServerAddress() + "/RunPath/runpath/upload.jsp";
            RequestBody requestBody = new FormBody.Builder()
                    .add("size", String.valueOf(LocalRunningRecordList.size()))
                    .add("date", String.valueOf(dateString.toString()))
                    .add("clock", String.valueOf(clockString.toString()))
                    .add("user", String.valueOf(userString.toString()))
                    .add("metre", String.valueOf(metreString.toString()))
                    .add("time", String.valueOf(timeString.toString()))
                    .add("line", String.valueOf(lineString.toString()))
                    .build();

            HttpUtil.sendOkHttpRequestPost(address, requestBody, new Callback() {

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    parseJson(response.body().string());
                }

                @Override
                public void onFailure(Call call, IOException e) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            Toast.makeText(getActivity(), "连接失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }else{
            closeProgressDialog();
            Toast.makeText(mActivity, "本地没有你的记录", Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * 分析 JSON 数据
     */
    private void parseJson(String responseData) {
        try {
            JSONArray jasonArray = new JSONArray(responseData);
            JSONObject jasonObject = jasonArray.getJSONObject(0);
            boolean upLoad_stat = jasonObject.getBoolean("upLoad_stat");
            if (upLoad_stat) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(getActivity(), "同步成功", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(getActivity(), "同步失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 进度条开启
     */
    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getContext());
            progressDialog.setMessage("正在连接...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }
    /**
     * 进度条关闭
     */
    private void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }


}
