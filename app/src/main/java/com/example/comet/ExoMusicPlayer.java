package com.example.comet;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.media3.common.MediaItem;
import androidx.media3.common.MediaMetadata;
import androidx.media3.common.Player;
import androidx.media3.session.MediaBrowser;
import androidx.media3.session.MediaController;
import androidx.media3.session.SessionToken;
import androidx.palette.graphics.Palette;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.comet.Song.MusicModel;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;

import java.io.File;
import java.util.concurrent.ExecutionException;

public class ExoMusicPlayer extends AppCompatActivity {
    //todo there are a lot of performance improvements that can be done in here listed below
    //basically I'm making UI calls willy nilly and that can be heavily condensed(maybe not as much now)
    //I need to condense where I am making the glide calls because for each state change or each instantiation I am calling glide multiple times
    private TextView songTitle, albumAndArtistTitle, currentTime, totalTime;
    private ImageView pausePlayButton, skipForward, skipBackwards, shuffleButton, repeatButton, songPlayerImage, minimizeButton;
    private ConstraintLayout musicActivityLayout;
    private SeekBar musicSeekBar;
    private MediaItem currentSong;
    private MusicService musicService;
    private int playbackState;
    private boolean isPlaying;
    //is activity bound to service?
    boolean isBound = false;
    private MediaBrowserCompat mediaBrowser;
    private ListenableFuture<MediaController> controllerFuture;
    private ListenableFuture<MediaBrowser> browserFuture;
    private SessionToken sessionToken;

    private String title;
    private String artist;
    private String albumTitle;
    private int duration;
    private String albumID;
    private String path;
    private static final String PAUSE_TAG = "pauseFrog";

    private final BroadcastReceiver playbackStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if ("PlaybackStateChanged".equals(action)) {
                playbackState = intent.getIntExtra("state", -1);
                isPlaying = intent.getBooleanExtra("isPlaying", false);
                // Update UI based on the playback state.
                updateUI(playbackState, isPlaying);
            } else if ("TrackChanged".equals(action)) {
                //take the information passed from the intent and create the media item(since we can't just pass the media item)
                title = intent.getStringExtra("title");
                artist = intent.getStringExtra("artist");
                albumTitle = intent.getStringExtra("albumTitle");
                duration = intent.getIntExtra("duration", 0);
                albumID = intent.getStringExtra("albumID");
                path = intent.getStringExtra("path");
                isPlaying = intent.getBooleanExtra("isPlaying", false);
                setResourcesWithSongBroadcast();
            }
        }
    };

    // Update UI based on the playback state.
    //take the information passed from the intent and create the media item(since we can't just pass the media item)
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //bind player instead of creating in this class
        doBindService();

        setContentView(R.layout.activity_music_player);

        songTitle = findViewById(R.id.songTitle);
        albumAndArtistTitle = findViewById(R.id.albumAndArtistTitle);
        currentTime = findViewById(R.id.currentTime);
        totalTime = findViewById(R.id.totalTime);
        pausePlayButton = findViewById(R.id.pausePlayButton);
        skipForward = findViewById(R.id.skipForward);
        skipBackwards = findViewById(R.id.skipBackwards);
        shuffleButton = findViewById(R.id.shuffleButton);
        repeatButton = findViewById(R.id.repeatButton);
        songPlayerImage = findViewById(R.id.songPlayerImage);
        musicSeekBar = findViewById(R.id.musicSeekBar);
        minimizeButton = findViewById(R.id.minimizeButtonAlbum);
        musicActivityLayout = findViewById(R.id.musicActivityLayout);

        if(isBound){
            onServiceConnectedActions();
        }

        //please don't accidentally delete the pausePlayButton again it scared me
        pausePlayButton.setOnClickListener(v -> pausePlay());
        Log.d(PAUSE_TAG, "PausePlay listener set in setResourcesWithSong");
        skipForward.setOnClickListener(v -> playNextSong());
        skipBackwards.setOnClickListener(v -> playPreviousSong());
        minimizeButton.setOnClickListener(v -> minimizeSong());
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Check if playback state is saved
        SharedPreferences preferences = getSharedPreferences("MusicPrefs", MODE_PRIVATE);
        String lastSong = preferences.getString("lastSong", null);
        String lastArtist = preferences.getString("lastArtist", null);
        String lastAlbum = preferences.getString("lastAlbum", null);
        long lastPosition = preferences.getLong("lastPosition", 0);

        if (lastSong != null) {
            // Populate the GUI with the saved state
            songTitle.setText(lastSong);
            albumAndArtistTitle.setText(String.format("%s // %s", lastArtist, lastAlbum));
            musicSeekBar.setProgress((int) lastPosition);

            // Restart the MusicService to restore the player state
            Intent intent = new Intent(this, MusicService.class);
            intent.putExtra("song", lastSong);
            intent.putExtra("position", lastPosition);
            startService(intent);
        } else {
            // Load the default fragment if no saved state
        }
    }

    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, MusicService.class);
        bindService(intent, musicServiceConnection, Context.BIND_AUTO_CREATE);
//        mediaBrowser.connect();
    }

        private final MediaBrowserCompat.ConnectionCallback connectionCallbacks =
                new MediaBrowserCompat.ConnectionCallback() {
                    @Override
                    public void onConnected() {
                        // Get the token for the MediaSession
                        MediaSessionCompat.Token token = mediaBrowser.getSessionToken();

                        // Create a MediaControllerCompat
                        MediaControllerCompat mediaController =
                                new MediaControllerCompat(ExoMusicPlayer.this, token);

                        // Save the controller
                        MediaControllerCompat.setMediaController(ExoMusicPlayer.this, mediaController);

                        // Now you can use mediaController to send commands to your service
                    }

                    @Override
                    public void onConnectionSuspended() {
                        // The service has crashed. Disable transport controls until it automatically reconnects
                    }

                    @Override
                    public void onConnectionFailed() {
                        // The service has refused our connection
                    }
                };


    private void onServiceConnectedActions(){
//        currentSong = getMediaItem(songsList.get(position));
        currentSong = musicService.getCurrentMediaItem();
        playbackState = musicService.getCurrentPlaybackState();

        // Add Player.Listener using musicService.getPlayer()
        musicService.getPlayer().addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                musicSeekBar.setMax((int) musicService.getPlayer().getDuration());
            }

            @Override
            public void onPositionDiscontinuity(Player.PositionInfo oldPosition, Player.PositionInfo newPosition, int reason) {
                musicSeekBar.setProgress((int) musicService.getPlayer().getCurrentPosition());
            }
        });

        //creating a filter for the intents this class needs to listen for and registering the receiver to catch them
        IntentFilter filter = new IntentFilter();
        filter.addAction("PlaybackStateChanged");
        filter.addAction("TrackChanged");
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(playbackStateReceiver, filter);


        musicService.setOnCurrentMediaItemChangedListener(new MusicService.OnCurrentMediaItemChangedListener() {
            @Override
            public void onCurrentMediaItemChanged(MediaItem newMediaItem) {
                // Handle the updated media item
                currentSong = newMediaItem;
                setResourcesWithSong();
            }
        });
    }


    void setResourcesWithSong(){
        //todo I have to set up a way to pass the current song to this, I can just send the songs list over even tho thats prob not the correct way
        //todo but I will see later, the other way would be to have a current song method in the MusicSerice class
//        currentSong = musicService.getCurrentMediaItem();
        songTitle.setText((currentSong.mediaMetadata.title));
        albumAndArtistTitle.setText(String.format("%s // %s", currentSong.mediaMetadata.artist, currentSong.mediaMetadata.albumTitle));
        currentTime.setText(R.string._0_00);
        totalTime.setText(UtilMethods.prettyDuration(currentSong.mediaMetadata.discNumber));

        Uri uri = ContentUris.withAppendedId(Constants.sArtworkUri,
                Long.parseLong(currentSong.mediaMetadata.compilation.toString()));

        //todo I think activity: this is crashing when Glide tries to load it after I click around furiously
        //I think I fixed it with this, still monitor until code cleanup
        if(!this.isDestroyed()) {
            //takes the constant artwork Uri and appends the ablumId to retrieve the album artwork
            //damn if this isn't the worst fucking call I've ever written
            Glide.with(this).asBitmap().load(uri).placeholder(R.drawable.background_for_load)
                    .error(R.drawable.hoshi).diskCacheStrategy(DiskCacheStrategy.ALL).centerCrop().into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            songPlayerImage.setImageBitmap(resource);
                            Palette.from(resource).generate(new Palette.PaletteAsyncListener() {
                                public void onGenerated(Palette palette) {
                                    Palette.Swatch swatch = palette.getVibrantSwatch();
                                    GradientDrawable gd;
                                    ConstraintLayout musicActivityLayout = findViewById(R.id.musicActivityLayout);
                                    if (swatch != null) {
                                        gd = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, new int[]{swatch.getRgb(), swatch.getRgb()});
                                        musicActivityLayout.setBackground(gd);
                                    }else {
                                        gd = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, new int[]{0x38393838, 0x38393838});
                                        musicActivityLayout.setBackground(gd);
                                    }
                                }

                            });
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                            songPlayerImage.setImageDrawable(placeholder);
                        }
                    });
        }

        playMusic();
    }

    void setResourcesWithSongBroadcast(){
        //todo I have to set up a way to pass the current song to this, I can just send the songs list over even tho thats prob not the correct way
        //todo but I will see later, the other way would be to have a current song method in the MusicSerice class
//        currentSong = musicService.getCurrentMediaItem();
        songTitle.setText((title));
        albumAndArtistTitle.setText(String.format("%s // %s", artist, albumTitle));
        currentTime.setText(R.string._0_00);
        totalTime.setText(UtilMethods.prettyDuration(duration));

        Uri uri = ContentUris.withAppendedId(Constants.sArtworkUri,
                Long.parseLong(albumID));

        //todo I think activity: this is crashing when Glide tries to load it after I click around furiously
        //I think I fixed it with this, still monitor until code cleanup
        if(!this.isDestroyed()) {
            //takes the constant artwork Uri and appends the ablumId to retrieve the album artwork
            //damn if this isn't the worst fucking call I've ever written
            Glide.with(this).asBitmap().load(uri).placeholder(R.drawable.background_for_load)
                    .error(R.drawable.hoshi).diskCacheStrategy(DiskCacheStrategy.ALL).centerCrop().into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            songPlayerImage.setImageBitmap(resource);
                            Palette.from(resource).generate(new Palette.PaletteAsyncListener() {
                                public void onGenerated(Palette palette) {
                                    Palette.Swatch swatch = palette.getVibrantSwatch();
                                    GradientDrawable gd;
                                    ConstraintLayout musicActivityLayout = findViewById(R.id.musicActivityLayout);
                                    if (swatch != null) {
                                        gd = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, new int[]{swatch.getRgb(), swatch.getRgb()});
                                        musicActivityLayout.setBackground(gd);
                                    }else {
                                        gd = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, new int[]{0x38393838, 0x38393838});
                                        musicActivityLayout.setBackground(gd);
                                    }
                                }

                            });
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                            songPlayerImage.setImageDrawable(placeholder);
                        }
                    });
        }

        playMusicBroadcast();
    }

    private void playMusic(){
        musicService.playMusic();
        musicSeekBar.setProgress(0);
        musicSeekBar.setMax(musicService.getCurrentMediaItem().mediaMetadata.discNumber);
    }

    private void playMusicBroadcast(){
        musicService.playMusic();
        musicSeekBar.setProgress(0);
        musicSeekBar.setMax(duration);
    }

    public void playNextSong(){
        musicService.skipToNext();
    }

    public void playPreviousSong(){
        musicService.skipToPrevious();
    }

    public void pausePlay(){
        if(isPlaying){
            musicService.pauseMusic();
        }else {
            musicService.playMusic();
        }
    }

    public void minimizeSong(){
        musicActivityLayout.setVisibility(View.GONE);
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    //todo make sure the instance of a playing song is not destroyed when backing out of a song
    protected void onDestroy(){
        super.onDestroy();
        if(isBound){
            unbindService(musicServiceConnection);
            isBound = false;
        }
        MediaController.releaseFuture(controllerFuture);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(playbackStateReceiver);
        if (musicService != null) {
//            musicService.stopMusic();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (MediaControllerCompat.getMediaController(ExoMusicPlayer.this) != null) {
            // Clear the controller
            MediaControllerCompat.setMediaController(ExoMusicPlayer.this, null);
        }
//        mediaBrowser.disconnect(); // Disconnect from the MediaBrowserService
        seekBarHandler.removeCallbacks(updateSeekBarTask);
    }

    private boolean isUpdatingUI = false;
    private void updateUI(int playbackState, boolean isPlaying) {
        if (musicService != null && !isUpdatingUI) {
            isUpdatingUI = true;
            // Update UI based on the playback state
            switch (playbackState) {

                //idle
                case Player.STATE_IDLE:

                    break;
                //playing
                case Player.STATE_BUFFERING:

                    break;
                case MusicService.PLAYBACK_STATE_IDLE:
                    //paused
                case Player.STATE_READY:
                    Log.d(PAUSE_TAG, "Playbackstate = " + playbackState + "\nisPlaying = " + isPlaying);
                    if (isPlaying) {
                        pausePlayButton.setImageResource(R.drawable.baseline_pause_circle_24);
                        Log.d(PAUSE_TAG, "Pause image set in updateUI if");
                    } else {
                        pausePlayButton.setImageResource(R.drawable.baseline_play_circle_24);
                        Log.d(PAUSE_TAG, "Play image set in updateUI else");
                    }
                    isUpdatingUI = false;
                    break;
                //stopped
                case Player.STATE_ENDED:
                    break;
                default:
                    break;
            }
        }


    }

    private MediaItem getMediaItem(MusicModel song) {
        MediaItem mediaItem =
                new MediaItem.Builder()
                        .setMediaId("media-1")
                        .setUri(Uri.fromFile(new File(song.getPath())))
                        .setMediaMetadata(getMetadata(song))
                        .build();
        return mediaItem;
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


    private void doBindService(){
        Intent musicServiceIntent = new Intent(this, MusicService.class);
        startService(musicServiceIntent);
        bindService(musicServiceIntent, musicServiceConnection, Context.BIND_AUTO_CREATE);
    }

    private boolean isServiceAvailable() {
        Intent intent = new Intent(this, MusicService.class);
        return getPackageManager().resolveService(intent, 0) != null;
    }

    ServiceConnection musicServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //get the service instance
            MusicService.MyServiceBinder binder = (MusicService.MyServiceBinder) service;
            musicService = binder.getMusicService();
            isBound = true;
            sessionToken = binder.getSessionToken();
            updateSeekBar();
            if (musicService != null) {
                currentSong = musicService.getCurrentMediaItem();
                if (currentSong != null) {
                    // Update UI with current song's information
                    setResourcesWithSong();
                }
            }

            controllerFuture =
                    new MediaController.Builder(ExoMusicPlayer.this, sessionToken).buildAsync();
            controllerFuture.addListener(() -> {
                // MediaController is available here with controllerFuture.get()
                try {
                    MediaController controller = controllerFuture.get();
                    runOnUiThread(() -> {
                        // Update the seek bar and current time
                        currentTime.setText(UtilMethods.prettyDuration((int) controller.getCurrentPosition()));
                        musicSeekBar.setProgress((int) controller.getCurrentPosition());

                        // Set the seek bar listener
                        musicSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                if (fromUser) {
                                    controller.seekTo(progress);
                                    seekBar.setProgress(progress);
                                }
                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {
                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                            }
                        });

                        // Update other UI elements
                        setResourcesWithSong();
                        Log.d(PAUSE_TAG, "SetResources in onServiceConnected");
                    });
                } catch (ExecutionException | InterruptedException e) {
                    Log.e("ControllerFutureError", "Error retrieving controller: " + e.getMessage(), e);
                }
            }, MoreExecutors.directExecutor());

            // Optionally, build the MediaBrowser if needed
            browserFuture = new MediaBrowser.Builder(ExoMusicPlayer.this, sessionToken).buildAsync();
            browserFuture.addListener(() -> {
                try {
                    MediaBrowser mediaBrowser = browserFuture.get();
                    // Use mediaBrowser here
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            }, MoreExecutors.directExecutor());
            Log.d("bingbong", "onServiceConnected: pog");
            //ready to show songs
            onServiceConnectedActions();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
            Log.d("bingbong", "onServiceDisconnected: why are we here");
        }
    };

    //this code handles querying the music service for the players current pos intermittently to update the seekbar
    private final Handler seekBarHandler = new Handler();
    private final Runnable updateSeekBarTask = new Runnable() {
        @Override
        public void run() {
            if (isBound && musicService != null) {
                long currentPosition = musicService.getCurrentPlayerPosition();
                musicSeekBar.setProgress((int) currentPosition);
                currentTime.setText(UtilMethods.prettyDuration((int) currentPosition));
            }
            seekBarHandler.postDelayed(this, 1000); // Update every second
        }
    };
    private void updateSeekBar() {
        seekBarHandler.post(updateSeekBarTask);
    }

}
