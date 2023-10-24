package com.cookandroid.capstone;

import android.content.Intent;
import android.content.SharedPreferences;
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
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.cookandroid.capstone.Fragment.ChatFragment;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class CommunityDetailActivity extends AppCompatActivity {

    private FirebaseDatabase database;
    private ScaleAnimation scaleAnimation;
    private BounceInterpolator bounceInterpolator;
    private CompoundButton button_favorite;
    private ScrollView scrollView;
    private TextView community_title;
    private TextView community_content;
    private String selectedCategory;
    private static final int REQUEST_DELETE_POST = 100;
    private ArrayList<String> commentList = new ArrayList<>(); // 댓글 데이터를 저장하는 변수
    private ArrayList<String> existingComments = new ArrayList<>();
    private ArrayList<String> savedCommentList = new ArrayList<>();

    private CommunityCommentCustomListAdapter adapter; // 어댑터 변수
    private String communityTitle;
    private String communityContent;
    private String title;
    private static final String SAVED_COMMENT_LIST = "saved_comment_list";
    private SharedPreferences prefs;

    private static final int REQUEST_EDIT_POST = 101; // 임의의 숫자로 설정

    private int likeCount = 0; // 좋아요 수를 추적하는 변수

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_detail);

        database = FirebaseDatabase.getInstance();

        scaleAnimation = new ScaleAnimation(0.7f, 1.0f, 0.7f, 1.0f, Animation.RELATIVE_TO_SELF, 0.7f, Animation.RELATIVE_TO_SELF, 0.7f);
        scaleAnimation.setDuration(500);
        bounceInterpolator = new BounceInterpolator();
        scaleAnimation.setInterpolator(bounceInterpolator);

        ToggleButton buttonFavorite = findViewById(R.id.button_favorite);
        TextView textView_likeNumber = findViewById(R.id.like_number);
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

        // 게시물의 좋아요 상태를 Firebase 데이터베이스에서 불러오고 업데이트합니다.
        updateLikeStatusFromFirebase(selectedCategory, title, buttonFavorite, textView_likeNumber);

        communityTitle = community_title.getText().toString();

        adapter = new CommunityCommentCustomListAdapter(getApplicationContext(), commentList, communityTitle, communityContent);
        listView_comment.setAdapter(adapter);

        selectedCategory = getIntent().getStringExtra("category");

        if (selectedCategory == null) {
            Intent intent1 = new Intent(getApplicationContext(), CommunityListActivity.class);
            finish();
            return;
        }
        topic.setText(selectedCategory);

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
        }

        // 이 부분에서 댓글 데이터를 페이지에 접근할 때 로드합니다.
        loadCommentsFromFirebase(selectedCategory, title);

        //날짜를 파이어베이스에서 불러오기
        loadPostDateFromFirebase(selectedCategory, title);

        listView_comment.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    scrollView.requestDisallowInterceptTouchEvent(false);
                } else {
                    scrollView.requestDisallowInterceptTouchEvent(true);
                }
                return false;
            }
        });

        // 좋아요 버튼의 상태가 변경될 때의 리스너를 설정합니다.
        buttonFavorite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                compoundButton.startAnimation(scaleAnimation);

                // 현재 사용자의 ID 가져오기
                String currentUserId = getCurrentUserId();

                if (currentUserId != null) {
                    DatabaseReference communityRef = database.getReference("Community").child(selectedCategory);
                    Query query = communityRef.orderByChild("title").equalTo(title);
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                    // 해당 게시물의 "Likes" 노드에 사용자 아이디를 저장
                                    DatabaseReference likesRef = postSnapshot.getRef().child("Likes").child(currentUserId);

                                    if (isChecked) {
                                        // 사용자가 좋아요를 누른 경우
                                        likesRef.setValue(true);
                                        likeCount++;
                                    } else {
                                        // 사용자가 좋아요를 취소한 경우
                                        likesRef.removeValue();
                                        likeCount--;
                                    }

                                    // TextView를 업데이트하여 좋아요 수를 표시합니다.
                                    textView_likeNumber.setText(String.valueOf(likeCount));
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // 데이터 가져오기 실패 처리
                        }
                    });
                }
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

        btn_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                String commentText = editText_comment.getText().toString().trim();

                if (!commentText.isEmpty()) {
                    DatabaseReference communityRef = database.getReference("Community").child(selectedCategory);
                    Query query = communityRef.orderByChild("title").equalTo(community_title.getText().toString());
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                                    String postId = postSnapshot.getKey();

                                    if (postId != null) {
                                        DatabaseReference commentsRef = communityRef.child(postId).child("comments");
                                        DatabaseReference newCommentRef = commentsRef.push();

                                        HashMap<String, Object> commentData = new HashMap<>();
                                        commentData.put("commentText", commentText);
                                        commentData.put("userId", userUid);

                                        newCommentRef.setValue(commentData); // 새로운 댓글을 Firebase에 추가

                                        // 댓글을 추가한 후 댓글 목록을 업데이트합니다.
                                        loadCommentsFromFirebase(selectedCategory, title);

                                        // EditText 초기화
                                        editText_comment.setText("");
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // 데이터 가져오기 실패 처리
                        }
                    });
                } else {
                    // 댓글 내용이 비어있을 경우 처리
                }
            }
        });
    }

    private void loadCommentsFromFirebase(String selectedCategory, String title) {
        DatabaseReference communityRef = database.getReference("Community").child(selectedCategory);

        // "title" 대신 "title" 필드를 사용하여 해당 게시물의 댓글을 가져옵니다.
        Query query = communityRef.orderByChild("title").equalTo(title);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // 댓글 목록을 초기화합니다.
                commentList.clear();

                // 댓글 데이터를 불러옵니다.
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot commentDataSnapshot : postSnapshot.child("comments").getChildren()) {
                        String commentText = commentDataSnapshot.child("commentText").getValue(String.class);
                        if (commentText != null) {
                            commentList.add(commentText);
                        }
                    }
                }

                // 어댑터를 업데이트하여 댓글 목록을 표시합니다.
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // 데이터 가져오기 실패 처리
            }
        });
    }

    // Firebase에서 게시물의 좋아요 상태를 불러오고 업데이트하는 메서드
    private void updateLikeStatusFromFirebase(String selectedCategory, String title, CompoundButton buttonFavorite, TextView textView_likeNumber) {
        if (selectedCategory == null || title == null) {
            // selectedCategory 또는 title이 null이면 처리하지 않음
            return;
        }

        DatabaseReference communityRef = database.getReference("Community").child(selectedCategory);

        // "title" 대신 "title" 필드를 사용하여 해당 게시물의 좋아요 상태를 가져옵니다.
        Query query = communityRef.orderByChild("title").equalTo(title);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        Boolean liked = postSnapshot.child("liked").getValue(Boolean.class);

                        // Firebase에서 가져온 좋아요 상태를 버튼에 반영합니다.
                        if (liked != null) {
                            buttonFavorite.setChecked(liked);

                            // 버튼 상태에 따라 좋아요 수를 업데이트합니다.
                            likeCount = liked ? likeCount + 1 : likeCount;
                            textView_likeNumber.setText(String.valueOf(likeCount));
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // 데이터 가져오기 실패 처리
            }
        });
    }

    // Firebase에서 작성 날짜를 가져오는 메서드
    private void loadPostDateFromFirebase(String selectedCategory, String title) {
        TextView textView_date = findViewById(R.id.write_date_time);
        DatabaseReference communityRef = database.getReference("Community").child(selectedCategory);

        // "title" 대신 "title" 필드를 사용하여 해당 게시물의 작성 날짜를 가져옵니다.
        Query query = communityRef.orderByChild("title").equalTo(title);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        String postDate = postSnapshot.child("date").getValue(String.class);
                        if (postDate != null) {
                            // 작성 날짜를 textView_date에 설정
                            textView_date.setText(postDate);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // 데이터 가져오기 실패 처리
            }
        });
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

        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ChatFragment를 생성합니다.
                ChatFragment chatFragment = new ChatFragment();

                // 필요한 데이터를 Bundle로 전달할 수 있으면 여기서 전달할 수 있습니다.
                Bundle args = new Bundle();
                args.putString("key1", "value1");
                chatFragment.setArguments(args);

                // FragmentManager를 사용하여 ChatFragment를 화면에 표시합니다.
                getSupportFragmentManager().beginTransaction()
                        //.replace(R.id.fragment_container, chatFragment)
                        .addToBackStack(null) // 뒤로 가기 스택에 추가 (선택 사항)
                        .commit();

                bottomSheetDialog.dismiss(); // 바텀시트를 닫습니다.
            }
        });


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

    private void openChatFragment(String userId) {
        // ChatFragment를 생성하고 현재 사용자의 아이디를 전달합니다.
        ChatFragment chatFragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putString("userId", userId);
        chatFragment.setArguments(args);

        // ChatFragment를 표시하기 위한 프래그먼트 트랜잭션 시작
        getSupportFragmentManager().beginTransaction()
                //.replace(R.id.fragment_container, chatFragment) // "fragment_container"는 프래그먼트를 표시할 레이아웃 컨테이너 ID입니다.
                .addToBackStack(null) // 이전 프래그먼트로 돌아갈 수 있도록 백 스택에 추가합니다.
                .commit();
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
    protected void onResume() {
        super.onResume();
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