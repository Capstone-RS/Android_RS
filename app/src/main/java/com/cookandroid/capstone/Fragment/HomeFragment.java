package com.cookandroid.capstone.Fragment;


import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import com.cookandroid.capstone.CheckListActivity;
import com.cookandroid.capstone.HelpActivity;
import com.cookandroid.capstone.MainActivity;
import com.cookandroid.capstone.R;
import com.cookandroid.capstone.WorkDataActivity;


public class HomeFragment extends Fragment {

    TextView textView_checklistadd;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_home, container, false);

        ImageView fragHelp = view.findViewById(R.id.frag_help);
        Button btnAdd = view.findViewById(R.id.btnAdd);
        textView_checklistadd = view.findViewById(R.id.btnChecklistAdd);

        fragHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 클릭 이벤트 처리
                Intent intent = new Intent(getActivity(), HelpActivity.class);
                startActivity(intent);
            }
        });

        //근무 추가 버튼
       btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 클릭 이벤트 처리
                Intent intent = new Intent(getActivity(), WorkDataActivity.class);
                startActivity(intent);
            }
        });

        //체크리스트 메모추가 버튼
        textView_checklistadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),
                        CheckListActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }
}