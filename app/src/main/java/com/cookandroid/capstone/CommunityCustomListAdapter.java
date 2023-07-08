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

        TextView textViewTitle = convertView.findViewById(R.id.communitylist_title);
        TextView textViewContent = convertView.findViewById(R.id.communitylist_content);
        TextView textViewBtnContent = convertView.findViewById(R.id.communitylist_content);

        textViewTitle.setText(item);
        textViewContent.setText(""); // 내용이 없으므로 빈 문자열로 설정


        textViewBtnContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = getContext();
                Intent intent = new Intent(context, CommunityDetailActivity.class);
                context.startActivity(intent);
            }
        });


        return convertView;
    }
}
