package com.cookandroid.capstone;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CommunityListActivity extends AppCompatActivity {
    TextView btnBack;
    Button btnWrite;
    ListView listView;
    CommunityCustomListAdapter adapter;
    ArrayList<String> itemList;

    String selectedCategory;
    private static final int REQUEST_DELETE_POST = 100;
    FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_list);

        btnBack = findViewById(R.id.btnBack);
        btnWrite = findViewById(R.id.btnWrite);
        listView = findViewById(R.id.listView);

        itemList = new ArrayList<>();

        firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference().child("Community");

        // 추가: 사용자가 선택한 카테고리를 Intent로 전달받음
        selectedCategory = getIntent().getStringExtra("category");

        // selectedCategory가 null인 경우 기본값 설정
        if (selectedCategory == null) {
            selectedCategory = "default"; // 적절한 기본값으로 설정해주세요.
        }

        // 어댑터 먼저 생성
        adapter = new CommunityCustomListAdapter(this, itemList);
        listView.setAdapter(adapter);

        // CommunityWriteActivity 호출
        btnWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CommunityWriteActivity.class);
                intent.putExtra("category", selectedCategory);
                startActivityForResult(intent, 1); // requestCode는 1로 설정
            }
        });

        //뒤로가기
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // 선택된 카테고리에 해당하는 레퍼런스 생성
        DatabaseReference categoryReference = databaseReference.child(selectedCategory);

        // ValueEventListener를 사용하여 선택된 카테고리의 글 목록을 가져옴
        categoryReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                itemList.clear(); // 데이터를 실시간으로 업데이트하기 위해 리스트 초기화

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    String title = postSnapshot.child("title").getValue(String.class);
                    String content = postSnapshot.child("content").getValue(String.class);
                    String itemData = selectedCategory + ";" + title + ";" + content;
                    itemList.add(0, itemData);
                }

                adapter.notifyDataSetChanged(); // 데이터가 변경되었음을 어댑터에 알려서 리스트뷰를 갱신
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItemData = itemList.get(position);
                Intent intent = new Intent(getApplicationContext(), CommunityDetailActivity.class);
                intent.putExtra("selectedItemData", selectedItemData);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack();
            // 이전 프래그먼트로 돌아갈 때 수행할 동작 추가
            updateListView();
        } else {
            Intent intent = new Intent();
            intent.putExtra("dataChanged", true); // 데이터가 변경되었음을 알려주는 플래그
            setResult(RESULT_OK, intent);
            super.onBackPressed();
            // 백 스택에 이전 프래그먼트가 없는 경우, 기본적으로 뒤로 가기 버튼을 처리
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            boolean dataChanged = data.getBooleanExtra("dataChanged", false);
            if (dataChanged) {
                // 데이터가 변경되었을 때, 리스트뷰를 업데이트하는 작업 수행
                updateListView();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateListView();
    }


    private void updateListView() {
        // 선택된 카테고리에 해당하는 레퍼런스 생성
        DatabaseReference categoryReference = firebaseDatabase.getReference().child("Community").child(selectedCategory);

        // ValueEventListener를 사용하여 선택된 카테고리의 글 목록을 가져옴
        categoryReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                itemList.clear(); // 데이터를 실시간으로 업데이트하기 위해 리스트 초기화

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    String title = postSnapshot.child("title").getValue(String.class);
                    String content = postSnapshot.child("content").getValue(String.class);
                    String itemData = selectedCategory + ";" + title + ";" + content;
                    itemList.add(0, itemData);
                }

                adapter.notifyDataSetChanged(); // 데이터가 변경되었음을 어댑터에 알려서 리스트뷰를 갱신
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
