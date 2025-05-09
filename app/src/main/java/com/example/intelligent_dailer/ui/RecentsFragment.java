package com.example.intelligent_dailer.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog.Calls;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.intelligent_dailer.R;
import com.example.intelligent_dailer.adapters.CallLogAdapter;
import com.example.intelligent_dailer.database.CallRecord;
import com.example.intelligent_dailer.database.DatabaseHelper;
import com.example.intelligent_dailer.models.CallLog;

import java.util.ArrayList;
import java.util.List;

public class RecentsFragment extends Fragment implements CallLogAdapter.CallLogClickListener {

    private RecyclerView callLogsRecyclerView;
    private TextView emptyTextView;
    private CallLogAdapter adapter;
    private List<CallLog> callLogs = new ArrayList<>();
    private DatabaseHelper databaseHelper;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true); // 启用选项菜单
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 复用联系人布局，只是显示通话记录
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);

        // 初始化数据库辅助类
        databaseHelper = new DatabaseHelper(requireContext());

        // 初始化视图
        callLogsRecyclerView = view.findViewById(R.id.contactsRecyclerView);
        emptyTextView = view.findViewById(R.id.emptyTextView);
        emptyTextView.setText(R.string.no_recent_calls);
        
        // 隐藏搜索框
        view.findViewById(R.id.searchEditText).setVisibility(View.GONE);

        // 设置RecyclerView
        callLogsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new CallLogAdapter(callLogs, this);
        callLogsRecyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED) {
            databaseHelper.clearCallRecords();
            // 同步系统通话记录和数据库
            syncCallLogsFromSystem();
            // 从数据库加载通话记录
            loadCallLogsFromDatabase();
        } else {
            emptyTextView.setText(R.string.permission_required);
            emptyTextView.setVisibility(View.VISIBLE);
        }
    }

    private void loadCallLogsFromDatabase() {
        // 从数据库加载通话记录
        databaseHelper.getAllCallRecords().observe(getViewLifecycleOwner(), callRecords -> {
            callLogs.clear();
            
            // 将数据库中的CallRecord转换为UI使用的CallLog对象
            for (CallRecord record : callRecords) {
                CallLog callLog = new CallLog(
                        String.valueOf(record.getId()),
                        record.getName(),
                        record.getPhoneNumber(),
                        record.getTimestamp(),
                        record.getDuration(),
                        record.getType()
                );
                callLogs.add(callLog);
            }
            
            // 更新UI
            updateUI();
        });
    }
    
    /**
     * 同步系统通话记录到数据库
     */
    private void syncCallLogsFromSystem() {
        // 定义查询的列
        String[] projection = new String[]{
                Calls._ID,
                Calls.NUMBER,
                Calls.CACHED_NAME,
                Calls.DATE,
                Calls.DURATION,
                Calls.TYPE
        };

        // 按日期降序排序，最近的记录靠前
        String sortOrder = Calls.DATE + " DESC";  // 限制只获取最近20条记录

        // 查询系统通话记录
        Cursor cursor = requireContext().getContentResolver().query(
                Calls.CONTENT_URI,
                projection,
                null,
                null,
                sortOrder);

        if (cursor != null) {
            try {
                while (cursor.moveToNext()) {
                    String id = cursor.getString(cursor.getColumnIndex(Calls._ID));
                    String number = cursor.getString(cursor.getColumnIndex(Calls.NUMBER));
                    String name = cursor.getString(cursor.getColumnIndex(Calls.CACHED_NAME));
                    long date = cursor.getLong(cursor.getColumnIndex(Calls.DATE));
                    int duration = cursor.getInt(cursor.getColumnIndex(Calls.DURATION));
                    int type = cursor.getInt(cursor.getColumnIndex(Calls.TYPE));

                    // 将系统通话记录保存到数据库
                    databaseHelper.addCallRecord(id, name, number, date, duration, type);
                }
            } finally {
                cursor.close();
            }
        }
    }
    
    private void updateUI() {
        if (callLogs.isEmpty()) {
            emptyTextView.setVisibility(View.VISIBLE);
            callLogsRecyclerView.setVisibility(View.GONE);
        } else {
            emptyTextView.setVisibility(View.GONE);
            callLogsRecyclerView.setVisibility(View.VISIBLE);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        // 添加清空通话记录菜单
        menu.add(Menu.NONE, 1, Menu.NONE, "清空通话记录")
            .setIcon(android.R.drawable.ic_menu_delete)
            .setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == 1) {
            // 清空通话记录
            databaseHelper.clearCallRecords();
            Toast.makeText(getContext(), "通话记录已清空", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCallLogClick(CallLog callLog) {
        // 点击通话记录项查看详情
        // 由于是简单实现，我们直接调用拨号
        onCallClick(callLog);
    }

    @Override
    public void onCallClick(CallLog callLog) {
        // 拨打电话
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" + callLog.getPhoneNumber()));
            startActivity(intent);
        } else {
            Toast.makeText(getContext(), R.string.permission_required, Toast.LENGTH_SHORT).show();
        }
    }
} 