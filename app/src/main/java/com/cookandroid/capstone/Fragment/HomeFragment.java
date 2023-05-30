package com.cookandroid.capstone.Fragment;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.cookandroid.capstone.CheckListActivity;
import com.cookandroid.capstone.HelpActivity;
import com.cookandroid.capstone.MainActivity;
import com.cookandroid.capstone.R;
import com.cookandroid.capstone.WorkDataActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class HomeFragment extends Fragment {

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();


    TextView textView_checklistadd;
    ListView listView_todo;
    ArrayList<String> arrayList = new ArrayList<>();
    ArrayAdapter<String> adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_home, container, false);

        ImageView fragHelp = view.findViewById(R.id.frag_help);
        Button btnAdd = view.findViewById(R.id.btnAdd);
        textView_checklistadd = view.findViewById(R.id.btnChecklistAdd);
        listView_todo = view.findViewById(R.id.lvWork);

        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1,
                arrayList);
        listView_todo.setAdapter(adapter);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("Todo");

        getValue();


        fragHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 클릭 이벤트 처리
                Intent intent = new Intent(getActivity(), HelpActivity.class);
                startActivity(intent);
            }
        });

        //근무 추가 버튼
       btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 클릭 이벤트 처리
                Intent intent = new Intent(getActivity(), WorkDataActivity.class);
                startActivity(intent);
            }
        });

        //체크리스트 메모추가 버튼
        textView_checklistadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),
                        CheckListActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }
    private void getValue() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrayList.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String sValue = dataSnapshot.child("work").getValue(String.class);
                    arrayList.add(sValue);
                }
                listView_todo.setAdapter(adapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}