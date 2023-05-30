package com.cookandroid.capstone;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class WorkDetailEditActivity extends AppCompatActivity {
    TextView textView_backbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_workdetail_edit); // workdata xml이랑 연결된 자바파일이라는 뜻

        textView_backbtn = findViewById(R.id.btnBack);

        textView_backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),
                        WorkDetail2Activity.class);
                startActivity(intent);
                finish();
            }
        });

        
    }
}