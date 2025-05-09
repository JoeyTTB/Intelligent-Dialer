package com.example.intelligent_dailer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.intelligent_dailer.adapters.ViewPagerAdapter;
import com.example.intelligent_dailer.database.AppDatabase;
import com.example.intelligent_dailer.database.DatabaseHelper;
import com.example.intelligent_dailer.database.UserSettings;
import com.example.intelligent_dailer.ui.ContactsFragment;
import com.example.intelligent_dailer.ui.DialerFragment;
import com.example.intelligent_dailer.ui.FavoritesFragment;
import com.example.intelligent_dailer.ui.RecentsFragment;
import com.example.intelligent_dailer.ui.SettingsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SettingsFragment.SettingsChangeListener {

    private ViewPager2 viewPager;
    private BottomNavigationView bottomNavigationView;
    private DatabaseHelper databaseHelper;

    private static final int PERMISSIONS_REQUEST_CODE = 100;
    private String[] requiredPermissions = {
            Manifest.permission.CALL_PHONE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.READ_CALL_LOG
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始化数据库
        databaseHelper = new DatabaseHelper(this);
        // 预加载数据库实例
        AppDatabase.getInstance(this);

        // 检查权限
        checkPermissions();

        // 初始化视图
        setupViews();
        
        // 加载设置
        loadSettings();
    }

    private void setupViews() {
        viewPager = findViewById(R.id.viewPager);
        bottomNavigationView = findViewById(R.id.bottomNavigation);

        // 初始化Fragment
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new DialerFragment());
        fragments.add(new ContactsFragment());
        fragments.add(new FavoritesFragment());
        fragments.add(new RecentsFragment());
        fragments.add(new SettingsFragment());

        // 设置ViewPager适配器
        ViewPagerAdapter adapter = new ViewPagerAdapter(this, fragments);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(fragments.size());

        // 防止ViewPager滑动
        viewPager.setUserInputEnabled(false);

        // 设置底部导航栏点击事件
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.menu_dialer) {
                viewPager.setCurrentItem(0, false);
                return true;
            } else if (itemId == R.id.menu_contacts) {
                viewPager.setCurrentItem(1, false);
                return true;
            } else if (itemId == R.id.menu_favorites) {
                viewPager.setCurrentItem(2, false);
                return true;
            } else if (itemId == R.id.menu_recents) {
                viewPager.setCurrentItem(3, false);
                return true;
            } else if (itemId == R.id.menu_settings) {
                viewPager.setCurrentItem(4, false);
                return true;
            }
            return false;
        });
    }
    
    private void loadSettings() {
        // 观察用户设置变化
        databaseHelper.getUserSettings().observe(this, settings -> {
            if (settings != null) {
                // 先应用主题设置
                applyTheme(settings);
                
                // 重置所有视图尺寸再应用新设置
                resetViewSizes(getWindow().getDecorView());
                
                // 分别应用字体和按钮大小设置
                applyFontSize(settings);
                applyButtonSize(settings);
            }
        });
    }
    
    private void applyTheme(UserSettings settings) {
        if (settings.isUseSystemTheme()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        } else if (settings.isDarkThemeEnabled()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }
    
    /**
     * 应用字体大小设置
     * 仅调整普通文本视图的字体大小，不影响按钮元素
     */
    private void applyFontSize(UserSettings settings) {
        float fontScale = 1.0f;
        switch (settings.getFontSize()) {
            case UserSettings.FONT_SIZE_SMALL:
                fontScale = 0.85f;
                break;
            case UserSettings.FONT_SIZE_MEDIUM:
                fontScale = 1.0f;
                break;
            case UserSettings.FONT_SIZE_LARGE:
                fontScale = 1.15f;
                break;
        }
        
        adjustFontSize(getWindow().getDecorView(), fontScale);
    }
    
    /**
     * 应用按钮大小设置
     * 仅调整按钮元素的大小和内边距，不影响文本大小
     */
    private void applyButtonSize(UserSettings settings) {
        int buttonSize = settings.getButtonSize();
        float buttonScale = buttonSize / 100f;
        
        // 确保应用按钮大小时不会干扰字体大小
        adjustButtonSize(getWindow().getDecorView(), buttonScale);
    }
    
    /**
     * 重置所有视图的尺寸到原始状态
     */
    private void resetViewSizes(View view) {
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                resetViewSizes(viewGroup.getChildAt(i));
            }
        } else {
            // 重置TextView (非Button)
            if (view instanceof TextView && !(view instanceof Button)) {
                TextView textView = (TextView) view;
                Object originalSize = textView.getTag(R.id.tag_original_text_size);
                if (originalSize != null) {
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, (float) originalSize);
                }
            }
            
            // 重置Button
            if (view instanceof Button) {
                Button button = (Button) view;
                
                // 重置布局参数
                Object originalDimensionsObj = button.getTag(R.id.tag_original_layout_params);
                if (originalDimensionsObj != null) {
                    int[] originalDimensions = (int[]) originalDimensionsObj;
                    ViewGroup.LayoutParams params = button.getLayoutParams();
                    
                    if (originalDimensions[0] > 0 && originalDimensions[0] != ViewGroup.LayoutParams.MATCH_PARENT) {
                        params.width = originalDimensions[0];
                    }
                    
                    if (originalDimensions[1] > 0) {
                        params.height = originalDimensions[1];
                    }
                    
                    button.setLayoutParams(params);
                }
                
                // 重置内边距
                Object originalPaddingObj = button.getTag(R.id.tag_original_padding);
                if (originalPaddingObj != null) {
                    int[] originalPadding = (int[]) originalPaddingObj;
                    button.setPadding(
                        originalPadding[0],
                        originalPadding[1],
                        originalPadding[2],
                        originalPadding[3]
                    );
                }
            }
        }
    }
    
    private void adjustFontSize(View view, float scale) {
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                adjustFontSize(viewGroup.getChildAt(i), scale);
            }
        } else if (view instanceof TextView && !(view instanceof Button)) {
            TextView textView = (TextView) view;
            // 保存原始字体大小
            if (textView.getTag(R.id.tag_original_text_size) == null) {
                textView.setTag(R.id.tag_original_text_size, textView.getTextSize());
            }
            // 基于原始大小计算新大小
            float originalSize = (float) textView.getTag(R.id.tag_original_text_size);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, originalSize * scale);
        }
    }
    
    private void adjustButtonSize(View view, float scale) {
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                adjustButtonSize(viewGroup.getChildAt(i), scale);
            }
        } else if (view instanceof Button) {
            Button button = (Button) view;
            
            // 保存原始尺寸
            if (button.getTag(R.id.tag_original_layout_params) == null) {
                ViewGroup.LayoutParams originalParams = button.getLayoutParams();
                int[] originalDimensions = new int[] {originalParams.width, originalParams.height};
                button.setTag(R.id.tag_original_layout_params, originalDimensions);
                
                // 保存原始内边距
                int[] originalPadding = new int[] {
                    button.getPaddingLeft(),
                    button.getPaddingTop(),
                    button.getPaddingRight(),
                    button.getPaddingBottom()
                };
                button.setTag(R.id.tag_original_padding, originalPadding);
            }
            
            // 基于原始尺寸计算新尺寸
            ViewGroup.LayoutParams params = button.getLayoutParams();
            int[] originalDimensions = (int[]) button.getTag(R.id.tag_original_layout_params);
            
            if (originalDimensions[0] > 0 && originalDimensions[0] != ViewGroup.LayoutParams.MATCH_PARENT) {
                params.width = (int) (originalDimensions[0] * scale);
            }
            
            if (originalDimensions[1] > 0) {
                params.height = (int) (originalDimensions[1] * scale);
            }
            
            button.setLayoutParams(params);
            
            // 基于原始内边距计算新内边距
            int[] originalPadding = (int[]) button.getTag(R.id.tag_original_padding);
            button.setPadding(
                (int) (originalPadding[0] * scale),
                (int) (originalPadding[1] * scale),
                (int) (originalPadding[2] * scale),
                (int) (originalPadding[3] * scale)
            );
        }
    }

    private void checkPermissions() {
        List<String> permissionsToRequest = new ArrayList<>();
        for (String permission : requiredPermissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(permission);
            }
        }

        if (!permissionsToRequest.isEmpty()) {
            ActivityCompat.requestPermissions(this, 
                permissionsToRequest.toArray(new String[0]), 
                PERMISSIONS_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }
            
            if (!allGranted) {
                Toast.makeText(this, R.string.permission_explanation, Toast.LENGTH_LONG).show();
            }
        }
    }
    
    @Override
    public void onSettingsChanged(UserSettings settings) {
        // 先应用主题设置
        applyTheme(settings);
        
        // 重置所有视图尺寸再应用新设置
        resetViewSizes(getWindow().getDecorView());
        
        // 分别应用字体和按钮大小设置
        applyFontSize(settings);
        applyButtonSize(settings);
    }
}