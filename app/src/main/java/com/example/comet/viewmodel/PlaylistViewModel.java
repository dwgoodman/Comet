package com.example.comet.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.comet.playlist.PlaylistModel;
import com.example.comet.song.SongModel;

import java.util.ArrayList;
import java.util.Arrays;
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

    public void loadDummyPlaylists() {
        List<PlaylistModel> playlists = new ArrayList<>();

        List<SongModel> songs = Arrays.asList(
                new SongModel("path1", "Song One", "200000", "Artist One", "Album One", "1", "162000"),
                new SongModel("path2", "Song Two", "210000", "Artist Two", "Album Two", "2", "162001")
        );

        playlists.add(new PlaylistModel("Playlist 1", songs));
        playlists.add(new PlaylistModel("Playlist 2", songs));

        if (playlistList.getValue() == null) {
            playlistList.setValue(new ArrayList<>()); // Ensure it's never null
        }

        playlistList.setValue(playlists);
    }

}
