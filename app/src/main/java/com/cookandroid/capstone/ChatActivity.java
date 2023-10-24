package com.cookandroid.capstone;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.cookandroid.capstone.Fragment.adapter.ChatAdapter;
import com.cookandroid.capstone.Fragment.model.ChatDTO;
import com.cookandroid.capstone.Fragment.model.ChatRoomDTO;
import com.cookandroid.capstone.Fragment.model.UserDTO;
import com.cookandroid.capstone.databinding.ActivityChat2Binding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {
    private ActivityChat2Binding binding;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ChatAdapter chatAdapter;
    private ChatRoomDTO chatRoomDTO;
    private ArrayList<ChatDTO> chatList = new ArrayList<>();
    private String roomId;
    private UserDTO mine = new UserDTO();
    private UserDTO other = new UserDTO();

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        SharedPreferences pref = getSharedPreferences("pref", Context.MODE_PRIVATE);
        mine.setId(pref.getString("uid", ""));
        mine.setNickname(pref.getString("nickName", ""));
        mine.setAvatarUrl(pref.getString("photoUrl", ""));

        if(getIntent().getStringExtra("roomId") != null) {
            roomId = getIntent().getStringExtra("roomId");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChat2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        onNewIntent(getIntent());
        getChatRoom();
    }

    private void initListener() {

        // 뒤로가기
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Glide.with(this).load(other.getAvatarUrl()).into(binding.ivProfile);

        binding.tvNickname.setText(other.getNickname());

        binding.btnOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SelectBottomSheet().show(getSupportFragmentManager(), "");
            }
        });

        // 채팅 리스트
        chatAdapter = new ChatAdapter(mine.getId(), new ChatAdapter.ChatDiffUtil(), this);
        binding.rvChatList.setAdapter(chatAdapter);
        binding.rvChatList.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v,
                                       int left, int top, int right, int bottom,
                                       int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (bottom < oldBottom) {
                    binding.rvChatList.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            binding.rvChatList.scrollToPosition(binding.rvChatList.getAdapter().getItemCount() - 1);
                        }
                    }, 10);
                }
            }
        });

        // 채팅 보내기
        binding.btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String chat = binding.etChat.getText().toString();
                binding.etChat.setText("");
                if(chat.isEmpty()) {
                    return;
                }
                sendChat(chat);
            }
        });

        addObserver();
    }

    private void getChatRoom() {
        db.collection("ChatRoom")
                .document(roomId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            chatRoomDTO = task.getResult().toObject(ChatRoomDTO.class);

                            for (UserDTO user : chatRoomDTO.getUsers()) {
                                if (!user.getId().equals(mine.getId())) {
                                    other = user;
                                    break;
                                }
                            }

                            initListener();
                        }
                    }
                });
    }


    private void addObserver() {
        db.collection("ChatRoom")
                .document(roomId)
                .collection("Chat")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {

                            return;
                        }

                        for (DocumentChange dc : value.getDocumentChanges()) {
                            if (dc.getType() == DocumentChange.Type.ADDED) {
                                QueryDocumentSnapshot queryDocumentSnapshot = dc.getDocument();
                                ChatDTO chatDTO = queryDocumentSnapshot.toObject(ChatDTO.class);
                                chatList.add(chatDTO);
                            }
                            chatAdapter.submitList(new ArrayList<>(chatList));


                            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    binding.rvChatList.scrollToPosition(chatList.size() - 1);
                                }
                            }, 50);
                        }
                    }
                });
    }


    private void sendChat(String chat) {
        long timeStamp = System.currentTimeMillis();

        String chatId = String.valueOf(timeStamp);


        ChatDTO chatDTO = new ChatDTO();
        chatDTO.setCreatedAt(timeStamp);
        chatDTO.setChatId(chatId);
        chatDTO.setUser(mine);
        chatDTO.setChat(chat);

        db.collection("ChatRoom")
                .document(roomId)
                .collection("Chat")
                .document(chatId)
                .set(chatDTO)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            chatRoomDTO.setLastChat(chatDTO);
                            updateChatRoom(chatRoomDTO);
                        }
                    }
                });
    }

    private void updateChatRoom(ChatRoomDTO chatRoomDTO) {
        DocumentReference docRef = db.collection("ChatRoom").document(chatRoomDTO.getRoomId());

        Map<String, Object> updates = new HashMap<>();
        updates.put("lastChat", chatRoomDTO.getLastChat());

        docRef.update(updates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                        }
                    }
                });
    }
}