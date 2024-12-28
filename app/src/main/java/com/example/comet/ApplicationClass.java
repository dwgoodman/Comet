package com.example.comet;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;

public class ApplicationClass extends Application {
    public static final String CHANNEL_ID_1 = "channel1";
    public static final String CHANNEL_ID_2 = "channel2";
    public static final String ACTION_PREVIOUS = "actionprevious";
    public static final String ACTION_NEXT = "actionnext";
    public static final String ACTION_PLAY = "actionplay";


    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel1();
    }

    private void createNotificationChannel1(){
        //creating notification channels to add music into the android notification bar (allows app to play music even after closing)
        NotificationChannel channel1 = new NotificationChannel(CHANNEL_ID_1, "Channel(1)", NotificationManager.IMPORTANCE_HIGH);
        channel1.setDescription("Channel 1 Description");

        NotificationChannel channel2 = new NotificationChannel(CHANNEL_ID_2, "Channel(2)", NotificationManager.IMPORTANCE_HIGH);
        channel2.setDescription("Channel 2 Description");

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel1);
        notificationManager.createNotificationChannel(channel2);
    }

}
