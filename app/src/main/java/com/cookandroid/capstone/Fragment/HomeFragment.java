package com.cookandroid.capstone.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;


import com.cookandroid.capstone.CheckListActivity;
import com.cookandroid.capstone.HelpActivity;
import com.cookandroid.capstone.Main_WorkDataActivity;
import com.cookandroid.capstone.R;
import com.cookandroid.capstone.WorkDataActivity;
import com.cookandroid.capstone.WorkDetailActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private DatabaseReference databaseRef;

    private RecyclerView recyclerViewWorkList;
    ArrayAdapter<String> adapter;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser currentUser = mAuth.getCurrentUser();

    ArrayList<String> todoList = new ArrayList<>();
    ArrayList<String> dataNameList = new ArrayList<>();
    ArrayList<String> dataMoneyList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_home, container, false);

        String userId = currentUser.getUid();

        ImageView fragHelp = view.findViewById(R.id.frag_help);
        Button btnAdd = view.findViewById(R.id.btnAdd);
        TextView textView_checklistadd = view.findViewById(R.id.btnChecklistAdd);
        ListView listView_todo = view.findViewById(R.id.lvWork);
        ScrollView scrollView = view.findViewById(R.id.scrollView);

        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, todoList);
        listView_todo.setAdapter(adapter);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("Users").child(userId).child("Todo");
        databaseRef = firebaseDatabase.getReference("Users").child(userId).child("Data");

        getValue();

        listView_todo.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    scrollView.requestDisallowInterceptTouchEvent(false);
                } else {
                    scrollView.requestDisallowInterceptTouchEvent(true);
                }
                return false;
            }
        });

        fragHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), HelpActivity.class);
                startActivity(intent);
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), WorkDataActivity.class);
                startActivity(intent);
            }
        });

        textView_checklistadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CheckListActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerViewWorkList = view.findViewById(R.id.workList);
        recyclerViewWorkList.setLayoutManager(new LinearLayoutManager(getActivity()));


        // dataNameList가 데이터로 채워졌을 때에만 어댑터 설정
        if (!dataNameList.isEmpty()) {
            WorkAdapter adapter = new WorkAdapter(dataNameList);
            recyclerViewWorkList.setAdapter(adapter);
        }

    }

    private class WorkAdapter extends RecyclerView.Adapter<WorkAdapter.ViewHolder> {
        private List<String> dataNameList;

        public WorkAdapter(List<String> dataNameList) {
            this.dataNameList = dataNameList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_customlistview, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            String dataName = dataNameList.get(position);
            String dataMoney = dataMoneyList.get(position);
            holder.textViewName.setText(dataName);
            holder.textViewMoney.setText(dataMoney);

            // 아이템 클릭 리스너 설정
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 클릭한 아이템의 정보를 가져오거나 필요한 작업을 수행
                    // 이동할 액티비티로 데이터를 전달하고 싶다면 Intent의 putExtra 메서드 활용
                    // 예: intent.putExtra("key", value);

                    // Main_WorkDataActivity로 이동하는 Intent 생성
                    Intent intent = new Intent(getActivity(), WorkDetailActivity.class);
                    // 필요하다면 데이터 전달 설정
                    // intent.putExtra("key", value);
                    intent.putExtra("clicked_name", dataName);
                    intent.putExtra("clicked_money",dataMoney);
                    startActivity(intent);

                }
            });
        }


        @Override
        public int getItemCount() {
            return dataNameList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView textViewName;
            TextView textViewMoney; // 총 earnings 값을 출력할 TextView

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                textViewName = itemView.findViewById(R.id.textViewName);
                textViewMoney = itemView.findViewById(R.id.textViewMoney); // home_customlistview.xml에 추가한 TextView의 ID
            }
        }
    }

    private void getValue() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                todoList.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String sValue = dataSnapshot.child("work").getValue(String.class);
                    if (sValue != null) {
                        todoList.add(sValue);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });

        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dataNameList.clear(); // 데이터를 추가하기 전에 기존 데이터를 초기화
                dataMoneyList.clear(); // earnings 항목을 위한 데이터도 초기화

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String nameValue = dataSnapshot.child("name").getValue(String.class);
                    if (nameValue != null) {
                        dataNameList.add(nameValue);

                        double totalEarnings = 0.0;
                        double totalmoney = 0.0;

                        for (DataSnapshot dateSnapshot : dataSnapshot.child("dates").getChildren()) {
                            String payType = dateSnapshot.child("pay").getValue(String.class); // "시급" 또는 "일급" 가져오기
                            if (payType != null && payType.trim().equals("시급")) {
                                Double earningsValue = dateSnapshot.child("earnings").getValue(Double.class);
                                if (earningsValue != null) {
                                    totalEarnings += earningsValue;
                                }
                            } else if (payType != null && payType.trim().equals("일급")) {
                                String moneyString = dateSnapshot.child("money").getValue(String.class);
                                if (moneyString != null) {
                                    Double moneyValue = Double.parseDouble(moneyString.replaceAll("[^0-9.]+", ""));
                                    totalmoney += moneyValue;
                                }
                            }
                        }
                        // isTaxEnabled 가져오기
                        Boolean isTaxEnabled = dataSnapshot.child("isTaxEnabled").getValue(Boolean.class);


                        String formattedEarnings = formatCurrency(totalEarnings + totalmoney, isTaxEnabled);
                        dataMoneyList.add(formattedEarnings);
                    }
                }

                // 데이터가 변경되었으므로 어댑터 갱신
                WorkAdapter adapter = new WorkAdapter(dataNameList);
                recyclerViewWorkList.setAdapter(adapter);
                adapter.notifyDataSetChanged(); // 어댑터에 데이터 변경을 알려줌
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });

    }
    private String formatCurrency(double amount, boolean applyTax) {
        DecimalFormat decimalFormat = new DecimalFormat("#,###원");

        // applyTax가 true인 경우에만 0.033을 곱한 값으로 포맷
        if (applyTax) {
            amount *= 0.967;
        }

        return decimalFormat.format(amount);
    }

}

