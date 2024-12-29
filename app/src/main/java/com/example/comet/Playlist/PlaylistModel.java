package com.example.comet.Playlist;

import com.example.comet.Song.MusicModel;

import java.util.List;

public class PlaylistModel {
    private String name;
    private List<MusicModel> songs;

    public PlaylistModel(String name, List<MusicModel> songs) {
        this.name = name;
        this.songs = songs;
    }

    public String getName() {
        return name;
    }

    public List<MusicModel> getSongs() {
        return songs;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSongs(List<MusicModel> songs) {
        this.songs = songs;
    }
}
