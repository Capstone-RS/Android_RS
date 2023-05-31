package com.cookandroid.capstone;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class WorkData2Activity extends AppCompatActivity {

    //파이어베이스 데이터 연동
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();

    BottomSheet_Calendar bottomSheet;
    int i = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workdata2);

        TextView btnBack = (TextView) findViewById(R.id.btnBack);
        Button btnNext = (Button) findViewById(R.id.btnNext);
        Button btnHome = (Button) findViewById(R.id.btnHome);
        TextView startTime = (TextView) findViewById(R.id.startTime);
        TextView endTime = (TextView) findViewById(R.id.endTime);
        EditText money = (EditText) findViewById(R.id.money);
        TextView workDay = (TextView) findViewById(R.id.btnWorkDay);
        //BottomSheet_Calendar 에서 선택된 날짜 Textview(workDay)에 출력하기

        // Intent에서 데이터 가져오기
        Intent intent = getIntent();
        String selectedDatesString = intent.getStringExtra("test");
        List<String> selectedDatesList;
        // 가져온 데이터를 사용하여 작업 수행
        if (selectedDatesString != null) {
            // 선택된 날짜 데이터가 있는 경우
            selectedDatesList = Arrays.asList(selectedDatesString.split(","));
            workDay.setText(selectedDatesString);
        } else {
            selectedDatesList = new ArrayList<>();
            workDay.setText("날짜를 선택 해 주세요");
        }


//            // TODO: 이후 작업 수행
//        } else {
//            // 선택된 날짜 데이터가 없는 경우
//            workDay.setText("날짜를 선택 해 주세요");
//            //Log.d("Selected Dates", "No selected dates");
//            // TODO: 이후 작업 수행
//        }
//        for (String str : stringArray) {
//            Log.d("Split String", "Element: " +str);
//        }

// 이후


        //Log.d("Selected Dates", "Selected dates string: " + stringArray);
        Intent intent2 = getIntent();
        String name = intent2.getStringExtra("name");
        String workPeriod = intent2.getStringExtra("workPeriod");
        String payDay = intent2.getStringExtra("payDay");


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
                        String state = "AM";
                        // 선택한 시간이 12를 넘을경우 "PM"으로 변경 및 -12시간하여 출력 (ex : PM 6시 30분)
                        if (selectedHour > 12) {
                            selectedHour -= 12;
                            state = "PM";
                        }
                        // TextView에 출력할 형식 지정
                        startTime.setText(state + " " + selectedHour + ": " + selectedMinute);
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
                        String state = "AM";
                        // 선택한 시간이 12를 넘을경우 "PM"으로 변경 및 -12시간하여 출력 (ex : PM 6시 30분)
                        if (selectedHour > 12) {
                            selectedHour -= 12;
                            state = "PM";
                        }
                        // TextView에 출력할 형식 지정
                        endTime.setText(state + " " + selectedHour + ": " + selectedMinute);
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
                String getName = name;
                String getWorkPeriod = workPeriod;
                String getPayDay = payDay;
                String getMoney = money.getText().toString();
                String getStartTime = startTime.getText().toString();
                String getEndTime = endTime.getText().toString();
                String getSelectPay = spnPay.getSelectedItem().toString(); //스피너 선택값 가져오기
                String getSelectRestTime = spnRestTime.getSelectedItem().toString();


                HashMap result = new HashMap<>();
                result.put("name", getName);
                result.put("workPeriod", getWorkPeriod);
                result.put("payDay", getPayDay);
                result.put("money", getMoney);
                result.put("startTime", getStartTime);
                result.put("endTime", getEndTime);
                result.put("Pay", getSelectPay);
                result.put("RestTime", getSelectRestTime);


                DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("Data");

                // 각 등록에 대해 고유한 키를 생성합니다.
                String key = databaseRef.push().getKey();

                // 선택된 날짜 데이터를 HashMap에 추가
                HashMap<String, Object> dates = new HashMap<>();
                for (int i = 0; i < selectedDatesList.size(); i++) {
                    String dateKey = "Date" + (i + 1);
                    String dateValue = selectedDatesList.get(i);
                    dates.put(dateKey, dateValue);
                }
                result.put("dates", dates);

                // 고유한 키를 가진 하위 노드를 생성하고 그 값을 "result" 데이터로 설정합니다.
                HashMap<String, Object> registrationData = new HashMap<>();
                registrationData.put(key, result);

                databaseRef.updateChildren(registrationData)
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
}

