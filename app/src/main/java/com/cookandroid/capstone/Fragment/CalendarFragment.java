package com.cookandroid.capstone.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.cookandroid.capstone.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class CalendarFragment extends Fragment {

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();

    private MaterialCalendarView calendarView;
    private TextView selectedDateTextView;
    ListView workList;
    private ArrayAdapter<String> adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_calendar, container, false);
        calendarView = v.findViewById(R.id.calendar);
        selectedDateTextView = v.findViewById(R.id.btn);
        workList = v.findViewById(R.id.selectStartTime);
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1);

        if (workList == null) {
            Log.e("CalendarFragment", "workList is null");
        } else {
            workList.setAdapter(adapter);
            // 이후의 코드 계속 실행
        }

        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                String formattedDate = sdf.format(date.getDate());
                selectedDateTextView.setText(formattedDate);
                DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("Data");

                Query query = databaseRef.orderByChild("dates");
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        List<String> startTimes = new ArrayList<>();

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            DataSnapshot datesSnapshot = snapshot.child("dates");
                            if (datesSnapshot.exists()) {
                                for (DataSnapshot dateSnapshot : datesSnapshot.getChildren()) {
                                    String date = dateSnapshot.getValue(String.class);
                                    if (date != null && date.trim().equals(formattedDate)) {
                                        String name = snapshot.child("name").getValue(String.class);
                                        if (name != null) {
                                            startTimes.add(name);
                                        }
                                        break;
                                    }
                                }
                            } else {
                                Log.e("dates", "dates is null");
                            }
                        }

                        if (adapter != null) {
                            adapter.clear();
                            adapter.addAll(startTimes);
                            adapter.notifyDataSetChanged();
                        } else {
                            Log.e("CalendarFragment", "adapter is null");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // 에러 처리 로직을 작성해주세요.
                    }
                });
            }
        });


        return v;
    }
}