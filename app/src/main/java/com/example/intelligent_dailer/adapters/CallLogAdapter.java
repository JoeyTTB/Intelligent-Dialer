package com.example.intelligent_dailer.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.intelligent_dailer.R;
import com.example.intelligent_dailer.models.CallLog;

import java.util.List;
import java.util.Random;

public class CallLogAdapter extends RecyclerView.Adapter<CallLogAdapter.CallLogViewHolder> {

    private final List<CallLog> callLogs;
    private final CallLogClickListener listener;
    private final Random random = new Random();

    public CallLogAdapter(List<CallLog> callLogs, CallLogClickListener listener) {
        this.callLogs = callLogs;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CallLogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_call_log, parent, false);
        return new CallLogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CallLogViewHolder holder, int position) {
        CallLog callLog = callLogs.get(position);
        holder.bind(callLog);
    }

    @Override
    public int getItemCount() {
        return callLogs.size();
    }

    public class CallLogViewHolder extends RecyclerView.ViewHolder {
        private final TextView initialTextView;
        private final TextView nameTextView;
        private final TextView phoneNumberTextView;
        private final TextView durationTextView;
        private final TextView dateTextView;
        private final ImageView callTypeIcon;
        private final ImageButton callButton;

        public CallLogViewHolder(@NonNull View itemView) {
            super(itemView);
            initialTextView = itemView.findViewById(R.id.initialTextView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            phoneNumberTextView = itemView.findViewById(R.id.phoneNumberTextView);
            durationTextView = itemView.findViewById(R.id.durationTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            callTypeIcon = itemView.findViewById(R.id.callTypeIcon);
            callButton = itemView.findViewById(R.id.callButton);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onCallLogClick(callLogs.get(position));
                }
            });

            callButton.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onCallClick(callLogs.get(position));
                }
            });
        }

        public void bind(CallLog callLog) {
            // 设置名称，没有名称则显示电话号码
            String displayName = callLog.getName();
            if (displayName == null || displayName.isEmpty()) {
                displayName = callLog.getPhoneNumber();
                nameTextView.setVisibility(View.GONE);
            } else {
                nameTextView.setVisibility(View.VISIBLE);
                nameTextView.setText(displayName);
            }
            
            // 设置电话号码
            phoneNumberTextView.setText(callLog.getPhoneNumber());
            
            // 设置通话时长和日期
            durationTextView.setText(callLog.getFormattedDuration());
            dateTextView.setText(callLog.getFormattedDate());
            
            // 设置头像初始字母
            initialTextView.setText(callLog.getInitial());
            
            // 设置颜色（随机颜色）
            initialTextView.setBackgroundColor(generateColor());
            
            // 设置通话类型图标
            switch (callLog.getType()) {
                case CallLog.CALL_TYPE_INCOMING:
                    callTypeIcon.setImageResource(R.drawable.ic_call_incoming);
                    break;
                case CallLog.CALL_TYPE_OUTGOING:
                    callTypeIcon.setImageResource(R.drawable.ic_call_outgoing);
                    break;
                case CallLog.CALL_TYPE_MISSED:
                case CallLog.CALL_TYPE_REJECTED:
                    callTypeIcon.setImageResource(R.drawable.ic_call_missed);
                    break;
            }
        }
        
        private int generateColor() {
            // 生成一个随机的暗色背景
            float[] hsv = new float[3];
            hsv[0] = random.nextInt(360);
            hsv[1] = 0.8f;
            hsv[2] = 0.6f;
            return Color.HSVToColor(hsv);
        }
    }

    public interface CallLogClickListener {
        void onCallLogClick(CallLog callLog);
        void onCallClick(CallLog callLog);
    }
} 