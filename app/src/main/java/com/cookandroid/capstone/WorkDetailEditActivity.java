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

        Spinner spnPay = (Spinner) findViewById(R.id.spnPay);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.array_workdatadetailedit_howpay, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnPay.setAdapter(adapter);

        Spinner spnRestTime = (Spinner) findViewById(R.id.spnRestTime);
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this,
                R.array.array_workdatadetailedit_rest, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnRestTime.setAdapter(adapter1);

        
    }
}