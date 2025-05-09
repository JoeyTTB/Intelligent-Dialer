package com.example.intelligent_dailer.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;

import com.example.intelligent_dailer.R;
import com.example.intelligent_dailer.database.DatabaseHelper;
import com.example.intelligent_dailer.database.FavoriteContact;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class ContactDetailActivity extends AppCompatActivity {

    private TextView initialTextView;
    private TextView nameTextView;
    private TextView phoneNumberTextView;
    private ImageView favoriteImageView;
    private FloatingActionButton callFab;
    private FloatingActionButton messageFab;
    
    private String contactId;
    private String contactName;
    private String contactNumber;
    private boolean isFavorite = false;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_detail);

        // 初始化数据库辅助类
        databaseHelper = new DatabaseHelper(this);

        // 设置标题栏
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("联系人详情");

        // 初始化视图
        initialTextView = findViewById(R.id.initialTextView);
        nameTextView = findViewById(R.id.nameTextView);
        phoneNumberTextView = findViewById(R.id.phoneNumberTextView);
        favoriteImageView = findViewById(R.id.favoriteImageView);
        callFab = findViewById(R.id.callFab);
        messageFab = findViewById(R.id.messageFab);
        
        // 获取传入的联系人信息
        Intent intent = getIntent();
        if (intent != null) {
            contactId = intent.getStringExtra("contact_id");
            contactName = intent.getStringExtra("contact_name");
            contactNumber = intent.getStringExtra("contact_number");
            
            // 显示联系人信息
            displayContactInfo();
            
            // 检查收藏状态
            checkFavoriteStatus();
            
            // 设置点击事件
            setupClickListeners();
        } else {
            // 无效数据，关闭页面
            finish();
        }
    }
    
    /**
     * 从数据库检查联系人是否收藏
     */
    private void checkFavoriteStatus() {
        // 从数据库查询收藏状态
        databaseHelper.getAllFavoriteContacts().observe(this, favoriteContacts -> {
            // 查找当前联系人是否在收藏列表中
            for (FavoriteContact favorite : favoriteContacts) {
                if (favorite.getContactId().equals(contactId)) {
                    isFavorite = true;
                    updateFavoriteIcon();
                    break;
                }
            }
        });
    }
    
    private void displayContactInfo() {
        // 设置联系人信息
        if (contactName != null && !contactName.isEmpty()) {
            nameTextView.setText(contactName);
            initialTextView.setText(contactName.substring(0, 1).toUpperCase());
        } else {
            nameTextView.setText("未知联系人");
            initialTextView.setText("?");
        }
        
        phoneNumberTextView.setText(contactNumber);
    }
    
    private void setupClickListeners() {
        // 拨打电话
        callFab.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + contactNumber));
                startActivity(intent);
            } else {
                Toast.makeText(this, R.string.permission_required, Toast.LENGTH_SHORT).show();
            }
        });
        
        // 发送短信
        messageFab.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("smsto:" + contactNumber));
            startActivity(intent);
        });
        
        // 收藏/取消收藏
        favoriteImageView.setOnClickListener(v -> {
            isFavorite = !isFavorite;
            updateFavoriteIcon();
            
            if (isFavorite) {
                // 添加到收藏
                databaseHelper.addFavoriteContact(contactId, contactName, contactNumber);
            } else {
                // 取消收藏
                databaseHelper.removeFavoriteContact(contactId);
            }
            
            String message = isFavorite ? 
                    getString(R.string.add_to_favorites) : 
                    getString(R.string.removed_from_favorites);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        });
    }
    
    private void updateFavoriteIcon() {
        if (isFavorite) {
            favoriteImageView.setImageResource(R.drawable.ic_favorite);
        } else {
            favoriteImageView.setImageResource(R.drawable.ic_favorite_border);
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