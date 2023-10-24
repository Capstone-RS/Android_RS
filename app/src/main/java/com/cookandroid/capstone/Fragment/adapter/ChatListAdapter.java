package com.cookandroid.capstone.Fragment.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cookandroid.capstone.Fragment.callback.IChatListAdapter;
import com.cookandroid.capstone.Fragment.model.ChatRoomDTO;
import com.cookandroid.capstone.Fragment.model.UserDTO;
import com.cookandroid.capstone.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class ChatListAdapter extends ListAdapter<ChatRoomDTO, ChatListAdapter.MyViewHolder> {
    private IChatListAdapter listener;
    private String myId;

    public void setListener(IChatListAdapter listener) {
        this.listener = listener;
    }

    public void setMyId(String userId) {
        this.myId = userId;
    }

    public ChatListAdapter(@NonNull DiffUtil.ItemCallback<ChatRoomDTO> diffCallback) {
        super(diffCallback);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        MyViewHolder holder = new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_room, parent, false));
        holder.setListener(listener);
        holder.setMyId(myId);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivProfile;
        private TextView tvNickname;
        private TextView tvChat;
        private TextView tvLastChatAt;
        private String myId;
        private IChatListAdapter listener;

        public void setListener(IChatListAdapter listener) {
            this.listener = listener;
        }

        public void setMyId(String userId) {
            this.myId = userId;
        }

        public MyViewHolder(View view) {
            super(view);
            ivProfile = view.findViewById(R.id.iv_profile);
            tvNickname = view.findViewById(R.id.tv_nickname);
            tvChat = view.findViewById(R.id.tv_chat);
            tvLastChatAt = view.findViewById(R.id.tv_lastChatAt);
        }

        private void bind(ChatRoomDTO chatRoomDTO) {
            ArrayList<UserDTO> users = chatRoomDTO.getUsers();
            UserDTO other;

            if(users.get(0).getId().equals(myId)) {
                other = users.get(1);
            } else {
                other = users.get(0);
            }

            Glide.with(itemView.getContext()).load(other.getAvatarUrl()).into(ivProfile);
            tvNickname.setText(other.getNickname());
            tvLastChatAt.setText(convertTimestampToCustomFormat(chatRoomDTO.getLastChat().getCreatedAt()));
            tvChat.setText(chatRoomDTO.getLastChat().getChat());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onClickListener(chatRoomDTO.getRoomId());
                }
            });
        }
    }

    public static String convertTimestampToCustomFormat(long timestamp) {
        Date date = new Date(timestamp);

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy HH:mm a", Locale.getDefault());

        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));

        String formattedDate = sdf.format(date);

        return formattedDate;
    }

    public static class ChatListDiffUtil extends DiffUtil.ItemCallback<ChatRoomDTO> {
        @Override
        public boolean areItemsTheSame(@NonNull ChatRoomDTO oldItem, @NonNull ChatRoomDTO newItem) {
            return oldItem.getRoomId().equals(newItem.getRoomId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull ChatRoomDTO oldItem, @NonNull ChatRoomDTO newItem) {
            return oldItem.equals(newItem);
        }
    }
}


