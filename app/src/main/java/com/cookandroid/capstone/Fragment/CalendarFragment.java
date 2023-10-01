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
import com.cookandroid.capstone.WorkDetail2Activity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
    private String userId;


    // 사용자 로그인 상태 확인
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser currentUser = mAuth.getCurrentUser();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userId = currentUser.getUid();
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
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd (EEE)", Locale.getDefault());
                String formattedDate = sdf.format(date.getDate());
                selectedDateTextView.setText(formattedDate);
                DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("Data");

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
            this.databaseRef = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("Data");
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.calendar_customlistview, parent, false);

            TextView textView1 = rowView.findViewById(R.id.textView1);
            TextView textView2 = rowView.findViewById(R.id.textView2);

            String itemName = items.get(position);
            textView1.setText(itemName);

            DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("Data");

            Query query = databaseRef.orderByChild("name").equalTo(itemName);
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        DataSnapshot datesSnapshot = snapshot.child("dates");
                        if (datesSnapshot.exists()) {
                            for (DataSnapshot dateSnapshot : datesSnapshot.getChildren()) {
                                String date = dateSnapshot.child("date").getValue(String.class);
                                Double earnings = dateSnapshot.child("earnings").getValue(Double.class);

                                // 선택된 날짜와 일치하는 경우 earnings 값을 가져와서 포맷해서 출력
                                if (date != null && date.trim().equals(selectedDateTextView.getText().toString().trim())) {
                                    DecimalFormat decimalFormat = new DecimalFormat("#,###원");
                                    String formattedEarnings = decimalFormat.format(earnings);
                                    textView2.setText(formattedEarnings);
                                    // 클릭 이벤트를 위해 값을 태그로 저장
                                    rowView.setTag(formattedEarnings);
                                }
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // 에러 처리 로직을 작성해주세요.
                }
            });

            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 클릭한 아이템에 해당하는 다른 페이지로 이동하는 코드를 작성합니다.
                    // 저장된 earnings 값을 가져옵니다.
                    String formattedEarnings = (String) rowView.getTag();
                    // 선택된 날짜 가져오기
                    String selectedDate = selectedDateTextView.getText().toString();

                    Intent intent = new Intent(getContext(), WorkDetail2Activity.class);
                    intent.putExtra("itemName", itemName); // 클릭한 아이템의 이름을 인텐트에 첨부합니다.
                    intent.putExtra("formattedEarnings", formattedEarnings); // formattedEarnings 값을 인텐트에 첨부합니다.
                    intent.putExtra("selectedDate", selectedDate); // 선택된 날짜를 인텐트에 첨부
                    startActivity(intent);
                }
            });
            return rowView;
        }
    }
}