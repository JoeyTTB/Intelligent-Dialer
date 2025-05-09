package com.example.intelligent_dailer.database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * 收藏联系人实体类
 */
@Entity(tableName = "favorite_contacts")
public class FavoriteContact {
    
    @PrimaryKey
    @NonNull
    private String contactId;
    
    private String name;
    private String phoneNumber;
    private long timestamp; // 添加收藏的时间戳

    public FavoriteContact(@NonNull String contactId, String name, String phoneNumber) {
        this.contactId = contactId;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.timestamp = System.currentTimeMillis();
    }

    @NonNull
    public String getContactId() {
        return contactId;
    }

    public void setContactId(@NonNull String contactId) {
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
} 