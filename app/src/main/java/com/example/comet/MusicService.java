package com.example.comet;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
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
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
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
import com.example.comet.song.SongModel;
import com.example.comet.util.Constants;
import com.example.comet.viewmodel.PlaylistViewModel;
import com.example.comet.viewmodel.SongListFromPlaylistViewModel;
import com.example.comet.viewmodel.SongViewModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@UnstableApi
public class MusicService extends MediaSessionService {

    private static final String CHANNEL_ID = "channel1";
    ExoPlayer player;
    private PlayerNotificationManager playerNotificationManager;
    private final int playbackState = PLAYBACK_STATE_IDLE;
    private static final int NOTIFICATION_ID = 111;
    private MediaItem currentSong;
    private List<MediaItem> mediaItemList = new ArrayList<>();

    private Notification currentNotification;
    private boolean isForegroundService = false;
    public static final int PLAYBACK_STATE_IDLE = 0;
    private OnCurrentMediaItemChangedListener currentMediaItemChangedListener;
    //in milliseconds
    private static final long UPDATE_INTERVAL = 200;
    private long lastUpdateTime = 0;
    private final IBinder serviceBinder = new MyServiceBinder();
    private MediaSession mediaSession = null;
    private int lastPlaybackState = -1;
    private boolean lastIsPlaying = false;

    private boolean isPlaying = false;
    private static final String TAG = "MusicService";

    private final IBinder binder = new MusicBinder();

    private MusicService musicService;
    private boolean isBound = false;

    private String activeListType = "songList";

    private SongViewModel songViewModel;
    private SongListFromPlaylistViewModel songListFromPlaylistViewModel;


    public MusicService(){

    }

    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
            musicService = binder.getService();
            isBound = true;
            Log.d("MusicService", "Service Bound");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicService = null;
            isBound = false;
            Log.d("MusicService", "Service Unbound");
        }
    };

    //todo make sure currentSong.mediaId lines up with the use case (i.e. if mediaId tries to search things by songId it might fail)
    private void savePlaybackState(String activeListType) {
        if (currentSong != null && currentSong.mediaId != null) {
            SharedPreferences prefs = getSharedPreferences("music_prefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("last_song_id", currentSong.mediaId);
            editor.putLong("last_position", player.getCurrentPosition());
            editor.putString("active_list", activeListType);
            editor.apply();
        } else {
            Log.e("MusicService", "savePlaybackState: currentSong is null, skipping save.");
        }
    }

    //todo connect these together, viewmodels, restorePlaybackState, updatesSongViewModel. Will probably need to create new vars for storing the last position of a song or others
    private void restorePlaybackState() {
        SharedPreferences prefs = getSharedPreferences("music_prefs", MODE_PRIVATE);
        String lastSongId = prefs.getString("last_song_id", null);
        long lastPosition = prefs.getLong("last_position", 0);
        String activeList = prefs.getString("active_list", "songList");

        if (lastSongId != null) {
            playSong(lastSongId);
            player.seekTo(lastPosition);

            if (activeList.equals("playlist")) {
                songListFromPlaylistViewModel.setLastPlayedSong(lastSongId);
            } else {
                songViewModel.setLastPlayedSong(lastSongId);
            }
        }
    }

    private List<MediaItem> convertToMediaItems(List<SongModel> songList) {
        List<MediaItem> mediaItems = new ArrayList<>();
        for (SongModel song : songList) {
            mediaItems.add(MediaItem.fromUri(song.getPath()));
        }
        return mediaItems;
    }

    public void playSong(String songId) {
        MediaItem mediaItem = findMediaItemById(songId);
        if (mediaItem != null) {
            player.setMediaItem(mediaItem);
            player.prepare();
            player.play();
            isPlaying = true;
        }
    }

    public void playPlaylist(List<SongModel> songList, int startIndex) {
        mediaItemList.clear();
        mediaItemList = getMediaItems(songList);
        player.setMediaItems(getMediaItems(songList), startIndex, 0);
        player.prepare();
        player.play();
    }

    private MediaItem findMediaItemById(String songId) {
        for (MediaItem item : mediaItemList) {
            if (item.mediaId.equals(songId)) {
                return item;
            }
        }
        return null;
    }

    public MediaItem getCurrentSong() {
        return currentSong;
    }

    private void updateCurrentSong(MediaItem mediaItem) {
        currentSong = mediaItem;

        if (activeListType.equals("playlist")) {
            songListFromPlaylistViewModel.setLastPlayedSongMediaItem(mediaItem);
        } else {
            songViewModel.setLastPlayedSongMediaItem(mediaItem);
        }
    }

    public void setViewModels(SongViewModel songViewModel, SongListFromPlaylistViewModel playlistViewModel) {
        this.songViewModel = songViewModel;
        this.songListFromPlaylistViewModel = playlistViewModel;
    }


    @OptIn(markerClass = UnstableApi.class)
    @Override
    public void onCreate() {
        super.onCreate();
        initializeMediaComponents();

        //Create the ExoMusicPlayer
        createPlayerListener();
        //Create a PlayerNotificationManager
        createPlayerNotificationManager();

        //Attach the player to the notification manager
        playerNotificationManager.setPlayer(player);

    }

    public void createPlayerListener(){
        // Add a Player.Listener for playback-specific events
        player.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                isPlaying = player.isPlaying();

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
    }
    public void createPlayerNotificationManager(){
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
                        currentNotification = notification;

                        //Only start foreground service if it has not been started previously
                        if (ongoing && !isForegroundService) {
                            startForeground(NOTIFICATION_ID, notification);
                            isForegroundService = true;
                        } else if (!ongoing) {
                            stopForeground(false);
                            isForegroundService = false;
                        }
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
                isPlaying = player.isPlaying();
            }
            @Override
            public void onMediaItemTransition(@Nullable MediaItem mediaItem, int reason) {
                updateCurrentSong(mediaItem);
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

    @OptIn(markerClass = UnstableApi.class) @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        //Make sure the service starts in foreground if not already started or killed
        if (!isForegroundService && currentNotification != null) {
            startForeground(NOTIFICATION_ID, currentNotification);
            isForegroundService = true;
        }

        restorePlaybackState();
        if (intent != null && intent.getAction() != null) {
            switch (intent.getAction()) {
                case "PLAY":
                    playMusic();
                    break;
                case "PAUSE":
                    pauseMusic();
                    break;
                case "STOP":
                    stopSelf();
                    break;
            }
        }


        return START_STICKY;
    }


    public void playMusic() {
        if (player != null) {
            player.play();
            isPlaying = true;
        }
    }

    public void pauseMusic() {
        if (player != null) {
            player.pause();
            isPlaying = false;
        }
    }

    //todo needs refactor probably
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

    }
    //todo needs refactor probably
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

    }

    public void seekTo(int position){
        player.seekTo(position, 0);
    }

    public void seekToMs(int position){
        player.seekTo(position);
    }

    //todo needs refactor probably
    public void stopMusic() {
//        playbackState = PLAYBACK_STATE_STOPPED;
        player.stop();

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

    public boolean getIsPlaying() {
        return isPlaying;
    }

    public interface OnCurrentMediaItemChangedListener {
        void onCurrentMediaItemChanged(MediaItem newMediaItem);
    }

    public void setOnCurrentMediaItemChangedListener(OnCurrentMediaItemChangedListener listener) {
        this.currentMediaItemChangedListener = listener;
    }

    @OptIn(markerClass = UnstableApi.class) @Override
    public void onDestroy() {
        super.onDestroy();
        if (player != null) {
            if (currentSong != null) {
                savePlaybackState(activeListType);
            }
            player.release();
            player = null;
        }
        stopForeground(true);
        stopSelf();
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
    private List<MediaItem> getMediaItems(List<SongModel> songsList){
        //turn songs into media items
        List<MediaItem> mediaItems = new ArrayList<>();
        for(SongModel song : songsList){
            MediaItem mediaItem = new MediaItem.Builder()
                    .setUri(Uri.fromFile(new File(song.getPath())))
                    .setMediaMetadata(getMetadata(song))
                    .build();

            //add media item to media items list
            mediaItems.add(mediaItem);
        }
        return mediaItems;
    }


    private MediaMetadata getMetadata(SongModel song) {
        return new MediaMetadata.Builder()
                .setTitle(song.getTitle())
                .setArtist(song.getArtist())
                .setAlbumTitle(song.getAlbum())
                .setDiscNumber(Integer.parseInt(song.getDuration()))
                .setCompilation(song.getAlbumId())
                .setSubtitle(song.getPath())
                .build();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        super.onBind(intent);
        return binder;
    }

    public class MusicBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
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

