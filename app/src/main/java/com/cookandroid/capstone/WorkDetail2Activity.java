package com.cookandroid.capstone;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.w3c.dom.Text;

public class WorkDetail2Activity extends AppCompatActivity {

   TextView textView_editbtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_workdetail2); // workdata xml이랑 연결된 자바파일이라는 뜻

        textView_editbtn = findViewById(R.id.btnCorrect);

        textView_editbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),
                        WorkDetailEditActivity.class);
                startActivity(intent);

            }
        });


    }
}