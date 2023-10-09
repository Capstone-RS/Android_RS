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

    private double earnings; // earnings 변수를 여기에 선언합니다.
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
        Switch swNightPay = findViewById(R.id.swNightPay);
        Switch swHolliDayPay = findViewById(R.id.swHollidayPay);





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
                                Boolean swNightPayValue = dateSnapshot.child("swNightPay").getValue(Boolean.class);
                                if (swNightPayValue != null) {
                                    swNightPay.setChecked(swNightPayValue);
                                }
                                Boolean swHolliDayPayValue = dateSnapshot.child("swHolliDayPay").getValue(Boolean.class);
                                if (swHolliDayPayValue != null) {
                                    swHolliDayPay.setChecked(swHolliDayPayValue);
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
                // 스위치의 현재 상태 가져오기
                boolean isNightPay = swNightPay.isChecked();
                boolean isHolliDayPay = swHolliDayPay.isChecked();

                // 수정된 값을 기반으로 급여 계산
                earnings = calculateEarnings(selectedStartTime, selectedEndTime, selectedRestTime, selectedMoney, isPlusPay);

                // 야간 수당 계산 스위치 상태 확인 후 계산
                if (isNightPay) {
                    if (isNightTime(selectedStartTime, selectedEndTime)) {
                        earnings += calculateNightPay(selectedStartTime, selectedEndTime, selectedMoney);
                    }
                }
                // 휴일 수당 계산 - 버튼이 눌려있을 때만 계산
                if (isHolliDayPay) {
                    earnings += calculateHolidayPay(selectedDate, selectedMoney);
                }

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
                                        dateSnapshot.child("swNightPay").getRef().setValue(isNightPay);
                                        dateSnapshot.child("swHolliDayPay").getRef().setValue(swHolliDayPay.isChecked());
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
    // 근무 시간이 야간 수당 적용 시간대인지 확인하는 메소드
    private boolean isNightTime(String startTime, String endTime) {
        int startHour = Integer.parseInt(startTime.split(":")[0]);
        int endHour = Integer.parseInt(endTime.split(":")[0]);
        return (startHour >= 22 || startHour <= 5 || endHour >= 22 || endHour <= 5);
    }

    // 야간 수당을 계산하는 메소드
    private double calculateNightPay(String startTime, String endTime, String hourlyRate) {
        int startHour = Integer.parseInt(startTime.split(":")[0]);
        int endHour = Integer.parseInt(endTime.split(":")[0]);
        int nightHours = 0;

        // 근무 시간 중 야간 수당 시간대 계산
        for (int hour = startHour; hour < endHour; hour++) {
            if (hour >= 22 || hour <= 5) {
                nightHours++;
            }
        }

        // 야간 수당 계산 및 반환
        double rate = Double.parseDouble(hourlyRate);
        return (nightHours * rate * 0.5);
    }

    // 휴일 수당을 계산하는 메소드
    private double calculateHolidayPay(String selectedDate, String hourlyRate) {
        // 선택한 날짜에서 괄호 안의 요일 부분을 추출합니다.
        int startIndex = selectedDate.indexOf('(');
        int endIndex = selectedDate.indexOf(')');
        if (startIndex != -1 && endIndex != -1) {
            String dayOfWeek = selectedDate.substring(startIndex + 1, endIndex);

            // 휴일 수당 계산을 하려는 요일이 (Sun) 또는 (Sat)인 경우에만 계산합니다.
            if (dayOfWeek.equals("Sun") || dayOfWeek.equals("Sat")) {

                // 예시: 시급에 1.5배를 곱하여 휴일 수당을 계산하는 경우
                double rate = Double.parseDouble(hourlyRate);
                double holidayPay = rate * 0.5; // 시급에 1.5배를 곱함

                return holidayPay;
            }
        }

        // 휴일이 아니거나 괄호 형식이 아닌 경우 0.0을 반환합니다.
        return 0.0;
    }
}
