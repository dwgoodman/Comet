package com.example.comet.Song;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.comet.MusicRepository;
import com.example.comet.ViewModel.SongViewModel;

import java.util.List;

public class SongViewModelFactory implements ViewModelProvider.Factory {
    private final MusicRepository repository;

    public SongViewModelFactory(MusicRepository repository) {
        this.repository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(SongViewModel.class)) {
            return (T) new SongViewModel(repository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
