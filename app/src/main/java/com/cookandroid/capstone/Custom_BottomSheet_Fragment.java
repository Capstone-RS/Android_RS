package com.cookandroid.capstone;


import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class Custom_BottomSheet_Fragment extends BottomSheetDialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);

        // 다이얼로그의 모서리를 둥글게 만들기 위해 Window 객체를 가져옵니다.
        Window window = dialog.getWindow();
        if (window != null) {
            // 모서리를 둥글게 만들기 위해 배경을 투명하게 설정합니다.
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            // 모서리를 둥글게 만들기 위해 커스텀 레이아웃을 적용합니다.
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            window.setDimAmount(0.4f); // 선택적으로 다이얼로그의 투명도를 조정할 수 있습니다.
            window.getDecorView().setPadding(0, 0, 0, 0);

            // 모서리를 둥글게 만들기 위해 커스텀 배경을 설정합니다.
            window.setBackgroundDrawableResource(R.drawable.bottom_sheet_background);
        }

        return dialog;
    }

    @Override
    public void onStart() {        super.onStart();
        // 선택적으로 다이얼로그의 크기와 위치를 조정할 수 있습니다.
    }
}