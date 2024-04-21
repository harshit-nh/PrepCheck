package com.example.prepcheck;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class NotificationBroadcastReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "reminder_channel";
    private static final int NOTIFICATION_ID = 100;
    private static final int MY_PERMISSION_FOREGROUND_SERVICE = 1;

    @Override
    public void onReceive(Context context, Intent intent) {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Reminder Notification")
                .setContentText("Reminder to re-check prep")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.FOREGROUND_SERVICE)
                != PackageManager.PERMISSION_GRANTED) {

            // Request the necessary notification permission if not granted
            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{android.Manifest.permission.FOREGROUND_SERVICE},
                    MY_PERMISSION_FOREGROUND_SERVICE);

        } else {
            // Permission granted, proceed to show the notification
            notificationManager.notify(NOTIFICATION_ID, builder.build());
        }

    }


}




