package com.example.alarmer.ListAlarms;

import java.text.SimpleDateFormat;

public class Alarm {

    String time;
    Boolean waiting;

    int id;

    public Alarm(String time, Boolean waiting, int id   ) {
        this.time = time;
        this.waiting = waiting;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setWaiting(Boolean waiting) {
        this.waiting = waiting;
    }

    public String getTime() {
        return time;
    }

    public Boolean getWaiting() {
        return waiting;
    }
}
