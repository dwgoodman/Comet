package com.example.comet.song;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.comet.MusicRepository;
import com.example.comet.viewmodel.SongViewModel;

public class SongViewModelFactory implements ViewModelProvider.Factory {
    private final MusicRepository repository;
    private final Application application;

    public SongViewModelFactory(Application application, MusicRepository repository) {
        this.application = application;
        this.repository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(SongViewModel.class)) {
            return (T) new SongViewModel(application, repository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
