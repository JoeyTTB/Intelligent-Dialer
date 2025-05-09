package com.example.intelligent_dailer.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.intelligent_dailer.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class DialerFragment extends Fragment implements View.OnClickListener {

    private EditText phoneNumberEditText;
    private ImageButton deleteButton;
    private ImageButton voiceDialButton;
    private ImageButton scanDialButton;
    private FloatingActionButton callButton;
    
    private View[] numberButtons = new View[12];
    private String[] dialpadLetters = {
            "", "ABC", "DEF", "GHI", "JKL", "MNO", "PQRS", "TUV", "WXYZ", "*", "+", "#"
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dialer, container, false);
        
        // 初始化视图
        phoneNumberEditText = view.findViewById(R.id.phoneNumberEditText);
        deleteButton = view.findViewById(R.id.deleteButton);
        callButton = view.findViewById(R.id.callButton);
        voiceDialButton = view.findViewById(R.id.voiceDialButton);
        scanDialButton = view.findViewById(R.id.scanDialButton);
        
        // 初始化数字按钮
        initDialpadButtons(view);
        
        // 设置点击事件
        deleteButton.setOnClickListener(this);
        callButton.setOnClickListener(this);
        voiceDialButton.setOnClickListener(this);
        scanDialButton.setOnClickListener(this);
        
        // 长按删除按钮清空输入
        deleteButton.setOnLongClickListener(v -> {
            phoneNumberEditText.setText("");
            return true;
        });
        
        // 监听电话号码变化
        phoneNumberEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                // 电话号码有内容时显示删除按钮，否则隐藏
                deleteButton.setVisibility(s.length() > 0 ? View.VISIBLE : View.INVISIBLE);
            }
        });
        
        return view;
    }
    
    private void initDialpadButtons(View view) {
        numberButtons[0] = view.findViewById(R.id.button1);
        numberButtons[1] = view.findViewById(R.id.button2);
        numberButtons[2] = view.findViewById(R.id.button3);
        numberButtons[3] = view.findViewById(R.id.button4);
        numberButtons[4] = view.findViewById(R.id.button5);
        numberButtons[5] = view.findViewById(R.id.button6);
        numberButtons[6] = view.findViewById(R.id.button7);
        numberButtons[7] = view.findViewById(R.id.button8);
        numberButtons[8] = view.findViewById(R.id.button9);
        numberButtons[9] = view.findViewById(R.id.buttonStar);
        numberButtons[10] = view.findViewById(R.id.button0);
        numberButtons[11] = view.findViewById(R.id.buttonHash);
        
        // 设置按钮文本和点击事件
        for (int i = 0; i < numberButtons.length; i++) {
            View button = numberButtons[i];
            
            // 设置数字
            TextView numberTextView = button.findViewById(R.id.dialpad_number);
            if (i == 9) {
                numberTextView.setText("*");
            } else if (i == 10) {
                numberTextView.setText("0");
            } else if (i == 11) {
                numberTextView.setText("#");
            } else {
                numberTextView.setText(String.valueOf(i + 1));
            }
            
            // 设置字母
            TextView lettersTextView = button.findViewById(R.id.dialpad_letters);
            lettersTextView.setText(dialpadLetters[i]);
            
            // 设置点击事件
            final int index = i;
            button.setOnClickListener(v -> {
                String digit;
                if (index == 9) {
                    digit = "*";
                } else if (index == 10) {
                    digit = "0";
                } else if (index == 11) {
                    digit = "#";
                } else {
                    digit = String.valueOf(index + 1);
                }
                appendDigit(digit);
            });
            
            // 设置0键长按事件为添加+号
            if (i == 10) {
                button.setOnLongClickListener(v -> {
                    appendDigit("+");
                    return true;
                });
            }
        }
    }
    
    private void appendDigit(String digit) {
        int cursorPosition = phoneNumberEditText.getSelectionStart();
        phoneNumberEditText.getText().insert(cursorPosition, digit);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        
        if (id == R.id.deleteButton) {
            // 删除一个字符
            Editable text = phoneNumberEditText.getText();
            int length = text.length();
            if (length > 0) {
                int cursorPosition = phoneNumberEditText.getSelectionStart();
                if (cursorPosition > 0) {
                    text.delete(cursorPosition - 1, cursorPosition);
                }
            }
        } else if (id == R.id.callButton) {
            // 拨打电话
            makePhoneCall();
        } else if (id == R.id.voiceDialButton) {
            // 打开语音拨号界面
            startActivity(new Intent(getActivity(), VoiceDialActivity.class));
        } else if (id == R.id.scanDialButton) {
            // 打开扫码拨号界面
            startActivity(new Intent(getActivity(), ScanActivity.class));
        }
    }
    
    private void makePhoneCall() {
        String phoneNumber = phoneNumberEditText.getText().toString().trim();
        if (phoneNumber.isEmpty()) {
            Toast.makeText(getActivity(), "请输入电话号码", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // 检查拨号权限
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" + phoneNumber));
            startActivity(intent);
        } else {
            Toast.makeText(getActivity(), R.string.permission_required, Toast.LENGTH_SHORT).show();
        }
    }
} 