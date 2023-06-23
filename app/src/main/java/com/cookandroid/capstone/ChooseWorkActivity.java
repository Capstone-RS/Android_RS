package com.cookandroid.capstone;


import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class ChooseWorkActivity extends AppCompatActivity {

    private ListView workList;
    private List<String> workNames;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choosework);

        workList = findViewById(R.id.workList);

        // workNames 리스트 초기화
        workNames = new ArrayList<>();

        // Adapter 초기화
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, workNames);

        // ListView와 Adapter 연결
        workList.setAdapter(adapter);

        // Firebase에서 데이터 가져오기
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("Data");
        Query query = databaseRef.orderByChild("name");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // 데이터 변경 시 호출되는 메소드

                // 기존 데이터 초기화
                workNames.clear();

                // 데이터 순회
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // name 데이터 가져와서 리스트에 추가
                    String name = snapshot.child("name").getValue(String.class);
                    workNames.add(name);
                }

                // Adapter에 변경된 데이터 알림
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // 에러 처리 로직을 작성해주세요.
            }
        });
    }
}