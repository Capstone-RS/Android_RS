package com.cookandroid.capstone.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;

import com.cookandroid.capstone.BottomSheet_Push_Notification;
import com.cookandroid.capstone.R;

public class ProfileFragment extends Fragment {

    private LinearLayout notificationSettingsLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_profile, container, false);

        notificationSettingsLayout = view.findViewById(R.id.notification_settings_layout);
        notificationSettingsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheet_Push_Notification bottomSheet = new BottomSheet_Push_Notification();
                bottomSheet.show(getParentFragmentManager(), "bottomSheet");
            }
        });

        return view;
    }
}

