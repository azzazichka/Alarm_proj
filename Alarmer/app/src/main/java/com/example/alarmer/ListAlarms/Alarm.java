package com.example.alarmer.ListAlarms;

import java.text.SimpleDateFormat;

public class Alarm {

    String time;
    Boolean waiting;

    String color;

    int id;

    public Alarm(String time, Boolean waiting, int id, String color) {
        this.time = time;
        this.waiting = waiting;
        this.id = id;
        this.color = color;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
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
