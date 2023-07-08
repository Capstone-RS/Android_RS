package com.cookandroid.capstone.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.cookandroid.capstone.CheckListActivity;
import com.cookandroid.capstone.CommunityListActivity;
import com.cookandroid.capstone.R;

public class CommunityFragment extends Fragment {

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_community_main, container, false);

        TextView textView_community1 = view.findViewById(R.id.community1);

        textView_community1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CommunityListActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }
}


