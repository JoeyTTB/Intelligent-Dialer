package com.example.intelligent_dailer.models;

import androidx.annotation.NonNull;

import java.util.Date;

public class CallLog {
    public static final int CALL_TYPE_INCOMING = 1;
    public static final int CALL_TYPE_OUTGOING = 2;
    public static final int CALL_TYPE_MISSED = 3;
    public static final int CALL_TYPE_REJECTED = 5;

    private String id;
    private String name;
    private String phoneNumber;
    private long date;
    private int duration;
    private int type;

    public CallLog(String id, String name, String phoneNumber, long date, int duration, int type) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.date = date;
        this.duration = duration;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    /**
     * 获取联系人名称的首字母作为头像
     */
    public String getInitial() {
        if (name != null && !name.isEmpty()) {
            return name.substring(0, 1).toUpperCase();
        } else if (phoneNumber != null && !phoneNumber.isEmpty()) {
            return "?"; // 未知联系人显示问号
        }
        return "?";
    }

    /**
     * 获取通话时间的友好显示
     */
    public String getFormattedDate() {
        Date callDate = new Date(date);
        Date now = new Date();
        
        // 简化处理，仅作示例
        long diff = now.getTime() - callDate.getTime();
        long hours = diff / (60 * 60 * 1000);
        
        if (hours < 24) {
            return "今天";
        } else if (hours < 48) {
            return "昨天";
        } else {
            return (hours / 24) + "天前";
        }
    }

    /**
     * 获取通话时长的友好显示
     */
    public String getFormattedDuration() {
        if (duration == 0) {
            return "未接通";
        }
        
        int minutes = duration / 60;
        int seconds = duration % 60;
        
        if (minutes > 0) {
            return minutes + "分" + seconds + "秒";
        } else {
            return seconds + "秒";
        }
    }

    @NonNull
    @Override
    public String toString() {
        return "CallLog{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", date=" + date +
                ", duration=" + duration +
                ", type=" + type +
                '}';
    }
} 