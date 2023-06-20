package com.cookandroid.capstone.Fragment;

import android.content.Context;
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

import java.text.DecimalFormat;
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
    CustomAdapter adapter ;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new CustomAdapter(getContext(), R.layout.calendar_customlistview);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_calendar, container, false);
        calendarView = v.findViewById(R.id.calendar);
        selectedDateTextView = v.findViewById(R.id.btn);
        workList = v.findViewById(R.id.workList);
        adapter = new CustomAdapter(getContext(), R.layout.calendar_customlistview);

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

    public class CustomAdapter extends ArrayAdapter<String> {
        private Context context;

        public CustomAdapter(Context context, int resource) {
            super(context, resource);
            this.context = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.calendar_customlistview, parent, false);

            TextView textView1 = rowView.findViewById(R.id.textView1);
            TextView textView2 = rowView.findViewById(R.id.textView2); // 추가된 부분

            String itemName = getItem(position); // 해당 position에 있는 데이터 가져오기
            textView1.setText(itemName); // 데이터를 textView1에 설정
            textView2.setText("");

            DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("Data");
            Query query = databaseRef.orderByChild("name").equalTo(itemName);

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    DecimalFormat decimalFormat = new DecimalFormat("#,##0");// 소수점 이하 자리수 제거
                    textView2.setText("");

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String money = snapshot.child("money").getValue(String.class);
                        String startTime = snapshot.child("startTime").getValue(String.class);
                        String endTime = snapshot.child("endTime").getValue(String.class);
                        String restTime = snapshot.child("RestTime").getValue(String.class);

                        // startTime과 endTime을 시간 형식("HH:mm")에서 분으로 변환
                        String[] startTimeParts = startTime.split(":");
                        int startHour = Integer.parseInt(startTimeParts[0]);
                        int startMinute = Integer.parseInt(startTimeParts[1]);

                        String[] endTimeParts = endTime.split(":");
                        int endHour = Integer.parseInt(endTimeParts[0]);
                        int endMinute = Integer.parseInt(endTimeParts[1]);

                        // restTime에서 "분" 문자열 제거 및 숫자 값만 추출
                        int restMinutes = 0; // 기본값 설정

                        if (restTime != null) {
                            String restTimeString = restTime.replace("분", "").trim();
                            restMinutes = Integer.parseInt(restTimeString);
                        }

                        // startTime과 endTime을 분으로 변환
                        int startTotalMinutes = startHour * 60 + startMinute;
                        int endTotalMinutes = endHour * 60 + endMinute;

                        // 휴게 시간을 분으로 변환
                        int restTotalMinutes = restMinutes;

                        // 근무 시간 계산 (분 단위)에서 휴게 시간 제외
                        int workMinutes = endTotalMinutes - startTotalMinutes - restTotalMinutes;

                        // 시급 계산
                        double hourlyRate = Double.parseDouble(money);

                        // 근무 시간을 시간 단위로 변환
                        double workHours = workMinutes / 60.0;

                        // 총 급여 계산
                        double earnings = hourlyRate * workHours;

                        // 시급을 원하는 형식으로 포맷팅
                        String formattedEarnings = decimalFormat.format(earnings);

                        // 이후의 코드는 동일하게 유지
                        String previousText = textView2.getText().toString();
                        String newText = previousText + formattedEarnings + "\n";
                        textView2.setText(newText);
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // 에러 처리 로직을 작성해주세요.
                }
            });

            return rowView;
        }
    }
}