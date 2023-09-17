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
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.Calendar;
import java.util.Locale;

public class WorkDetailEditActivity extends AppCompatActivity {

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();
    // 사용자 로그인 상태 확인
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser currentUser = mAuth.getCurrentUser();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workdetail_edit);

        // 사용자 ID 가져오기
        String userId = currentUser.getUid();

        TextView date = findViewById(R.id.date);
        EditText money = findViewById(R.id.money);
        TextView startTime = findViewById(R.id.startTime);
        TextView endTime = findViewById(R.id.endTime);
        Button btnNext = findViewById(R.id.btnNext);
        TextView btnCorrect = findViewById(R.id.btnCorrect);
        Spinner spnPay = findViewById(R.id.spnPay);
        Spinner spnRestTime = findViewById(R.id.spnRestTime);
        Switch swPlusPay = findViewById(R.id.swPlusPay);




        Intent intent = getIntent();
        String itemName = intent.getStringExtra("itemName");
        String selectedDate = intent.getStringExtra("selectedDate");

        if (selectedDate != null) {
            date.setText(selectedDate);

            DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("Data");
            Query query = databaseRef.orderByChild("name").equalTo(itemName);
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        DataSnapshot datesSnapshot = snapshot.child("dates");
                        if (datesSnapshot.exists()) {
                            for (DataSnapshot dateSnapshot : datesSnapshot.getChildren()) {
                                String dateValue = dateSnapshot.child("date").getValue(String.class);
                                String startTimeValue = dateSnapshot.child("startTime").getValue(String.class);
                                String endTimeValue = dateSnapshot.child("endTime").getValue(String.class);
                                String moneyValue = dateSnapshot.child("money").getValue(String.class);
                                String payMethod = dateSnapshot.child("pay").getValue(String.class);
                                String restTimeMethod = dateSnapshot.child("restTime").getValue(String.class);
                                // 여기에 swPlusPay 값 가져오는 코드를 추가
                                Boolean swPlusPayValue = dateSnapshot.child("swPlusPay").getValue(Boolean.class);
                                if (swPlusPayValue != null) {
                                    swPlusPay.setChecked(swPlusPayValue);
                                }

                                if (dateValue != null && dateValue.trim().equals(selectedDate.trim())) {
                                    if (startTimeValue != null) {
                                        startTime.setText(startTimeValue);
                                    }
                                    if (endTimeValue != null) {
                                        endTime.setText(endTimeValue);
                                    }
                                    if (moneyValue != null) {
                                        money.setText(moneyValue);
                                    }
                                    // "Pay" 값에 따라 다른 스피너 값 설정
                                    if (payMethod != null) {
                                        int arrayResource;
                                        if (payMethod.equals("시급")) {
                                            arrayResource = R.array.array_workdata2_pay_method1;
                                        } else if (payMethod.equals("일급")) {
                                            arrayResource = R.array.array_workdata2_pay_method2;
                                        } else {
                                            // 기본적으로 시급 배열 사용
                                            arrayResource = R.array.array_workdata2_pay_method1;
                                        }

                                        ArrayAdapter<CharSequence> payAdapter = ArrayAdapter.createFromResource(WorkDetailEditActivity.this,
                                                arrayResource, android.R.layout.simple_spinner_item);
                                        payAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                        spnPay.setAdapter(payAdapter);
                                    }

                                    // "restTime" 값에 따라 다른 스피너 값 설정
                                    if (restTimeMethod != null) {
                                        int arrayResource;
                                        if (restTimeMethod.equals("10분")) {
                                            arrayResource = R.array.array_workdata2_restTime_method1;
                                        } else if (restTimeMethod.equals("20분")) {
                                            arrayResource = R.array.array_workdata2_restTime_method2;
                                        }else if (restTimeMethod.equals("30분")) {
                                                arrayResource = R.array.array_workdata2_restTime_method3;
                                        } else {
                                            // 기본적으로 시급 배열 사용
                                            arrayResource = R.array.array_workdata2_restTime_method1;
                                        }

                                        ArrayAdapter<CharSequence> payAdapter = ArrayAdapter.createFromResource(WorkDetailEditActivity.this,
                                                arrayResource, android.R.layout.simple_spinner_item);
                                        payAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                        spnRestTime.setAdapter(payAdapter);
                                    }
                                    break;
                                }
                            }
                        } else {
                            Log.e("WorkDetailEditActivity", "dates is null");
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // 에러 처리 로직을 작성해주세요.
                }
            });
        }

        btnCorrect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("Data");
                Query query = databaseRef.orderByChild("name").equalTo(itemName);
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            DataSnapshot datesSnapshot = snapshot.child("dates");
                            if (datesSnapshot.exists()) {
                                for (DataSnapshot dateSnapshot : datesSnapshot.getChildren()) {
                                    String dateValue = dateSnapshot.child("date").getValue(String.class);

                                    if (dateValue != null && dateValue.trim().equals(selectedDate.trim())) {
                                        // 해당 데이터 삭제
                                        dateSnapshot.getRef().removeValue();
                                        break;
                                    }
                                }
                            } else {
                                Log.e("WorkDetailEditActivity", "dates is null");
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // 에러 처리 로직을 작성해주세요.
                    }
                });
                Intent intent = new Intent(WorkDetailEditActivity.this, MainActivity.class);
                intent.putExtra("showCalendar", true); // calendarFragment를 표시하기 위한 정보 전달
                startActivity(intent);
            }
        });




        //타임피커 생성
        startTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog dialog = new TimePickerDialog(WorkDetailEditActivity.this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar, new TimePickerDialog.OnTimeSetListener() {
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
                TimePickerDialog dialog = new TimePickerDialog(WorkDetailEditActivity.this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar, new TimePickerDialog.OnTimeSetListener() {
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
            public void onClick(View view) {
                // 수정한 데이터 가져오기
                String selectedDate = date.getText().toString();
                String startTimeValue = startTime.getText().toString();
                String endTimeValue = endTime.getText().toString();
                String moneyValue = money.getText().toString();
                String payMethod = spnPay.getSelectedItem().toString();
                String restTimeMethod = spnRestTime.getSelectedItem().toString();

                // 수정한 값들을 가져옵니다.
                String selectedStartTime = startTime.getText().toString();
                String selectedEndTime = endTime.getText().toString();
                String selectedMoney = money.getText().toString();
                String selectedRestTime = spnRestTime.getSelectedItem().toString();

                // 스위치의 현재 상태 가져오기
                boolean isPlusPay = swPlusPay.isChecked();

                // 수정된 값을 기반으로 급여 계산
                double earnings = calculateEarnings(selectedStartTime, selectedEndTime, selectedRestTime, selectedMoney, isPlusPay);


                // DatabaseReference 참조 가져오기
                DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("Data");
                Query query = databaseRef.orderByChild("name").equalTo(itemName);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            DataSnapshot datesSnapshot = snapshot.child("dates");
                            if (datesSnapshot.exists()) {
                                for (DataSnapshot dateSnapshot : datesSnapshot.getChildren()) {
                                    String dateValue = dateSnapshot.child("date").getValue(String.class);

                                    if (dateValue != null && dateValue.trim().equals(selectedDate.trim())) {
                                        // 해당 데이터 수정
                                        dateSnapshot.child("startTime").getRef().setValue(startTimeValue);
                                        dateSnapshot.child("endTime").getRef().setValue(endTimeValue);
                                        dateSnapshot.child("money").getRef().setValue(moneyValue);
                                        dateSnapshot.child("pay").getRef().setValue(payMethod);
                                        dateSnapshot.child("restTime").getRef().setValue(restTimeMethod);
                                        dateSnapshot.child("earnings").getRef().setValue(earnings); // 수정된 "earnings" 값 설정
                                        dateSnapshot.child("swPlusPay").getRef().setValue(isPlusPay);

                                        break;
                                    }
                                }
                            } else {
                                Log.e("WorkDetailEditActivity", "dates is null");
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // 에러 처리 로직을 작성해주세요.
                    }
                });

                // 수정 후 다시 MainActivity로 이동
                Intent intent = new Intent(WorkDetailEditActivity.this, MainActivity.class);
                intent.putExtra("showCalendar", true); // calendarFragment를 표시하기 위한 정보 전달
                startActivity(intent);
            }
        });

    }
    // 연장 근무 및 급여 계산 함수
    private double calculateEarnings(String startTime, String endTime, String restTime, String money, boolean isPlusPay) {
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

        // 연장근무 시간과 급여 계산
        if (isPlusPay && workMinutes > 8 * 60) { // 8시간 초과 근무일 때 연장수당 적용
            int overtimeMinutes = workMinutes - 8 * 60; // 8시간을 초과한 근무 시간
            earnings += (overtimeMinutes / 60.0) * hourlyRate * 1.5; // 연장근무 수당 추가
        }

        // 소수점 아래를 제거한 정수값으로 변환하여 반환
        return Math.floor(earnings);
    }

    // 시간을 분으로 변환하는 메서드
    private int calculateTotalMinutes(String time) {
        String[] timeParts = time.split(":");
        int hours = Integer.parseInt(timeParts[0]);
        int minutes = Integer.parseInt(timeParts[1]);
        return hours * 60 + minutes;
    }
}
