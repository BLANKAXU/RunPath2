package com.example.blanka.runpath.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.blanka.runpath.db.RunningRecord;
import com.example.blanka.runpath.R;
import com.example.blanka.runpath.gson.Record;
import com.example.blanka.runpath.util.HttpUtil;
import com.example.blanka.runpath.util.RunningRecordAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.example.blanka.runpath.util.HttpUtil.GetServerAddress;

public class History_Activity extends AppCompatActivity {
    private static final String TAG = "History_Activity";
    private List<RunningRecord> RunningRecordList;//读取本地数据库数据
    private List<RunningRecord> UserRunningRecordList;
    private TextView tv_no_user;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RunningRecordAdapter adapter;
    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        setTitle("历史记录");
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView_history);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        tv_no_user = (TextView) findViewById(R.id.tv_no_user);

        dataInit();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new RunningRecordAdapter(RunningRecordList);

        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
            }
        });
        //添加粘性头部
        final StickyRecyclerHeadersDecoration headersDecor = new StickyRecyclerHeadersDecoration(adapter); //绑定之前的adapter
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                headersDecor.invalidateHeaders();
            }
        });  //刷新数据的时候回刷新头部
        recyclerView.addItemDecoration(headersDecor);
        recyclerView.setAdapter(adapter);

    }

    private void dataInit() {
        SharedPreferences pref = getSharedPreferences("data", Context.MODE_PRIVATE);
        userName = pref.getString("user", "");
        RunningRecordList = DataSupport.where("user = ? or user = ?", userName ,"").order("date desc").find(RunningRecord.class);//读取本地数据库数据
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        //如果没有记录
        if (RunningRecordList.size() == 0) {
            tv_no_user.setVisibility(View.VISIBLE);
        } else {
            tv_no_user.setVisibility(View.GONE);
        }
    }

    private void refreshData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (!userName.equals("")) {
                        download_localdb();
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                swipeRefreshLayout.setRefreshing(false);
                                Toast.makeText(History_Activity.this, "刷新同步 需要先登录", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    /**
     * 用户上传运动记录模块
     */
    private void download_localdb() {
        UserRunningRecordList = DataSupport.where("user = ?", userName).find(RunningRecord.class);//读取本地数据库数据
        StringBuilder dateString = new StringBuilder();
        StringBuilder clockString = new StringBuilder();
        StringBuilder userString = new StringBuilder();
        StringBuilder metreString = new StringBuilder();
        StringBuilder timeString = new StringBuilder();
        StringBuilder lineString = new StringBuilder();
        for (int i = 0; i < UserRunningRecordList.size(); i++) {
            dateString.append(UserRunningRecordList.get(i).getDate() + "#");
            clockString.append(UserRunningRecordList.get(i).getClock() + "#");
            userString.append(userName);
            metreString.append(UserRunningRecordList.get(i).getMetre() + "#");
            timeString.append(UserRunningRecordList.get(i).getTime() + "#");
            lineString.append(UserRunningRecordList.get(i).getLine() + " #");
        }

        String address = GetServerAddress()+"/RunPath/runpath/download.jsp";
        RequestBody requestBody = new FormBody.Builder()
                .add("size", String.valueOf(UserRunningRecordList.size()))
                .add("date", String.valueOf(dateString.toString()))
                .add("clock", String.valueOf(clockString.toString()))
                .add("user", String.valueOf(userName))
                .add("metre", String.valueOf(metreString.toString()))
                .add("time", String.valueOf(timeString.toString()))
                .add("line", String.valueOf(lineString.toString()))
                .build();

        HttpUtil.sendOkHttpRequestPost(address, requestBody, new Callback() {

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                parseJsonWithGson(response.body().string());
            }

            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(History_Activity.this, "连接失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    /**
     * 分析 JSON 数据
     */
    private void parseJsonWithGson(String responseData) {
        try {

            Gson gson = new Gson();
            final List<Record> mRecord = gson.fromJson(responseData, new TypeToken<List<Record>>() {
            }.getType());
            if (mRecord != null) {
                for (Record record : mRecord) {
                    RunningRecord runningRecord = new RunningRecord(
                            record.getDate(),
                            record.getClock(),
                            record.getUser(),
                            Float.valueOf(record.getMetre()),
                            Long.valueOf(record.getTime()),
                            record.getLine());
                    runningRecord.save();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                        RunningRecordList.clear();
                        RunningRecordList.addAll(DataSupport.findAll(RunningRecord.class));//读取本地数据库数据
                        adapter.notifyDataSetChanged();
                        Toast.makeText(History_Activity.this, "同步成功，新增" + mRecord.size() + "条数据", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                        RunningRecordList.clear();
                        RunningRecordList.addAll(DataSupport.where("user = ? or user = ?", userName ,"").order("date desc").find(RunningRecord.class));//读取本地数据库数据
                        adapter.notifyDataSetChanged();
                        Toast.makeText(History_Activity.this, "同步成功", Toast.LENGTH_SHORT).show();
                    }
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
