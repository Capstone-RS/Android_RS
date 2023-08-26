package com.cookandroid.capstone;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class WorkDetailActivity extends AppCompatActivity {
    private String clickedName; // 클릭한 아이템의 이름을 저장하는 변수
    private String clickedMoney; // 클릭한 아이템의 이름을 저장하는 변수

    // 사용자 로그인 상태 확인
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser currentUser = mAuth.getCurrentUser();

    private ArrayList<String> dateList = new ArrayList<>(); // 날짜 목록을 저장할 리스트
    private ArrayList<String> earningsList = new ArrayList<>(); // 급여 목록을 저장할 리스트
    private MyAdapter adapter; // 커스텀 어댑터

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workdetail);

        TextView name = (TextView) findViewById(R.id.name);
        TextView money = (TextView) findViewById(R.id.money);
        TextView btnEdit = (TextView) findViewById(R.id.btnEdit);
        TextView btnBack = (TextView) findViewById(R.id.btnBack);
        ListView listView = (ListView) findViewById(R.id.listView);

        // 사용자의 고유한 아이디를 가져옵니다.
        String userId = currentUser.getUid();

        Intent intent = getIntent();
        if (intent != null) {
            clickedName = intent.getStringExtra("clicked_name");
            clickedMoney = intent.getStringExtra("clicked_money");
            if (clickedName != null) {
                // 클릭한 아이템의 이름을 EditText에 출력
                name.setText(clickedName);
            }
            if (clickedMoney != null) {
                // 클릭한 아이템의 이름을 EditText에 출력
                money.setText(clickedMoney);
            }
        }

        adapter = new MyAdapter(dateList, earningsList);
        listView.setAdapter(adapter);

        // Firebase 데이터베이스에서 데이터 가져오기
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("Data");
        databaseReference.orderByChild("name").equalTo(clickedName).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dateList.clear(); // 리스트 초기화
                earningsList.clear(); // 급여 리스트 초기화

                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    DataSnapshot datesSnapshot = userSnapshot.child("dates");

                    // dates 하위의 모든 날짜와 급여를 가져와서 리스트에 추가
                    for (DataSnapshot dateSnapshot : datesSnapshot.getChildren()) {
                        String dateWithSpaces = dateSnapshot.child("date").getValue(String.class);
                        Long earningsValue = dateSnapshot.child("earnings").getValue(Long.class);
                        String earnings = (earningsValue != null) ? String.valueOf(earningsValue) : "";
                        String dateWithoutSpaces = dateWithSpaces.trim(); // 앞뒤 공백 제거
                        dateList.add(dateWithoutSpaces);
                        earningsList.add(earnings);
                    }
                }

                // 어댑터에 데이터 변경을 알립니다.
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // 데이터 가져오기 실패 시 처리
            }
        });


        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newName = name.getText().toString();


                // Intent 생성
                Intent intent = new Intent(WorkDetailActivity.this, Main_WorkDataActivity.class);

                // 데이터를 Intent에 추가
                intent.putExtra("clicked_name", newName);


                // Intent 시작
                startActivity(intent);
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent homeIntent = new Intent(WorkDetailActivity.this, MainActivity.class);
                homeIntent.putExtra("showCommunity", false); // showCommunity 값을 false로 설정
                homeIntent.putExtra("showCalendar", false); // showCalendar 값을 false로 설정
                startActivity(homeIntent); // 홈 화면으로 이동
            }
        });


    }

    class MyAdapter extends ArrayAdapter<String> {
        private ArrayList<String> earningsList; // 이 변수를 추가해줍니다.

        MyAdapter(ArrayList<String> dateList, ArrayList<String> earningsList) {
            super(WorkDetailActivity.this, R.layout.workdetail_customlistview, dateList);
            this.earningsList = earningsList;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                view = getLayoutInflater().inflate(R.layout.workdetail_customlistview, parent, false);
            }

            // Firebase에서 가져온 날짜 데이터를 가져와서 설정
            String date = dateList.get(position);
            TextView textViewDate = view.findViewById(R.id.textViewDate);
            textViewDate.setText(date);

            // Firebase에서 가져온 급여 데이터를 가져와서 설정
            String earningsString = this.earningsList.get(position);
            double earnings = 0.0;

            if (!TextUtils.isEmpty(earningsString)) {
                try {
                    earnings = Double.parseDouble(earningsString);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }

            TextView textViewEarnings = view.findViewById(R.id.textViewEarnings);
            textViewEarnings.setText(String.format("%,.0f원", earnings)); // 정수로 변환된 급여를 설정

            // 추가로 원하는 뷰에 데이터를 설정할 수 있습니다.

            return view;
        }
    }
}