package com.cookandroid.capstone.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.cookandroid.capstone.ChatActivity;
import com.cookandroid.capstone.Fragment.adapter.ChatListAdapter;
import com.cookandroid.capstone.Fragment.callback.IChatListAdapter;
import com.cookandroid.capstone.Fragment.model.ChatRoomDTO;
import com.cookandroid.capstone.Fragment.model.UserDTO;
import com.cookandroid.capstone.databinding.ActivityChatlistBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ChatFragment extends Fragment {

    private ActivityChatlistBinding binding;
    private ChatListAdapter adapter;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private UserDTO mine = new UserDTO();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = ActivityChatlistBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SharedPreferences pref = requireContext().getSharedPreferences("pref", Context.MODE_PRIVATE);
        mine.setId(pref.getString("uid", ""));

        adapter = new ChatListAdapter(new ChatListAdapter.ChatListDiffUtil());
        adapter.setListener(new IChatListAdapter() {
            @Override
            public void onClickListener(String roomId) {
                Intent intent = new Intent(requireContext(), ChatActivity.class);
                intent.putExtra("roomId", roomId);
                startActivity(intent);
            }
        });
        adapter.setMyId(mine.getId());
        binding.rvChatRoomList.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        getChatList();
    }


    private void getChatList() {
        db.collection("ChatRoom")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<ChatRoomDTO> chatRoomList = new ArrayList<>();

                            for (QueryDocumentSnapshot document : task.getResult()) {

                                ChatRoomDTO chatRoomDTO = document.toObject(ChatRoomDTO.class);
                                ArrayList<UserDTO> users = chatRoomDTO.getUsers();

                                for (UserDTO user : users) {
                                    if (user.getId().contains(mine.getId())) {
                                        chatRoomList.add(chatRoomDTO);
                                    }
                                }
                            }

                            adapter.submitList(chatRoomList);
                        }
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}