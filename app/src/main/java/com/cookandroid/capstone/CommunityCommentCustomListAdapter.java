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
import android.widget.ToggleButton;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;

public class CommunityCommentCustomListAdapter extends ArrayAdapter<String> {
    private ArrayList<String> commentList;
    private ScaleAnimation scaleAnimation;
    private BounceInterpolator bounceInterpolator;
    private Context context;


    public CommunityCommentCustomListAdapter(Context context, ArrayList<String> commentList) {
        super(context, 0, commentList);
        this.context = context;
        this.commentList = commentList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.community_comment_customlistview, parent, false);
        }

        String item = commentList.get(position);

        scaleAnimation = new ScaleAnimation(0.7f, 1.0f, 0.7f, 1.0f, Animation.RELATIVE_TO_SELF, 0.7f, Animation.RELATIVE_TO_SELF, 0.7f);
        scaleAnimation.setDuration(500);
        bounceInterpolator = new BounceInterpolator();
        scaleAnimation.setInterpolator(bounceInterpolator);

        ToggleButton buttonCommentFavorite = convertView.findViewById(R.id.button_comment_favorite);
        ImageButton btn_comment_bottomsheet = convertView.findViewById(R.id.btn_comment_bottomsheet);

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
