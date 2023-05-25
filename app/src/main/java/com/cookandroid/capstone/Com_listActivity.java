package com.cookandroid.capstone;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class Com_listActivity extends Activity {

    Button btn_back; //뒤로가기(com_main)
    Button btn_write; //글작성 페이지

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_list);

        btn_back = findViewById(R.id.btnBack);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), comActivity.class);
                startActivity(intent);
            }
        });

        btn_write = findViewById(R.id.btn_write);

        btn_write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Com_writeActivity.class);
                startActivity(intent);
            }
        });

    }
}
