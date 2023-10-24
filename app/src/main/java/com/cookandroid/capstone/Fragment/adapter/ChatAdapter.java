package com.cookandroid.capstone.Fragment.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.cookandroid.capstone.Fragment.model.ChatDTO;
import com.cookandroid.capstone.R;
import com.cookandroid.capstone.constant.Constants;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class ChatAdapter extends ListAdapter<ChatDTO, RecyclerView.ViewHolder> {
    Context context;
    String myId;

    public ChatAdapter(String userId, @NonNull DiffUtil.ItemCallback<ChatDTO> diffCallback, Context context) {
        super(diffCallback);
        this.myId = userId;
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        if(myId.equals(getItem(position).getUser().getId())) {
            return Constants.VIEW_TYPE_CHAT_MINE;
        } else {
            return Constants.VIEW_TYPE_CHAT_OTHER;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder;
        if(viewType == Constants.VIEW_TYPE_CHAT_MINE) {
            holder = new ChatMineViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_mine, parent, false));
        } else {
            holder = new ChatOtherViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_other, parent, false));
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof ChatMineViewHolder) {
            ((ChatMineViewHolder)holder).bind(getItem(position));
        }
        else if(holder instanceof ChatOtherViewHolder) {
            ((ChatOtherViewHolder)holder).bind(getItem(position));
        }
    }

    static class ChatMineViewHolder extends RecyclerView.ViewHolder {
        private TextView tvChat;
        private TextView tvCreateAt;

        public ChatMineViewHolder(View view) {
            super(view);
            tvChat = view.findViewById(R.id.tv_chat);
            tvCreateAt = view.findViewById(R.id.tv_createdAt);
        }

        private void bind(ChatDTO chatDTO) {
            tvChat.setText(chatDTO.getChat());
            tvCreateAt.setText(convertTimestampToCustomFormat(chatDTO.getCreatedAt()));
        }
    }

    static class ChatOtherViewHolder extends RecyclerView.ViewHolder {
        private TextView tvChat;
        private TextView tvCreateAt;

        public ChatOtherViewHolder(View view) {
            super(view);
            tvChat = view.findViewById(R.id.tv_chat);
            tvCreateAt = view.findViewById(R.id.tv_createdAt);
        }

        private void bind(ChatDTO chatDTO) {
            tvChat.setText(chatDTO.getChat());
            tvCreateAt.setText(convertTimestampToCustomFormat(chatDTO.getCreatedAt()));
        }
    }

    public static String convertTimestampToCustomFormat(long timestamp) {
        Date date = new Date(timestamp);

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy HH:mm a", Locale.getDefault());

        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));

        String formattedDate = sdf.format(date);

        return formattedDate;
    }

    public static class ChatDiffUtil extends DiffUtil.ItemCallback<ChatDTO> {
        @Override
        public boolean areItemsTheSame(@NonNull ChatDTO oldItem, @NonNull ChatDTO newItem) {
            return oldItem.getChatId().equals(newItem.getChatId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull ChatDTO oldItem, @NonNull ChatDTO newItem) {
            return oldItem.equals(newItem);
        }
    }
}


