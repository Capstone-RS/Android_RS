package com.cookandroid.capstone.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.cookandroid.capstone.Com_listActivity;
import com.cookandroid.capstone.R;
import com.cookandroid.capstone.WorkDataActivity;

public class CommunityFragment extends Fragment {

    TextView com1_1;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        com1_1 = getView().findViewById(R.id.com1_1);
        com1_1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getActivity(), Com_listActivity.class);
                startActivity(intent);
            }
        });
        return inflater.inflate(R.layout.activity_community_main, container, false);
    }
}