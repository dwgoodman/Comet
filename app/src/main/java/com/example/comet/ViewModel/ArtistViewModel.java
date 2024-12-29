package com.example.comet.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.comet.Artist.ArtistModel;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ArtistViewModel extends ViewModel {
    private final MutableLiveData<List<ArtistModel>> artistList = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isDescending = new MutableLiveData<>(true);

    public LiveData<List<ArtistModel>> getArtistList() {
        return artistList;
    }

    public LiveData<Boolean> getSortOrder() {
        return isDescending;
    }

    public void loadArtistList(List<ArtistModel> data) {
        artistList.setValue(data);
    }

    public void toggleSortOrder() {
        isDescending.setValue(!Boolean.TRUE.equals(isDescending.getValue()));
        sortArtists();
    }

    public void sortArtists() {
        if (artistList.getValue() != null) {
            List<ArtistModel> sortedList = new ArrayList<>(artistList.getValue());
            Comparator<ArtistModel> comparator = Comparator.comparing(ArtistModel::getArtist);
            if (Boolean.FALSE.equals(isDescending.getValue())) {
                comparator = comparator.reversed();
            }
            sortedList.sort(comparator);
            artistList.setValue(sortedList);
        }
    }
}
