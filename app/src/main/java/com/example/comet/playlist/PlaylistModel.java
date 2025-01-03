package com.example.comet.playlist;

import com.example.comet.song.SongModel;

import java.util.List;

public class PlaylistModel {
    private String name;
    private List<SongModel> songs;

    public PlaylistModel(String name, List<SongModel> songs) {
        this.name = name;
        this.songs = songs;
    }

    public String getName() {
        return name;
    }

    public List<SongModel> getSongs() {
        return songs;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSongs(List<SongModel> songs) {
        this.songs = songs;
    }
}
