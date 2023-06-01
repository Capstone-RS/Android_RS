package com.cookandroid.capstone;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CheckListActivity extends AppCompatActivity {

    TextView textView_backbtn;
    Button sendbt;
    EditText editdt;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    ArrayList<String> arrayList = new ArrayList<>();
    ArrayAdapter<String> adapter;
    ListView listView;
    String sOldValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checklist);

        sendbt = findViewById(R.id.btnRegist);
        editdt = findViewById(R.id.etWork);
        listView = findViewById(R.id.lvWork);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, arrayList);
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("Todo");

        getValue();

        sendbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String sName = editdt.getText().toString();
                if (sendbt.getText().toString().equals("등록")) {
                    String sKey = databaseReference.push().getKey();
                    if (sKey != null) {
                        databaseReference.child(sKey).child("work").setValue(sName);
                        editdt.setText("");
                    }
                } else {
                    Query query = databaseReference.orderByChild("work").equalTo(sOldValue);
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                dataSnapshot.getRef().child("work").setValue(sName);
                                editdt.setText("");
                                sendbt.setText("등록");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(CheckListActivity.this, "error:" +
                                    error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                sOldValue = arrayList.get(position);
                editdt.setText(sOldValue);
                sendbt.setText("수정");
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                String sValue = arrayList.get(position);
                AlertDialog.Builder builder = new AlertDialog.Builder(CheckListActivity.this);
                builder.setTitle("삭제");
                builder.setMessage("삭제하시겠습니까?");
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Query query = databaseReference.orderByChild("work").equalTo(sValue);
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    dataSnapshot.getRef().removeValue();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(CheckListActivity.this, "error:" +
                                        error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                builder.setNegativeButton("Cancle", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.show();
                return true;
            }
        });

        textView_backbtn = findViewById(R.id.btnBack);
        textView_backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void getValue() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrayList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String sValue = dataSnapshot.child("work").getValue(String.class);
                    if (sValue != null) {
                        arrayList.add(sValue);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CheckListActivity.this, "error:" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}