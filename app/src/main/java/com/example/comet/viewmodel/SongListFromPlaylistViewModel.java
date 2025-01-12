package com.example.comet.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.comet.database.PlaylistDatabase;
import com.example.comet.database.PlaylistSongDao;
import com.example.comet.database.PlaylistSongEntity;
import com.example.comet.song.SongModel;

import java.util.ArrayList;
import java.util.List;

public class SongListFromPlaylistViewModel extends ViewModel {
    private final LiveData<List<PlaylistSongEntity>> playlistSongs;
    private final MediatorLiveData<String> playlistSize = new MediatorLiveData<>();
    private final PlaylistSongDao playlistSongDao;

    public SongListFromPlaylistViewModel(Application application, int playlistId) {
        super();
        PlaylistDatabase db = PlaylistDatabase.getInstance(application);
        playlistSongDao = db.playlistSongDao();
        playlistSongs = playlistSongDao.getSongsInPlaylist(playlistId);

        // Observe playlistSongs and update playlistSize dynamically
        playlistSize.addSource(playlistSongs, songs -> {
            if (songs != null) {
                playlistSize.setValue(songs.size() + (songs.size() == 1 ? " song" : " songs"));
            } else {
                playlistSize.setValue("0 songs");
            }
        });
    }

    public LiveData<List<PlaylistSongEntity>> getPlaylistSongs() {
        return playlistSongs;
    }

    public LiveData<String> getPlaylistSize() {
        return playlistSize;
    }
}
