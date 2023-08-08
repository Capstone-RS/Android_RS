package com.cookandroid.capstone;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
    ArrayList<Boolean> checkedList = new ArrayList<>();
    private  static final  String PREFS_NAME = "CheckListPrefs";
    private static final String CHECKED_PREF_PREFIX = "checked_";
    ArrayAdapter<String> adapter;
    ListView listView;
    String sOldValue;

    // 사용자 로그인 상태 확인
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser currentUser = mAuth.getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checklist);

        // 사용자 ID 가져오기
        String userId = currentUser.getUid();


        sendbt = findViewById(R.id.btnRegist);
        editdt = findViewById(R.id.etWork);
        listView = findViewById(R.id.lvWork);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, arrayList);
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        firebaseDatabase = FirebaseDatabase.getInstance();
        // 사용자의 ID를 데이터베이스 참조의 자식 노드로 추가하기
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child("Todo");

        getValue();
        restoreCheckedState();

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

        //수정
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                checkedList.set(position, listView.isItemChecked(position));
                String selectedItem = arrayList.get(position);
                showEditDialog(selectedItem);
            }
        });


        //길게 클릭시 삭제 다이얼로그
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

    //한번 클릭시 수정 다이얼로그
    private void showEditDialog(final String selectedItem) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("아이템 수정");

        final EditText editText = new EditText(this);
        editText.setText(selectedItem);
        builder.setView(editText);

        builder.setPositiveButton("수정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String updatedItem = editText.getText().toString().trim();
                if (!updatedItem.isEmpty()) {
                    int position = arrayList.indexOf(selectedItem);
                    if (position != -1) {
                        arrayList.set(position, updatedItem);
                        adapter.notifyDataSetChanged();
                        updateItem(selectedItem, updatedItem);
                    }
                }
            }
        });

        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    // 아이템 업데이트 메서드
    private void updateItem(String oldItem, String newItem) {
        Query query = databaseReference.orderByChild("work").equalTo(oldItem);

        int index = arrayList.indexOf(oldItem);
        if(index != -1){
            checkedList.set(index, false);
        }
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    dataSnapshot.getRef().child("work").setValue(newItem);
                    dataSnapshot.getRef().child(newItem).setValue(true);
                    dataSnapshot.getRef().child(oldItem).removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CheckListActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getValue() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrayList.clear();
                checkedList.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String sValue = dataSnapshot.child("work").getValue(String.class);
                    if (sValue != null) {
                        arrayList.add(sValue);
                        checkedList.add(false);
                    }
                }
                adapter.notifyDataSetChanged();

                for(int i=0; i<arrayList.size(); i++){
                    listView.setItemChecked(i, checkedList.get(i));
                }
                restoreCheckedState();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CheckListActivity.this, "error:" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
//        restoreCheckedState();
    }
    @Override
    protected void onPause() {
        super.onPause();
        saveCheckedState();
    }
    private void saveCheckedState() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        for (int i = 0; i < arrayList.size(); i++) {
            String key = CHECKED_PREF_PREFIX + i;
            editor.putBoolean(key, checkedList.get(i));
        }
        editor.apply();
    }
    private void restoreCheckedState() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        checkedList.clear();
        for (int i = 0; i < arrayList.size(); i++) {
            String key = CHECKED_PREF_PREFIX + i;
            boolean isChecked = prefs.getBoolean(key, false);
            checkedList.add(isChecked);
            listView.setItemChecked(i, isChecked);
        }
    }
}