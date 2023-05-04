package com.cookandroid.capstone;

import android.os.Bundle;
import android.view.View;
import android.widget.CalendarView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class CalendarActivity extends AppCompatActivity {

    BottomSheet_Calendar_date bottomSheet;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        View calendarView = findViewById(R.id.calendar);
        //캘린더뷰 바텀시트 연결 해야해

//        calendarView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                bottomSheet = new BottomSheet_Calendar_date();
//                bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag());
//            }
//        });
    }
}