package com.example.blanka.runpath.db;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * Created by BLANKA on 2017/5/10 0010.
 */

public class RunningRecord extends DataSupport implements Serializable{
    private int id;
    private String Date;
    private String clock;
    private String user;
    private float metre;
    private long time;
    private String line;

    public RunningRecord(String date, String clock, String user, float metre, Long time, String line) {
        this.Date = date;
        this.clock = clock;
        this.user = user;
        this.metre = metre;
        this.time = time;
        this.line = line;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getClock() {
        return clock;
    }

    public void setClock(String clock) {
        this.clock = clock;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public float getMetre() {
        return metre;
    }

    public void setMetre(float metre) {
        this.metre = metre;
    }


    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }
}
