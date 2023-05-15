package com.cookandroid.capstone;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;

import org.w3c.dom.Text;

import java.util.Calendar;

public class WorkData2Activity extends AppCompatActivity {

    BottomSheet_Calendar bottomSheet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workdata2);

        TextView btnBack = (TextView) findViewById(R.id.btnBack);
        Button btnNext = (Button) findViewById(R.id.btnNext);
        Button btnHome = (Button) findViewById(R.id.btnHome);
        TextView btnDate = (findViewById(R.id.btnDate));
        TextView startTime = (TextView)findViewById(R.id.startTime) ;
        TextView endTime = (TextView)findViewById(R.id.endTime);


        //스피너
        Spinner spnPay = (Spinner) findViewById(R.id.spnPay);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.array_workdata2_howpay, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnPay.setAdapter(adapter);


        Spinner spnRestTime = (Spinner) findViewById(R.id.spnRestTime);
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this,
                R.array.array_workdata2_rest, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnRestTime.setAdapter(adapter1);


        //달력바텀시트 연결
        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheet = new BottomSheet_Calendar();
                bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag());
            }
        });

        //타임피커
        startTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog dialog = new TimePickerDialog(WorkData2Activity.this,android.R.style.Theme_Holo_Light_Dialog_NoActionBar,new TimePickerDialog.OnTimeSetListener(){
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        String state = "AM";
                        // 선택한 시간이 12를 넘을경우 "PM"으로 변경 및 -12시간하여 출력 (ex : PM 6시 30분)
                        if (selectedHour > 12) {
                            selectedHour -= 12;
                            state = "PM";
                        }
                        // TextView에 출력할 형식 지정
                       startTime.setText(state + " " + selectedHour + ": " + selectedMinute);
                    }
                }, hour, minute, false); // true의 경우 24시간 형식의 TimePicker 출현
                dialog.setTitle("Select Time");
                dialog.show();
            }
        });
        endTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog dialog = new TimePickerDialog(WorkData2Activity.this,android.R.style.Theme_Holo_Light_Dialog_NoActionBar,new TimePickerDialog.OnTimeSetListener(){
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        String state = "AM";
                        // 선택한 시간이 12를 넘을경우 "PM"으로 변경 및 -12시간하여 출력 (ex : PM 6시 30분)
                        if (selectedHour > 12) {
                            selectedHour -= 12;
                            state = "PM";
                        }
                        // TextView에 출력할 형식 지정
                        endTime.setText(state + " " + selectedHour + ": " + selectedMinute);
                    }
                }, hour, minute, false); // true의 경우 24시간 형식의 TimePicker 출현
                dialog.setTitle("Select Time");
                dialog.show();
            }
        });



        //버튼
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), WorkDataActivity.class);
                startActivity(intent);
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }

}