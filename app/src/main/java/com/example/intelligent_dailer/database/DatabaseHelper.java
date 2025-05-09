package com.example.intelligent_dailer.database;

import android.content.Context;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 数据库操作辅助类，封装数据库操作
 */
public class DatabaseHelper {

    private final FavoriteContactDao favoriteContactDao;
    private final CallRecordDao callRecordDao;
    private final UserSettingsDao userSettingsDao;
    private final ExecutorService executorService;
    
    public DatabaseHelper(Context context) {
        AppDatabase database = AppDatabase.getInstance(context);
        favoriteContactDao = database.favoriteContactDao();
        callRecordDao = database.callRecordDao();
        userSettingsDao = database.userSettingsDao();
        executorService = Executors.newFixedThreadPool(4);
    }
    
    // ==== 收藏联系人操作 ====
    
    /**
     * 添加收藏联系人
     */
    public void addFavoriteContact(String contactId, String name, String phoneNumber) {
        FavoriteContact contact = new FavoriteContact(contactId, name, phoneNumber);
        executorService.execute(() -> favoriteContactDao.insert(contact));
    }
    
    /**
     * 移除收藏联系人
     */
    public void removeFavoriteContact(String contactId) {
        executorService.execute(() -> favoriteContactDao.deleteByContactId(contactId));
    }
    
    /**
     * 获取所有收藏联系人
     */
    public LiveData<List<FavoriteContact>> getAllFavoriteContacts() {
        return favoriteContactDao.getAllFavoriteContacts();
    }
    
    /**
     * 检查联系人是否已收藏
     */
    public boolean isFavoriteContact(String contactId) {
        try {
            return new IsContactFavoriteTask(favoriteContactDao).execute(contactId).get() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // ==== 通话记录操作 ====
    
    /**
     * 添加通话记录
     */
    public void addCallRecord(String contactId, String name, String phoneNumber, long timestamp, int duration, int type) {
        CallRecord record = new CallRecord(contactId, name, phoneNumber, timestamp, duration, type);
        executorService.execute(() -> callRecordDao.insert(record));
    }
    
    /**
     * 删除通话记录
     */
    public void deleteCallRecord(long id) {
        executorService.execute(() -> callRecordDao.deleteById(id));
    }
    
    /**
     * 清空通话记录
     */
    public void clearCallRecords() {
        executorService.execute(callRecordDao::deleteAll);
    }
    
    /**
     * 获取所有通话记录
     */
    public LiveData<List<CallRecord>> getAllCallRecords() {
        return callRecordDao.getAllCallRecords();
    }
    
    /**
     * 获取指定电话号码的通话记录
     */
    public LiveData<List<CallRecord>> getCallRecordsByPhoneNumber(String phoneNumber) {
        return callRecordDao.getCallRecordsByPhoneNumber(phoneNumber);
    }
    
    /**
     * 获取指定类型的通话记录
     */
    public LiveData<List<CallRecord>> getCallRecordsByType(int type) {
        return callRecordDao.getCallRecordsByType(type);
    }
    
    // ==== 用户设置操作 ====
    
    /**
     * 获取用户设置，不存在则创建默认设置
     */
    public LiveData<UserSettings> getUserSettings() {
        // 检查设置是否存在，不存在则创建默认设置
        executorService.execute(() -> {
            try {
                int count = userSettingsDao.hasSettings(UserSettings.DEFAULT_SETTINGS_ID);
                if (count == 0) {
                    // 创建默认设置
                    UserSettings defaultSettings = new UserSettings(UserSettings.DEFAULT_SETTINGS_ID);
                    userSettingsDao.insert(defaultSettings);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        
        return userSettingsDao.getUserSettings(UserSettings.DEFAULT_SETTINGS_ID);
    }
    
    /**
     * 更新主题设置
     */
    public void updateDarkTheme(boolean enabled) {
        executorService.execute(() -> {
            UserSettings settings = userSettingsDao.getUserSettingsSync(UserSettings.DEFAULT_SETTINGS_ID);
            if (settings != null) {
                settings.setDarkThemeEnabled(enabled);
                userSettingsDao.update(settings);
            }
        });
    }
    
    /**
     * 更新是否跟随系统主题
     */
    public void updateUseSystemTheme(boolean useSystem) {
        executorService.execute(() -> {
            UserSettings settings = userSettingsDao.getUserSettingsSync(UserSettings.DEFAULT_SETTINGS_ID);
            if (settings != null) {
                settings.setUseSystemTheme(useSystem);
                userSettingsDao.update(settings);
            }
        });
    }
    
    /**
     * 更新字体大小
     */
    public void updateFontSize(String fontSize) {
        executorService.execute(() -> {
            UserSettings settings = userSettingsDao.getUserSettingsSync(UserSettings.DEFAULT_SETTINGS_ID);
            if (settings != null) {
                settings.setFontSize(fontSize);
                userSettingsDao.update(settings);
            }
        });
    }
    
    /**
     * 更新按钮大小
     */
    public void updateButtonSize(int buttonSize) {
        executorService.execute(() -> {
            UserSettings settings = userSettingsDao.getUserSettingsSync(UserSettings.DEFAULT_SETTINGS_ID);
            if (settings != null) {
                settings.setButtonSize(buttonSize);
                userSettingsDao.update(settings);
            }
        });
    }
    
    /**
     * 异步任务，检查联系人是否已收藏
     */
    private static class IsContactFavoriteTask extends AsyncTask<String, Void, Integer> {
        private final FavoriteContactDao dao;
        
        IsContactFavoriteTask(FavoriteContactDao dao) {
            this.dao = dao;
        }
        
        @Override
        protected Integer doInBackground(String... contactIds) {
            return dao.isFavorite(contactIds[0]);
        }
    }
} 