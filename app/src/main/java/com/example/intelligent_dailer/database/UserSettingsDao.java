package com.example.intelligent_dailer.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

/**
 * 用户设置DAO接口
 */
@Dao
public interface UserSettingsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(UserSettings settings);
    
    @Update
    void update(UserSettings settings);
    
    @Query("SELECT * FROM user_settings WHERE settingId = :settingId LIMIT 1")
    LiveData<UserSettings> getUserSettings(String settingId);
    
    @Query("SELECT * FROM user_settings WHERE settingId = :settingId LIMIT 1")
    UserSettings getUserSettingsSync(String settingId);
    
    @Query("SELECT COUNT(*) FROM user_settings WHERE settingId = :settingId")
    int hasSettings(String settingId);
} 