package com.cookandroid.capstone.alarm;

import static android.content.Context.ALARM_SERVICE;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;

import com.cookandroid.capstone.Fragment.HomeFragment;
import com.cookandroid.capstone.PrefUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AlarmUtil {

    public static final String TAG = AlarmUtil.class.getSimpleName();

    public static int getPendingIntentFlag(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE;
        }
        return PendingIntent.FLAG_UPDATE_CURRENT;
    }

    public static void cancelAllAlarm(final Context context){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser != null){
            DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid()).child("Data");
            databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String nameValue = dataSnapshot.child("name").getValue(String.class);
                        if (nameValue != null) {
                            for (DataSnapshot dateSnapshot : dataSnapshot.child("dates").getChildren()) {
                                String key = dataSnapshot.getKey() + ":" + dateSnapshot.getKey();
                                cancelAlarm(context, key);
                            }

                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    public static void registerAllAlarm(final Context context){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser != null){
            DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid()).child("Data");
            databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String nameValue = dataSnapshot.child("name").getValue(String.class);
                        if (nameValue != null) {
                            for (DataSnapshot dateSnapshot : dataSnapshot.child("dates").getChildren()) {
                                String date = dateSnapshot.child("date").getValue(String.class);
                                String startTime = dateSnapshot.child("startTime").getValue(String.class);
                                String key = dataSnapshot.getKey() + ":" + dateSnapshot.getKey();
                                DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                                try {
                                    Date alarmDate = formatter.parse(date+ " " + startTime);
                                    if(alarmDate != null){
//                                        if(alarmDate.getTime() < Calendar.getInstance().getTimeInMillis()){
//                                            Log.w(TAG, "old Alarm " + key + " " + alarmDate.getTime() + " " + formatter.format(alarmDate));
//                                        }else{
//                                            Log.i(TAG, "register Alarm " + key + " " + alarmDate.getTime() + " " + formatter.format(alarmDate));
//                                            registerAlarm(context, key, nameValue, alarmDate.getTime());
//                                        }
                                        Log.i(TAG, "register Alarm " + key + " " + alarmDate.getTime() + " " + formatter.format(alarmDate));
                                        registerAlarm(context, key, nameValue, alarmDate.getTime());
                                    }

                                } catch (ParseException e) {
                                    Log.e(TAG, "failed to add Alarm " + e);
                                }

                            }

                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }


    }

    public static void registerAlarm(Context context, String key, String name, long time){
        if(context != null) {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
            if (alarmManager != null) {
                Intent intent = new Intent(context, AlarmReceiver.class);
//                intent.putExtra(AlarmReceiver.INTENT_EXTRA_ALARM_KEY, key);
                intent.putExtra(AlarmReceiver.INTENT_EXTRA_ALARM_NAME, name);
                intent.setAction(key);

                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1000, intent, getPendingIntentFlag());
                alarmManager.cancel(pendingIntent);


                Calendar calendar = Calendar.getInstance();

                long before = PrefUtils.getSelectedBefore(PrefUtils.getCurrentSelectedLayout(context));
                if(before >= 0){
                    Log.i(TAG, "alarm set before " + before);
                    time = time - before;
                    if(time >= Calendar.getInstance().getTimeInMillis()){

                        calendar.setTimeInMillis(time);
                        calendar.set(Calendar.SECOND, 0);

                        alarmManager.setAlarmClock(new AlarmManager.AlarmClockInfo(calendar.getTimeInMillis(), pendingIntent), pendingIntent);
                    }else{
                        Log.w(TAG, "old alarm " + key);
                    }

                }else{
                    Log.i(TAG, "alarm before time not selected!!");
                }


            }
        }
    }

    public static void cancelAlarm(Context context, String key){
        if(context != null) {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
            if (alarmManager != null) {
                Intent intent = new Intent(context, AlarmReceiver.class);
                intent.setAction(key);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1000, intent, getPendingIntentFlag());

                Log.d(TAG, "cancel Alarm for (" + key + ") \n");

                alarmManager.cancel(pendingIntent);


            }
        }
    }
}
