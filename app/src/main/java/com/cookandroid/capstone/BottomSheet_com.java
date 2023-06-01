package com.cookandroid.capstone;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class BottomSheet_com extends BottomSheetDialogFragment {
    private View view;
    private BottomSheetListener mListener;
    private Button btn_hide_bt_sheet;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.bottom_sheet_layout, container, false);

        mListener = (BottomSheetListener) getContext();

        btn_hide_bt_sheet = view.findViewById(R.id.btn_hide_bt_sheet);
        btn_hide_bt_sheet.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                mListener.onButtonClicked("바텀시트숨겨짐");
                dismiss();
            }
        });
        return view;
    }
    public interface BottomSheetListener {
        void onButtonClicked(String text);
    }
}