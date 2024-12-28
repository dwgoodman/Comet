package com.example.comet;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.OptIn;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.media3.common.MediaItem;
import androidx.media3.common.MediaMetadata;
import androidx.media3.common.PlaybackException;
import androidx.media3.common.Player;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.session.MediaSession;
import androidx.media3.session.MediaSessionService;
import androidx.media3.session.SessionToken;
import androidx.media3.ui.PlayerNotificationManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.comet.Song.MusicModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MusicService extends MediaSessionService {
    private static MusicService instance;

    private static final String CHANNEL_ID = "channel1";
    ExoPlayer player;
    private PlayerNotificationManager playerNotificationManager;
    private final int playbackState = PLAYBACK_STATE_IDLE;
    private static final int NOTIFICATION_ID = 111;
    private MediaItem currentSong;

    public static final int PLAYBACK_STATE_IDLE = 0;
    private OnCurrentMediaItemChangedListener currentMediaItemChangedListener;
    //in milliseconds
    private static final long UPDATE_INTERVAL = 200;
    private long lastUpdateTime = 0;
    private final String TAG = "MusicMan";
    private final IBinder serviceBinder = new MyServiceBinder();
    private MediaSession mediaSession = null;
    private int lastPlaybackState = -1;
    private boolean lastIsPlaying = false;
    private boolean isForegroundService = false;


    public MusicService(){
        instance = this;
    }

    public static synchronized MusicService getInstance(){
        return instance;
    }


    @OptIn(markerClass = UnstableApi.class)
    @Override
    public void onCreate() {
        super.onCreate();
        initializeMediaComponents();

        // Add a Player.Listener for playback-specific events
        player.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                if (playbackState == Player.STATE_READY && player.getPlayWhenReady()) {
                    // Ensure notification is persistent during playback
                    if (!isForegroundService) {
                        isForegroundService = true;
                    }
                } else if (playbackState == Player.STATE_READY) {
                    // Handle paused playback
                    isForegroundService = true;
                } else if (playbackState == Player.STATE_ENDED) {
                    isForegroundService = false;
                }
            }

            @Override
            public void onPlayerError(PlaybackException error) {
                Log.e("MusicService", "Player error: " + error.getMessage(), error);
            }
        });

        // Create a PlayerNotificationManager
        playerNotificationManager = new PlayerNotificationManager.Builder(this, NOTIFICATION_ID, CHANNEL_ID)
                .setMediaDescriptionAdapter(new PlayerNotificationManager.MediaDescriptionAdapter() {
                    @Override
                    public CharSequence getCurrentContentTitle(Player player) {
                        return player.getCurrentMediaItem().mediaMetadata.title;
                    }

                    @Nullable
                    @Override
                    public PendingIntent createCurrentContentIntent(Player player) {
                        return createPendingIntent();
                    }

                    @Nullable
                    @Override
                    public CharSequence getCurrentContentText(Player player) {
                        return player.getCurrentMediaItem().mediaMetadata.artist;
                    }

                    @Nullable
                    @Override
                    public Bitmap getCurrentLargeIcon(Player player, PlayerNotificationManager.BitmapCallback callback) {
                        Uri uri = ContentUris.withAppendedId(Constants.sArtworkUri,
                                Long.parseLong(player.getCurrentMediaItem().mediaMetadata.compilation.toString()));
                        Glide.with(MusicService.this)
                                .asBitmap()
                                .load(uri)
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(new CustomTarget<Bitmap>() {
                                    @Override
                                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                        callback.onBitmap(resource);
                                    }

                                    @Override
                                    public void onLoadCleared(@Nullable Drawable placeholder) {
                                        // Handle if needed
                                    }
                                });
                        return null;
                    }
                    // Implement the adapter methods to provide information for the notification
                })
                .setNotificationListener(new PlayerNotificationManager.NotificationListener() {
                    @Override
                    public void onNotificationPosted(int notificationId, Notification notification, boolean ongoing) {
                        // Always keep the notification as a foreground service
                        startForeground(NOTIFICATION_ID, notification);
                    }

                    @Override
                    public void onNotificationCancelled(int notificationId, boolean dismissedByUser) {
                        if (dismissedByUser) {
                            stopForeground(true); // Fully remove the notification
                            stopSelf(); // Stop the service
                        }
                    }
                })
                .build();

        // Attach the player to the notification manager
        playerNotificationManager.setPlayer(player);
    }

    @Nullable
    @Override
    public MediaSession onGetSession(MediaSession.ControllerInfo controllerInfo) {
        return mediaSession;
    }

    private void initializeMediaComponents() {
        // Initialize ExoPlayer instance
        player = new ExoPlayer.Builder(this).build();
        player.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                sendPlaybackStateBroadcast();
            }
            @Override
            public void onMediaItemTransition(@Nullable MediaItem mediaItem, int reason) {
                sendTrackChangeBroadcast(mediaItem);
            }
            public void onPlayerError(PlaybackException error) {
                Log.e(TAG, "Playback error: " + error.getMessage(), error);
            }
        });
        mediaSession = new MediaSession.Builder(this, player).build();
    }

    // The user dismissed the app from the recent tasks
    @Override
    public void onTaskRemoved(Intent rootIntent){
        Player player = mediaSession.getPlayer();
        if(player != null && (player.getMediaItemCount() == 0 || !player.getPlayWhenReady())){
            // Stop the service if not playing, continue playing in the background
            // otherwise.
            player.release();
            stopSelf();
        }
        super.onTaskRemoved(rootIntent);
        //todo if you want the playback to keep going in the background(don't know what that looks like rn)
//        if (player.getPlayWhenReady()) {
//            // Make sure the service is not in foreground.
//            player.pause();
//        }
//        stopSelf();
    }


    private void sendPlaybackStateBroadcast() {
        long currentMS = System.currentTimeMillis();
        int currentPlaybackState = player.getPlaybackState();
        boolean currentIsPlaying = player.isPlaying();

        if (currentMS - lastUpdateTime >= UPDATE_INTERVAL) {
            Intent intent = new Intent("PlaybackStateChanged");
            intent.putExtra("state", currentPlaybackState);
            intent.putExtra("isPlaying", currentIsPlaying);
            LocalBroadcastManager.getInstance(MusicService.this).sendBroadcast(intent);

            // Update last state and time
            lastPlaybackState = currentPlaybackState;
            lastIsPlaying = currentIsPlaying;
            lastUpdateTime = currentMS;
        }
    }

    private void sendTrackChangeBroadcast(MediaItem mediaItem) {
        if(mediaItem != null){
            Intent intent = new Intent("TrackChanged");
            MediaMetadata metadata = mediaItem.mediaMetadata;

            // Update preferences
            updateSharedPreferences(metadata);

            intent.putExtra("title", metadata.title);
            intent.putExtra("artist", metadata.artist);
            intent.putExtra("albumTitle", metadata.albumTitle);
            intent.putExtra("duration", metadata.discNumber);
            intent.putExtra("albumID", metadata.compilation);
            intent.putExtra("path", metadata.subtitle);
            intent.putExtra("isPlaying", player.isPlaying());
            LocalBroadcastManager.getInstance(MusicService.this).sendBroadcast(intent);
        }
    }
    public int getCurrentPlaybackState() {
        // Return the current playback state (e.g., PLAYING, PAUSED, STOPPED).
        return playbackState;
    }

    private PendingIntent createPendingIntent() {
        // This will be launched when the user taps on the notification
        Intent intent = new Intent(MusicService.this, ExoMusicPlayer.class);
        intent.setAction("OPEN_PLAYER_ACTION");
        int requestCode = 0;
        return PendingIntent.getActivity(MusicService.this, requestCode, intent, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);
    }

    private PendingIntent createPendingIntentForAction(String action) {
        // Create a PendingIntent for playback actions
        Intent intent = new Intent(this, MusicService.class);
        //can put extras inside intent if needed
        intent.setAction(action);
        int requestCode = 0;
        return PendingIntent.getService(this, requestCode, intent, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);
    }

    // Other methods, such as handling audio focus, releasing resources, etc.

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        super.onBind(intent);
        return serviceBinder;
    }

    @OptIn(markerClass = UnstableApi.class) @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int result = super.onStartCommand(intent, flags, startId);
        if(intent != null && intent.getAction() != null){
            String action = intent.getAction();
            Log.d(TAG, "ACTION: " + action);
            switch (action){
                case Constants.ACTION_PLAY:
                    ArrayList<MusicModel> songsList = intent.getParcelableArrayListExtra("SONGS");
                    int position = intent.getIntExtra("POS", 0);
                    if (songsList != null && !songsList.isEmpty()) {
                        player.setMediaItems(getMediaItems(songsList), position, 0);
                        player.prepare();
                        currentSong = player.getCurrentMediaItem();
                    }
                    playMusic();
                    break;
                case Constants.ACTION_PAUSE:
                    pauseMusic();
                    break;
                case Constants.ACTION_SKIP_NEXT:
                    skipToNext();
                    break;
                case Constants.ACTION_SKIP_PREVIOUS:
                    skipToPrevious();
                    break;
                default:
                    break;
            }
        }

        return result;
    }


    public void playMusic() {
//        playbackState = PLAYBACK_STATE_PLAYING;
        player.prepare();
        player.play();
        sendPlaybackStateBroadcast();
    }

    public void pauseMusic() {
//        playbackState = PLAYBACK_STATE_PAUSED;
        player.pause();
        sendPlaybackStateBroadcast();
    }

    public void skipToNext(){
//        playbackState = PLAYBACK_STATE_SONG_CHANGED;
        if(player.hasNextMediaItem()){
            player.seekToNext();
            currentSong = player.getCurrentMediaItem();

            // Notify the listener after the song has changed
            if (currentMediaItemChangedListener != null) {
                currentMediaItemChangedListener.onCurrentMediaItemChanged(currentSong);
            }
        }
        sendPlaybackStateBroadcast();
    }
    public void skipToPrevious(){
//        playbackState = PLAYBACK_STATE_SONG_CHANGED;
        if(player.hasPreviousMediaItem()){
            player.seekToPrevious();
            currentSong = player.getCurrentMediaItem();

            // Notify the listener after the song has changed
            if (currentMediaItemChangedListener != null) {
                currentMediaItemChangedListener.onCurrentMediaItemChanged(currentSong);
            }
        }
        sendPlaybackStateBroadcast();
    }

    public void seekTo(int position){
        player.seekTo(position, 0);
    }

    public void seekToMs(int position){
        player.seekTo(position);
    }

    public void stopMusic() {
//        playbackState = PLAYBACK_STATE_STOPPED;
        player.stop();
        sendPlaybackStateBroadcast();
    }

    public void releasePlayer(){
//        playbackState = PLAYBACK_STATE_IDLE;
        if(player != null){
            player.release();
            player.release();
        }
        player = null;
    }

    public long getCurrentPlayerPosition(){
        if(player != null){
            return player.getCurrentPosition();
        }
        return 0;
    }

    public MediaItem getCurrentMediaItem(){
        return player.getCurrentMediaItem();
    }

    public long getDuration() {
        if (player != null && player.getCurrentMediaItem() != null) {
            MediaMetadata metadata = player.getCurrentMediaItem().mediaMetadata;
            if (metadata != null) {
                // Check if durationMs is not null before unboxing it
                Integer duration = metadata.discNumber;
                return (duration != null) ? duration : 0;
            }
        }
        return 0;
    }

    public ExoPlayer getPlayer() {
        return player;
    }

    public int getPlaybackState() {
        return playbackState;
    }

    public boolean isPlaying(){
        return player.isPlaying();
    }

    public interface OnCurrentMediaItemChangedListener {
        void onCurrentMediaItemChanged(MediaItem newMediaItem);
    }

    public void setOnCurrentMediaItemChangedListener(OnCurrentMediaItemChangedListener listener) {
        this.currentMediaItemChangedListener = listener;
    }

    @OptIn(markerClass = UnstableApi.class) @Override
    public void onDestroy() {
        // Stop the foreground service
        // If music is still playing, keep the service foreground
        // Otherwise, clean up and stop the foreground service
        stopForeground(player == null || !player.isPlaying()); // Keep the notification

        // Detach player from notification manager
        if (playerNotificationManager != null) {
            playerNotificationManager.setPlayer(null);
        }

        mediaSession.getPlayer().release();
        mediaSession.release();
        mediaSession = null;

        savePlaybackState(); // Save playback state when service is stopped

        if (player != null) {
            player.release();
            player = null;
        }
        super.onDestroy();
    }

    //information about the current media item that can be taken in by other classes
    private void updateSharedPreferences(MediaMetadata metadata){
        SharedPreferences.Editor editor = getSharedPreferences(Constants.MUSIC_LAST_PLAYED, MODE_PRIVATE)
                .edit();
        //subtitle holds currentSong.getPath()
        editor.putString(Constants.MUSIC_FILE, (String) metadata.subtitle);
        //compilation holds currentSong.getAlbumId()
        editor.putString(Constants.ALBUM_ID, (String) metadata.compilation);
        editor.putString(Constants.TITLE, (String) metadata.title);
        editor.putString(Constants.ARTIST, (String) metadata.artist);
        editor.apply();
    }

    private void savePlaybackState() {
        if (player != null && player.getCurrentMediaItem() != null) {
            MediaMetadata metadata = player.getCurrentMediaItem().mediaMetadata;
            SharedPreferences.Editor editor = getSharedPreferences("MusicPrefs", MODE_PRIVATE).edit();

            editor.putString("lastSong", metadata.title.toString());
            editor.putString("lastArtist", metadata.artist.toString());
            editor.putString("lastAlbum", metadata.albumTitle.toString());
            editor.putLong("lastPosition", player.getCurrentPosition());
            editor.apply(); // Save the state
        }
    }

    private List<MediaItem> getMediaItems(ArrayList<MusicModel> songsList){
        //turn songs into media items
        List<MediaItem> mediaItems = new ArrayList<>();
        for(MusicModel song : songsList){
            MediaItem mediaItem = new MediaItem.Builder()
                    .setUri(Uri.fromFile(new File(song.getPath())))
                    .setMediaMetadata(getMetadata(song))
                    .build();

            //add media item to media items list
            mediaItems.add(mediaItem);
        }
        return mediaItems;
    }


    private MediaMetadata getMetadata(MusicModel song) {
        return new MediaMetadata.Builder()
                .setTitle(song.getTitle())
                .setArtist(song.getArtist())
                .setAlbumTitle(song.getAlbum())
                .setDiscNumber(Integer.parseInt(song.getDuration()))
                .setCompilation(song.getAlbumId())
                .setSubtitle(song.getPath())
                .build();
    }



    public class MyServiceBinder extends Binder {
        public MusicService getMusicService(){
            return MusicService.this;
        }

        public SessionToken getSessionToken() {
            return mediaSession.getToken();
        }

    }
}

