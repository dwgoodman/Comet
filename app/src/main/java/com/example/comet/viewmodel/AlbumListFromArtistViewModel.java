package com.example.comet.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.comet.album.AlbumModel;

import java.util.ArrayList;
import java.util.List;

public class AlbumListFromArtistViewModel extends ViewModel {
    //stores the first album's id for picture retrieval
    private final MutableLiveData<String> albumId = new MutableLiveData<>();
    private final MutableLiveData<List<AlbumModel>> artistAlbums = new MutableLiveData<>(new ArrayList<>());
    public LiveData<String> getAlbumId() {
        return albumId;
    }

    public LiveData<List<AlbumModel>> getArtistAlbums() {
        return artistAlbums;
    }

    public void setAlbumData(String id, List<AlbumModel> albums) {
        albumId.setValue(id);
        artistAlbums.setValue(albums);
    }

    public void loadArtistAlbums(List<AlbumModel> albums) {
        if (albums != null && !albums.isEmpty()) {
            albumId.setValue(albums.get(0).getId());
        }
        artistAlbums.setValue(new ArrayList<>(albums));
    }

    public LiveData<String> getAlbumCountAndSongCount() {
        return Transformations.map(artistAlbums, albums -> {
            long numAlbumSongs = 0;
            for (AlbumModel album : albums) {
                numAlbumSongs += Long.parseLong(album.getNumberOfSongs());
            }
            return albums.size() + (albums.size() == 1 ? " album" : " albums") + ", " +
                    numAlbumSongs + (numAlbumSongs == 1 ? " song" : " songs");
        });
    }
}