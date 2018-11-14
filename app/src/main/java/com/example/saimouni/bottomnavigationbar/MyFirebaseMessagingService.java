package com.example.saimouni.bottomnavigationbar;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String CHANNEL_ID = "com.example.saimouni.bottomnavigationbar";
    private static final String CHANNEL_NAME = "SkillsClubChannel";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            NotificationChannel nc = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            nc.setDescription("Open in app");
            nc.enableLights(true);
            nm.createNotificationChannel(nc);

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID);
            notificationBuilder.setAutoCancel(true)
                    .setSmallIcon(R.drawable.skillsclub);
            notificationBuilder.setContentTitle("SkillsClub notification");
            notificationBuilder.setContentText(remoteMessage.getNotification().getBody());
            notificationBuilder.setContentIntent(pendingIntent);

            nm.notify(0, notificationBuilder.build());
        }
        else {

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);

            notificationBuilder.setContentTitle("SkillsClub notification");
            notificationBuilder.setContentText(remoteMessage.getNotification().getBody());
            notificationBuilder.setAutoCancel(true);
            notificationBuilder.setSmallIcon(R.drawable.skillsclub);
            notificationBuilder.setContentIntent(pendingIntent);
            notificationBuilder.setSound(defaultSoundUri);


            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(0, notificationBuilder.build());

            String link = remoteMessage.getData().get("link");
            Log.d("link", link);
        }
    }


    @Override
    public void onNewToken(String s) {
        Log.d("SaiToken", s);
    }
}
