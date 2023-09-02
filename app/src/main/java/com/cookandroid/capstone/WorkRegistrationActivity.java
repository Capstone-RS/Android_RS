package com.cookandroid.capstone;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.cookandroid.capstone.Fragment.CalendarFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Locale;

public class WorkRegistrationActivity extends AppCompatActivity {

    private DatabaseReference databaseRef;
    CalendarFragment calendarFragment;

    // 사용자 로그인 상태 확인
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser currentUser = mAuth.getCurrentUser();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workregistration);

        // 사용자 ID 가져오기
        String userId = currentUser.getUid();

        TextView workDay = findViewById(R.id.workDay);
        EditText money = findViewById(R.id.money);
        TextView startTime = findViewById(R.id.startTime);
        TextView endTime = findViewById(R.id.endTime);
        Button btnNext = findViewById(R.id.btnNext);
        Button btnBack = findViewById(R.id.btnBack);

        // 선택된 날짜를 받아옴
        String selectedDate = getIntent().getStringExtra("selectedDate");
        String selectedItem = getIntent().getStringExtra("selectedItem");

        workDay.setText(selectedDate);

        //스피너
        Spinner spnPay = (Spinner) findViewById(R.id.spnPay);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.array_workdata2_howpay, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnPay.setAdapter(adapter);


        Spinner spnRestTime = (Spinner) findViewById(R.id.spnRestTime);
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this,
                R.array.array_workdata2_rest, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnRestTime.setAdapter(adapter1);


        //타임피커 생성
        startTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog dialog = new TimePickerDialog(WorkRegistrationActivity.this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        // TextView에 출력할 형식 지정
                        startTime.setText(String.format(Locale.US, "%02d:%02d", selectedHour, selectedMinute));
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
                TimePickerDialog dialog = new TimePickerDialog(WorkRegistrationActivity.this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        // TextView에 출력할 형식 지정
                        endTime.setText(String.format(Locale.US, "%02d:%02d", selectedHour, selectedMinute));
                    }
                }, hour, minute, false); // true의 경우 24시간 형식의 TimePicker 출현
                dialog.setTitle("Select Time");
                dialog.show();
            }
        });





        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedStartTime = startTime.getText().toString();
                String selectedEndTime = endTime.getText().toString();
                String selectedMoney = money.getText().toString();
                String selectedSpnPay = spnPay.getSelectedItem().toString();
                String selectedRestTime = spnRestTime.getSelectedItem().toString();

                double earnings;

                if ("시급".equals(selectedSpnPay)) {
                    // 시급인 경우에만 계산
                    earnings = calculateEarnings(selectedStartTime, selectedEndTime, selectedRestTime, selectedMoney);
                } else {
                    // 시급이 아닌 경우, 입력된 money 값을 그대로 사용
                    earnings = Double.parseDouble(selectedMoney);
                }

                // Firebase에서 데이터 가져오기
                DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("Data");

                // 선택된 아이템의 키 가져오기
                String selectedItemKey = getIntent().getStringExtra("selectedItem");

                // 데이터베이스에 저장할 WorkData 객체 생성
                WorkData workData = new WorkData(selectedSpnPay, selectedRestTime, selectedDate, selectedEndTime, selectedMoney, selectedStartTime);
                workData.setEarnings(earnings); // 계산된 급여 값을 설정

                // 데이터베이스에서 name 값이 selectedItemKey와 일치하는 데이터를 찾기 위한 쿼리 생성
                Query query = databaseRef.orderByChild("name").equalTo(selectedItemKey);

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot itemSnapshot : dataSnapshot.getChildren()) {
                            // 해당 데이터의 dates 하위에 새로운 데이터 추가

                            // dates 하위의 마지막 자식의 키 값을 가져오는 메소드
                            String lastChildKey = getLastChildKey(itemSnapshot.child("dates"));

                            // 새로운 자식 키를 생성하는 메소드
                            String newChildKey = generateNewChildKey(lastChildKey);

                            // dates 하위의 새로운 자식 노드 생성
                            DatabaseReference newDateRef = itemSnapshot.child("dates").child(newChildKey).getRef();
                            newDateRef.setValue(workData);
                        }

                        // 저장이 완료되었음을 로그로 확인
                        Log.d("WorkRegistrationActivity", "Data saved successfully.");

                        // 추가적인 작업 또는 화면 전환 등을 수행할 수 있습니다.
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // 데이터베이스 조회 중 오류 발생 시 처리할 내용을 여기에 작성합니다.
                    }
                });
                Intent intent = new Intent(WorkRegistrationActivity.this, MainActivity.class);
                intent.putExtra("showCalendar", true); // calendarFragment를 표시하기 위한 정보 전달
                startActivity(intent);
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WorkRegistrationActivity.this, MainActivity.class);
                intent.putExtra("showCalendar", true); // calendarFragment를 표시하기 위한 정보 전달
                startActivity(intent);
            }
        });







    }

    // dates 하위의 마지막 자식의 키 값을 가져오는 메소드
    private String getLastChildKey(DataSnapshot datesSnapshot) {
        String lastChildKey = "";
        for (DataSnapshot childSnapshot : datesSnapshot.getChildren()) {
            lastChildKey = childSnapshot.getKey();
        }
        return lastChildKey;
    }

    // 새로운 자식 키를 생성하는 메소드
    private String generateNewChildKey(String lastChildKey) {
        int lastChildIndex = Integer.parseInt(lastChildKey.replaceAll("[^0-9]+", ""));
        int newChildIndex = lastChildIndex + 1;
        return "Date" + newChildIndex;
    }


    private double calculateEarnings(String startTime, String endTime, String restTime, String money) {
        // 시작 시간 및 종료 시간을 분으로 변환
        int startMinutes = calculateTotalMinutes(startTime);
        int endMinutes = calculateTotalMinutes(endTime);

        // 휴식 시간을 분으로 변환
        int restMinutes = Integer.parseInt(restTime.replace("분", "").trim());

        // 근무 시간 계산
        int workMinutes = endMinutes - startMinutes - restMinutes;

        // 시급과 근무 시간을 기반으로 급여 계산
        double hourlyRate = Double.parseDouble(money);
        double earnings = (workMinutes / 60.0) * hourlyRate;

        // 소수점 아래를 제거한 정수값으로 변환하여 반환
        return Math.floor(earnings);
    }

    private int calculateTotalMinutes(String time) {
        String[] timeParts = time.split(":");
        int hours = Integer.parseInt(timeParts[0]);
        int minutes = Integer.parseInt(timeParts[1]);
        return hours * 60 + minutes;
    }
}