package com.cookandroid.capstone;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;

import org.w3c.dom.Text;


public class WorkDetailActivity extends AppCompatActivity {
    private String clickedName; // 클릭한 아이템의 이름을 저장하는 변수
    private String clickedMoney; // 클릭한 아이템의 이름을 저장하는 변수
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workdetail);

        TextView name = (TextView) findViewById(R.id.name);
        TextView money= (TextView) findViewById(R.id.money);
        TextView btnEdit = (TextView) findViewById(R.id.btnEdit);
        TextView btnBack = (TextView) findViewById(R.id.btnBack);

        Intent intent = getIntent();
        if (intent != null) {
            clickedName = intent.getStringExtra("clicked_name");
            clickedMoney = intent.getStringExtra("clicked_money");
            if (clickedName != null) {
                // 클릭한 아이템의 이름을 EditText에 출력
                name.setText(clickedName);
            }
            if (clickedMoney != null) {
                // 클릭한 아이템의 이름을 EditText에 출력
                money.setText(clickedMoney);
            }
        }

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newName = name.getText().toString();


                // Intent 생성
                Intent intent = new Intent(WorkDetailActivity.this, Main_WorkDataActivity.class);

                // 데이터를 Intent에 추가
                intent.putExtra("clicked_name", newName);


                // Intent 시작
                startActivity(intent);
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent homeIntent = new Intent(WorkDetailActivity.this, MainActivity.class);
                homeIntent.putExtra("showCommunity", false); // showCommunity 값을 false로 설정
                homeIntent.putExtra("showCalendar", false); // showCalendar 값을 false로 설정
                startActivity(homeIntent); // 홈 화면으로 이동
            }
        });





    }
}