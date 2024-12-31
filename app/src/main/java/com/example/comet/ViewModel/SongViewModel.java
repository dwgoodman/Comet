package com.example.comet.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.comet.MusicRepository;
import com.example.comet.Song.MusicModel;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SongViewModel extends ViewModel {
    private final MusicRepository repository;
    private final MutableLiveData<List<MusicModel>> songList = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isDescending = new MutableLiveData<>(true);

    public SongViewModel(MusicRepository repository) {
        this.repository = repository;
    }

    public LiveData<List<MusicModel>> getSongList() {
        return songList;
    }

    public LiveData<Boolean> getSortOrder() {
        return isDescending;
    }

    public void toggleSortOrder() {
        isDescending.setValue(!Boolean.TRUE.equals(isDescending.getValue()));
        sortSongs();
    }

    public void loadSongs(List<MusicModel> songs) {
        songList.setValue(songs);
    }

    public void sortSongs() {
        if (songList.getValue() != null) {
            List<MusicModel> sortedList = new ArrayList<>(songList.getValue());
            Comparator<MusicModel> comparator = Comparator.comparing(MusicModel::getTitle);
            if (Boolean.FALSE.equals(isDescending.getValue())) {
                comparator = comparator.reversed();
            }
            sortedList.sort(comparator);
            songList.setValue(sortedList);
        }
    }
}
