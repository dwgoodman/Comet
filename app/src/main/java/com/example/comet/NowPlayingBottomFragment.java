package com.example.comet;

import static com.example.comet.MainActivity.ALBUM_ID_PASSED;
import static com.example.comet.MainActivity.ARTIST_PASSED;
import static com.example.comet.MainActivity.TITLE_PASSED;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.media3.common.Player;

import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.comet.util.Constants;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class NowPlayingBottomFragment extends Fragment {

    TextView homeSongTitle, homeSongArtist;
    ImageView homeSongImage;
    FloatingActionButton homePlayPauseButton;
    MusicService musicService;
    ConstraintLayout homeMusicPlayer;
    private boolean isPlaying;
    private boolean isBound = false;
    private int playbackState;
    private String title;
    private String artist;
    private String albumTitle;
    private int duration;
    private String albumID;
    private String path;

    public NowPlayingBottomFragment() {
        // Required empty public constructor
    }

    public static NowPlayingBottomFragment newInstance() {
        NowPlayingBottomFragment fragment = new NowPlayingBottomFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_now_playing_bottom, container, false);

        homeSongTitle = view.findViewById(R.id.homeSongTitle);
        homeSongArtist = view.findViewById(R.id.homeSongArtist);
        homeSongImage = view.findViewById(R.id.homeSongImage);
        homePlayPauseButton = view.findViewById(R.id.homePlayPauseButton);
        homeMusicPlayer = view.findViewById(R.id.homeMusicPlayer);

//        if(getContext() != null) {
//            Intent intent = new Intent(getContext(), MusicService.class);
//            getContext().bindService(intent, musicServiceConnection, Context.BIND_AUTO_CREATE);
//        }

        homePlayPauseButton.setOnClickListener(v -> pausePlay());
        homeMusicPlayer.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), ExoMusicPlayer.class);
            // Optionally, pass any required data through intent extras
            startActivity(intent);
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(MainActivity.showHomeMusicPlayer){
            Uri uri = ContentUris.withAppendedId(Constants.sArtworkUri,
                    Long.parseLong(ALBUM_ID_PASSED));
            Glide.with(getContext()).asBitmap().load(uri)
                    .placeholder(R.drawable.background_for_load)
                    .error(R.drawable.hoshi)
                    .centerCrop().into(homeSongImage);
            homeSongTitle.setText(TITLE_PASSED);
            homeSongArtist.setText(ARTIST_PASSED);
        }
        if (isAdded() && isVisible() && getContext() != null && musicService != null) {
            playbackState = musicService.getPlaybackState();
            isPlaying = musicService.isPlaying();
            updateUI(playbackState, isPlaying);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
//        if(getContext() != null){
//            getContext().unbindService(this);
//        }
    }

    public void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter();
        filter.addAction("PlaybackStateChanged");
        filter.addAction("TrackChanged");
        if(getContext() != null) {
            LocalBroadcastManager.getInstance(getContext()).registerReceiver(playbackStateReceiver, filter);
        }

        if(getContext() != null) {
            Intent intent = new Intent(getContext(), MusicService.class);
            getContext().bindService(intent, musicServiceConnection, Context.BIND_AUTO_CREATE);
        }
    }

    public void onStop() {
        super.onStop();
        //Unregister BroadcastReceiver
        if(isBound && getContext() != null) {
            getContext().unbindService(musicServiceConnection);
            isBound = false;
        }
    }

    private final ServiceConnection musicServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MyServiceBinder binder = (MusicService.MyServiceBinder) service;
            musicService = binder.getMusicService();
            isBound = true;
//            updatePlaybackControls();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
            musicService = null;
        }
    };


    private void updateUI(int playbackState, boolean isPlaying) {
        if (musicService != null) {
            switch (playbackState){
                //idle
                case Player.STATE_IDLE:
                    break;
                //custom idle state
                case MusicService.PLAYBACK_STATE_IDLE:
                case Player.STATE_READY:
                    // Normal ready state logic
                    // Fallback: Check if playback is active despite idle state
                    Drawable drawable = ContextCompat.getDrawable(getContext(),
                            isPlaying ? R.drawable.baseline_pause_circle_24 : R.drawable.baseline_play_circle_24);
                    homePlayPauseButton.setImageDrawable(drawable);
                    homePlayPauseButton.requestLayout();
                    Log.d("pauseFrog", "Setting drawable: " +
                            (isPlaying ? "Pause Button" : "Play Button"));
                    homePlayPauseButton.requestLayout();
                    break;
                //playing
                case Player.STATE_BUFFERING:

                    break;
                //stopped
                case Player.STATE_ENDED:
                    // Ended state logic
                    homePlayPauseButton.setImageResource(R.drawable.baseline_play_circle_24);
                    break;
                default:
                    break;
            }
        }
    }

    private void playMusic(){
        musicService.playMusic();
    }

    public void playNextSong(){
        musicService.skipToNext();
    }

    public void pausePlay(){
        if (musicService != null && musicService.getCurrentMediaItem() != null) {
            if (isPlaying) {
                musicService.pauseMusic();
            } else {
                musicService.playMusic();
            }
        } else {
            // Inform the user
            Toast.makeText(getContext(), "Please select a song first", Toast.LENGTH_SHORT).show();
        }
    }
    private final BroadcastReceiver playbackStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ("PlaybackStateChanged".equals(intent.getAction())) {
                playbackState = intent.getIntExtra("state", -1);
                isPlaying = intent.getBooleanExtra("isPlaying", false);
                // Update UI based on the playback state.
                updateUI(playbackState, isPlaying);
            }
            if("TrackChanged".equals(intent.getAction())){
                //take the information passed from the intent and create the media item(since we can't just pass the media item)
                title = intent.getStringExtra("title");
                artist = intent.getStringExtra("artist");
                albumTitle = intent.getStringExtra("albumTitle");
                duration = intent.getIntExtra("duration", 0);
                albumID = intent.getStringExtra("albumID");
                path = intent.getStringExtra("path");
                isPlaying = intent.getBooleanExtra("isPlaying", false);
                //then set the song's resources
                setResourcesWithSongBroadcast();
            }
        }
    };

    private void setResourcesWithSongBroadcast(){
        homeSongTitle.setText(title);
        homeSongArtist.setText(artist);
        // Load album art with Glide
        // Set play/pause button based on isPlaying
        if(isPlaying) {
            homePlayPauseButton.setImageResource(R.drawable.baseline_pause_circle_24);
        } else {
            homePlayPauseButton.setImageResource(R.drawable.baseline_play_circle_24);
        }
    }

    public void onDestroy(){
        super.onDestroy();
        if(getContext() != null) {
            LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(playbackStateReceiver);
        }
        if (musicService != null) {
            musicService.stopMusic();
        }
    }

}