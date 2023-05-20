package com.cookandroid.capstone;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

    public class BottomSheet_Push_Notification extends BottomSheetDialogFragment implements View.OnClickListener{
        private TextView selectedTimeTextView;

        private ImageView selectedImageViewNone;
        private ImageView selectedImageViewStart;
        private ImageView selectedImageView10;
        private ImageView selectedImageView30;
        private ImageView selectedImageView1;

        private SharedPreferences sharedPreferences;
        private static final String PREF_SELECTED_LAYOUT = "selected_layout";
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
            String selectedLayout = sharedPreferences.getString(PREF_SELECTED_LAYOUT, "layout_30_minutes");
            int selectedImageResId;
            String selectedText;

            switch (selectedLayout) {
                case "layout_none":
                    selectedImageResId = R.drawable.ic_check;
                    selectedText = "설정 안함";
                    break;
                case "layout_start_time":
                    selectedImageResId = R.drawable.ic_check;
                    selectedText = "근무 시작 시간";
                    break;
                case "layout_10_minutes":
                    selectedImageResId = R.drawable.ic_check;
                    selectedText = "10분 전";
                    break;
                case "layout_30_minutes":
                    selectedImageResId = R.drawable.ic_check;
                    selectedText = "30분 전";
                    break;
                case "layout_1_hour":
                    selectedImageResId = R.drawable.ic_check;
                    selectedText = "1시간 전";
                    break;
                default:
                    selectedImageResId = R.drawable.ic_check;
                    selectedText = "30분 전";
                    break;
            }

            selectedImageViewNone.setImageResource(selectedImageResId);
            selectedImageViewStart.setImageResource(selectedImageResId);
            selectedImageView10.setImageResource(selectedImageResId);
            selectedImageView30.setImageResource(selectedImageResId);
            selectedImageView1.setImageResource(selectedImageResId);
            selectedTimeTextView.setText(selectedText);
        }

        public void onClick(View v) {
            String selectedLayout = "layout_30_minutes";
            int selectedImageResId = R.drawable.ic_check_blue;
            String selectedText;

            switch (v.getId()) {
                case R.id.layout_none:
                    selectedLayout = "layout_none";
                    selectedTimeTextView.setText("설정 안함");
                    break;
                case R.id.layout_start_time:
                    selectedLayout = "layout_start_time";
                    selectedTimeTextView.setText("근무 시작 시간");
                    break;
                case R.id.layout_10_minutes:
                    selectedLayout = "layout_10_minutes";
                    selectedTimeTextView.setText("10분 전");
                    break;
                case R.id.layout_30_minutes:
                    selectedLayout = "layout_30_minutes";
                    selectedTimeTextView.setText("30분 전");
                    break;
                case R.id.layout_1_hour:
                    selectedLayout = "layout_1_hour";
                    selectedTimeTextView.setText("1시간 전");
                    break;
            }

            // 선택된 레이아웃 저장
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(PREF_SELECTED_LAYOUT, selectedLayout);
            editor.apply();

            setSelectedLayout();

            dismiss();
        }
    }