package com.cookandroid.capstone;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

        import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class ProfileActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profile); // profile xml이랑 연결된 자바파일이라는 뜻


    }
}