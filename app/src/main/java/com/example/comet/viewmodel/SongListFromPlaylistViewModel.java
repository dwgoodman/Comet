package com.example.comet.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.comet.song.SongModel;

import java.util.ArrayList;
import java.util.List;

public class SongListFromPlaylistViewModel extends ViewModel {
    private final MutableLiveData<List<SongModel>> playlistSongs = new MutableLiveData<>();

    public LiveData<List<SongModel>> getPlaylistSongs() {
        return playlistSongs;
    }

    public void loadPlaylistSongs(List<SongModel> songs) {
        playlistSongs.setValue(new ArrayList<>(songs));
    }

    public LiveData<String> getPlaylistSize() {
        return Transformations.map(playlistSongs, songs -> songs.size() + (songs.size() == 1 ? " song" : " songs"));
    }

    public void removeSong(SongModel song) {
        if (playlistSongs.getValue() != null) {
            List<SongModel> updatedList = new ArrayList<>(playlistSongs.getValue());
            updatedList.remove(song);
            playlistSongs.setValue(updatedList);
        }
    }
}
