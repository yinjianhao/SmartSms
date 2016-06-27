package com.me.smartsms.bean;

import android.database.Cursor;

public class SmsDetailBean {
    private int id;
    private String body;
    private long date;
    private int type;

    public SmsDetailBean(Cursor cursor) {
        this.id = cursor.getInt(cursor.getColumnIndex("_id"));
        this.body = cursor.getString(cursor.getColumnIndex("body"));
        this.date = cursor.getLong(cursor.getColumnIndex("date"));
        this.type = cursor.getInt(cursor.getColumnIndex("type"));
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }
}
