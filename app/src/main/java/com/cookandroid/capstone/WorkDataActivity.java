package com.cookandroid.capstone;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class WorkDataActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workdata);

        TextView btnBack = (TextView) findViewById(R.id.btnBack);
        Button btnNext = (Button) findViewById(R.id.btnNext);
        Button btnHome = (Button) findViewById(R.id.btnHome);
        EditText name = (EditText) findViewById(R.id.name);
        //사용할 스피너 선언
        Spinner spn1 = (Spinner) findViewById(R.id.spn1);
        Spinner spn2 = (Spinner) findViewById(R.id.spn2);
        Spinner spnInsurance = (Spinner)findViewById(R.id.spnInsurance);
        //토글버튼
        Switch swTax = (Switch) findViewById(R.id.swTax);
        Switch swInsurance = (Switch) findViewById(R.id.swInsurance);



        //스피너
        //스피너 workperiod(한달, 일주일) Adapter로 연결
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.array_workdata_workperiod, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spn1.setAdapter(adapter); //spn1 위치에 adapter 연결

        //스피너 paydate(1일~) Adapter1로 연결
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this,
                R.array.array_workdata_paydate, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //스피너 payweek(월~일) Adapter2로 연결
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                R.array.array_workdata_payweek, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //스피너 Adapter3로 연결
        ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(this,
                R.array.array_workdata_insurance, android.R.layout.simple_spinner_item);
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnInsurance.setAdapter(adapter3); //spnInsurance 위치에 adapter3 연결

        //index 값을 사용하여 spn1에 스피너 값 선택시 해당 스피너 출력 되도록 구현
        spn1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int item = spn1.getSelectedItemPosition(); // spn1에서 선택한 값 index번호를 int형으로 저장
                if(item == 0){
                    spn2.setAdapter(adapter1);
                } else if (item == 1){
                    spn2.setAdapter(adapter2);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });





        //뒤로가기버튼
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
        //다음버튼
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String getName = name.getText().toString();
                String getWorkPeriod = spn1.getSelectedItem().toString();
                String getPayDay = spn2.getSelectedItem().toString();
                boolean isTaxEnabled = swTax.isChecked();
                Intent intent = new Intent(getApplicationContext(), WorkData2Activity.class);
                intent.putExtra("name",getName);
                intent.putExtra("workPeriod",getWorkPeriod);
                intent.putExtra("payDay",getPayDay);
                intent.putExtra("isTaxEnabled", isTaxEnabled); //토글버튼의 상태 넘기기
                startActivity(intent);
            }
        });
        //나중에하기버튼
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }

}