package com.cookandroid.capstone;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

public class WorkDetail2Activity extends AppCompatActivity {

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_workdetail2);

        TextView name = findViewById(R.id.name);
        TextView money = findViewById(R.id.money);
        TextView date = findViewById(R.id.date);
        TextView startTime = findViewById(R.id.startTime);
        TextView endTime = findViewById(R.id.endTime);
        TextView restTime = findViewById(R.id.restTime);

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
                money.setText(formattedEarnings+"원");
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
                                for (DataSnapshot dateSnapshot : datesSnapshot.getChildren()) {
                                    String dateValue = dateSnapshot.child("date").getValue(String.class);
                                    String startTimeValue = dateSnapshot.child("startTime").getValue(String.class);
                                    String endTimeValue = dateSnapshot.child("endTime").getValue(String.class);
                                    String restTimeValue = dateSnapshot.child("restTime").getValue(String.class);

                                    if (dateValue != null && dateValue.trim().equals(selectedDate.trim())) {
                                        if (startTimeValue != null) {
                                            startTime.setText(startTimeValue);
                                        }
                                        if (endTimeValue != null) {
                                            endTime.setText(endTimeValue);
                                        }
                                        if (restTimeValue != null) {
                                            restTime.setText(restTimeValue);
                                        }
                                        break;
                                    }
                                }
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