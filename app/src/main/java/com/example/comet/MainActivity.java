package com.example.comet;

import androidx.annotation.OptIn;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.media3.common.util.Log;
import androidx.media3.common.util.UnstableApi;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.comet.Album.AlbumFragment;
import com.example.comet.Album.AlbumListFromArtistFragment;
import com.example.comet.Album.AlbumModel;
import com.example.comet.Artist.ArtistFragment;
import com.example.comet.Artist.ArtistModel;
import com.example.comet.Song.MusicModel;
import com.example.comet.Song.SongFragment;
import com.example.comet.Song.SongListFromAlbumFragment;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.tabs.TabLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity implements  AlbumFragment.AlbumFragmentListener, ArtistFragment.ArtistFragmentListener, AlbumListFromArtistFragment.AlbumListFromArtistFragmentListener, SongListFromAlbumFragment.SongListFromAlbumFragmentListener {

    ArrayList<MusicModel> musicList = new ArrayList<>();
    ArrayList<AlbumModel> albumList = new ArrayList<>();
    ArrayList<ArtistModel> artistList = new ArrayList<>();
    public static boolean showHomeMusicPlayer = false;
    public static String PATH_PASSED = null;
    public static String TITLE_PASSED = null;
    public static String ALBUM_ID_PASSED = null;
    public static String ARTIST_PASSED = null;
    private BottomSheetBehavior<View> bottomSheetBehavior;
    boolean isDescending = true;

    @OptIn(markerClass = UnstableApi.class) @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView noMusicTextView = findViewById(R.id.noMusicTextView);

        if(!checkPermission()){
            requestPermission();
            return;
        }

        String[] projection = {
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.DATE_ADDED
        };

        //only taking music from media store
        String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";

        //query the media store for my selected audio parameters
        Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, selection, null, MediaStore.Audio.Media.TITLE);


        //iterating over selected parameters and adding to the custom model
        while(cursor.moveToNext()){
            MusicModel musicData = new MusicModel(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6));
            if(new File(musicData.getPath()).exists()) {
                musicList.add(musicData);
            }
        }
        cursor.close();

        //creating new projection/cursor to fill albumModel
        String[] projection1 = {
                MediaStore.Audio.Albums._ID,
                MediaStore.Audio.Albums.ARTIST,
                MediaStore.Audio.Albums.ALBUM,
                MediaStore.Audio.Albums.ALBUM_ART,
                MediaStore.Audio.Albums.NUMBER_OF_SONGS,
                MediaStore.Audio.Albums.FIRST_YEAR
        };

        //query the media store for my selected audio parameters
        Cursor cursor1 = getContentResolver().query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, projection1, null, null, null);

        while(cursor1.moveToNext()){
            AlbumModel albumData = new AlbumModel(cursor1.getString(0), cursor1.getString(1), cursor1.getString(2), cursor1.getString(3), cursor1.getString(4), cursor1.getString(5));
            albumList.add(albumData);
        }
        cursor1.close();

        //todo maybe see if I can use one cursor instead of many, used many to make sure no errors came up between threads when retrieving
        String[] projection2 = {
                MediaStore.Audio.Artists._ID,
                MediaStore.Audio.Artists.ARTIST,
                MediaStore.Audio.Artists.NUMBER_OF_ALBUMS
        };

        //query the media store for my selected audio parameters
        Cursor cursor2 = getContentResolver().query(MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI, projection2, null, null, null);


        //iterating over selected parameters and adding to the custom model
        while(cursor2.moveToNext()){
            ArtistModel artistData = new ArtistModel(cursor2.getString(0), cursor2.getString(1), cursor2.getString(2));
            artistList.add(artistData);
        }
        cursor2.close();


        setUpTabs(musicList, albumList, artistList);

        ImageView toggleSortOrderButton = findViewById(R.id.toggleSortOrderButton);
        toggleSortOrderButton.setOnClickListener(v -> {
                    // Toggle the sort order
                    isDescending = !isDescending;

                    // Update icon to reflect sort order
                    toggleSortOrderButton.setImageResource(
                            isDescending ? R.drawable.descending : R.drawable.ascending);
                });

        ImageView filterButton = findViewById(R.id.mainFilterButton); // Ensure this ID matches your XML
        filterButton.setOnClickListener(v -> {
            Log.d("MainActivity", "Filter button clicked");
            Fragment currentFragment = getCurrentFragment(); // Determine the active fragment

            if (currentFragment instanceof SongFragment) {
                showSortOptionsDialog((SongFragment) currentFragment);
            } else if (currentFragment instanceof AlbumFragment) {
                showSortOptionsDialog((AlbumFragment) currentFragment);
            } else if (currentFragment instanceof ArtistFragment) {
                showSortOptionsDialog((ArtistFragment) currentFragment);
            }
        });



//        FrameLayout bottomSheet = findViewById(R.id.bottom_sheet);
//        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
//        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
//        findViewById(R.id.full_player_layout).setVisibility(View.VISIBLE);
//        bottomSheetBehavior.setPeekHeight(59);
//
//        // Optionally set callbacks
//        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
//            @Override
//            public void onStateChanged(@NonNull View bottomSheet, int newState) {
//                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
//                    // Show full player, hide mini player
//                    findViewById(R.id.full_player_layout).setVisibility(View.VISIBLE);
//                    findViewById(R.id.mini_player_layout).setVisibility(View.GONE);
//                } else if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
//                    // Show mini player, hide full player
//                    findViewById(R.id.full_player_layout).setVisibility(View.GONE);
//                    findViewById(R.id.mini_player_layout).setVisibility(View.VISIBLE);
//                }
//            }
//
//            @Override
//            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
//                // Handle sliding
//            }
//        });
//        showBottomSheetPlayer();

    }
    boolean checkPermission(){
        int result = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    void requestPermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)){
            Toast.makeText(MainActivity.this, "Read permission is required, please allow from settings", Toast.LENGTH_SHORT);
        }else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 123);
        }
    }
    @Override
    public void toSongListFromAlbumFragment(ArrayList<MusicModel> songsList){
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.mainContainer, SongListFromAlbumFragment.newInstance(songsList))
                .addToBackStack(null)
                .commit();
    }



    @Override
    public void toSongsListFromAlbumRowFragment(ArrayList<MusicModel> songsList) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.mainContainer, SongListFromAlbumFragment.newInstance(songsList))
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void toAlbumListFromArtistFragment(ArrayList<AlbumModel> albumsList){
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.mainContainer, AlbumListFromArtistFragment.newInstance(albumsList))
                .addToBackStack(null)
                .commit();
    }

    public void moveBackFromAlbum(){
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed(); // Handle default back navigation
        }
    }

    public void moveBackFromArtistAlbum() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed(); // Handle default back navigation
        }
    }

    void setUpTabs(ArrayList<MusicModel> musicList, ArrayList<AlbumModel> albumList, ArrayList<ArtistModel> artistList){
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(Constants.SONGS_PARAM, musicList);
        bundle.putParcelableArrayList(Constants.ALBUMS_PARAM, albumList);
        SongFragment songFragment = new SongFragment();
        songFragment.setArguments(bundle);

        Bundle bundle1 = new Bundle();
        bundle1.putParcelableArrayList(Constants.ALBUMS_PARAM, albumList);
        AlbumFragment albumFragment = new AlbumFragment();
        albumFragment.setArguments(bundle1);

        Bundle bundle2 = new Bundle();
        bundle2.putParcelableArrayList(Constants.ARTISTS_PARAM, artistList);
        ArtistFragment artistFragment = new ArtistFragment();
        artistFragment.setArguments(bundle2);

        adapter.addFragment(songFragment, "Songs");
        adapter.addFragment(albumFragment, "Albums");
        adapter.addFragment(artistFragment, "Artists");


        ViewPager viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(adapter);
        TabLayout tabs = findViewById(R.id.tabs);


        setUpSeekBar(viewPager);
        tabs.setupWithViewPager(viewPager);


    }

    public void setUpSeekBar(ViewPager viewPager){
        SeekBar verticalScrollBar = findViewById(R.id.verticalScrollBar);

        // Link the scroll bar to the initial fragment
        Fragment initialFragment = ((ViewPagerAdapter) viewPager.getAdapter()).getItem(viewPager.getCurrentItem());
        if (initialFragment instanceof SongFragment) {
            ((SongFragment) initialFragment).linkScrollBar(verticalScrollBar);
        } else if (initialFragment instanceof AlbumFragment) {
            ((AlbumFragment) initialFragment).linkScrollBar(verticalScrollBar);
        } else if (initialFragment instanceof ArtistFragment) {
            ((ArtistFragment) initialFragment).linkScrollBar(verticalScrollBar);
        }

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                Fragment currentFragment = ((ViewPagerAdapter) viewPager.getAdapter()).getItem(position);
                if (currentFragment instanceof SongFragment) {
                    ((SongFragment) currentFragment).linkScrollBar(verticalScrollBar);
                } else if (currentFragment instanceof AlbumFragment) {
                    ((AlbumFragment) currentFragment).linkScrollBar(verticalScrollBar);
                } else if (currentFragment instanceof ArtistFragment) {
                    ((ArtistFragment) currentFragment).linkScrollBar(verticalScrollBar);
                }
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageScrollStateChanged(int state) {}
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isMusicServiceRunning()) {
            // Restart the service with minimal initialization
            Intent intent = new Intent(this, MusicService.class);
            startService(intent);
        }


        SharedPreferences preferences = getSharedPreferences(Constants.MUSIC_LAST_PLAYED, MODE_PRIVATE);
        String path = preferences.getString(Constants.MUSIC_FILE, null);
        String albumId = preferences.getString(Constants.ALBUM_ID, null);
        String title = preferences.getString(Constants.TITLE, null);
        String artist = preferences.getString(Constants.ARTIST, null);
        if(path != null && albumId != null){
            showHomeMusicPlayer = true;
            PATH_PASSED = path;
            ALBUM_ID_PASSED = albumId;
            TITLE_PASSED = title;
            ARTIST_PASSED = artist;
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.homeMusicContainer, new NowPlayingBottomFragment())
                    .addToBackStack(null)
                    .commit();
        }else{
            showHomeMusicPlayer = false;
            PATH_PASSED = null;
            TITLE_PASSED = null;
            ALBUM_ID_PASSED = null;
            ARTIST_PASSED = null;
        }
    }

    private boolean isMusicServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (MusicService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    //todo restores the playback state i.e. reinitializes the ExoMusicPlayer screen with the last saved song after the notification is dismissed
    private void restorePlaybackState() {
        SharedPreferences preferences = getSharedPreferences("MusicPrefs", MODE_PRIVATE);
        String lastSong = preferences.getString("lastSong", null);
        long lastPosition = preferences.getLong("lastPosition", 0);

        if (lastSong != null) {
            // Restore the last playback state in the UI or service
            Intent intent = new Intent(this, MusicService.class);
            intent.putExtra("song", lastSong);
            intent.putExtra("position", lastPosition);
            startService(intent);
        } else {
            // Show the default fragment to pick a new song
//            loadDefaultFragment();
        }
    }

    void minimizeSongsMain(){

    }

    private void showBottomSheetPlayer() {
        PlayerBottomSheetFragment bottomSheetFragment = new PlayerBottomSheetFragment();
        bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
    }

    public void toggleBottomSheet() {
        if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        } else {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }

    private Fragment getCurrentFragment() {
        ViewPager viewPager = findViewById(R.id.viewPager); // Ensure this ID matches your XML
        int currentItem = viewPager.getCurrentItem();
        return ((ViewPagerAdapter) viewPager.getAdapter()).getItem(currentItem);
    }

    private void showSortOptionsDialog(SongFragment fragment) {
        String[] sortOptions = {"Title", "Artist", "Duration", "Date Added"};
        new AlertDialog.Builder(this)
                .setTitle("Sort Songs By")
                .setItems(sortOptions, (dialog, which) -> {
                    switch (which) {
                        case 0: // Title
                            fragment.sortSongs(Comparator.comparing(MusicModel::getTitle), isDescending);
                            break;
                        case 1: // Artist
                            fragment.sortSongs(Comparator.comparing(MusicModel::getArtist), isDescending);
                            break;
                        case 2: // Duration
                            fragment.sortSongs(Comparator.comparing(MusicModel::getDuration), isDescending);
                            break;
                        case 3: // Date Added
                            fragment.sortSongs(Comparator.comparing(MusicModel::getDateAdded), isDescending);
                            break;
                    }
                })
                .show();
    }

    private void showSortOptionsDialog(AlbumFragment fragment) {
        String[] sortOptions = {"Album Name", "Artist", "Release Year"};
        new AlertDialog.Builder(this)
                .setTitle("Sort Albums By")
                .setItems(sortOptions, (dialog, which) -> {
                    switch (which) {
                        case 0: // Album Name
                            fragment.sortAlbums(Comparator.comparing(AlbumModel::getAlbum), isDescending);
                            break;
                        case 1: // Artist
                            fragment.sortAlbums(Comparator.comparing(AlbumModel::getAlbumArtist), isDescending);
                            break;
                        case 2: // Release Year
                            fragment.sortAlbums(Comparator.comparing(AlbumModel::getFirstYear), isDescending);
                            break;
                    }
                })
                .show();
    }

    private void showSortOptionsDialog(ArtistFragment fragment) {
        String[] sortOptions = {"Album Name", "Artist", "Release Year"};
        new AlertDialog.Builder(this)
                .setTitle("Sort Albums By")
                .setItems(sortOptions, (dialog, which) -> {
                    if (which == 0) { // Album Name
                        fragment.sortAlbums(Comparator.comparing(ArtistModel::getArtist), isDescending);
                    }
                })
                .show();
    }

}