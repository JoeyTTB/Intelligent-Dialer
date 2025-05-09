package com.example.intelligent_dailer.database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * 通话记录实体类
 */
@Entity(tableName = "call_records")
public class CallRecord {
    
    @PrimaryKey(autoGenerate = true)
    private long id;
    
    private String contactId; // 联系人ID，可能为null（未知联系人）
    private String name; // 联系人名称，可能为null
    private String phoneNumber; // 电话号码
    private long timestamp; // 通话时间
    private int duration; // 通话时长（秒）
    private int type; // 通话类型：1-呼入，2-呼出，3-未接，5-拒绝
    
    public static final int CALL_TYPE_INCOMING = 1;
    public static final int CALL_TYPE_OUTGOING = 2;
    public static final int CALL_TYPE_MISSED = 3;
    public static final int CALL_TYPE_REJECTED = 5;

    public CallRecord(String contactId, String name, String phoneNumber, long timestamp, int duration, int type) {
        this.contactId = contactId;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.timestamp = timestamp;
        this.duration = duration;
        this.type = type;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
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

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
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
            return phoneNumber.substring(0, 1);
        }
        return "?";
    }
    
    /**
     * 获取通话时间的友好显示
     */
    public String getFormattedDate() {
        java.util.Date callDate = new java.util.Date(timestamp);
        java.util.Date now = new java.util.Date();
        
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
} 