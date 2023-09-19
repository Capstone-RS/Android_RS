package com.cookandroid.capstone;



import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.cookandroid.capstone.alarm.AlarmUtil;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class BottomSheet_Push_Notification extends BottomSheetDialogFragment implements View.OnClickListener{
    private static final String TAG = BottomSheet_Push_Notification.class.getSimpleName();
    private TextView selectedTimeTextView;

    private ImageView selectedImageViewNone;
    private ImageView selectedImageViewStart;
    private ImageView selectedImageView10;
    private ImageView selectedImageView30;
    private ImageView selectedImageView1;

    private SharedPreferences sharedPreferences;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_bottom_sheet__push_notification, container, false);

        // SharedPreferences 초기화
        sharedPreferences = getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        // 텍스트뷰 찾기
        selectedTimeTextView = getActivity().findViewById(R.id.selectedTimeTextView);

        // 이미지뷰 찾기
        selectedImageViewNone = rootView.findViewById(R.id.check_none);
        selectedImageViewStart = rootView.findViewById(R.id.check_start);
        selectedImageView10 = rootView.findViewById(R.id.check_10);
        selectedImageView30 = rootView.findViewById(R.id.check_30);
        selectedImageView1 = rootView.findViewById(R.id.check_1);



        // 버튼 또는 레이아웃 클릭 이벤트 설정
        rootView.findViewById(R.id.layout_none).setOnClickListener(this);
        rootView.findViewById(R.id.layout_start_time).setOnClickListener(this);
        rootView.findViewById(R.id.layout_10_minutes).setOnClickListener(this);
        rootView.findViewById(R.id.layout_30_minutes).setOnClickListener(this);
        rootView.findViewById(R.id.layout_1_hour).setOnClickListener(this);

        // 기본 세팅
        setSelectedLayout();
        return rootView;
    }
    private void setSelectedLayout() {
        Activity activity = getActivity();
        if(activity == null) return;

        String selectedLayout = PrefUtils.getCurrentSelectedLayout(getActivity());
        int selectedImageResId;
        String selectedText;
        Log.i(TAG, "setSelectedLayout " + selectedLayout);
        selectedImageResId = R.drawable.ic_check;

//            switch (selectedLayout) {
//                case "layout_none":
//                    selectedImageResId = R.drawable.ic_check;
//                    selectedText = "설정 안함";
//                    break;
//                case "layout_start_time":
//                    selectedImageResId = R.drawable.ic_check;
//                    selectedText = "근무 시작 시간";
//                    break;
//                case "layout_10_minutes":
//                    selectedImageResId = R.drawable.ic_check;
//                    selectedText = "10분 전";
//                    break;
//                case "layout_30_minutes":
//                    selectedImageResId = R.drawable.ic_check;
//                    selectedText = "30분 전";
//                    break;
//                case "layout_1_hour":
//                    selectedImageResId = R.drawable.ic_check;
//                    selectedText = "1시간 전";
//                    break;
//                default:
//                    selectedImageResId = R.drawable.ic_check;
//                    selectedText = "30분 전";
//                    break;
//            }
        selectedImageViewNone.setImageResource(selectedImageResId);
        selectedImageViewStart.setImageResource(selectedImageResId);
        selectedImageView10.setImageResource(selectedImageResId);
        selectedImageView30.setImageResource(selectedImageResId);
        selectedImageView1.setImageResource(selectedImageResId);
        switch (selectedLayout) {
            case "layout_none":
                DrawableCompat.setTint(selectedImageViewNone.getDrawable(),
                        ContextCompat.getColor(getActivity(), R.color.blue));
//                    selectedImageViewNone.setColorFilter(R.color.blue, PorterDuff.Mode.SRC_IN);
                break;
            case "layout_start_time":
                DrawableCompat.setTint(selectedImageViewStart.getDrawable(),
                        ContextCompat.getColor(getActivity(), R.color.blue));
                break;
            case "layout_10_minutes":
                DrawableCompat.setTint(selectedImageView10.getDrawable(),
                        ContextCompat.getColor(getActivity(), R.color.blue));
                break;
            case "layout_30_minutes":
                DrawableCompat.setTint(selectedImageView30.getDrawable(),
                        ContextCompat.getColor(getActivity(), R.color.blue));
                break;
            case "layout_1_hour":
                DrawableCompat.setTint(selectedImageView1.getDrawable(),
                        ContextCompat.getColor(getActivity(), R.color.blue));
                break;
            default:
                break;
        }


        selectedTimeTextView.setText(PrefUtils.getSelectedText(selectedLayout));
    }

    public void onClick(View v) {
        String selectedLayout = "layout_30_minutes";
        int selectedImageResId = R.drawable.ic_check_blue;
        String selectedText;

        switch (v.getId()) {
            case R.id.layout_none:
                selectedLayout = "layout_none";
                break;
            case R.id.layout_start_time:
                selectedLayout = "layout_start_time";
                break;
            case R.id.layout_10_minutes:
                selectedLayout = "layout_10_minutes";
                break;
            case R.id.layout_30_minutes:
                selectedLayout = "layout_30_minutes";
                break;
            case R.id.layout_1_hour:
                selectedLayout = "layout_1_hour";
                break;
        }

        selectedTimeTextView.setText(PrefUtils.getSelectedText(selectedLayout));

        // 선택된 레이아웃 저장
        Activity activity = getActivity();
        if(activity != null){
            PrefUtils.setCurrentSelectedLayout(activity, selectedLayout);
            AlarmUtil.registerAllAlarm(activity);
        }

        setSelectedLayout();

        dismiss();
    }
}