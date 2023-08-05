package com.cookandroid.capstone;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
                // 수정 내용을 가져옴
                String updatedTitle = et_title.getText().toString();
                String updatedContent = et_content.getText().toString();

                // 기존 게시글의 레퍼런스를 가져옴
                DatabaseReference postRef = databaseReference;

                // 기존 게시글의 데이터를 삭제
                postRef.removeValue();

                // 수정된 데이터를 새로 저장
                DatabaseReference newPostRef = databaseReference.push();
                newPostRef.child("title").setValue(updatedTitle);
                newPostRef.child("content").setValue(updatedContent);

                // 수정이 완료되면 결과를 CommunityDetailActivity로 전달하여 화면 갱신
                Intent resultIntent = new Intent();
                resultIntent.putExtra("dataChanged", true);
                resultIntent.putExtra("title", updatedTitle); // 수정된 제목 전달
                resultIntent.putExtra("content", updatedContent); // 수정된 내용 전달
                setResult(RESULT_OK, resultIntent);

                // CommunityDetail 화면으로 이동하면서 수정된 데이터 전달
                Intent detailIntent = new Intent(getApplicationContext(), CommunityDetailActivity.class);
                detailIntent.putExtra("title", updatedTitle);
                detailIntent.putExtra("content", updatedContent);
                startActivity(detailIntent);

                finish(); // 액티비티 종료
            }
        });

    }
}
