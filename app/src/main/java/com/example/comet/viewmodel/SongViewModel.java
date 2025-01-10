package com.example.comet.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.comet.MusicRepository;
import com.example.comet.song.SongModel;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SongViewModel extends ViewModel {
    private final MusicRepository repository;
    private final MutableLiveData<List<SongModel>> songList = new MutableLiveData<>();

    public SongViewModel(MusicRepository repository) {
        this.repository = repository;
    }

    public LiveData<List<SongModel>> getSongList() {
        return songList;
    }

    public void loadSongs(List<SongModel> songs) {
        songList.setValue(songs);
    }

    public void sortSongs(Comparator<SongModel> comparator, boolean isDescending) {
        if (songList.getValue() != null) {
            List<SongModel> sortedList = new ArrayList<>(songList.getValue());

            // Apply sorting
            if (!isDescending) {
                sortedList.sort(comparator.reversed());
            } else {
                sortedList.sort(comparator);
            }

            // Update LiveData
            songList.setValue(sortedList);
        }
    }
}
