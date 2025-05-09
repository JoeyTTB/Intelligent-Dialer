package com.example.intelligent_dailer.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.intelligent_dailer.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScanActivity extends AppCompatActivity implements DecoratedBarcodeView.TorchListener {

    private DecoratedBarcodeView barcodeView;
    private TextView instructionTextView;
    private Button flashlightButton;
    private Button cancelButton;
    
    private boolean isTorchOn = false;
    private static final int REQUEST_CAMERA = 102;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        // 设置标题栏
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("扫码拨号");

        // 初始化视图
        barcodeView = findViewById(R.id.barcodeView);
        instructionTextView = findViewById(R.id.instructionTextView);
        flashlightButton = findViewById(R.id.flashlightButton);
        cancelButton = findViewById(R.id.cancelButton);
        
        // 设置扫描框
        barcodeView.setTorchListener(this);
        
        // 设置按钮点击事件
        flashlightButton.setOnClickListener(v -> {
            if (isTorchOn) {
                barcodeView.setTorchOff();
            } else {
                barcodeView.setTorchOn();
            }
        });
        
        cancelButton.setOnClickListener(v -> finish());

        // 检查权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            initializeScanner();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
        }
    }
    
    private void initializeScanner() {
        barcodeView.decodeSingle(new BarcodeCallback() {
            @Override
            public void barcodeResult(BarcodeResult result) {
                if (result.getText() != null) {
                    // 扫描结果处理
                    handleScanResult(result.getText());
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        barcodeView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        barcodeView.pause();
    }

    @Override
    public void onTorchOn() {
        isTorchOn = true;
        flashlightButton.setText("关闭闪光灯");
    }

    @Override
    public void onTorchOff() {
        isTorchOn = false;
        flashlightButton.setText("打开闪光灯");
    }

    /**
     * 处理扫描结果
     */
    private void handleScanResult(String scannedText) {
        // 尝试从扫描内容中提取电话号码
        String phoneNumber = extractPhoneNumber(scannedText);
        
        if (phoneNumber != null) {
            // 拨打电话
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + phoneNumber));
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, R.string.permission_required, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "未找到有效的电话号码，请重新扫描", Toast.LENGTH_SHORT).show();
            // 继续扫描
            barcodeView.decodeSingle(result -> {
                if (result.getText() != null) {
                    handleScanResult(result.getText());
                }
            });
        }
    }
    
    /**
     * 从文本中提取电话号码
     */
    private String extractPhoneNumber(String text) {
        // 匹配电话号码的正则表达式（简单版本）
        Pattern pattern = Pattern.compile("\\d{7,13}");
        Matcher matcher = pattern.matcher(text);
        
        if (matcher.find()) {
            return matcher.group();
        }
        
        return null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 权限已授予，初始化扫描器
                initializeScanner();
            } else {
                // 没有相机权限
                Toast.makeText(this, R.string.scan_permission_required, Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
} 