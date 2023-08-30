package com.cookandroid.capstone.alarm;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.cookandroid.capstone.MainActivity;
import com.cookandroid.capstone.R;

public class AlarmReceiver extends BroadcastReceiver {

//    public static final String INTENT_EXTRA_ALARM_KEY = "com.cookandroid.capstone.alarm_key";
    public static final String INTENT_EXTRA_ALARM_NAME = "com.cookandroid.capstone.alarm_name";

    private static final String TAG = AlarmReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive " + intent.getAction());

        String alarmName = intent.getStringExtra(INTENT_EXTRA_ALARM_NAME);

        Intent notifyIntent = new Intent(context, MainActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntentWithParentStack(notifyIntent);
        PendingIntent pendingIntent =
                stackBuilder.getPendingIntent(1, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);


        final NotificationCompat.Builder notificationBuilder=new NotificationCompat.Builder(context,"alarm_channel_id")
                .setSmallIcon(R.mipmap.ic_launcher).setDefaults(Notification.DEFAULT_ALL)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_VIBRATE)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentTitle("작업 알림")
                .setContentText("근무지 : "+ alarmName)
                .setContentIntent(pendingIntent);


        final NotificationManager notificationManager=(NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationChannel channel=new NotificationChannel("alarm_channel_id","alarm_channel",NotificationManager.IMPORTANCE_HIGH);
        notificationManager.createNotificationChannel(channel);

        int id= (int) System.currentTimeMillis();

        notificationManager.notify(id,notificationBuilder.build());

    }
}
