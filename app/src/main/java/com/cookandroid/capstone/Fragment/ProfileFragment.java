package com.cookandroid.capstone.Fragment;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.cookandroid.capstone.BottomSheet_Push_Notification;
import com.cookandroid.capstone.PrefUtils;
import com.cookandroid.capstone.R;
import com.cookandroid.capstone.SignInActivity;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ProfileFragment extends Fragment {

    private static final String PREF_SELECTED_LAYOUT = "selected_layout";

    private LinearLayout notificationSettingsLayout;
    private TextView usernameTextView; // 닉네임 text
    private TextView emailTextView; // email text
    private ImageView profileImageView; // 프로필 이미지 view
    private FirebaseAuth auth;
    private LinearLayout logoutLayout;

    private LinearLayout withdrawLayout;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private FirebaseUser currentUser = mAuth.getCurrentUser();

    private BarChart barChart;
    private DatabaseReference databaseReference;
    ArrayList<String> dataNameList = new ArrayList<>();
    ArrayList<String> dataMoneyList = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_profile, container, false);
        auth = FirebaseAuth.getInstance();

        // 사용자의 고유한 아이디 가져오기
        String userId = currentUser.getUid();

        // 그래프 관련 코드
        BarChart barChart = view.findViewById(R.id.barChart);
        barChart.getDescription().setEnabled(false);

        // 데이터 가져오기
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("Data").child("name");
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("Data");

        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Double[] earningMonths = {0d, 0d, 0d, 0d, 0d, 0d, 0d, 0d, 0d, 0d, 0d, 0d};
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Calendar now = Calendar.getInstance();

                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {

                    for (DataSnapshot dateSnapshot : itemSnapshot.child("dates").getChildren()) {
                        String dateStr = dateSnapshot.child("date").getValue(String.class);
                        Calendar dataDate = Calendar.getInstance();
                        try {
                            Date date = sdf.parse(dateStr);
                            dataDate.setTime(date);
//                            Log.i("ProfileFragment", "date " + dataDate.get(Calendar.YEAR));
                            if(dataDate.get(Calendar.YEAR) == now.get(Calendar.YEAR)){
                                int month = dataDate.get(Calendar.MONTH);
                                Log.i("ProfileFragment", "month " + month);

                                String payType = dateSnapshot.child("pay").getValue(String.class); // "시급" 또는 "일급" 가져오기
                                if (payType != null && payType.trim().equals("시급")) {
                                    Double earningsValue = dateSnapshot.child("earnings").getValue(Double.class);
                                    if (earningsValue != null) {
                                        earningMonths[month] = earningMonths[month] + earningsValue;
                                        Log.i("ProfileFragment", "시급 " + dateStr + " " + earningMonths[month]);
                                    }
                                } else if (payType != null && payType.trim().equals("일급")) {
                                    String moneyString = dateSnapshot.child("money").getValue(String.class);
                                    if (moneyString != null) {
                                        Double moneyValue = Double.parseDouble(moneyString.replaceAll("[^0-9.]+", ""));
                                        earningMonths[month] = earningMonths[month] + moneyValue;
                                    }
                                }
                            }else{
                                Log.w("ProfileFragment", "not this year data");
                            }

                        } catch (ParseException e) {
                            Log.e("ProfileFragment", "failed to get data " + e);
                        }


                    }
                    // 1. 데이터를 정의
                    List<BarEntry> barEntries = new ArrayList<>();
                    String[] months = {"1월", "2월", "3월", "4월", "5월", "6월", "7월", "8월", "9월", "10월", "11월", "12월"};

                    // formatCurrency 메소드 사용하여 데이터에 추가
                    for (int i = 0; i < months.length; i++) {
                        //            // 이 부분에서 데이터를 가져와서 formatCurrency 메소드로 형식화
                        String formattedAmount = formatCurrency(earningMonths[i], false);
                        float amountFloat = parseFormattedAmount(formattedAmount);
                        barEntries.add(new BarEntry(i, amountFloat));
                    }

                    XAxis xAxis = barChart.getXAxis();
                    xAxis.setValueFormatter(new IndexAxisValueFormatter(months)); // X축 레이블 설정
                    xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                    xAxis.setGranularity(1f);
                    xAxis.setGranularityEnabled(true);
                    xAxis.setLabelRotationAngle(45);

                    BarDataSet barDataSet = new BarDataSet(barEntries, "월별 수입");
                    BarData barData = new BarData(barDataSet);
                    barChart.setData(barData);
                    barChart.invalidate();
                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        TextView tvNickName = view.findViewById(R.id.usernameTextView);
        TextView tvEmail = view.findViewById(R.id.emailTextView);
        ImageView ivPhoto = view.findViewById(R.id.profileImageView);

        SharedPreferences pref = getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);
        String nickName = pref.getString("nickName", "");
        String email = pref.getString("eMail", "");
        String photoUrl = pref.getString("photoUrl", "");

        tvNickName.setText(nickName);
        tvEmail.setText(email);
        Glide.with(this).load(photoUrl).into(ivPhoto);

        TextView selectedTimeTextView = view.findViewById(R.id.selectedTimeTextView);

        String selectedLayout = PrefUtils.getCurrentSelectedLayout(getActivity());
        selectedTimeTextView.setText(PrefUtils.getSelectedText(selectedLayout));

        notificationSettingsLayout = view.findViewById(R.id.notification_settings_layout);
        notificationSettingsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheet_Push_Notification bottomSheet = new BottomSheet_Push_Notification();
                bottomSheet.show(getParentFragmentManager(), "bottomSheet");
            }
        });


        logoutLayout = view.findViewById(R.id.logoutLayout);
        // withdrawLayout = view.findViewById(R.id.withdrawLayout);

        logoutLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("로그아웃").setMessage("로그아웃 하시겠습니까?");
                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // Firebase Auth 로그아웃 처리
                        FirebaseAuth.getInstance().signOut();

                        // 구글 계정 로그아웃 처리
                        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                .requestIdToken(getString(R.string.default_web_client_id))
                                .requestEmail()
                                .build();
                        Activity activity = getActivity();
                        if(activity != null){
                            GoogleSignIn.getClient(activity, googleSignInOptions).signOut();
                        }


                        // SharedPreferences에서 저장된 정보를 삭제
                        SharedPreferences pref = getActivity().getSharedPreferences("pref", MODE_PRIVATE);
                        SharedPreferences.Editor editor = pref.edit();
                        editor.clear();
                        editor.apply();

                        // SignInActivity로 화면 전환
                        Intent intent = new Intent(getActivity(), SignInActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        getActivity().finish();
                    }
                });
//
                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
                builder.show();
            }
        });

        return view;
    }

    private float parseFormattedAmount(String formattedAmount) {
        // 금액 문자열에서 숫자와 구분 기호(예: 쉼표)를 제거
        String cleanAmount = formattedAmount.replaceAll("[^0-9.]+", "");

        // 형식화된 숫자 문자열을 실수(float)로 변환
        try {
            return Float.parseFloat(cleanAmount);
        } catch (NumberFormatException e) {
            // 변환 중 오류가 발생하면 0.0 또는 원하는 오류 처리를 수행
            e.printStackTrace(); // 또는 오류 처리 로직을 추가할 수 있음
            return 0.0f;
        }
    }

    private void getValue() {
        databaseReference.addValueEventListener(new ValueEventListener() {
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

                        // Insurance 값을 가져오기
                        String insuranceValue = dataSnapshot.child("Insurance").getValue(String.class);

                        // 4대보험 계산 적용
                        double totalEarningsAfterInsurance = calculateFourMajorInsurances(totalEarnings, insuranceValue);

                        String formattedEarnings = formatCurrency(totalEarningsAfterInsurance + totalmoney, isTaxEnabled);
                        dataMoneyList.add(formattedEarnings);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }

    private double calculateFourMajorInsurances(double earnings, String insuranceValue) {
        double fourMajorInsurances = 0.0;

        // Insurance 값에 따라 4대보험 계산 적용
        switch (insuranceValue) {
            case "4대보험 모두 가입":
                // 4대보험 적용 비율 (예: 건강보험 3.06%, 장기요양보험 0.91%, 고용보험 0.65%, 국민연금 9%)
                double healthInsuranceRate = 0.0306;
                double longTermCareInsuranceRate = 0.0091;
                double employmentInsuranceRate = 0.0065;
                double nationalPensionRate = 0.09;

                // 4대보험 금액 계산
                double healthInsurance = earnings * healthInsuranceRate;
                double longTermCareInsurance = earnings * longTermCareInsuranceRate;
                double employmentInsurance = earnings * employmentInsuranceRate;
                double nationalPension = earnings * nationalPensionRate;

                // 4대보험 금액 합산
                fourMajorInsurances = healthInsurance + longTermCareInsurance + employmentInsurance + nationalPension;
                break;
            case "고용보험만 가입":
                // 고용보험만 적용 (고용보험 0.65%)
                double employmentInsuranceOnlyRate = 0.0065;

                // 고용보험 금액 계산
                fourMajorInsurances = earnings * employmentInsuranceOnlyRate;
                break;
            default:
                // 기본값 (4대보험 미반영)
                break;
        }

        return earnings - fourMajorInsurances;
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