package com.example.intelligent_dailer.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.intelligent_dailer.R;
import com.example.intelligent_dailer.models.Contact;

import java.util.List;
import java.util.Random;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ContactViewHolder> {

    private final List<Contact> contacts;
    private final ContactClickListener listener;
    private final Random random = new Random();

    public ContactsAdapter(List<Contact> contacts, ContactClickListener listener) {
        this.contacts = contacts;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        Contact contact = contacts.get(position);
        holder.bind(contact);
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public class ContactViewHolder extends RecyclerView.ViewHolder {
        private final TextView initialTextView;
        private final TextView nameTextView;
        private final TextView numberTextView;
        private final ImageView favoriteImageView;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            initialTextView = itemView.findViewById(R.id.contactInitialTextView);
            nameTextView = itemView.findViewById(R.id.contactNameTextView);
            numberTextView = itemView.findViewById(R.id.contactNumberTextView);
            favoriteImageView = itemView.findViewById(R.id.favoriteImageView);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onContactClick(contacts.get(position));
                }
            });

            itemView.setOnLongClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onCallClick(contacts.get(position));
                    return true;
                }
                return false;
            });

            favoriteImageView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onFavoriteClick(contacts.get(position));
                }
            });
        }

        public void bind(Contact contact) {
            nameTextView.setText(contact.getName());
            numberTextView.setText(contact.getPhoneNumber());
            initialTextView.setText(contact.getInitial());
            
            // 设置初始字母背景颜色（随机生成一个暗色背景）
            int color = generateColor();
            initialTextView.setBackgroundColor(color);
            
            // 设置收藏图标
            if (contact.isFavorite()) {
                favoriteImageView.setImageResource(R.drawable.ic_favorite);
            } else {
                favoriteImageView.setImageResource(R.drawable.ic_favorite_border);
            }
        }
        
        private int generateColor() {
            // 生成一个随机的暗色背景（保证文字可见），保持相同名称有相同颜色
            float[] hsv = new float[3];
            hsv[0] = random.nextInt(360); // 色相，随机选取一个色相值
            hsv[1] = 0.8f; // 饱和度，设置为80%以确保颜色鲜明
            hsv[2] = 0.6f; // 明度，设置为60%确保足够深色但文字仍可见
            return Color.HSVToColor(hsv);
        }
    }

    public interface ContactClickListener {
        void onContactClick(Contact contact);
        void onCallClick(Contact contact);
        void onFavoriteClick(Contact contact);
    }
}