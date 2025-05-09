package com.example.intelligent_dailer.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.example.intelligent_dailer.R;
import com.example.intelligent_dailer.database.DatabaseHelper;
import com.example.intelligent_dailer.database.UserSettings;

/**
 * 设置页面，管理用户个性化设置
 */
public class SettingsFragment extends Fragment {

    private DatabaseHelper databaseHelper;
    private Switch switchDarkTheme;
    private Switch switchFollowSystem;
    private RadioGroup radioGroupFontSize;
    private RadioButton radioBtnSmall;
    private RadioButton radioBtnMedium;
    private RadioButton radioBtnLarge;
    private SeekBar seekBarButtonSize;
    private TextView textButtonSizeValue;
    private Button btnApply;
    
    private UserSettings currentSettings;
    private boolean isInitializing = true;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseHelper = new DatabaseHelper(requireContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        initViews(view);
        loadSettings();
        return view;
    }

    private void initViews(View view) {
        switchDarkTheme = view.findViewById(R.id.switch_dark_theme);
        switchFollowSystem = view.findViewById(R.id.switch_follow_system);
        radioGroupFontSize = view.findViewById(R.id.radio_group_font_size);
        radioBtnSmall = view.findViewById(R.id.radio_btn_small);
        radioBtnMedium = view.findViewById(R.id.radio_btn_medium);
        radioBtnLarge = view.findViewById(R.id.radio_btn_large);
        seekBarButtonSize = view.findViewById(R.id.seekbar_button_size);
        textButtonSizeValue = view.findViewById(R.id.text_button_size_value);
        btnApply = view.findViewById(R.id.btn_apply_settings);
        
        switchFollowSystem.setOnCheckedChangeListener((buttonView, isChecked) -> {
            switchDarkTheme.setEnabled(!isChecked);
            if (!isInitializing) {
                databaseHelper.updateUseSystemTheme(isChecked);
            }
        });
        
        switchDarkTheme.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isInitializing && !switchFollowSystem.isChecked()) {
                databaseHelper.updateDarkTheme(isChecked);
            }
        });
        
        radioGroupFontSize.setOnCheckedChangeListener((group, checkedId) -> {
            if (!isInitializing) {
                String fontSize = UserSettings.FONT_SIZE_MEDIUM;
                if (checkedId == R.id.radio_btn_small) {
                    fontSize = UserSettings.FONT_SIZE_SMALL;
                } else if (checkedId == R.id.radio_btn_large) {
                    fontSize = UserSettings.FONT_SIZE_LARGE;
                }
                databaseHelper.updateFontSize(fontSize);
            }
        });
        
        seekBarButtonSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int buttonSize = progress + 80; // 从80%开始，最大120%
                textButtonSizeValue.setText(buttonSize + "%");
                if (!isInitializing && fromUser) {
                    databaseHelper.updateButtonSize(buttonSize);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        
        btnApply.setOnClickListener(v -> applySettings());
    }

    private void loadSettings() {
        isInitializing = true;
        databaseHelper.getUserSettings().observe(getViewLifecycleOwner(), settings -> {
            currentSettings = settings;
            
            // 更新UI以反映当前设置
            switchFollowSystem.setChecked(settings.isUseSystemTheme());
            switchDarkTheme.setChecked(settings.isDarkThemeEnabled());
            switchDarkTheme.setEnabled(!settings.isUseSystemTheme());
            
            String fontSize = settings.getFontSize();
            if (UserSettings.FONT_SIZE_SMALL.equals(fontSize)) {
                radioBtnSmall.setChecked(true);
            } else if (UserSettings.FONT_SIZE_LARGE.equals(fontSize)) {
                radioBtnLarge.setChecked(true);
            } else {
                radioBtnMedium.setChecked(true);
            }
            
            int buttonSize = settings.getButtonSize();
            seekBarButtonSize.setProgress(buttonSize - 80); // 换算回进度条值
            textButtonSizeValue.setText(buttonSize + "%");
            
            isInitializing = false;
        });
    }
    
    private void applySettings() {
        // 应用主题设置
        if (currentSettings != null) {
            if (currentSettings.isUseSystemTheme()) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
            } else if (currentSettings.isDarkThemeEnabled()) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
            
            // 通知Activity应用字体和按钮大小设置
            if (getActivity() instanceof SettingsChangeListener) {
                ((SettingsChangeListener) getActivity()).onSettingsChanged(currentSettings);
                Toast.makeText(getActivity(), "设置应用并保存成功", Toast.LENGTH_LONG).show();
            }
        }
    }
    
    /**
     * 设置变更监听器接口，由Activity实现
     */
    public interface SettingsChangeListener {
        void onSettingsChanged(UserSettings settings);
    }
} 