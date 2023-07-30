package com.cookandroid.capstone;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.ArrayList;

public class CommunityCustomListAdapter extends ArrayAdapter<String> {
    private ArrayList<String> itemList;

    public CommunityCustomListAdapter(Context context, ArrayList<String> itemList) {
        super(context, 0, itemList);
        this.itemList = itemList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.community_customlistview, parent, false);
        }

        String item = itemList.get(position);
        String[] data = item.split(";"); // 세미콜론을 구분자로 카테고리, 제목, 내용 분리

        String category = data[0]; // data[0]에는 카테고리가 들어있습니다.
        String title = data[1];    // data[1]에는 제목이 들어있습니다.
        String content = data[2];  // data[2]에는 내용이 들어있습니다.

        TextView textViewTitle = convertView.findViewById(R.id.communitylist_title);
        TextView textViewContent = convertView.findViewById(R.id.communitylist_content);

        textViewTitle.setText(title);
        textViewContent.setText(content);

        textViewContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 상세 화면으로 이동할 때 해당 아이템의 정보를 전달
                Context context = getContext();
                Intent intent = new Intent(context, CommunityDetailActivity.class);
                intent.putExtra("category", category);
                intent.putExtra("title", title);
                intent.putExtra("content", content);
                context.startActivity(intent);
            }
        });

        return convertView;
    }
}