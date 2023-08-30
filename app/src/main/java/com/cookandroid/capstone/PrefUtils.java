package com.cookandroid.capstone;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefUtils {
    private static final String PREF_SELECTED_LAYOUT = "selected_layout";

    public static String getCurrentSelectedLayout(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getString(PREF_SELECTED_LAYOUT, "layout_30_minutes");
    }
    public static void setCurrentSelectedLayout(Context context, String selectedLayout){
        SharedPreferences.Editor editor = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE).edit();
        editor.putString(PREF_SELECTED_LAYOUT, selectedLayout);
        editor.apply();
    }


    public static String getSelectedText(String selectedLayout){
        switch (selectedLayout) {
            case "layout_none":
                return "설정 안함";
            case "layout_start_time":
                return "근무 시작 시간";
            case "layout_10_minutes":
                return "10분 전";
            case "layout_1_hour":
                return "1시간 전";
            case "layout_30_minutes":
            default:
                return "30분 전";
        }
    }

    public static long getSelectedBefore(String selectedLayout){
        switch (selectedLayout) {
            case "layout_none":
                return -1;
            case "layout_start_time":
                return 0;
            case "layout_10_minutes":
                return 10 * 60 * 1000;
            case "layout_1_hour":
                return 60 * 60 * 1000;
            case "layout_30_minutes":
            default:
                return 30 * 60 * 1000;
        }
    }
}
