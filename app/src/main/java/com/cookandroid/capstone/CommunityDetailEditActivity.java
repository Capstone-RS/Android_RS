package com.cookandroid.capstone;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CommunityDetailEditActivity extends AppCompatActivity {

    TextView textView_backbtn;
    TextView topic;
    Button btn_edit;
    EditText et_title;
    EditText et_content;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    String selectedCategory; // 추가: 사용자가 선택한 카테고리를 저장할 변수

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_detail_edit);

        textView_backbtn = findViewById(R.id.btnBack);
        topic = findViewById(R.id.community_topic);
        btn_edit = findViewById(R.id.btn_write_edit);
        et_title = findViewById(R.id.write_title);
        et_content = findViewById(R.id.write_content);

        firebaseDatabase = FirebaseDatabase.getInstance();

        // 추가: 사용자가 선택한 카테고리를 Intent로 전달받음
        selectedCategory = getIntent().getStringExtra("category");

        if (selectedCategory == null) {
            // 선택된 카테고리가 없을 경우 CommunityListActivity로 이동
            Intent intent = new Intent(getApplicationContext(), CommunityListActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        topic.setText(selectedCategory);

        // Intent를 통해 전달받은 title과 content 데이터를 EditText에 표시
        String title = getIntent().getStringExtra("title");
        String content = getIntent().getStringExtra("content");
        et_title.setText(title);
        et_content.setText(content);

        // 선택된 카테고리에 해당하는 레퍼런스 생성
        databaseReference = firebaseDatabase.getReference().child("Community").child(selectedCategory);

        // 뒤로가기
        textView_backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 수정된 내용을 가져옵니다.
                String updatedTitle = et_title.getText().toString(); // 수정된 제목을 가져옵니다.
                String updatedContent = et_content.getText().toString(); // 수정된 내용을 가져옵니다.

                // 현재 사용자의 userid를 가져옵니다.
                String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                // 데이터베이스 레퍼런스를 가져옵니다.
                DatabaseReference postRef = databaseReference;

                // ValueEventListener를 사용하여 기존 게시물의 데이터를 수정합니다.
                postRef.orderByChild("title").equalTo(title).limitToFirst(1).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                            // 수정 대상 게시물을 찾았으므로 해당 게시물의 내용을 수정합니다.
                            postSnapshot.child("title").getRef().setValue(updatedTitle); // 수정된 제목 저장
                            postSnapshot.child("content").getRef().setValue(updatedContent); // 수정된 내용 저장
                            postSnapshot.child("userId").getRef().setValue(userUid); // 현재 사용자의 userid 저장
                        }

                        // 수정이 완료되면 CommunityDetailActivity로 결과를 전달합니다.
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("title", updatedTitle); // 수정된 제목 정보를 전달
                        resultIntent.putExtra("content", updatedContent); // 수정된 내용 정보를 전달
                        setResult(RESULT_OK, resultIntent);

                        // 현재 액티비티를 종료합니다.
                        finish();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // 오류 처리 코드
                    }
                });
            }
        });

    }
}