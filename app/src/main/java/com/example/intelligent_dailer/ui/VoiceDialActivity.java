package com.example.intelligent_dailer.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.intelligent_dailer.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VoiceDialActivity extends AppCompatActivity {

    private TextView instructionTextView;
    private TextView resultTextView;
    private FloatingActionButton micButton;
    private Button dialButton;
    private Button retryButton;
    private ImageView micWaveImageView;
    
    private SpeechRecognizer speechRecognizer;
    private String phoneNumber = "";
    private static final int REQUEST_RECORD_AUDIO = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_dial);

        // 设置标题栏
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("语音拨号");

        // 初始化视图
        instructionTextView = findViewById(R.id.instructionTextView);
        resultTextView = findViewById(R.id.resultTextView);
        micButton = findViewById(R.id.micButton);
        dialButton = findViewById(R.id.dialButton);
        retryButton = findViewById(R.id.retryButton);
        micWaveImageView = findViewById(R.id.micWaveImageView);
        
        // 初始化语音识别器
        initSpeechRecognizer();
        
        // 设置点击事件
        setupClickListeners();
        
        // 检查权限
        checkPermission();
    }
    
    private void initSpeechRecognizer() {
        if (SpeechRecognizer.isRecognitionAvailable(this)) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
            speechRecognizer.setRecognitionListener(new RecognitionListener() {
                @Override
                public void onReadyForSpeech(Bundle params) {
                    instructionTextView.setText(R.string.voice_listening);
                    micWaveImageView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onBeginningOfSpeech() {}

                @Override
                public void onRmsChanged(float rmsdB) {}

                @Override
                public void onBufferReceived(byte[] buffer) {}

                @Override
                public void onEndOfSpeech() {
                    instructionTextView.setText(R.string.voice_processing);
                    micWaveImageView.setVisibility(View.INVISIBLE);
                    micButton.setEnabled(true);
                }

                @Override
                public void onError(int error) {
                    instructionTextView.setText(R.string.voice_error);
                    micButton.setEnabled(true);
                    micWaveImageView.setVisibility(View.INVISIBLE);
                    String errorMessage;
                    switch (error) {
                        case SpeechRecognizer.ERROR_AUDIO:
                            errorMessage = "音频错误";
                            break;
                        case SpeechRecognizer.ERROR_CLIENT:
                            errorMessage = "客户端错误";
                            break;
                        case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                            errorMessage = "权限不足";
                            break;
                        case SpeechRecognizer.ERROR_NETWORK:
                            errorMessage = "网络错误";
                            break;
                        case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                            errorMessage = "网络超时";
                            break;
                        case SpeechRecognizer.ERROR_NO_MATCH:
                            errorMessage = "无法识别内容";
                            break;
                        case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                            errorMessage = "识别服务忙";
                            break;
                        case SpeechRecognizer.ERROR_SERVER:
                            errorMessage = "服务器错误";
                            break;
                        case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                            errorMessage = "未检测到语音";
                            break;
                        default:
                            errorMessage = "未知错误";
                    }

                    instructionTextView.setText("错误：" + errorMessage);
                    micButton.setEnabled(true);
                    micWaveImageView.setVisibility(View.INVISIBLE);
                    Log.e("VoiceDial", "语音识别错误: " + errorMessage);
                }

                @Override
                public void onResults(Bundle results) {
                    ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                    if (matches != null && !matches.isEmpty()) {
                        String recognizedText = matches.get(0);
                        resultTextView.setText(recognizedText);
                        extractPhoneNumber(recognizedText);
                    }
                    micButton.setEnabled(true);
                }

                @Override
                public void onPartialResults(Bundle partialResults) {}

                @Override
                public void onEvent(int eventType, Bundle params) {}
            });
        } else {
            Toast.makeText(this, "设备不支持语音识别", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
    
    private void setupClickListeners() {
        // 语音识别按钮
        micButton.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                startSpeechRecognition();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_AUDIO);
            }
        });
        
        // 拨号按钮
        dialButton.setOnClickListener(v -> {
            if (phoneNumber.isEmpty()) {
                Toast.makeText(this, "请先说出电话号码", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // 拨打电话
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + phoneNumber));
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, R.string.permission_required, Toast.LENGTH_SHORT).show();
            }
        });
        
        // 重试按钮
        retryButton.setOnClickListener(v -> {
            phoneNumber = "";
            resultTextView.setText("");
            dialButton.setVisibility(View.INVISIBLE);
            retryButton.setVisibility(View.INVISIBLE);
            instructionTextView.setText(R.string.voice_instruction);
        });
    }
    
    private void startSpeechRecognition() {
        // 停止正在进行的识别
        speechRecognizer.cancel();
        
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
        
        micButton.setEnabled(false);
        speechRecognizer.startListening(intent);
    }
    
    private void extractPhoneNumber(String text) {
        // 尝试提取电话号码
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(text);
        StringBuilder builder = new StringBuilder();
        
        while (matcher.find()) {
            builder.append(matcher.group());
        }
        
        // 如果提取到号码，显示拨号和重试按钮
        if (builder.length() >= 7) {
            phoneNumber = builder.toString();
            dialButton.setVisibility(View.VISIBLE);
            retryButton.setVisibility(View.VISIBLE);
        } else {
            instructionTextView.setText(R.string.voice_error);
            retryButton.setVisibility(View.VISIBLE);
        }
    }
    
    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_AUDIO);
        }
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_RECORD_AUDIO) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "语音权限已授予", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "没有录音权限，无法使用语音拨号", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (speechRecognizer != null) {
            speechRecognizer.destroy();
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