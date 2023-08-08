package com.cookandroid.capstone;


import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class WorkData2Activity extends AppCompatActivity implements BottomSheetListener {

    //파이어베이스 데이터 연동
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();
    private TextView startTime;
    private TextView endTime;
    BottomSheet_Calendar bottomSheet;
    private TextView workDay;
    private List<String> selectedDatesList;
    private Switch swPlusPay; // 연장 수당 토글 버튼
    private Switch swHollidayPay; // 휴일 수당 토글 버튼

    // 사용자 로그인 상태 확인
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser currentUser = mAuth.getCurrentUser();

    int i = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workdata2);


        TextView btnBack = (TextView) findViewById(R.id.btnBack);
        Button btnNext = (Button) findViewById(R.id.btnNext);
        Button btnHome = (Button) findViewById(R.id.btnHome);
        startTime = (TextView) findViewById(R.id.startTime);
        endTime = (TextView) findViewById(R.id.endTime);
        EditText money = (EditText) findViewById(R.id.money);
        workDay = (TextView) findViewById(R.id.btnWorkDay);
        //연장수당, 휴일수당 토글버튼
        swPlusPay = (Switch) findViewById(R.id.swPlusPay);
        swHollidayPay = (Switch) findViewById(R.id.swHollidayPay);



        //Log.d("Selected Dates", "Selected dates string: " + stringArray);
        Intent intent2 = getIntent();
        String name = intent2.getStringExtra("name");
        String workPeriod = intent2.getStringExtra("workPeriod");
        String payDay = intent2.getStringExtra("payDay");
        boolean isTaxEnabled = intent2.getBooleanExtra("isTaxEnabled", false);
        String insurance = intent2.getStringExtra("Insurance");

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


        //달력바텀시트 연결
        workDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheet = new BottomSheet_Calendar();
                bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag());
                bottomSheet.setListener(WorkData2Activity.this);
            }
        });


        //타임피커

        startTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog dialog = new TimePickerDialog(WorkData2Activity.this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar, new TimePickerDialog.OnTimeSetListener() {
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
                TimePickerDialog dialog = new TimePickerDialog(WorkData2Activity.this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar, new TimePickerDialog.OnTimeSetListener() {
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


        //뒤로가기버튼

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), WorkDataActivity.class);
                startActivity(intent);
            }
        });


        //계산하기 버튼
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 사용자가 로그인한 경우에만 데이터를 저장하도록 확인합니다.
                if (currentUser == null) {
                    // 사용자가 로그인하지 않은 경우 처리 또는 에러 메시지를 표시합니다.
                    Toast.makeText(getApplicationContext(), "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 사용자의 고유한 아이디를 가져옵니다.
                String userId = currentUser.getUid();


                String getName = name;
                String getWorkPeriod = workPeriod;
                String getPayDay = payDay;
                String getMoney = money.getText().toString();
                String getStartTime = startTime.getText().toString();
                String getEndTime = endTime.getText().toString();
                String getSelectPay = spnPay.getSelectedItem().toString(); //스피너 선택값 가져오기
                String getSelectRestTime = spnRestTime.getSelectedItem().toString();
                if (selectedDatesList == null || selectedDatesList.isEmpty()) {
                    // 선택된 날짜가 없는 경우 처리
                    Toast.makeText(getApplicationContext(), "날짜를 선택해주세요.", Toast.LENGTH_SHORT).show();
                } else if(TextUtils.isEmpty(getMoney)) {
                    // 사용자가 돈을 입력하지 않은 경우 처리
                    Toast.makeText(getApplicationContext(), "돈을 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(getStartTime)) {
                    // 사용자가 시작 시간을 입력하지 않은 경우 처리
                    Toast.makeText(getApplicationContext(), "시작 시간을 선택해주세요.", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(getEndTime)) {
                    // 사용자가 종료 시간을 입력하지 않은 경우 처리
                    Toast.makeText(getApplicationContext(), "종료 시간을 선택해주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    // 모든 필수 입력값이 존재하는 경우 처리 로직
                    HashMap result = new HashMap<>();
                    result.put("name", getName);
                    result.put("workPeriod", getWorkPeriod);
                    result.put("payDay", getPayDay);
                    result.put("isTaxEnabled", isTaxEnabled);
                    result.put("Insurance",insurance);
                    // 연장 수당과 휴일 수당을 파이어베이스에 저장
                    result.put("swPlusPay", swPlusPay.isChecked());
                    result.put("swHolliDayPay", swHollidayPay.isChecked());

                    DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("Data");

                    // 각 등록에 대해 고유한 키를 생성합니다.
                    String key = databaseRef.push().getKey();

                    // 선택된 날짜 데이터를 HashMap에 추가
                    HashMap<String, Object> dates = new HashMap<>();
                    for (int i = 0; i < selectedDatesList.size(); i++) {
                        String dateKey = "Date" + (i + 1);
                        String dateValue = selectedDatesList.get(i);
                        HashMap<String, Object> dateData = new HashMap<>();
                        dateData.put("date", dateValue);
                        dateData.put("startTime", getStartTime);
                        dateData.put("endTime", getEndTime);
                        dateData.put("restTime", getSelectRestTime);
                        dateData.put("money", getMoney);
                        dateData.put("pay", getSelectPay);
                        dates.put(dateKey, dateData);
                    }
                    result.put("dates", dates);

                    // 고유한 키를 가진 하위 노드를 생성하고 그 값을 "result" 데이터로 설정합니다.
                    HashMap<String, Object> registrationData = new HashMap<>();
                    registrationData.put(key, result);

                    DatabaseReference dataRef = databaseReference.child("Users").child(userId).child("Data").child(name);
                    dataRef.setValue(result)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // Firebase에 데이터가 성공적으로 등록됨
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(intent);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Firebase에 데이터 등록 실패
                                    Toast.makeText(getApplicationContext(), "Firebase에 데이터를 등록하는 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });
        //나중에하기 버튼
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onDataReceived(String selectedDates) {

        // 가져온 데이터를 사용하여 작업 수행
        if (selectedDates != null) {
            // 선택된 날짜 데이터가 있는 경우
            selectedDatesList = Arrays.asList(selectedDates.split(","));
            workDay.setText(selectedDates);
        } else {
            selectedDatesList = new ArrayList<>();
            workDay.setText("날짜를 선택 해 주세요");
        }
        Log.d("Selected Dates", "Selected dates: " + selectedDates);
    }
}

