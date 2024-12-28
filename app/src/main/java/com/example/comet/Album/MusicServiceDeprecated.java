//package com.example.myapplication.Album;
//
//import static androidx.media3.common.util.NotificationUtil.IMPORTANCE_HIGH;
//
//import static android.app.NotificationManager.IMPORTANCE_LOW;
//import static androidx.media3.common.util.NotificationUtil.IMPORTANCE_HIGH;
//import static androidx.media3.exoplayer.mediacodec.MediaCodecInfo.TAG;
//
//import android.app.Notification;
//import android.app.NotificationManager;
//import android.app.PendingIntent;
//import android.app.Service;
//import android.content.Intent;
//import android.graphics.Color;
//import android.net.Uri;
//import android.os.Binder;
//import android.os.Build;
//import android.os.IBinder;
//import android.support.v4.media.session.MediaSessionCompat;
//import android.util.Log;
//import android.widget.RemoteViews;
//
//import androidx.annotation.Nullable;
//import androidx.annotation.OptIn;
//import androidx.core.app.NotificationCompat;
//import androidx.localbroadcastmanager.content.LocalBroadcastManager;
//import androidx.media3.common.MediaItem;
//import androidx.media3.common.MediaMetadata;
//import androidx.media3.common.Player;
//import androidx.media3.common.util.UnstableApi;
//import androidx.media3.exoplayer.ExoPlayer;
//import androidx.media3.ui.PlayerNotificationManager;
//
//import com.example.myapplication.Song.MusicModel;
//
//import java.io.File;
//import java.util.ArrayList;
//import java.util.List;
//
//public class MusicServiceDeprecated {
//    private static MusicService instance;
//
//    private static final String CHANNEL_ID = "channel1";
//    ExoPlayer player;
//    private MediaSessionCompat mediaSession;
//    private NotificationManager notificationManager;
//    private PlayerNotificationManager playerNotificationManager;
//    private int playbackState = PLAYBACK_STATE_IDLE;
//    private static final int NOTIFICATION_ID = 111;
//    private MediaItem currentSong;
//
//    public static final int PLAYBACK_STATE_IDLE = 0;
//    public static final int PLAYBACK_STATE_PLAYING = 1;
//    public static final int PLAYBACK_STATE_PAUSED = 2;
//    public static final int PLAYBACK_STATE_STOPPED = 3;
//    public static final int PLAYBACK_STATE_SONG_CHANGED = 4;
//    private OnCurrentMediaItemChangedListener currentMediaItemChangedListener;
//    private static final long UPDATE_INTERVAL = 1000;
//    private long lastUpdateTime = 0;
//    private String TAG = "MusicMan";
//
//
//
//    private final IBinder serviceBinder = new MyServiceBinder();
//
//    //todo change my actions updated to here isntead of the updateNotification method
//    PlayerNotificationManager.NotificationListener notificationListener = new PlayerNotificationManager.NotificationListener() {
//        @OptIn(markerClass = UnstableApi.class) @Override
//        public void onNotificationCancelled(int notificationId, boolean dismissedByUser) {
//            PlayerNotificationManager.NotificationListener.super.onNotificationCancelled(notificationId, dismissedByUser);
//            stopForeground(true);
//            if(player != null && isPlaying()){
//                pauseMusic();
//            }
//        }
//
//        @OptIn(markerClass = UnstableApi.class) @Override
//        public void onNotificationPosted(int notificationId, Notification notification, boolean ongoing) {
//            PlayerNotificationManager.NotificationListener.super.onNotificationPosted(notificationId, notification, ongoing);
//            startForeground(notificationId, notification);
//            if(ongoing){
//                long currentTime = System.currentTimeMillis();
//                if(currentTime - lastUpdateTime >= UPDATE_INTERVAL){
////                    updateNotification();
//                    RemoteViews remoteView = notification.contentView;
//                    lastUpdateTime = currentTime;
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//                        notification.getContextualActions().get(0);
//                    }
//                }
//            }
//        }
//    };
//
//    public MusicService(){
//        instance = this;
//    }
//
//    public static synchronized MusicService getInstance(){
//        return instance;
//    }
//
//
//    @OptIn(markerClass = UnstableApi.class)
//    @Override
//    public void onCreate() {
//        super.onCreate();
//        initializePlayer();
//        initializeMediaSession();
//        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//        playerNotificationManager = new PlayerNotificationManager.Builder(this, NOTIFICATION_ID, ApplicationClass.CHANNEL_ID_1)
//                .setNotificationListener(notificationListener)
//                .setMediaDescriptionAdapter(new MyMediaDescriptionAdapter(this))
//                .setChannelImportance(IMPORTANCE_HIGH)
//                .setSmallIconResourceId(R.drawable.hoshi)
//                .setChannelDescriptionResourceId(R.string.app_name)
//                .setNextActionIconResourceId(R.drawable.baseline_skip_next_24)
//                .setPreviousActionIconResourceId(R.drawable.baseline_skip_previous_24)
//                .setPauseActionIconResourceId(R.drawable.baseline_pause_circle_24)
//                .setPlayActionIconResourceId(R.drawable.baseline_play_circle_24)
//                .setChannelNameResourceId(R.string.app_name)
//                .build();
//
//        playerNotificationManager.setPlayer(player);
//        playerNotificationManager.setMediaSessionToken(mediaSession.getSessionToken());
//        playerNotificationManager.setPriority(NotificationCompat.PRIORITY_HIGH);
//        playerNotificationManager.setUseRewindAction(false);
//        playerNotificationManager.setUseFastForwardAction(false);
//        Log.d("bingbong", "MusicService onCreate");
//    }
//
//    private void initializePlayer() {
//        // Initialize ExoPlayer instance
//        player = new ExoPlayer.Builder(getApplicationContext()).build();
//        player.addListener(new Player.Listener() {
//            public void onPlayBackStateChanged(int playbackState){
//                switch (playbackState) {
//                    case Player.STATE_READY:
//                        currentSong = getCurrentMediaItem();
//                        break;
//                    case Player.STATE_ENDED:
//                        break;
//                    default:
//                        break;
//                }
//            }
//        });
//    }
//
//    private void initializeMediaSession() {
//        // Initialize MediaSession
//        mediaSession = new MediaSessionCompat(this, "MusicService");
//        mediaSession.setActive(true);
//        mediaSession.setCallback(new MediaSessionCallback());
//        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
//                MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
//    }
//
//    private class MediaSessionCallback extends MediaSessionCompat.Callback {
//        // Implement media controls callbacks
//        // e.g., onPlay, onPause, onSkipToNext, onSkipToPrevious, onStop, onSeekTo, etc.
//        public void onPlay() {
//            // Handle play command (start or resume playback)
//            playMusic();
//        }
//
//        @Override
//        public void onPause() {
//            // Handle pause command
//            pauseMusic();
//        }
//
//        @Override
//        public void onSkipToNext() {
//            // Handle skip to next command
//            skipToNext();
//        }
//
//        @Override
//        public void onSkipToPrevious() {
//            // Handle skip to previous command
//            skipToPrevious();
//        }
//
//    }
//
//    private void sendPlaybackStateBroadcast() {
//        Intent intent = new Intent("PlaybackStateChanged");
//        intent.putExtra("state", getCurrentPlaybackState());
//        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
//    }
//    public int getCurrentPlaybackState() {
//        // Return the current playback state (e.g., PLAYING, PAUSED, STOPPED).
//        return playbackState;
//    }
//
//    private void updateNotification() {
//        //todo I think playerNotificationmanager is overwriting the style which is causing the buttons to not work correctly
//        // Build and update the notification
//        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, ApplicationClass.CHANNEL_ID_1)
//                .setContentTitle("Your Music Title")
//                .setContentText("Your Music Artist")
//                .setSmallIcon(R.drawable.hoshi)
//                .setContentIntent(createPendingIntent())
//                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
//                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
//                        .setMediaSession(mediaSession.getSessionToken())
//                        .setShowActionsInCompactView(0, 1, 2))
//                .setPriority(NotificationCompat.PRIORITY_MAX);
//
//        // Add playback actions
//        notificationBuilder.addAction(new NotificationCompat.Action(
//                R.drawable.baseline_skip_previous_24,
//                "Previous",
//                createPendingIntentForAction(Constants.ACTION_SKIP_PREVIOUS)));
//
//        notificationBuilder.addAction(new NotificationCompat.Action(
//                R.drawable.baseline_skip_next_24,
//                "Next",
//                createPendingIntentForAction(Constants.ACTION_SKIP_NEXT)));
//
//        notificationBuilder.addAction(new NotificationCompat.Action(
//                R.drawable.baseline_play_circle_24,
//                "Play",
//                createPendingIntentForAction(Constants.ACTION_PLAY)));
//
//        notificationBuilder.addAction(new NotificationCompat.Action(
//                R.drawable.baseline_pause_circle_24,
//                "Pause",
//                createPendingIntentForAction(Constants.ACTION_PAUSE)));
//
//        // Add other actions (play/pause, skip next, etc.)
//
//        Notification notification = notificationBuilder.build();
//        startForeground(NOTIFICATION_ID, notification);
//    }
//
//    private PendingIntent createPendingIntent() {
//        // This will be launched when the user taps on the notification
//        Intent intent = new Intent(this, ExoMusicPlayer.class);
//        intent.setAction("OPEN_PLAYER_ACTION");
//        int requestCode = 0;
//        return PendingIntent.getActivity(this, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//    }
//
//    private PendingIntent createPendingIntentForAction(String action) {
//        // Create a PendingIntent for playback actions
//        Intent intent = new Intent(this, MusicService.class);
//        //can put extras inside intent if needed
//        intent.setAction(action);
//        int requestCode = 0;
//        return PendingIntent.getService(this, requestCode, intent, PendingIntent.FLAG_CANCEL_CURRENT);
//    }
//
//    // Other methods, such as handling audio focus, releasing resources, etc.
//
//    @Nullable
//    @Override
//    public IBinder onBind(Intent intent) {
//        return serviceBinder;
//    }
//
//    @OptIn(markerClass = UnstableApi.class) @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        if(intent != null && intent.getAction() != null){
//            String action = intent.getAction();
//            Log.d(TAG, "ACTION: " + action);
//            switch (action){
//                case Constants.ACTION_PLAY:
//                    ArrayList<MusicModel> songsList = intent.getParcelableArrayListExtra("SONGS");
//                    int position = intent.getIntExtra("POS", 0);
//                    if (songsList != null && !songsList.isEmpty()) {
//                        player.setMediaItems(getMediaItems(songsList), position, 0);
//                        currentSong = player.getCurrentMediaItem();
//                    }
//                    playMusic();
//                    break;
//                case Constants.ACTION_PAUSE:
//                    pauseMusic();
//                    break;
//                case Constants.ACTION_SKIP_NEXT:
//                    skipToNext();
//                    break;
//                case Constants.ACTION_SKIP_PREVIOUS:
//                    skipToPrevious();
//                    break;
//                default:
//                    break;
//            }
//        }
//
//        // Ensure that the playerNotificationManager is attached to the player and mediaSession
////        playerNotificationManager.setPlayer(player);
////        playerNotificationManager.setMediaSessionToken(mediaSession.getSessionToken());
//
//        //Update the notification after handling the action
//        updateNotification();
//
//        //default
////        return super.onStartCommand(intent, flags, startId);
//
//        //todo monitor
//        //start sticky will restart the service if it is terminated by the system(trying it out)
//        return START_STICKY;
//    }
//
//
//    public void playMusic() {
//        playbackState = PLAYBACK_STATE_PLAYING;
//        player.prepare();
//        player.play();
//        updateNotification();
//        sendPlaybackStateBroadcast();
//    }
//
//    public void pauseMusic() {
//        playbackState = PLAYBACK_STATE_PAUSED;
//        player.pause();
//        updateNotification();
//        sendPlaybackStateBroadcast();
//    }
//
//    public void skipToNext(){
//        playbackState = PLAYBACK_STATE_SONG_CHANGED;
//        if(player.hasNextMediaItem()){
//            player.seekToNext();
//            currentSong = player.getCurrentMediaItem();
//
//            // Notify the listener after the song has changed
//            if (currentMediaItemChangedListener != null) {
//                currentMediaItemChangedListener.onCurrentMediaItemChanged(currentSong);
//            }
//        }
//        updateNotification();
//        sendPlaybackStateBroadcast();
//    }
//    public void skipToPrevious(){
//        playbackState = PLAYBACK_STATE_SONG_CHANGED;
//        if(player.hasPreviousMediaItem()){
//            player.seekToPrevious();
//            currentSong = player.getCurrentMediaItem();
//
//            // Notify the listener after the song has changed
//            if (currentMediaItemChangedListener != null) {
//                currentMediaItemChangedListener.onCurrentMediaItemChanged(currentSong);
//            }
//        }
//        updateNotification();
//        sendPlaybackStateBroadcast();
//    }
//
//    public void seekTo(int position){
//        player.seekTo(position, 0);
//    }
//
//    public void seekToMs(int position){
//        player.seekTo(position);
//    }
//
//    public void stopMusic() {
//        playbackState = PLAYBACK_STATE_STOPPED;
//        player.stop();
//        updateNotification();
//        sendPlaybackStateBroadcast();
//    }
//
//    public boolean isPlaying(){
//        return player != null && playbackState == PLAYBACK_STATE_PLAYING;
//    }
//
//    public void releasePlayer(){
//        playbackState = PLAYBACK_STATE_IDLE;
//        if(player != null){
//            player.release();
//        }
//        player = null;
//        updateNotification();
//    }
//
//    public MediaItem getCurrentMediaItem(){
//        return currentSong;
//    }
//
//    public interface OnCurrentMediaItemChangedListener {
//        void onCurrentMediaItemChanged(MediaItem newMediaItem);
//    }
//
//    public void setOnCurrentMediaItemChangedListener(OnCurrentMediaItemChangedListener listener) {
//        this.currentMediaItemChangedListener = listener;
//    }
//
//    @OptIn(markerClass = UnstableApi.class) @Override
//    public void onDestroy() {
//        if(isPlaying()) stopMusic();
//        // Release resources, stop playback, etc.
//        stopForeground(true);
//        playerNotificationManager.setPlayer(null);
//        releasePlayer();
//        mediaSession.release();
//        super.onDestroy();
//    }
//
//    private List<MediaItem> getMediaItems(ArrayList<MusicModel> songsList){
//        //turn songs into media items
//        List<MediaItem> mediaItems = new ArrayList<>();
//        for(MusicModel song : songsList){
//            MediaItem mediaItem = new MediaItem.Builder()
//                    .setUri(Uri.fromFile(new File(song.getPath())))
//                    .setMediaMetadata(getMetadata(song))
//                    .build();
//
//            //add media item to media items list
//            mediaItems.add(mediaItem);
//        }
//        return mediaItems;
//    }
//
//    private MediaMetadata getMetadata(MusicModel song) {
//        return new MediaMetadata.Builder()
//                .setTitle(song.getTitle())
//                .setArtist(song.getArtist())
//                .setAlbumTitle(song.getAlbum())
//                .setDiscNumber(Integer.parseInt(song.getDuration()))
//                .setCompilation(song.getAlbumId())
//                .setSubtitle(song.getPath())
//                .build();
//    }
//
//
//
//    public class MyServiceBinder extends Binder {
//        public MusicService getMusicService(){
//            return MusicService.this;
//        }
//    }
//}
