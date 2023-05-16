package com.cookandroid.capstone;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.datepicker.DateSelector;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class BottomSheet_Calendar extends BottomSheetDialogFragment {

    private MaterialCalendarView calendarView;
    Button btnFinish;


    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_bottom_sheet__calendar, container, false);

        calendarView = v.findViewById(R.id.calendar);
        calendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_MULTIPLE);//캘린더 날짜 다중 선택

        //선택된 날짜 workData2로 전달
        btnFinish = v.findViewById(R.id.btnFinish);
        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), WorkData2Activity.class);
                String Date = calendarView.getSelectedDates().toString();
                intent.putExtra("text",Date);
                startActivity(intent);
            }
        });

        return v;
    }

}