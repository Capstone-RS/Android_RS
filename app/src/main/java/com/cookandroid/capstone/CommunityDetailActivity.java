package com.cookandroid.capstone;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class CommunityDetailActivity extends AppCompatActivity {

    private ScaleAnimation scaleAnimation;
    private BounceInterpolator bounceInterpolator;
    private CompoundButton button_favorite;
    private ScrollView scrollView;
    private TextView community_title;
    private TextView community_content;
    private String selectedCategory;
    private static final int REQUEST_DELETE_POST = 100;
    private ArrayList<String> commentList = new ArrayList<>(); // 댓글 데이터를 저장하는 변수
    private ArrayList<String> savedCommentList = new ArrayList<>(); // 이전에 저장된 댓글 데이터를 저장하는 변수
    private CommunityCommentCustomListAdapter adapter; // 어댑터 변수
    private String communityTitle;
    private String communityContent;
    private String title;
    private static final String SAVED_COMMENT_LIST = "saved_comment_list";

    private static final int REQUEST_EDIT_POST = 101; // 임의의 숫자로 설정

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
        TextView topic = findViewById(R.id.community_topic);
        ListView listView_comment = findViewById(R.id.lv_comment);
        scrollView = findViewById(R.id.scrollView);
        community_title = findViewById(R.id.community_title);
        community_content = findViewById(R.id.community_content);
        EditText editText_comment = findViewById(R.id.etComment);
        Button btn_comment = findViewById(R.id.btn_comment);

        buttonFavorite.setOnCheckedChangeListener(null);
        buttonFavorite.setChecked(false);

        // 이전에 저장된 댓글 데이터를 초기화
        savedCommentList = new ArrayList<>();

        communityTitle = community_title.getText().toString(); // Add this line to initialize communityTitle

        if (savedInstanceState != null) {
            // savedInstanceState에서 commentList를 복원합니다.
            commentList = savedInstanceState.getStringArrayList(SAVED_COMMENT_LIST);
        }

        adapter = new CommunityCommentCustomListAdapter(getApplicationContext(), commentList, communityTitle, communityContent);
        listView_comment.setAdapter(adapter);

        selectedCategory = getIntent().getStringExtra("category");

        if(selectedCategory == null){
            Intent intent1 = new Intent(getApplicationContext(),CommunityListActivity.class);
            finish();
            return;
        }
        topic.setText(selectedCategory);

        // Firebase Realtime Database 인스턴스를 초기화합니다.
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        Intent intent = getIntent();
        if (intent != null) {
            title = intent.getStringExtra("title");
            String content = intent.getStringExtra("content");

            if (title != null) {
                community_title.setText(title);
            }

            if (content != null) {
                community_content.setText(content);
            }

            // 파이어베이스에서 해당 커뮤니티 데이터를 가져오기 위한 레퍼런스를 만듭니다.
            DatabaseReference communityRef = database.getReference("Community").child(selectedCategory);

            communityRef.orderByChild("title").equalTo(title).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    // 데이터를 가져오는 작업을 수행합니다.
                    if (snapshot.exists()) {
                        for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                            String postContent = postSnapshot.child("content").getValue(String.class);

                            // 수정된 데이터로 제목과 내용을 갱신합니다.
                            if (postContent != null) {
                                community_content.setText(postContent);
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

        // 댓글 추가 버튼 클릭 시 호출되는 리스너 설정
        btn_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newComment = editText_comment.getText().toString();
                if (!newComment.isEmpty()) {
                    // 새 댓글 데이터를 Firebase Realtime Database에 추가
                    addCommentToFirebase(newComment);
                    editText_comment.setText(""); // 입력 필드 초기화
                }
            }
        });
    }
    private void addCommentToFirebase(String newComment) {
        // Firebase Realtime Database 인스턴스를 초기화합니다.
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        // 카테고리와 게시물 제목을 가져옵니다.
        String category = selectedCategory;
        String postTitle = community_title.getText().toString();

        // 카테고리와 게시물 제목을 조합하여 고유 식별자를 생성합니다.
        String categoryPostTitle = category + "_" + postTitle;

        // 댓글을 해당 게시물의 'Comments' 노드에 추가합니다.
        DatabaseReference commentRef = database.getReference("Comments")
                .child(category)
                .child(categoryPostTitle);

        String commentId = commentRef.push().getKey(); // 댓글 고유 식별자 생성
        commentRef.child(commentId).setValue(newComment);

        // 댓글 리스트를 업데이트
        commentList.add(newComment);
        adapter.notifyDataSetChanged();
    }

    //현재 사용자의 아이디 가져오기
    private String getCurrentUserId(){
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser != null){
            return currentUser.getUid();
        }else{
            return null;
        }
    }

    // 해당 게시물 작성자의 아이디 가져오기
    private void getPostUserId(final DataCallback<String> callback) {
        // Firebase Realtime Database 인스턴스 초기화
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference communityRef = database.getReference("Community").child(selectedCategory);

        // title을 기준으로 해당 게시글 찾아서 작성자의 아이디를 가져옵니다.
        communityRef.orderByChild("title").equalTo(community_title.getText().toString())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        // 데이터를 가져오는 작업을 수행합니다.
                        if (snapshot.exists()) {
                            for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                                String postUserId = postSnapshot.child("userId").getValue(String.class);
                                callback.onDataReceived(postUserId); // 작성자의 아이디를 콜백으로 반환
                                return; // 아이디를 가져왔으므로 루프를 종료합니다.
                            }
                        }
                        callback.onDataReceived(null); // 게시물을 찾지 못한 경우 null을 반환
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // 데이터 가져오기 실패
                        callback.onDataReceived(null);
                    }
                });
    }
    public interface DataCallback<T> {
        void onDataReceived(T data);
    }

    private void openBottomSheet() {
        String currentUserId = getCurrentUserId();
        getPostUserId(new DataCallback<String>() {
            @Override
            public void onDataReceived(String postUserId) {
                if (currentUserId != null && postUserId != null && currentUserId.equals(postUserId)) {
                    openBottomSheetForCurrentUser();
                } else {
                    openBottomSheetForOtherUser();
                }
            }
        });
    }

    //게시물 작성한 유저일 경우
    private void openBottomSheetForCurrentUser() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_community, null);

        TextView btnDelete = bottomSheetView.findViewById(R.id.btn_delete);
        TextView btnEdit = bottomSheetView.findViewById(R.id.btn_edit);
        TextView btnCancle = bottomSheetView.findViewById(R.id.btn_cancle);

        // 바텀시트에서 삭제하기 버튼을 클릭했을 때 처리
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 삭제 작업 수행
                deletePostFromFirebase();
                bottomSheetDialog.dismiss();
            }
        });

        // 바텀시트에서 수정하기 버튼을 클릭했을 때 처리
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 수정 작업 수행
                openEditActivity(community_title.getText().toString(), community_content.getText().toString());
                bottomSheetDialog.dismiss();
            }
        });

        // 바텀시트에서 취소 버튼을 클릭했을 때 처리
        btnCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });

        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }

    //게시물 작성한 유저와 다를 경우
    private void openBottomSheetForOtherUser() {
        // 다른 사용자를 위한 바텀시트 열기
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_community2, null);

        TextView btnChat = bottomSheetView.findViewById(R.id.btn_chat);
        TextView btnCancle2 = bottomSheetView.findViewById(R.id.btn_cancle);

        // 바텀시트에서 채팅 버튼을 클릭했을 때 처리




        // 바텀시트에서 취소 버튼을 클릭했을 때 처리
        btnCancle2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });

        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }

    // CommunityDetailEdit 페이지 열기 및 데이터 전달
    private void openEditActivity(String title, String content) {
        Intent intent = new Intent(this, CommunityDetailEditActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("content", content);
        intent.putExtra("category", selectedCategory);
        startActivityForResult(intent, REQUEST_EDIT_POST); // 요청 코드 설정

    }

    // Firebase에서 해당 게시글 삭제하는 함수
    private void deletePostFromFirebase() {
        String title = community_title.getText().toString();

        // Firebase Realtime Database 인스턴스를 초기화합니다.
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference communityRef = database.getReference("Community");

        // title을 기준으로 해당 게시글 찾아 삭제
        communityRef.child(selectedCategory).orderByChild("title").equalTo(title).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // 데이터를 가져오는 작업을 수행합니다.
                if (snapshot.exists()) {
                    for (DataSnapshot categorySnapshot : snapshot.getChildren()) {
                        for (DataSnapshot postSnapshot : categorySnapshot.getChildren()) {
                            postSnapshot.getRef().removeValue(); // 해당 게시글 삭제
                        }
                    }
                    // 삭제가 완료되면 결과를 CommunityListActivity로 전달
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("dataChanged", true);
                    setResult(RESULT_OK, resultIntent);
                    finish(); // 액티비티 종료
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // 데이터 가져오기 실패
            }
        });
    }
    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // commentList 데이터를 번들에 저장합니다.
        outState.putStringArrayList(SAVED_COMMENT_LIST, commentList);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // 파이어베이스에서 해당 커뮤니티 데이터를 가져오기 위한 레퍼런스를 만듭니다.
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference communityRef = database.getReference("Community").child(selectedCategory);
        String title = community_title.getText().toString();

        communityRef.orderByChild("title").equalTo(title).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // 데이터를 가져오는 작업을 수행합니다.
                if (snapshot.exists()) {
                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        String postContent = postSnapshot.child("content").getValue(String.class);

                        // 수정된 데이터로 내용을 갱신합니다.
                        if (postContent != null) {
                            community_content.setText(postContent);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // 데이터 가져오기 실패
            }
        });

        //댓글 데이터 초기화
        commentList.clear();

        DatabaseReference commentRef = database.getReference("Comment");

        //해당 게시물의 제목과 동일한 댓글 데이터를 가져옴.
        String categoryPostTitleContent = selectedCategory;
        Query query = commentRef.child(categoryPostTitleContent)
                .orderByChild("PostTitle")
                .equalTo(community_title.getText().toString());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //댓글 데이터를 가져와 commentList에 추가합니다.
                for(DataSnapshot commentSnapshot : snapshot.getChildren()){
                    String comment = commentSnapshot.child("comment").getValue(String.class);
                    commentList.add(comment);
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_EDIT_POST) {
            if (resultCode == RESULT_OK && data != null) {
                // 수정 결과를 처리합니다.
                String updatedTitle = data.getStringExtra("title");
                String updatedContent = data.getStringExtra("content");

                // 수정된 제목과 내용을 화면에 반영합니다.
                if (updatedTitle != null) {
                    community_title.setText(updatedTitle);
                }
                if (updatedContent != null) {
                    community_content.setText(updatedContent);
                }
            }
        }
    }

}