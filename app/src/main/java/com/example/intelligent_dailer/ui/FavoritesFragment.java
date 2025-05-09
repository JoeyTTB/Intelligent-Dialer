package com.example.intelligent_dailer.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.intelligent_dailer.R;
import com.example.intelligent_dailer.adapters.ContactsAdapter;
import com.example.intelligent_dailer.database.DatabaseHelper;
import com.example.intelligent_dailer.database.FavoriteContact;
import com.example.intelligent_dailer.models.Contact;

import java.util.ArrayList;
import java.util.List;

public class FavoritesFragment extends Fragment implements ContactsAdapter.ContactClickListener {

    private RecyclerView favoritesRecyclerView;
    private TextView emptyTextView;
    private ContactsAdapter adapter;
    private List<Contact> favoriteContacts = new ArrayList<>();
    private DatabaseHelper databaseHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 复用联系人布局，只是显示收藏的联系人
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);

        // 初始化数据库辅助类
        databaseHelper = new DatabaseHelper(requireContext());

        // 初始化视图
        favoritesRecyclerView = view.findViewById(R.id.contactsRecyclerView);
        emptyTextView = view.findViewById(R.id.emptyTextView);
        emptyTextView.setText(R.string.no_favorites);
        
        // 隐藏搜索框，因为收藏联系人一般不多
        view.findViewById(R.id.searchEditText).setVisibility(View.GONE);

        // 设置RecyclerView
        favoritesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ContactsAdapter(favoriteContacts, this);
        favoritesRecyclerView.setAdapter(adapter);

        // 加载收藏联系人
        loadFavoriteContacts();

        return view;
    }

    private void loadFavoriteContacts() {
        // 从数据库加载收藏联系人
        databaseHelper.getAllFavoriteContacts().observe(getViewLifecycleOwner(), favoriteContactList -> {
            favoriteContacts.clear();
            
            // 将数据库中的FavoriteContact转换为UI使用的Contact对象
            for (FavoriteContact favoriteContact : favoriteContactList) {
                Contact contact = new Contact(
                        favoriteContact.getContactId(),
                        favoriteContact.getName(),
                        favoriteContact.getPhoneNumber(),
                        true
                );
                favoriteContacts.add(contact);
            }
            
            // 更新UI
            updateUI();
        });
    }
    
    private void updateUI() {
        if (favoriteContacts.isEmpty()) {
            emptyTextView.setVisibility(View.VISIBLE);
            favoritesRecyclerView.setVisibility(View.GONE);
        } else {
            emptyTextView.setVisibility(View.GONE);
            favoritesRecyclerView.setVisibility(View.VISIBLE);
            adapter.notifyDataSetChanged();
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
        // 从收藏列表中移除
        contact.setFavorite(false);
        
        // 更新数据库
        databaseHelper.removeFavoriteContact(contact.getId());
        
        Toast.makeText(getContext(), R.string.removed_from_favorites, Toast.LENGTH_SHORT).show();
        
        // LiveData会自动通知UI更新，但为了更好的用户体验，可以立即从列表移除
        favoriteContacts.remove(contact);
        adapter.notifyDataSetChanged();
        updateUI();
    }
} 