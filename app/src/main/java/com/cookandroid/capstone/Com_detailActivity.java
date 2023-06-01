package com.cookandroid.capstone;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

public class Com_detailActivity extends AppCompatActivity implements BottomSheet_com.BottomSheetListener {

    TextView btn_back;
    Button btn_reg;
    // 바텀 다이얼로그 띄우기 버튼
    private Button btn_open_bt_sheet;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_detail);

        btn_back = findViewById(R.id.btn_Back);
        btn_open_bt_sheet = findViewById(R.id.btn_ex);

        btn_open_bt_sheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                BottomSheet_com bottomSheetDialog = new BottomSheet_com();
                bottomSheetDialog.show(getSupportFragmentManager(), "exampleBottomSheet");
            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Com_listActivity.class);
                startActivity(intent);
            }
        });

        btn_reg = findViewById(R.id.regg_button);

        btn_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Com_listActivity.class);
                startActivity(intent);
            }
        });

    }


    @Override
    public void onButtonClicked(String text) {
    }
}
