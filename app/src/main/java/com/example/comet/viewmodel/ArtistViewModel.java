package com.example.comet.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.comet.album.AlbumModel;
import com.example.comet.artist.ArtistModel;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ArtistViewModel extends ViewModel {
    private final MutableLiveData<List<ArtistModel>> artistList = new MutableLiveData<>();
    public LiveData<List<ArtistModel>> getArtistList() {
        return artistList;
    }

    public void loadArtists(List<ArtistModel> data) {
        artistList.setValue(data);
    }

    public void sortArtists(Comparator<ArtistModel> comparator, boolean isDescending) {
        if (artistList.getValue() != null) {
            List<ArtistModel> sortedList = new ArrayList<>(artistList.getValue());
            if (!isDescending) {
                sortedList.sort(comparator.reversed());
            } else {
                sortedList.sort(comparator);
            }
            artistList.setValue(sortedList);
        }
    }
}
