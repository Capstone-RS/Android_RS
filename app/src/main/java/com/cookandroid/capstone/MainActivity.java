package com.cookandroid.capstone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;


import com.cookandroid.capstone.Fragment.CalendarFragment;
import com.cookandroid.capstone.Fragment.ChatFragment;
import com.cookandroid.capstone.Fragment.CommunityFragment;
import com.cookandroid.capstone.Fragment.HomeFragment;
import com.cookandroid.capstone.Fragment.ProfileFragment;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;


public class MainActivity extends AppCompatActivity{

    HomeFragment homeFragment;
    CalendarFragment calendarFragment;
    ChatFragment chatFragment;
    CommunityFragment communityFragment;
    ProfileFragment profileFragment;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        homeFragment = new HomeFragment();
        calendarFragment = new CalendarFragment();
        chatFragment = new ChatFragment();
        communityFragment = new CommunityFragment();
        profileFragment = new ProfileFragment();

        auth = FirebaseAuth.getInstance();

        // 기본 화면 설정
        getSupportFragmentManager().beginTransaction().replace(R.id.containers, homeFragment).commit();


        NavigationBarView navigationBarView = findViewById(R.id.bottomNav);
        navigationBarView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.homeFragment:
                        getSupportFragmentManager().beginTransaction().replace(R.id.containers, homeFragment).commit();
                        return true;
                    case R.id.calendarFragment:
                        getSupportFragmentManager().beginTransaction().replace(R.id.containers, calendarFragment).commit();
                        return true;
                    case R.id.chatFragment:
                        getSupportFragmentManager().beginTransaction().replace(R.id.containers, chatFragment).commit();
                        return true;
                    case R.id.communityFragment:
                        getSupportFragmentManager().beginTransaction().replace(R.id.containers, communityFragment).commit();
                        return true;
                    case R.id.profileFragment:
                        getSupportFragmentManager().beginTransaction().replace(R.id.containers, profileFragment).commit();
                        return true;
                }
                return false;
            }
        });
    }
}