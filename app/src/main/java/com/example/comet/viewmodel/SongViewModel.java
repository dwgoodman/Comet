package com.example.comet.viewmodel;

import android.app.Application;
import android.net.Uri;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.media3.common.MediaItem;
import androidx.media3.common.MediaMetadata;

import com.example.comet.MusicRepository;
import com.example.comet.song.SongModel;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SongViewModel extends AndroidViewModel {
    private final MusicRepository repository;
    private final MutableLiveData<List<SongModel>> songList = new MutableLiveData<>();
    private final MutableLiveData<MediaItem> lastPlayedSong = new MutableLiveData<>();

    public SongViewModel(Application application,MusicRepository repository) {
        super(application);
        repository = new MusicRepository((application));
        this.repository = repository;
    }

    public LiveData<List<SongModel>> getSongList() {
        return songList;
    }
    public List<SongModel> getSongListOnly() {
        return songList.getValue() != null ? songList.getValue() : new ArrayList<>();
    }

    public void loadSongs(List<SongModel> songs) {
        songList.setValue(songs);
    }

    public void sortSongs(Comparator<SongModel> comparator, boolean isDescending) {
        if (songList.getValue() != null) {
            List<SongModel> sortedList = new ArrayList<>(songList.getValue());

            // Apply sorting
            if (!isDescending) {
                sortedList.sort(comparator.reversed());
            } else {
                sortedList.sort(comparator);
            }

            // Update LiveData
            songList.setValue(sortedList);
        }
    }

    public LiveData<MediaItem> getLastPlayedSong() {
        return lastPlayedSong;
    }

    public List<SongModel> getSongModelList() {
        return songList.getValue() != null ? songList.getValue() : new ArrayList<>();
    }

    public void setLastPlayedSongMediaItem(MediaItem mediaItem){
        lastPlayedSong.setValue(mediaItem);
    }

    public void setLastPlayedSong(String songId) {
        MediaItem mediaItem = findMediaItemById(songId);
        if (mediaItem != null) {
            lastPlayedSong.postValue(mediaItem);
        }
    }

    public MediaItem findMediaItemById(String songId) {
        for (SongModel song : getSongModelList()) {
            if (song.getSongId().equals(songId)) {
                return new MediaItem.Builder()
                        .setUri(Uri.fromFile(new File(song.getPath())))
                        .setMediaMetadata(new MediaMetadata.Builder()
                                .setTitle(song.getTitle())
                                .setArtist(song.getArtist())
                                .setAlbumTitle(song.getAlbum())
                                .build())
                        .build();
            }
        }
        return null;
    }
}
