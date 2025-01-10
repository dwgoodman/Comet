package com.example.comet.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.comet.database.PlaylistDao;
import com.example.comet.database.PlaylistDatabase;
import com.example.comet.database.PlaylistEntity;
import com.example.comet.database.PlaylistSongDao;
import com.example.comet.database.PlaylistSongEntity;

import java.util.List;
import java.util.concurrent.Executors;

public class PlaylistViewModel extends AndroidViewModel {
    private final PlaylistDao playlistDao;
    private final PlaylistSongDao playlistSongDao;
    private final LiveData<List<PlaylistEntity>> allPlaylists;
    private final MutableLiveData<Boolean> showAddPlaylistDialog = new MutableLiveData<>();


    public PlaylistViewModel(@NonNull Application application) {
        super(application);
        PlaylistDatabase db = PlaylistDatabase.getInstance(application);
        playlistDao = db.playlistDao();
        playlistSongDao = db.playlistSongDao();
        allPlaylists = playlistDao.getAllPlaylists(); // Fetch from DB instead of MutableLiveData
    }

    public LiveData<List<PlaylistEntity>> getAllPlaylists() {
        return allPlaylists; // Now database-backed LiveData
    }

    public void insertPlaylist(PlaylistEntity playlist) {
        Executors.newSingleThreadExecutor().execute(() -> playlistDao.insertPlaylist(playlist));
    }

    public void deletePlaylist(PlaylistEntity playlist) {
        Executors.newSingleThreadExecutor().execute(() -> playlistDao.deletePlaylist(playlist));
    }

    public void onAddPlaylistClicked() {
        showAddPlaylistDialog.setValue(true); // Triggers UI to open the dialog
    }

    public void resetAddPlaylistDialog() {
        showAddPlaylistDialog.setValue(false);
    }
    public LiveData<Boolean> getShowAddPlaylistDialog() {
        return showAddPlaylistDialog;
    }

    public LiveData<List<PlaylistSongEntity>> getSongsInPlaylist(int playlistId) {
        return playlistSongDao.getSongsInPlaylist(playlistId);
    }

    public LiveData<Integer> getSongCount(int playlistId) {
        return playlistSongDao.getSongCountInPlaylist(playlistId);
    }
}
