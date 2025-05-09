package com.example.intelligent_dailer.models;

import androidx.annotation.NonNull;

public class Contact {
    private String id;
    private String name;
    private String phoneNumber;
    private boolean isFavorite;

    public Contact(String id, String name, String phoneNumber) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.isFavorite = false;
    }

    public Contact(String id, String name, String phoneNumber, boolean isFavorite) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.isFavorite = isFavorite;
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

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    /**
     * 获取联系人名称的首字母作为头像
     */
    public String getInitial() {
        if (name != null && !name.isEmpty()) {
            return name.substring(0, 1).toUpperCase();
        }
        return "?";
    }

    @NonNull
    @Override
    public String toString() {
        return "Contact{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", isFavorite=" + isFavorite +
                '}';
    }
} 