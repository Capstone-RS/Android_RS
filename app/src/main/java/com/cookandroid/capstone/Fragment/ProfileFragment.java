package com.cookandroid.capstone.Fragment;

import android.content.Intent;
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
        Intent intent = getActivity().getIntent();
        String nickName = intent.getStringExtra("nickName"); // SignInActivity로부터 닉네임 전달받음.
        String eMail = intent.getStringExtra("eMail"); // SignInActivity로부터 이메일 전달받음.
        String photoUrl = intent.getStringExtra("photoUrl"); // SignInActivity로부터 프로필사진 url 전달받음.
        auth = FirebaseAuth.getInstance();
        usernameTextView = view.findViewById(R.id.usernameTextView);
        usernameTextView.setText(nickName); // 닉네임 text를 텍스트뷰에 세팅

        emailTextView = view.findViewById(R.id.emailTextView);
        emailTextView.setText(eMail); // 이메일 text를 텍스트뷰에 세팅

        profileImageView = view.findViewById(R.id.profileImageView);
        Glide.with(this).load(photoUrl).into(profileImageView); // 프로필 url을 이미지뷰에 세팅



        notificationSettingsLayout = view.findViewById(R.id.notification_settings_layout);
        notificationSettingsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheet_Push_Notification bottomSheet = new BottomSheet_Push_Notification();
                bottomSheet.show(getParentFragmentManager(), "bottomSheet");
            }
        });



        logoutLayout = view.findViewById(R.id.logoutLayout);
        withdrawLayout = view.findViewById(R.id.withdrawLayout);

        logoutLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });
        withdrawLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                revokeAccess();
            }
        });

        return view;
    }
    private void signOut() {
        FirebaseAuth.getInstance().signOut();
    }

    private void revokeAccess() {
        auth.getCurrentUser().delete();
    }
}

