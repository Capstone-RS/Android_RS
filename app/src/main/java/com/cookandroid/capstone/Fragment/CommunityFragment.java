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

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_community_main, container, false);

        TextView textView_community1 = view.findViewById(R.id.community1);
        TextView textView_community2 = view.findViewById(R.id.community2);
        TextView textView_community3 = view.findViewById(R.id.community3);
        TextView textView_community4 = view.findViewById(R.id.community4);
        TextView textView_community5 = view.findViewById(R.id.community5);
        TextView textView_community6 = view.findViewById(R.id.community6);
        TextView textView_community7 = view.findViewById(R.id.community7);
        TextView textView_community8 = view.findViewById(R.id.community8);
        TextView textView_community9 = view.findViewById(R.id.community9);
        TextView textView_community10 = view.findViewById(R.id.community10);
        TextView textView_community11 = view.findViewById(R.id.community11);


        //카페
        textView_community1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveToCommunityList("Cafe");
            }
        });

        //학원, 과외
        textView_community2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveToCommunityList("Academy");
            }
        });

        //아이스크림
        textView_community3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveToCommunityList("Icecream");
            }
        });

        //패스트푸드
        textView_community4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveToCommunityList("Fastfood");
            }
        });

        //의류, 신발
        textView_community5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveToCommunityList("Clothes,Shoes");
            }
        });

        //음식점
        textView_community6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveToCommunityList("Restaurant");
            }
        });

        //영화관
        textView_community7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveToCommunityList("Movie");
            }
        });

        //웨딩홀
        textView_community8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveToCommunityList("Wedding");
            }
        });

        //편의점
        textView_community9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveToCommunityList("ConvenienceStore");
            }
        });

        //빵집
        textView_community10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveToCommunityList("Bread");
            }
        });

        //그 외 기타
        textView_community11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveToCommunityList("Other");
            }
        });

        return view;
    }

    private void moveToCommunityList(String category) {
        Intent intent = new Intent(getActivity(), CommunityListActivity.class);
        intent.putExtra("category", category); // 선택한 카테고리를 CommunityListActivity로 전달
        startActivity(intent);
    }
}
