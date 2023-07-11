package com.cookandroid.capstone;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class CommunityListActivity extends AppCompatActivity {
    Button btnWrite;
    ListView listView;
    CommunityCustomListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_list);

        btnWrite = findViewById(R.id.btnWrite);
        listView = findViewById(R.id.listView);

        // 데이터 리스트 생성 (임의의 데이터로 예시)
        ArrayList<String> itemList = new ArrayList<>();
        itemList.add("아이템 1");
        itemList.add("아이템 2");
        itemList.add("아이템 3");
        itemList.add("아이템 4");
        itemList.add("아이템 5");
        itemList.add("아이템 6");

        // 커스텀 어댑터 생성 및 리스트뷰에 설정
        adapter = new CommunityCustomListAdapter(this, itemList);
        listView.setAdapter(adapter);

        btnWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CommunityWriteActivity.class);
                startActivity(intent);
            }
        });
    }
}
