package com.example.comet.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.comet.playlist.PlaylistModel;
import com.example.comet.song.SongModel;

import java.util.ArrayList;
import java.util.List;

public class PlaylistViewModel extends ViewModel {
    private final MutableLiveData<List<PlaylistModel>> playlistList = new MutableLiveData<>(new ArrayList<>());

    public LiveData<List<PlaylistModel>> getPlaylistList() {
        return playlistList;
    }

    public void addPlaylist(String name, List<SongModel> songs) {
        if (playlistList.getValue() != null) {
            List<PlaylistModel> updatedList = new ArrayList<>(playlistList.getValue());
            updatedList.add(new PlaylistModel(name, songs));
            playlistList.setValue(updatedList);
        }
    }

    public void removePlaylist(String name) {
        if (playlistList.getValue() != null) {
            List<PlaylistModel> updatedList = new ArrayList<>(playlistList.getValue());
            updatedList.removeIf(playlist -> playlist.getName().equals(name));
            playlistList.setValue(updatedList);
        }
    }
}
