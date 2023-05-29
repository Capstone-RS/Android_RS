package com.cookandroid.capstone;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class BottomSheet_Calendar extends BottomSheetDialogFragment {

    private MaterialCalendarView calendarView;
    Button btnFinish;
    private List<CalendarDay> selectedDates;


    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_bottom_sheet__calendar, container, false);
        selectedDates = new ArrayList<>();
        calendarView = v.findViewById(R.id.calendar);
        calendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_MULTIPLE);

        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                if (selected) {
                    selectedDates.add(date); // 선택된 날짜를 리스트에 추가
                } else {
                    selectedDates.remove(date); // 선택이 해제된 경우 리스트에서 제거
                }
            }
        });

        btnFinish = v.findViewById(R.id.btnFinish);
        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 선택된 날짜들을 문자열로 변환
                List<String> selectedDatesStringList = new ArrayList<>();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                for (CalendarDay calendarDay : selectedDates) {
                    String dateString = sdf.format(calendarDay.getDate());
                    selectedDatesStringList.add(dateString);
                }
                String selectedDatesString = TextUtils.join(", ", selectedDatesStringList);

                Intent intent = new Intent(getActivity(), WorkData2Activity.class);
                intent.putExtra("test", selectedDatesString);
                startActivity(intent);
            }
        });

        return v;
    }
}