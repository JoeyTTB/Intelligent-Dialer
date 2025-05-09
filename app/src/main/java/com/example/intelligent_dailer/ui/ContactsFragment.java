package com.example.intelligent_dailer.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.intelligent_dailer.R;
import com.example.intelligent_dailer.adapters.ContactsAdapter;
import com.example.intelligent_dailer.database.DatabaseHelper;
import com.example.intelligent_dailer.database.FavoriteContact;
import com.example.intelligent_dailer.models.Contact;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContactsFragment extends Fragment implements ContactsAdapter.ContactClickListener {

    private RecyclerView contactsRecyclerView;
    private TextView emptyTextView;
    private EditText searchEditText;
    private ContactsAdapter adapter;
    private List<Contact> allContacts = new ArrayList<>();
    private List<Contact> filteredContacts = new ArrayList<>();
    private DatabaseHelper databaseHelper;
    private Map<String, Boolean> favoriteStatusMap = new HashMap<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);

        // 初始化数据库辅助类
        databaseHelper = new DatabaseHelper(requireContext());

        // 初始化视图
        contactsRecyclerView = view.findViewById(R.id.contactsRecyclerView);
        emptyTextView = view.findViewById(R.id.emptyTextView);
        searchEditText = view.findViewById(R.id.searchEditText);

        // 设置RecyclerView
        contactsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ContactsAdapter(filteredContacts, this);
        contactsRecyclerView.setAdapter(adapter);

        // 设置搜索监听
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                filterContacts(s.toString());
            }
        });

        // 加载收藏联系人状态
        loadFavoriteStatus();

        // 检查权限并加载联系人
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            loadContacts();
        } else {
            emptyTextView.setText(R.string.permission_required);
            emptyTextView.setVisibility(View.VISIBLE);
        }

        return view;
    }

    /**
     * 加载所有收藏联系人，并构建收藏状态映射
     */
    private void loadFavoriteStatus() {
        databaseHelper.getAllFavoriteContacts().observe(getViewLifecycleOwner(), favoriteContacts -> {
            favoriteStatusMap.clear();
            for (FavoriteContact favorite : favoriteContacts) {
                favoriteStatusMap.put(favorite.getContactId(), true);
            }
            
            // 更新已加载联系人的收藏状态
            if (!allContacts.isEmpty()) {
                updateContactsFavoriteStatus();
            }
        });
    }
    
    /**
     * 更新联系人的收藏状态
     */
    private void updateContactsFavoriteStatus() {
        for (Contact contact : allContacts) {
            contact.setFavorite(favoriteStatusMap.containsKey(contact.getId()));
        }
        adapter.notifyDataSetChanged();
    }

    private void loadContacts() {
        allContacts.clear();

        // 获取联系人内容提供者的Uri
        Uri uri = ContactsContract.Contacts.CONTENT_URI;
        
        // 查询列
        String[] projection = new String[]{
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.Contacts.HAS_PHONE_NUMBER
        };
        
        // 排序
        String sortOrder = ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC";
        
        // 使用ContentResolver查询联系人
        Cursor cursor = requireContext().getContentResolver().query(
                uri, projection, null, null, sortOrder);
        
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)));
                
                if (hasPhoneNumber > 0) {
                    // 查询该联系人的电话号码
                    Cursor phoneCursor = requireContext().getContentResolver().query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id},
                            null);
                    
                    if (phoneCursor != null) {
                        while (phoneCursor.moveToNext()) {
                            String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            
                            // 创建联系人对象，并设置收藏状态
                            Contact contact = new Contact(id, name, phoneNumber);
                            contact.setFavorite(favoriteStatusMap.containsKey(id));
                            
                            allContacts.add(contact);
                        }
                        phoneCursor.close();
                    }
                }
            }
            cursor.close();
        }

        filterContacts(searchEditText.getText().toString());
        // 更新UI
        updateUI();
    }
    
    private void filterContacts(String query) {
        filteredContacts.clear();
        
        if (query.isEmpty()) {
            filteredContacts.addAll(allContacts);
        } else {
            String lowerCaseQuery = query.toLowerCase();
            for (Contact contact : allContacts) {
                if (contact.getName().toLowerCase().contains(lowerCaseQuery) || 
                    contact.getPhoneNumber().contains(query)) {
                    filteredContacts.add(contact);
                }
            }
        }
        
        updateUI();
    }
    
    private void updateUI() {
        if (filteredContacts.isEmpty()) {
            emptyTextView.setVisibility(View.VISIBLE);
            contactsRecyclerView.setVisibility(View.GONE);
        } else {
            emptyTextView.setVisibility(View.GONE);
            contactsRecyclerView.setVisibility(View.VISIBLE);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            loadContacts();
        }
    }

    @Override
    public void onContactClick(Contact contact) {
        // 点击联系人打开详情
        Intent intent = new Intent(getActivity(), ContactDetailActivity.class);
        intent.putExtra("contact_id", contact.getId());
        intent.putExtra("contact_name", contact.getName());
        intent.putExtra("contact_number", contact.getPhoneNumber());
        startActivity(intent);
    }

    @Override
    public void onCallClick(Contact contact) {
        // 直接拨打联系人电话
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" + contact.getPhoneNumber()));
            startActivity(intent);
        } else {
            Toast.makeText(getContext(), R.string.permission_required, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onFavoriteClick(Contact contact) {
        // 收藏/取消收藏联系人
        boolean newFavoriteStatus = !contact.isFavorite();
        contact.setFavorite(newFavoriteStatus);
        
        // 更新数据库收藏状态
        if (newFavoriteStatus) {
            // 添加到收藏
            databaseHelper.addFavoriteContact(contact.getId(), contact.getName(), contact.getPhoneNumber());
        } else {
            // 取消收藏
            databaseHelper.removeFavoriteContact(contact.getId());
        }
        
        // 通知适配器更新UI
        adapter.notifyDataSetChanged();
        
        String message = newFavoriteStatus ? 
                getString(R.string.add_to_favorites) : 
                getString(R.string.removed_from_favorites);
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}