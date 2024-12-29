package com.example.comet.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.comet.Album.AlbumModel;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class AlbumViewModel extends ViewModel {
    private final MutableLiveData<List<AlbumModel>> albumList = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isDescending = new MutableLiveData<>(true);

    public LiveData<List<AlbumModel>> getAlbumList() {
        return albumList;
    }

    public LiveData<Boolean> getSortOrder() {
        return isDescending;
    }

    public void loadAlbumList(List<AlbumModel> data) {
        albumList.setValue(data);
    }

    public void toggleSortOrder() {
        isDescending.setValue(!Boolean.TRUE.equals(isDescending.getValue()));
        sortAlbums();
    }

    public void sortAlbums() {
        if (albumList.getValue() != null) {
            List<AlbumModel> sortedList = new ArrayList<>(albumList.getValue());
            Comparator<AlbumModel> comparator = Comparator.comparing(AlbumModel::getAlbum);
            if (Boolean.FALSE.equals(isDescending.getValue())) {
                comparator = comparator.reversed();
            }
            sortedList.sort(comparator);
            albumList.setValue(sortedList);
        }
    }
}
