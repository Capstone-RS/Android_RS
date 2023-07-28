package com.cookandroid.capstone;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CommunityDetailActivity extends AppCompatActivity {

    private ScaleAnimation scaleAnimation;
    private BounceInterpolator bounceInterpolator;
    private CompoundButton button_favorite;
    private ScrollView scrollView;
    CommunityCommentCustomListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_detail);

        scaleAnimation = new ScaleAnimation(0.7f, 1.0f, 0.7f, 1.0f, Animation.RELATIVE_TO_SELF, 0.7f, Animation.RELATIVE_TO_SELF, 0.7f);
        scaleAnimation.setDuration(500);
        bounceInterpolator = new BounceInterpolator();
        scaleAnimation.setInterpolator(bounceInterpolator);

        ToggleButton buttonFavorite = findViewById(R.id.button_favorite);
        ImageButton btn_bottomsheet = findViewById(R.id.btn_bottomsheet);
        TextView textView_backbtn = findViewById(R.id.btnBack);
        ListView listView_comment = findViewById(R.id.lv_comment);
        scrollView = findViewById(R.id.scrollView);
        TextView community_title = findViewById(R.id.community_title);
        TextView community_content = findViewById(R.id.community_content);


        buttonFavorite.setOnCheckedChangeListener(null);
        buttonFavorite.setChecked(false);

        //댓글 커스텀리스트 적용
        ArrayList<String> commentList = new ArrayList<>();
        commentList.add("아이템 1");
        commentList.add("아이템 2");
        commentList.add("아이템 3");
        commentList.add("아이템 4");
        commentList.add("아이템 5");
        commentList.add("아이템 6");

        adapter = new CommunityCommentCustomListAdapter(this, commentList);
        listView_comment.setAdapter(adapter);

        // Firebase Realtime Database 인스턴스를 초기화합니다.
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        Intent intent = getIntent();
        if (intent != null) {
            String title = intent.getStringExtra("title");
            String content = intent.getStringExtra("content");

            if (title != null) {
                community_title.setText(title);
            }

            if (content != null) {
                community_content.setText(content);
            }

            // 파이어베이스에서 해당 커뮤니티 데이터를 가져오기 위한 레퍼런스를 만듭니다.
            DatabaseReference communityRef = database.getReference("Community");

            communityRef.orderByChild("title").equalTo(title).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    // 데이터를 가져오는 작업을 수행합니다.
                    if (snapshot.exists()) {
                        for (DataSnapshot categorySnapshot : snapshot.getChildren()) {
                            for (DataSnapshot postSnapshot : categorySnapshot.getChildren()) {
                                String postContent = postSnapshot.child("content").getValue(String.class);

                                // TODO: 가져온 커뮤니티 데이터의 내용을 사용하여 화면에 표시하거나 처리합니다.
                            }
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // 데이터 가져오기 실패
                }
            });

        }

        listView_comment.setOnTouchListener(new View.OnTouchListener() {
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


        buttonFavorite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                compoundButton.startAnimation(scaleAnimation);
                // TODO: 버튼 상태 변경 시 수행할 작업을 추가하세요
            }
        });

        btn_bottomsheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openBottomSheet();
            }
        });

        // 뒤로가기
        textView_backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    private void openBottomSheet() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_community, null);
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();

        // TODO: 바텀시트 내용을 설정하고 동작을 처리하세요
    }
}
