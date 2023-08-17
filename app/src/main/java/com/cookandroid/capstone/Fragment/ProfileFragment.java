package com.cookandroid.capstone.Fragment;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.cookandroid.capstone.BottomSheet_Push_Notification;
import com.cookandroid.capstone.R;
import com.cookandroid.capstone.SignInActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;

public class ProfileFragment extends Fragment {

    private LinearLayout notificationSettingsLayout;
    private TextView usernameTextView; // 닉네임 text
    private TextView emailTextView; // email text
    private ImageView profileImageView; // 프로필 이미지 view
    private FirebaseAuth auth;
    private LinearLayout logoutLayout;

    private LinearLayout withdrawLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_profile, container, false);
        auth = FirebaseAuth.getInstance();

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
//        Intent intent = getActivity().getIntent();
//        String nickName = intent.getStringExtra("nickName"); // SignInActivity로부터 닉네임 전달받음.
//        String eMail = intent.getStringExtra("eMail"); // SignInActivity로부터 이메일 전달받음.
//        String photoUrl = intent.getStringExtra("photoUrl"); // SignInActivity로부터 프로필사진 url 전달받음.
//
//        usernameTextView = view.findViewById(R.id.usernameTextView);
//        usernameTextView.setText(nickName); // 닉네임 text를 텍스트뷰에 세팅
//
//        emailTextView = view.findViewById(R.id.emailTextView);
//        emailTextView.setText(eMail); // 이메일 text를 텍스트뷰에 세팅
//
//        profileImageView = view.findViewById(R.id.profileImageView);
//        Glide.with(this).load(photoUrl).into(profileImageView); // 프로필 url을 이미지뷰에 세팅


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
}