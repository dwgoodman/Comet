package com.example.comet;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;

import com.example.comet.song.SongListFromPlaylistViewModelFactory;

public class ApplicationClass extends Application {
    public static final String CHANNEL_ID_1 = "channel1";
    public static final String CHANNEL_ID_2 = "channel2";
    public static final String ACTION_PREVIOUS = "actionprevious";
    public static final String ACTION_NEXT = "actionnext";
    public static final String ACTION_PLAY = "actionplay";
    private final ViewModelStore viewModelStore = new ViewModelStore();
    private ViewModelProvider.Factory factory;
    private SongListFromPlaylistViewModelFactory songListFromPlaylistViewModelFactory;


    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel1();
        factory = new ViewModelProvider.AndroidViewModelFactory(this);
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

    @NonNull
    public ViewModelStore getViewModelStore() {
        return viewModelStore;
    }

    public ViewModelProvider.Factory getSongViewModelFactory() {
        return factory;
    }

    public void createSongListFromPlaylistViewModelFactory(int playlistId) {
        songListFromPlaylistViewModelFactory = new SongListFromPlaylistViewModelFactory(this, playlistId);
    }

    public ViewModelProvider.Factory getSongListFromPlaylistViewModelFactory() {
        return songListFromPlaylistViewModelFactory;
    }

    public ViewModelProvider getSongListFromPlaylistViewModelProvider(int playlistId) {
        SongListFromPlaylistViewModelFactory factory = new SongListFromPlaylistViewModelFactory(this, playlistId);
        return new ViewModelProvider(viewModelStore, factory);
    }

}
