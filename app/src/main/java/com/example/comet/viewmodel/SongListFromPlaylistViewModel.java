package com.example.comet.viewmodel;

import android.app.Application;
import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import androidx.media3.common.MediaItem;
import androidx.media3.common.MediaMetadata;

import com.example.comet.database.PlaylistDatabase;
import com.example.comet.database.PlaylistSongDao;
import com.example.comet.database.PlaylistSongEntity;
import com.example.comet.song.SongModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SongListFromPlaylistViewModel extends AndroidViewModel {
    private LiveData<List<PlaylistSongEntity>> playlistSongs;
    private final MediatorLiveData<String> playlistSize = new MediatorLiveData<>();
    private final PlaylistSongDao playlistSongDao;
    private LiveData<String> firstAlbumId;
    private final MutableLiveData<MediaItem> lastPlayedSong = new MutableLiveData<>();

    public SongListFromPlaylistViewModel(Application application, int playlistId) {
        super(application);
        PlaylistDatabase db = PlaylistDatabase.getInstance(application);
        playlistSongDao = db.playlistSongDao();
        playlistSongs = playlistSongDao.getSongsInPlaylist(playlistId);

        firstAlbumId = Transformations.map(playlistSongs, songs -> {
            if (songs != null && !songs.isEmpty()) {
                return songs.get(0).albumId;
            } else {
                return "0"; // Default album ID
            }
        });

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
    public List<PlaylistSongEntity> getPlaylistSongsOnly() {
        return playlistSongs.getValue() != null ? playlistSongs.getValue() : new ArrayList<>();
    }

    public LiveData<String> getPlaylistSize() {
        return playlistSize;
    }

    public LiveData<String> getFirstAlbumId() {
        return firstAlbumId;
    }

    public LiveData<MediaItem> getLastPlayedSong() {
        return lastPlayedSong;
    }

    public void setPlaylistSongs(Integer playlistId){
        playlistSongs = playlistSongDao.getSongsInPlaylist(playlistId);
    }

    public void setAlbumId(){
        firstAlbumId = Transformations.map(playlistSongs, songs -> {
            if (songs != null && !songs.isEmpty()) {
                return songs.get(0).albumId;
            } else {
                return "0"; // Default album ID
            }
        });
    }

    public void setLastPlayedSong(String songId) {
        MediaItem mediaItem = findMediaItemById(songId);
        if (mediaItem != null) {
            lastPlayedSong.postValue(mediaItem);
        }
    }

    private MediaItem findMediaItemById(String songId) {
        for (PlaylistSongEntity song : getPlaylistSongsOnly()) {
            if (song.songId.equals(songId)) {
                return new MediaItem.Builder()
                        .setUri(Uri.fromFile(new File(song.path)))
                        .setMediaMetadata(new MediaMetadata.Builder()
                                .setTitle(song.title)
                                .setArtist(song.artist)
                                .setAlbumTitle(song.album)
                                .build())
                        .build();
            }
        }
        return null;
    }
    public void setLastPlayedSongMediaItem(MediaItem mediaItem){
        lastPlayedSong.setValue(mediaItem);
    }


}
