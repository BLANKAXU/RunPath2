package com.example.blanka.runpath.gson;

/**
 * Created by BLANKA on 2017/5/24 0024.
 */

public class Record {
    private Boolean downLoad_count;
    private String date;
    private String clock;
    private String user;
    private String metre;
    private String time;
    private String line;

    public Boolean getDownLoad_count() {
        return downLoad_count;
    }

    public void setDownLoad_count(Boolean downLoad_count) {
        this.downLoad_count = downLoad_count;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getClock() {
        return clock;
    }

    public void setClock(String clock) {
        this.clock = clock;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getMetre() {
        return metre;
    }

    public void setMetre(String metre) {
        this.metre = metre;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }
}
