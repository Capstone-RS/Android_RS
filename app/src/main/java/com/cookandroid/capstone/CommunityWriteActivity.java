package com.cookandroid.capstone;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CommunityWriteActivity extends AppCompatActivity {

    TextView textView_backbtn;
    TextView topic;
    Button btn_done;
    EditText et_title;
    EditText et_content;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    String selectedCategory; // 추가: 사용자가 선택한 카테고리를 저장할 변수

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_write);

        textView_backbtn = findViewById(R.id.btnBack);
        topic = findViewById(R.id.community_topic);
        btn_done = findViewById(R.id.btn_write_done);
        et_title = findViewById(R.id.write_title);
        et_content = findViewById(R.id.write_content);

        firebaseDatabase = FirebaseDatabase.getInstance();

        // 추가: 사용자가 선택한 카테고리를 Intent로 전달받음
        selectedCategory = getIntent().getStringExtra("category");

        if (selectedCategory == null) {
            // 선택된 카테고리가 없을 경우 CommunityListActivity로 이동
            Intent intent = new Intent(getApplicationContext(), CommunityListActivity.class);
            finish();
            return;
        }

        topic.setText(selectedCategory);

        // 선택된 카테고리에 해당하는 레퍼런스 생성
        databaseReference = firebaseDatabase.getReference().child("Community").child(selectedCategory);

        // 뒤로가기
        textView_backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        btn_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                DatabaseReference communityPostsRef = firebaseDatabase.getReference().child("Community");

                String title = et_title.getText().toString().trim();
                String content = et_content.getText().toString().trim();

                if (!title.isEmpty() && !content.isEmpty()) {
                    // 카테고리별로 게시물 저장
                    DatabaseReference categoryRef = communityPostsRef.child(selectedCategory);
                    DatabaseReference newPostRef = categoryRef.push();

                    newPostRef.child("title").setValue(title);
                    newPostRef.child("content").setValue(content);
                    newPostRef.child("userId").setValue(userUid);

                    // 데이터 저장 성공 시에만 결과 설정
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("dataChanged", true);
                    setResult(RESULT_OK, resultIntent);
                    finish();
                } else {
                    // 데이터 저장 실패 시에 대응 코드 (옵션)
                    // 실패 상황에 따라 적절한 처리를 추가할 수 있습니다.
                }

            }
        });
    }
}