package com.cookandroid.capstone;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CommunityCommentCustomListAdapter extends ArrayAdapter<String> {
    private ArrayList<String> commentList;
    private ScaleAnimation scaleAnimation;
    private BounceInterpolator bounceInterpolator;
    private Context context;

    private TextView textViewCommentContent;
    private String communityTitle;
    private String communityContent;

    public CommunityCommentCustomListAdapter(Context context, ArrayList<String> commentList, String communityTitle, String communityContent) {
        super(context, 0, commentList);
        this.context = context;
        this.commentList = commentList;
        this.communityTitle = communityTitle;
        this.communityContent = communityContent;

        // Firebase에서 해당 게시물에 해당하는 댓글을 가져와서 표시
        Query commentQuery = FirebaseDatabase.getInstance().getReference("Comment");
        commentQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<String> updatedCommentList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String commentPostTitle = snapshot.child("PostTitle").getValue(String.class);
                    if (commentPostTitle != null && commentPostTitle.equals(communityTitle)) {
                        String comment = snapshot.child("comment").getValue(String.class);
                        if (comment != null) {
                            updatedCommentList.add(comment);
                        }
                    }
                }
                // 댓글 데이터 업데이트
                commentList.clear(); // 댓글 리스트 초기화
                commentList.addAll(updatedCommentList);

                // 어댑터에 변경 사항 알림
                notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // 실패 시 처리
            }
        });
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.community_comment_customlistview, parent, false);
        }

        scaleAnimation = new ScaleAnimation(0.7f, 1.0f, 0.7f, 1.0f, Animation.RELATIVE_TO_SELF, 0.7f, Animation.RELATIVE_TO_SELF, 0.7f);
        scaleAnimation.setDuration(500);
        bounceInterpolator = new BounceInterpolator();
        scaleAnimation.setInterpolator(bounceInterpolator);

        ToggleButton buttonCommentFavorite = convertView.findViewById(R.id.button_comment_favorite);
        ImageButton btn_comment_bottomsheet = convertView.findViewById(R.id.btn_comment_bottomsheet);
        textViewCommentContent = convertView.findViewById(R.id.comment);

        buttonCommentFavorite.setOnCheckedChangeListener(null);
        buttonCommentFavorite.setChecked(false);

        buttonCommentFavorite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                compoundButton.startAnimation(scaleAnimation);
                // TODO: 버튼 상태 변경 시 수행할 작업을 추가하세요
            }
        });

        btn_comment_bottomsheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openBottomSheet();
            }
        });

        // 여기에서 댓글 데이터를 가져와서 textViewCommentContent에 설정
        String comment = getItem(position); // 댓글 데이터 가져오기
        textViewCommentContent.setText(comment); // 댓글 데이터 설정

        return convertView;
    }

    private void openBottomSheet() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        View bottomSheetView = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_community_comment, null);
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();

        // TODO: 바텀시트 내용을 설정하고 동작을 처리하세요
    }
}