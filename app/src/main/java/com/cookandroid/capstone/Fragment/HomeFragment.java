package com.cookandroid.capstone.Fragment;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;


import com.cookandroid.capstone.CheckListActivity;
import com.cookandroid.capstone.HelpActivity;
import com.cookandroid.capstone.R;
import com.cookandroid.capstone.WorkDataActivity;
import com.cookandroid.capstone.WorkDetailActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    ArrayList<String> arrayList;
    ArrayAdapter<String> adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_home, container, false);

        ImageView fragHelp = view.findViewById(R.id.frag_help);
        Button btnAdd = view.findViewById(R.id.btnAdd);
        TextView textView_checklistadd = view.findViewById(R.id.btnChecklistAdd);
        ListView listView_todo = view.findViewById(R.id.lvWork);
        ScrollView scrollView = view.findViewById(R.id.scrollView);
        TextView tv_workdetail_1 = view.findViewById(R.id.tv_workdetail_1);
        TextView tv_workdetail_2 = view.findViewById(R.id.tv_workdetail_2);


        arrayList = new ArrayList<>();
        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, arrayList);
        listView_todo.setAdapter(adapter);


        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("Todo");

        getValue();

        listView_todo.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP){
                    scrollView.requestDisallowInterceptTouchEvent(false);
                } else {
                    scrollView.requestDisallowInterceptTouchEvent(true);
                }
                return false;
            }
        });



        fragHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), HelpActivity.class);
                startActivity(intent);
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), WorkDataActivity.class);
                startActivity(intent);
            }
        });


        textView_checklistadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CheckListActivity.class);
                startActivity(intent);
            }
        });

        tv_workdetail_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), WorkDetailActivity.class);
                startActivity(intent);
            }
        });

        tv_workdetail_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), WorkDetailActivity.class);
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
                    if (sValue != null) {
                        arrayList.add(sValue);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // 오류 처리
            }
        });
    }
}