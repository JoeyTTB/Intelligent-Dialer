package com.example.intelligent_dailer.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

/**
 * 应用数据库类
 */
@Database(entities = {FavoriteContact.class, CallRecord.class, UserSettings.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    
    // 数据库名称
    private static final String DATABASE_NAME = "intelligent_dailer_db";
    
    // 单例实例
    private static AppDatabase instance;
    
    // 获取DAO
    public abstract FavoriteContactDao favoriteContactDao();
    public abstract CallRecordDao callRecordDao();
    public abstract UserSettingsDao userSettingsDao();
    
    // 获取数据库实例
    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    AppDatabase.class,
                    DATABASE_NAME)
                    .fallbackToDestructiveMigration() // 如果版本升级，暴力迁移（删除旧表，创建新表）
                    .build();
        }
        return instance;
    }
} 