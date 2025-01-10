package com.example.comet.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.comet.song.SongModel;
import com.example.comet.util.UtilMethods;

import java.util.ArrayList;
import java.util.List;

public class SongListFromAlbumViewModel extends ViewModel {
    private final MutableLiveData<List<SongModel>> albumSongs = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<String> albumId = new MutableLiveData<>();

    public LiveData<List<SongModel>> getAlbumSongs() {
        return albumSongs;
    }

    public LiveData<String> getAlbumId() {
        return albumId;
    }

    public void loadAlbumId(String id) {
        albumId.setValue(id);
    }

    public void loadAlbumSongs(List<SongModel> songs) {
        albumSongs.setValue(new ArrayList<>(songs));
    }

    public void setAlbumData(String id, List<SongModel> songs) {
        albumId.setValue(id);
        albumSongs.setValue(songs);
    }

    public LiveData<String> getSongCountAndDuration() {
        return Transformations.map(albumSongs, songs -> {
            long totalDuration = 0;
            for (SongModel song : songs) {
                totalDuration += Long.parseLong(song.getDuration());
            }
            String durationFormatted = UtilMethods.prettyDuration(totalDuration);
            return songs.size() + (songs.size() == 1 ? " song" : " songs") + " (" + durationFormatted + ")";
        });
    }
}