package com.example.blanka.runpath.util;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.blanka.runpath.R;
import com.example.blanka.runpath.activity.Record_Activity;
import com.example.blanka.runpath.activity.Running_Activity;
import com.example.blanka.runpath.db.RunningRecord;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import org.litepal.crud.DataSupport;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by BLANKA on 2017/5/3 0003.
 */

public class RunningRecordAdapter extends RecyclerView.Adapter<RunningRecordAdapter.ViewHolder> implements StickyRecyclerHeadersAdapter<RunningRecordAdapter.ViewHolder>{

    private List<RunningRecord> mRunningRecordList;

    public class ViewHolder extends RecyclerView.ViewHolder {
        //初始化
        View mView;
        TextView date_year;
        TextView date_month;
        TextView date_day;
        TextView date_time;
        TextView time;
        TextView distance;
        TextView header;
        public ViewHolder(View view) {
            super(view);
            mView = view;
            date_day = (TextView) view.findViewById(R.id.tv_dateDay);
            date_time = (TextView) view.findViewById(R.id.tv_dateTime);
            time = (TextView) view.findViewById(R.id.tv_time);
            distance = (TextView) view.findViewById(R.id.tv_distance);
            header = (TextView) view.findViewById(R.id.tv_header);
        }
    }

    public RunningRecordAdapter(List<RunningRecord> runningRecordList){
        mRunningRecordList = runningRecordList;//传入数据
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.exercise_item, parent, false);//载入布局
        final ViewHolder holder = new ViewHolder(view);
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                RunningRecord runningRecord = mRunningRecordList.get(position);
                Intent intent = new Intent(v.getContext(), Record_Activity.class);
                intent.putExtra("RunningData", runningRecord);
                v.getContext().startActivity(intent);
            }
        });
        holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(v.getContext());
                dialog.setMessage("确定删除后无法恢复");
                dialog.setCancelable(false);
                dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DataSupport.deleteAll(RunningRecord.class, "id = ?",
                                String.valueOf(mRunningRecordList.get(holder.getAdapterPosition()).getId()));
                        mRunningRecordList.remove(holder.getAdapterPosition());
                        notifyDataSetChanged();
                    }
                });
                dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                dialog.show();
                return false;
            }
        });
        return  holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //设置数据
        RunningRecord runningRecord = mRunningRecordList.get(position);
        String dateAll[] = runningRecord.getDate().split("/");
        holder.date_day.setText(dateAll[2] + "日");
        holder.date_time.setText("时间:" + runningRecord.getClock());
        holder.time.setText("用时:" + unitConversion.time(runningRecord.getTime()));
        holder.distance.setText(unitConversion.distance(runningRecord.getMetre()));
    }


    @Override
    public int getItemCount() {
        return mRunningRecordList.size();
    }

/**
 * 为添加粘性头部
 *
* */

    @Override
    public long getHeaderId(int position) {
        return getGroupPosition(position);//这个方法是用来固定头部的，有着相同的HeaderId的Item的头部将会是同一个，这个ID可以自己设置
    }

    private int getGroupPosition(int position) {
        RunningRecord runningRecord = mRunningRecordList.get(position);
        String dateAll[] = runningRecord.getDate().split("/");
        return Integer.parseInt(dateAll[0] + dateAll[1]);
    }

    @Override
    public ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.exercise_head_choose, parent, false);//载入头部布局
        ViewHolder viewHolder = new ViewHolder(view);
        return  viewHolder ;
    }

    @Override
    public void onBindHeaderViewHolder(ViewHolder holder, int position) {
        //设置数据
        RunningRecord runningRecord = mRunningRecordList.get(position);
        String dateAll[] = runningRecord.getDate().split("/");
        holder.header.setText(dateAll[0] + "年" + dateAll[1] + "月份");
    }
}
