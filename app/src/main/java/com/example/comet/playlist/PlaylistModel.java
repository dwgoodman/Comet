package com.example.comet.playlist;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.comet.song.SongModel;

import java.util.ArrayList;
import java.util.List;

public class PlaylistModel implements Parcelable {
    private String name;
    private List<SongModel> songs;

    public PlaylistModel(String name, List<SongModel> songs) {
        this.name = name;
        this.songs = (songs != null) ? songs : new ArrayList<>();
    }

    protected PlaylistModel(Parcel in) {
        name = in.readString();
        songs = in.createTypedArrayList(SongModel.CREATOR); // Ensure SongModel is Parcelable
    }

    public static final Creator<PlaylistModel> CREATOR = new Creator<PlaylistModel>() {
        @Override
        public PlaylistModel createFromParcel(Parcel in) {
            return new PlaylistModel(in);
        }

        @Override
        public PlaylistModel[] newArray(int size) {
            return new PlaylistModel[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeTypedList(songs); // Writes the song list
    }

    @Override
    public int describeContents() {
        return 0;
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
        return (songs != null) ? songs.size() : 0;
    }
}
