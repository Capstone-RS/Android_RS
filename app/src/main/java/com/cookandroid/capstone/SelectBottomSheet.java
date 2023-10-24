package com.cookandroid.capstone;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cookandroid.capstone.databinding.DialogBottomSheetSelectBinding;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class SelectBottomSheet extends BottomSheetDialogFragment {

   private DialogBottomSheetSelectBinding binding;

   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setStyle(STYLE_NORMAL, R.style.BottomSheetDialogTheme);
   }

   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
      binding = DialogBottomSheetSelectBinding.inflate(getLayoutInflater());
      return binding.getRoot();
   }

   @Override
   public void onViewCreated(View view, Bundle savedInstanceState) {
      super.onViewCreated(view, savedInstanceState);
      binding.btnBack.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            dismiss();
         }
      });
   }
}