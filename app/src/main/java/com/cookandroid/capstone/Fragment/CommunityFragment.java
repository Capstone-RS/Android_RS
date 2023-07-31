package com.cookandroid.capstone.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.cookandroid.capstone.CheckListActivity;
import com.cookandroid.capstone.CommunityListActivity;
import com.cookandroid.capstone.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CommunityFragment extends Fragment {
    private TextView[] communityTextViews;
    private String[] categoryNames;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_community_main, container, false);

        // TextView 배열과 카테고리 이름 배열 초기화
        communityTextViews = new TextView[11];
        categoryNames = new String[]{
                "카페", "학원, 과외", "아이스크림", "패스트푸드", "의류, 신발",
                "음식점", "영화관", "웨딩홀", "편의점", "빵집", "그 외 기타"
        };

        // TextView 배열 초기화
        communityTextViews[0] = view.findViewById(R.id.community1);
        communityTextViews[1] = view.findViewById(R.id.community2);
        communityTextViews[2] = view.findViewById(R.id.community3);
        communityTextViews[3] = view.findViewById(R.id.community4);
        communityTextViews[4] = view.findViewById(R.id.community5);
        communityTextViews[5] = view.findViewById(R.id.community6);
        communityTextViews[6] = view.findViewById(R.id.community7);
        communityTextViews[7] = view.findViewById(R.id.community8);
        communityTextViews[8] = view.findViewById(R.id.community9);
        communityTextViews[9] = view.findViewById(R.id.community10);
        communityTextViews[10] = view.findViewById(R.id.community11);

        // 각 카테고리에 대해 최신 글 내용 가져오기 및 클릭 리스너 설정
        for (int i = 0; i < communityTextViews.length; i++) {
            getLatestContent(i);
            setClickListener(i);
        }

        return view;
    }

    private void getLatestContent(int index) {
        DatabaseReference categoryReference = FirebaseDatabase.getInstance().getReference().child("Community").child(categoryNames[index]);
        categoryReference.orderByKey().limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    String content = postSnapshot.child("content").getValue(String.class);
                    // 최신 글 내용을 해당 카테고리 TextView에 설정
                    communityTextViews[index].setText(content);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setClickListener(int index) {
        communityTextViews[index].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveToCommunityList(categoryNames[index]);
            }
        });
    }

    private void moveToCommunityList(String category) {
        Intent intent = new Intent(getActivity(), CommunityListActivity.class);
        intent.putExtra("category", category); // 선택한 카테고리를 CommunityListActivity로 전달
        startActivityForResult(intent, 1); // requestCode는 1로 설정
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == getActivity().RESULT_OK && data != null) {
            boolean dataChanged = data.getBooleanExtra("dataChanged", false);
            if (dataChanged) {
                // 데이터가 변경되었을 때, 최신 글을 가져오는 작업 수행
                for (int i = 0; i < communityTextViews.length; i++) {
                    getLatestContent(i);
                }
            }
        }
    }
}
