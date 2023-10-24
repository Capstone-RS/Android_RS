package com.cookandroid.capstone;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.cookandroid.capstone.Fragment.HomeFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Main_WorkDataActivity extends AppCompatActivity {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser currentUser = mAuth.getCurrentUser();
    private String clickedName; // 클릭한 아이템의 이름을 저장하는 변수

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_workdata);

        String userId = currentUser.getUid();
        TextView btnCorrect = (TextView) findViewById(R.id.btnCorrect);
        Button btnNext = (Button) findViewById(R.id.btnNext);
        Button btnHome = (Button) findViewById(R.id.btnHome);
        EditText name = (EditText) findViewById(R.id.name);
        //사용할 스피너 선언
        Spinner spn1 = (Spinner) findViewById(R.id.spn1);
        Spinner spn2 = (Spinner) findViewById(R.id.spn2);
        Spinner spnInsurance = (Spinner) findViewById(R.id.spnInsurance);
        //토글버튼
        Switch swTax = (Switch) findViewById(R.id.swTax);
        Switch swInsurance = (Switch) findViewById(R.id.swInsurance);
        LinearLayout spinnerContainer = (LinearLayout) findViewById(R.id.spinnerContainer);


        // 인텐트에서 전달받은 데이터 확인 및 사용
        Intent intent = getIntent();
        if (intent != null) {
            clickedName = intent.getStringExtra("clicked_name");
            if (clickedName != null) {
                // 클릭한 아이템의 이름을 EditText에 출력
                name.setText(clickedName);

                // Firebase 데이터베이스에서 데이터 조회 및 swTax 값을 가져와서 토글버튼에 설정
                DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("Data");
                Query query = databaseRef.orderByChild("name").equalTo(clickedName);

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Boolean swTaxValue = snapshot.child("isTaxEnabled").getValue(Boolean.class);
                            String swInsuranceValue = snapshot.child("Insurance").getValue(String.class);


                            if (swTaxValue != null) {
                                swTax.setChecked(swTaxValue); // 토글버튼 상태 설정
                            }
                            // "4대보험 모두 가입"이라는 문자열이 들어있으면 토글버튼을 On으로 설정
                            if (swInsuranceValue != null && swInsuranceValue.contains("4대보험 모두 가입")) {
                                swInsurance.setChecked(true); // 토글버튼 상태 On으로 설정
                            } else if (swInsuranceValue != null && swInsuranceValue.contains("고용보험만 가입")) {
                                swInsurance.setChecked(true); // 토글버튼 상태 On으로 설정
                            } else {
                                swInsurance.setChecked(false); // 다른 경우에는 Off로 설정
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // 에러 처리 로직을 작성해주세요.
                    }
                });
            }
        }


        // 토글 버튼의 상태를 감지하는 리스너 추가
        swInsurance.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // 토글 버튼의 상태에 따라 스피너의 가시성 조정
                if (isChecked) {
                    spinnerContainer.setVisibility(View.VISIBLE); // 보이도록 설정
                } else {
                    spinnerContainer.setVisibility(View.GONE);    // 숨기도록 설정
                }
            }
        });

        //스피너
        //스피너 workperiod(한달, 일주일) Adapter로 연결
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.array_workdata_workperiod, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spn1.setAdapter(adapter); //spn1 위치에 adapter 연결

        //스피너 paydate(1일~) Adapter1로 연결
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this,
                R.array.array_workdata_paydate, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //스피너 payweek(월~일) Adapter2로 연결
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                R.array.array_workdata_payweek, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //스피너 Adapter3로 연결
        ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(this,
                R.array.array_workdata_insurance, android.R.layout.simple_spinner_item);
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnInsurance.setAdapter(adapter3); //spnInsurance 위치에 adapter3 연결


        //index 값을 사용하여 spn1에 스피너 값 선택시 해당 스피너 출력 되도록 구현
        spn1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int item = spn1.getSelectedItemPosition(); // spn1에서 선택한 값 index번호를 int형으로 저장
                if (item == 0) {
                    spn2.setAdapter(adapter1);
                } else if (item == 1) {
                    spn2.setAdapter(adapter2);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // EditText로부터 수정된 데이터 가져오기
                String newName = name.getText().toString(); // 이름 수정 내용
                String selectedWorkPeriod = spn1.getSelectedItem().toString(); // 수정된 스피너 값
                String selectedPayDay = spn2.getSelectedItem().toString();
                boolean isTaxEnabled = swTax.isChecked(); // 세금 스위치의 상태 가져오기
               // 스피너에서 선택한 "Insurance" 값을 가져오기
                // 토글 버튼 상태에 따라 Insurance 값을 설정
                String getInsurance = swInsurance.isChecked() ? spnInsurance.getSelectedItem().toString() : "";


                // 여기에 다른 수정 내용을 가져오는 코드 추가

                // Firebase 데이터베이스 레퍼런스 가져오기
                DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("Data");
                Query query = databaseRef.orderByChild("name").equalTo(clickedName);

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            // 수정할 데이터에 대한 레퍼런스 가져오기
                            DatabaseReference dataRef = snapshot.getRef();

                            // 수정된 데이터를 Firebase에 업데이트
                            dataRef.child("name").setValue(newName); // 이름 업데이트
                            dataRef.child("workPeriod").setValue(selectedWorkPeriod); // 스피너 값 업데이트
                            dataRef.child("isTaxEnabled").setValue(isTaxEnabled);
                            dataRef.child("payDay").setValue(selectedPayDay);
                            dataRef.child("Insurance").setValue(getInsurance);


                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // 에러 처리 로직을 작성해주세요.
                    }
                });
                // 홈 화면으로 이동하는 Intent 생성
                Intent homeIntent = new Intent(Main_WorkDataActivity.this, MainActivity.class);
                homeIntent.putExtra("showCommunity", false); // showCommunity 값을 false로 설정
                homeIntent.putExtra("showCalendar", false); // showCalendar 값을 false로 설정
                startActivity(homeIntent); // 홈 화면으로 이동
            }
        });

        btnCorrect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Firebase 데이터베이스 레퍼런스 가져오기
                DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("Data");
                Query query = databaseRef.orderByChild("name").equalTo(clickedName);

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            // 데이터와 하위 데이터를 삭제
                            snapshot.getRef().removeValue();
                        }

                        // 삭제가 완료되면 홈 화면으로 이동하는 Intent 생성
                        Intent homeIntent = new Intent(Main_WorkDataActivity.this, MainActivity.class);
                        homeIntent.putExtra("showCommunity", false); // showCommunity 값을 false로 설정
                        homeIntent.putExtra("showCalendar", false); // showCalendar 값을 false로 설정
                        startActivity(homeIntent); // 홈 화면으로 이동
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // 에러 처리 로직을 작성해주세요.
                    }
                });
            }
        });



    }
}







