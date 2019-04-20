package com.internshiporganizer;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.internshiporganizer.ApiClients.EmployeeClient;
import com.internshiporganizer.Entities.Employee;

import java.util.Random;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.d("TOKENFB", s);
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.APP_PREFERENCES, Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(Constants.FIREBASE_TOKEN, s).apply();

        if (sharedPreferences.contains(Constants.ID)) {
            Employee employee = new Employee();
            employee.setId(sharedPreferences.getLong(Constants.ID, -1));
            employee.setFireBaseToken(s);

            EmployeeClient employeeClient = new EmployeeClient(this, null);
            employeeClient.updateToken(employee);
        }
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        String NOTIFICATION_ID = "com.internshiporganizer.test";
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel
                    = new NotificationChannel(NOTIFICATION_ID, "Notifications", NotificationManager.IMPORTANCE_DEFAULT);

            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_ID);
        notificationBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.icon_notes)
                .setContentTitle(remoteMessage.getNotification().getTitle())
                .setContentText(remoteMessage.getNotification().getBody())
                .setContentInfo("Info");

        manager.notify(new Random().nextInt(), notificationBuilder.build());
    }
}
