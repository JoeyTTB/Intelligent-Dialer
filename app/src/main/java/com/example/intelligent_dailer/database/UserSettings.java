package com.example.intelligent_dailer.database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * 用户设置实体类，保存用户个性化设置
 */
@Entity(tableName = "user_settings")
public class UserSettings {
    
    @PrimaryKey
    @NonNull
    private String settingId;
    
    private boolean darkThemeEnabled; // 是否启用深色主题
    private String fontSize; // 字体大小: small, medium, large
    private boolean useSystemTheme; // 是否跟随系统主题
    private int buttonSize; // 按钮大小，百分比值: 80-120
    private long lastModified; // 上次修改时间
    
    // 字体大小常量
    public static final String FONT_SIZE_SMALL = "small";
    public static final String FONT_SIZE_MEDIUM = "medium";
    public static final String FONT_SIZE_LARGE = "large";
    
    // 默认设置ID
    public static final String DEFAULT_SETTINGS_ID = "user_settings";
    
    public UserSettings(@NonNull String settingId) {
        this.settingId = settingId;
        this.darkThemeEnabled = false;
        this.fontSize = FONT_SIZE_MEDIUM;
        this.useSystemTheme = true;
        this.buttonSize = 100;
        this.lastModified = System.currentTimeMillis();
    }

    @NonNull
    public String getSettingId() {
        return settingId;
    }

    public void setSettingId(@NonNull String settingId) {
        this.settingId = settingId;
    }

    public boolean isDarkThemeEnabled() {
        return darkThemeEnabled;
    }

    public void setDarkThemeEnabled(boolean darkThemeEnabled) {
        this.darkThemeEnabled = darkThemeEnabled;
        this.lastModified = System.currentTimeMillis();
    }

    public String getFontSize() {
        return fontSize;
    }

    public void setFontSize(String fontSize) {
        this.fontSize = fontSize;
        this.lastModified = System.currentTimeMillis();
    }
    
    public boolean isUseSystemTheme() {
        return useSystemTheme;
    }
    
    public void setUseSystemTheme(boolean useSystemTheme) {
        this.useSystemTheme = useSystemTheme;
        this.lastModified = System.currentTimeMillis();
    }
    
    public int getButtonSize() {
        return buttonSize;
    }
    
    public void setButtonSize(int buttonSize) {
        this.buttonSize = buttonSize;
        this.lastModified = System.currentTimeMillis();
    }

    public long getLastModified() {
        return lastModified;
    }

    public void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }
} 