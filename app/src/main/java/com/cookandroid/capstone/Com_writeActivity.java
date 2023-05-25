package com.cookandroid.capstone;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class Com_writeActivity extends Activity {

    Button btn_write; //글작성 버튼

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_write);

        btn_write = findViewById(R.id.reg_button);

        btn_write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Com_detailActivity.class);
                startActivity(intent);
            }
        });

    }
}