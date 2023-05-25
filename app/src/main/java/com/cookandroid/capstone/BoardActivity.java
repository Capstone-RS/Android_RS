package com.cookandroid.capstone;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import com.cookandroid.capstone.BoardAdapter;
import com.cookandroid.capstone.Board;

public class BoardActivity extends AppCompatActivity {

    private RecyclerView mPostRecyclerView;

    private BoardAdapter mAdpater;
    private List<Board> mDatas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_list);

        mPostRecyclerView = findViewById(R.id.listRecycle);
        mDatas = new ArrayList<>(); // 샘플 데이터 추가
        mDatas.add(new Board("title","contents",20,10));
        mDatas.add(new Board("title","contents",20,10));
        mDatas.add(new Board("title","contents",20,10));
        mDatas.add(new Board("title","contents",20,10));
        mDatas.add(new Board("title","contents",20,10));

        // Adapter, LayoutManager 연결
        mAdpater = new BoardAdapter(mDatas);
        mPostRecyclerView.setAdapter(mAdpater);
        mPostRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }



}