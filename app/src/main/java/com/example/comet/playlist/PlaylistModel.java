package com.example.comet.playlist;

import com.example.comet.song.SongModel;

import java.util.ArrayList;
import java.util.List;

public class PlaylistModel {
    private String name;
    private List<SongModel> songs;
    private int numSongs;

    public PlaylistModel(String name, List<SongModel> songs) {
        this.name = name;
        this.songs = (songs != null) ? songs : new ArrayList<>();
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

    public int getNumSongs() {
        return songs.size();
    }
}
