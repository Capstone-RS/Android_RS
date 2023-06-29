package com.cookandroid.capstone.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.cookandroid.capstone.ChooseWorkActivity;
import com.cookandroid.capstone.R;
import com.cookandroid.capstone.WorkDataActivity;
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
    CustomAdapter adapter;
    Button btnAdd;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new CustomAdapter(getContext(), R.layout.calendar_customlistview, new ArrayList<String>());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_calendar, container, false);
        calendarView = v.findViewById(R.id.calendar);
        selectedDateTextView = v.findViewById(R.id.btn);
        workList = v.findViewById(R.id.workList);
        btnAdd = v.findViewById(R.id.btnAdd);

        //근무 추가 버튼
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedDate = selectedDateTextView.getText().toString();

                // 선택된 날짜를 다른 활동으로 전달하기 위한 인텐트 생성
                Intent intent = new Intent(getActivity(), ChooseWorkActivity.class);
                intent.putExtra("selectedDate", selectedDate); // 선택된 날짜를 인텐트에 첨부

                startActivity(intent);
            }
        });

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
                                    HashMap<String, Object> dateData = dateSnapshot.getValue(new GenericTypeIndicator<HashMap<String, Object>>() {});
                                    if (dateData != null) {
                                        String date = (String) dateData.get("date");
                                        if (date != null && date.trim().equals(formattedDate)) {
                                            String name = snapshot.child("name").getValue(String.class);
                                            if (name != null) {
                                                startTimes.add(name);
                                            }
                                            break;
                                        }
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
        private List<String> items;
        private DatabaseReference databaseRef;

        public CustomAdapter(Context context, int resource, List<String> items) {
            super(context, resource, items);
            this.context = context;
            this.items = items;
            this.databaseRef = FirebaseDatabase.getInstance().getReference("Data");
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.calendar_customlistview, parent, false);

            TextView textView1 = rowView.findViewById(R.id.textView1);
            TextView textView2 = rowView.findViewById(R.id.textView2);

            String itemName = items.get(position);
            textView1.setText(itemName);

            DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("Data");

            Query query = databaseRef.orderByChild("name").equalTo(itemName);
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    DecimalFormat decimalFormat = new DecimalFormat("#,##0");
                    double totalEarnings = 0.0;

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        DataSnapshot datesSnapshot = snapshot.child("dates");
                        if (datesSnapshot.exists()) {
                            for (DataSnapshot dateSnapshot : datesSnapshot.getChildren()) {
                                String date = dateSnapshot.child("date").getValue(String.class);
                                String money = dateSnapshot.child("money").getValue(String.class);
                                String startTime = dateSnapshot.child("startTime").getValue(String.class);
                                String endTime = dateSnapshot.child("endTime").getValue(String.class);
                                String restTime = dateSnapshot.child("restTime").getValue(String.class);

                                // 선택된 날짜와 일치하는 경우에만 계산
                                if (date != null && date.trim().equals(selectedDateTextView.getText().toString().trim())) {
                                    // startTime과 endTime을 시간 형식("HH:mm")에서 분으로 변환
                                    String[] startTimeParts = startTime.split(":");
                                    int startHour = Integer.parseInt(startTimeParts[0]);
                                    int startMinute = Integer.parseInt(startTimeParts[1]);

                                    String[] endTimeParts = endTime.split(":");
                                    int endHour = Integer.parseInt(endTimeParts[0]);
                                    int endMinute = Integer.parseInt(endTimeParts[1]);

                                    // restTime에서 "분" 문자열 제거 및 숫자 값만 추출
                                    int restMinutes = 0; // 기본값 설정

                                    if (restTime != null && !restTime.trim().isEmpty()) {
                                        String restTimeValue = restTime.replace("분", "").trim();
                                        restMinutes = Integer.parseInt(restTimeValue);
                                    }

                                    // 근무 시간 계산
                                    int totalMinutes = (endHour * 60 + endMinute) - (startHour * 60 + startMinute) - restMinutes;

                                    // 시급 가져오기 (Firebase에서 "money" 필드의 값을 가져옴)
                                    double hourlyRate = Double.parseDouble(money);
                                    double earnings = (double) totalMinutes / 60 * hourlyRate;

                                    totalEarnings += earnings;
                                }
                            }
                        }
                    }

                    String formattedEarnings = decimalFormat.format(totalEarnings);
                    textView2.setText(formattedEarnings + "원");
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