package com.example.comet.song;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.comet.viewmodel.SongListFromPlaylistViewModel;

public class SongListFromPlaylistViewModelFactory implements ViewModelProvider.Factory {
    private final Application application;
    private final int playlistId;

    public SongListFromPlaylistViewModelFactory(Application application, int playlistId) {
        this.application = application;
        this.playlistId = playlistId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(SongListFromPlaylistViewModel.class)) {
            return (T) new SongListFromPlaylistViewModel(application, playlistId);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}