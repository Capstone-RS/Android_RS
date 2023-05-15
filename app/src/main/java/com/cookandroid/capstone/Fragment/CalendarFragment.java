package com.cookandroid.capstone.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.cookandroid.capstone.BottomSheet_Calendar_date;
import com.cookandroid.capstone.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

public class CalendarFragment extends Fragment {

    BottomSheet_Calendar_date bottomSheet;
    private MaterialCalendarView calendarView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.activity_calendar, container, false);
        calendarView = v.findViewById(R.id.calendar);


        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                bottomSheet = new BottomSheet_Calendar_date();
                bottomSheet.show(getFragmentManager(), bottomSheet.getTag());
            }
        });

        return v;
    }
}