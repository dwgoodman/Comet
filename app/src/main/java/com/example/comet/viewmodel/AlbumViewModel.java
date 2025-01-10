package com.example.comet.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.comet.album.AlbumModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AlbumViewModel extends ViewModel {
    private final MutableLiveData<List<AlbumModel>> albumList = new MutableLiveData<>();
    public LiveData<List<AlbumModel>> getAlbumList() {
        return albumList;
    }

    public void loadAlbums(List<AlbumModel> data) {
        albumList.setValue(data);
    }

    public void sortAlbums(Comparator<AlbumModel> comparator, boolean isDescending) {
        if (albumList.getValue() != null) {
            List<AlbumModel> sortedList = new ArrayList<>(albumList.getValue());
            if (!isDescending) {
                sortedList.sort(comparator.reversed());
            } else {
                sortedList.sort(comparator);
            }
            albumList.setValue(sortedList);
        }
    }
}
