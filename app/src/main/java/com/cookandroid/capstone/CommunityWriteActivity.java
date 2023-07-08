package com.cookandroid.capstone;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class CommunityWriteActivity extends AppCompatActivity {

    TextView textView_backbtn;
    Button btn_done;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_write);

        textView_backbtn = findViewById(R.id.btnBack);
        btn_done = findViewById(R.id.btn_write_done);

        textView_backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CommunityListActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btn_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CommunityListActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }
}
