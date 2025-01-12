package com.example.comet.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.comet.database.PlaylistDao;
import com.example.comet.database.PlaylistDatabase;
import com.example.comet.database.PlaylistEntity;
import com.example.comet.database.PlaylistSongDao;
import com.example.comet.database.PlaylistSongEntity;
import com.example.comet.song.SongModel;

import java.util.List;
import java.util.concurrent.Executors;

public class PlaylistViewModel extends AndroidViewModel {
    private final PlaylistDao playlistDao;
    private final PlaylistSongDao playlistSongDao;
    private final LiveData<List<PlaylistEntity>> allPlaylists;
    private final MutableLiveData<Boolean> showAddPlaylistDialog = new MutableLiveData<>();
    private final MutableLiveData<Integer> selectedPlaylistId = new MutableLiveData<>();


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

    public void setSelectedPlaylistId(int playlistId) {
        selectedPlaylistId.setValue(playlistId);
    }

    public LiveData<Integer> getSelectedPlaylistId() {
        return selectedPlaylistId;
    }

    public LiveData<List<PlaylistSongEntity>> getSongsInPlaylist(int playlistId) {
        return playlistSongDao.getSongsInPlaylist(playlistId);
    }

    public void clearAllPlaylists() {
        Executors.newSingleThreadExecutor().execute(() -> {
            playlistDao.deleteAllPlaylists();
            playlistSongDao.deleteAllSongs();
        });
    }

    public LiveData<Integer> getSongCount(int playlistId) {
        return playlistSongDao.getSongCountInPlaylist(playlistId);
    }
    public void addSongToPlaylist(int playlistId, SongModel song) {
        Executors.newSingleThreadExecutor().execute(() -> {
            PlaylistSongEntity songEntry = new PlaylistSongEntity(
                    playlistId,
                    song.getSongId(),
                    song.getTitle(),
                    song.getArtist(),
                    song.getAlbumId(),
                    song.getDuration(),
                    System.currentTimeMillis(),
                    0
            );
            playlistSongDao.insertSongToPlaylist(songEntry);
        });
    }
}
