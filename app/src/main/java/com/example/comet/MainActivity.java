package com.example.comet;

import androidx.annotation.OptIn;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
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

import com.example.comet.album.AlbumFragment;
import com.example.comet.album.AlbumListFromArtistFragment;
import com.example.comet.album.AlbumModel;
import com.example.comet.artist.ArtistFragment;
import com.example.comet.artist.ArtistModel;
import com.example.comet.playlist.PlaylistFragment;
import com.example.comet.song.SongModel;
import com.example.comet.song.SongFragment;
import com.example.comet.song.SongListFromAlbumFragment;
import com.example.comet.song.SongViewModelFactory;
import com.example.comet.util.Constants;
import com.example.comet.viewmodel.AlbumViewModel;
import com.example.comet.viewmodel.ArtistViewModel;
import com.example.comet.viewmodel.SongViewModel;
import com.example.comet.viewmodel.PlaylistViewModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity implements  AlbumFragment.AlbumFragmentListener, ArtistFragment.ArtistFragmentListener, AlbumListFromArtistFragment.AlbumListFromArtistFragmentListener, SongListFromAlbumFragment.SongListFromAlbumFragmentListener {

    ArrayList<SongModel> musicList = new ArrayList<>();
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

        //Creating ViewModels
        MusicRepository musicRepository = new MusicRepository(this);
        SongViewModelFactory factory = new SongViewModelFactory(musicRepository);
        SongViewModel songViewModel = new ViewModelProvider(this, factory).get(SongViewModel.class);
        AlbumViewModel albumViewModel = new ViewModelProvider(this).get(AlbumViewModel.class);
        ArtistViewModel artistViewModel = new ViewModelProvider(this).get(ArtistViewModel.class);
        PlaylistViewModel playlistViewModel = new ViewModelProvider(this).get(PlaylistViewModel.class);

        //grabbing data from Media Store and loading in ViewModels
        List<SongModel> queriedSongs = musicRepository.querySongs();
        songViewModel.loadSongs(queriedSongs);

        List<AlbumModel> queriedAlbums = musicRepository.queryAlbums();
        albumViewModel.loadAlbums(queriedAlbums);

        List<ArtistModel> queriedArtists = musicRepository.queryArtists();
        artistViewModel.loadArtists(queriedArtists);

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
                showSortOptionsDialog(songViewModel);
            } else if (currentFragment instanceof AlbumFragment) {
                showSortOptionsDialog(albumViewModel);
            } else if (currentFragment instanceof ArtistFragment) {
                showSortOptionsDialog(artistViewModel);
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
    public void toSongListFromAlbumFragment(ArrayList<SongModel> songsList){
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.mainContainer, SongListFromAlbumFragment.newInstance(songsList))
                .addToBackStack(null)
                .commit();
    }



    @Override
    public void toSongsListFromAlbumRowFragment(ArrayList<SongModel> songsList) {
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

    void setUpTabs(ArrayList<SongModel> musicList, ArrayList<AlbumModel> albumList, ArrayList<ArtistModel> artistList){
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

        //todo update to actually use playlistList when it is created
        Bundle bundle3 = new Bundle();
        bundle3.putParcelableArrayList(Constants.PLAYLISTS_PARAM, artistList);
        PlaylistFragment playlistFragment = new PlaylistFragment();
        playlistFragment.setArguments(bundle3);

        adapter.addFragment(songFragment, "Songs");
        adapter.addFragment(albumFragment, "Albums");
        adapter.addFragment(artistFragment, "Artists");
        adapter.addFragment(playlistFragment, "Playlists");


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

    @OptIn(markerClass = UnstableApi.class)
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

    @OptIn(markerClass = UnstableApi.class)
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
    @OptIn(markerClass = UnstableApi.class)
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

    private void showSortOptionsDialog(SongViewModel songViewModel) {
        String[] sortOptions = {"Title", "Artist", "Duration", "Date Added"};
        new AlertDialog.Builder(this)
                .setTitle("Sort Songs By")
                .setItems(sortOptions, (dialog, which) -> {
                    switch (which) {
                        case 0: // Title
                            songViewModel.sortSongs(Comparator.comparing(SongModel::getTitle), isDescending);
                            break;
                        case 1: // Artist
                            songViewModel.sortSongs(Comparator.comparing(SongModel::getArtist), isDescending);
                            break;
                        case 2: // Duration
                            songViewModel.sortSongs(Comparator.comparing(SongModel::getDuration), isDescending);
                            break;
                        case 3: // Date Added
                            songViewModel.sortSongs(Comparator.comparing(SongModel::getDateAdded), isDescending);
                            break;
                    }
                })
                .show();
    }

    private void showSortOptionsDialog(AlbumViewModel albumViewModel) {
        String[] sortOptions = {"Album Name", "Artist", "Release Year"};
        new AlertDialog.Builder(this)
                .setTitle("Sort Albums By")
                .setItems(sortOptions, (dialog, which) -> {
                    switch (which) {
                        case 0: // Album Name
                            albumViewModel.sortAlbums(Comparator.comparing(AlbumModel::getAlbum), isDescending);
                            break;
                        case 1: // Artist
                            albumViewModel.sortAlbums(Comparator.comparing(AlbumModel::getAlbumArtist), isDescending);
                            break;
                        case 2: // Release Year
                            albumViewModel.sortAlbums(Comparator.comparing(AlbumModel::getFirstYear), isDescending);
                            break;
                    }
                })
                .show();
    }

    private void showSortOptionsDialog(ArtistViewModel artistViewModel) {
        String[] sortOptions = {"Artist Name"};
        new AlertDialog.Builder(this)
                .setTitle("Sort Artists By")
                .setItems(sortOptions, (dialog, which) -> {
                    if (which == 0) { // Artist Name
                        artistViewModel.sortArtists(Comparator.comparing(ArtistModel::getArtist), isDescending);
                    }
                })
                .show();
    }

}