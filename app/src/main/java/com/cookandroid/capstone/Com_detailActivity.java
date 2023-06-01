package com.cookandroid.capstone;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Com_detailActivity extends Activity {

    TextView btn_back;
    Button btn_reg;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_detail);

        btn_back = findViewById(R.id.btn_Back);

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
}
