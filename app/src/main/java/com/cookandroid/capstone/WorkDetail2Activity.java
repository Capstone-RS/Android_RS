package com.cookandroid.capstone;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;

public class WorkDetail2Activity extends AppCompatActivity {

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_workdetail2);

        TextView btnBack = findViewById(R.id.btnBack);
        TextView name = findViewById(R.id.name);
        TextView money = findViewById(R.id.money);
        TextView date = findViewById(R.id.date);
        TextView startTime = findViewById(R.id.startTime);
        TextView endTime = findViewById(R.id.endTime);
        TextView restTime = findViewById(R.id.restTime);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WorkDetail2Activity.this, MainActivity.class);
                intent.putExtra("showCalendar", true); // calendarFragment를 표시하기 위한 정보 전달
                startActivity(intent);
            }
        });

        Intent intent = getIntent();
        if (intent != null) {
            String itemName = intent.getStringExtra("itemName");
            String formattedEarnings = intent.getStringExtra("formattedEarnings");
            String selectedDate = intent.getStringExtra("selectedDate");
            // 값 표시하기
            if (itemName != null) {
                name.setText(itemName);
            }
            if (formattedEarnings != null) {
                money.setText(formattedEarnings + "원");
            }
            if (selectedDate != null) {
                date.setText(selectedDate);

                // Firebase에서 데이터 가져오기
                DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("Data");
                Query query = databaseRef.orderByChild("name").equalTo(itemName);
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            DataSnapshot datesSnapshot = snapshot.child("dates");
                            if (datesSnapshot.exists()) {
                                ArrayList<HashMap<String, String>> groupData = new ArrayList<>();
                                ArrayList<ArrayList<HashMap<String, String>>> childData = new ArrayList<>();

                                // [부모 리스트]
                                HashMap<String, String> groupA = new HashMap<>();
                                groupA.put("group", "총 급여");
                                groupData.add(groupA);

                                // [자식 리스트]
                                ArrayList<HashMap<String, String>> childListA = new ArrayList<>();

                                for (DataSnapshot dateSnapshot : datesSnapshot.getChildren()) {
                                    String dateValue = dateSnapshot.child("date").getValue(String.class);
                                    String startTimeValue = dateSnapshot.child("startTime").getValue(String.class);
                                    String endTimeValue = dateSnapshot.child("endTime").getValue(String.class);
                                    String restTimeValue = dateSnapshot.child("restTime").getValue(String.class);
                                    String moneyValue = dateSnapshot.child("money").getValue(String.class);

                                    if (dateValue != null && dateValue.trim().equals(selectedDate.trim())) {
                                        // 자식 데이터를 HashMap에 추가
                                        HashMap<String, String> childDataMap = new HashMap<>();
                                        childDataMap.put("data", "시급: " + moneyValue+"원");
                                        childListA.add(childDataMap);

                                        HashMap<String, String> childDataMap2 = new HashMap<>();
                                        childDataMap2.put("data", "근무 수당: " + formattedEarnings +"원");
                                        childListA.add(childDataMap2);

                                        // 값 표시하기
                                        if (startTimeValue != null) {
                                            startTime.setText(startTimeValue);
                                        }
                                        if (endTimeValue != null) {
                                            endTime.setText(endTimeValue);
                                        }
                                        if (restTimeValue != null) {
                                            restTime.setText(restTimeValue);
                                        }
                                    }
                                }

                                childData.add(childListA);

                                // [부모 리스트와 자식 리스트를 포함한 Adapter를 생성]
                                SimpleExpandableListAdapter adapter = new SimpleExpandableListAdapter(
                                        WorkDetail2Activity.this, groupData,
                                        android.R.layout.simple_expandable_list_item_1,
                                        new String[]{"group"}, new int[]{android.R.id.text1},
                                        childData, android.R.layout.simple_expandable_list_item_2,
                                        new String[]{"data"}, new int[]{android.R.id.text1});

                                // [ExpandableListView 에 Adapter 설정]
                                ExpandableListView listView = findViewById(R.id.expandableListView);
                                listView.setAdapter(adapter);

                                break;
                            } else {
                                Log.e("WorkDetail2Activity", "dates is null");
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // 에러 처리 로직을 작성해주세요.
                    }
                });
            }
        }
    }
}
