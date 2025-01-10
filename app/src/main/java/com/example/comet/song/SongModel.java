package com.example.comet.song;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

//had to implement parcelable, don't know if I need to take Serializable out or not
//hopefully can still be used as a normal class, we'll see idk man
public class SongModel implements Serializable, Parcelable {
    String path;
    String title;
    String duration;
    String artist;
    String album;
    String albumId;
    String dateAdded;

    public SongModel(Parcel in) {
        super();
        readFromParcel(in);
    }

    public static final Parcelable.Creator<SongModel> CREATOR = new Parcelable.Creator<SongModel>() {
        public SongModel createFromParcel(Parcel in) {
            return new SongModel(in);
        }

        public SongModel[] newArray(int size) {

            return new SongModel[size];
        }

    };

    public void readFromParcel(Parcel in) {
        path = in.readString();
        title = in.readString();
        duration = in.readString();
        artist = in.readString();
        album = in.readString();
        albumId = in.readString();
        dateAdded = in.readString();
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(path);
        dest.writeString(title);
        dest.writeString(duration);
        dest.writeString(artist);
        dest.writeString(album);
        dest.writeString(albumId);
        dest.writeString(dateAdded);
    }


    public SongModel(String path, String title, String duration, String artist, String album, String albumId, String dateAdded) {
        this.path = path;
        this.title = title;
        this.duration = duration;
        this.artist = artist;
        this.album = album;
        this.albumId = albumId;
        this.dateAdded = dateAdded;
    }


    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbumId() {
        return albumId;
    }

    public void setAlbumId(String albumId) {
        this.albumId = albumId;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getDateAdded() {
        return dateAdded;
    }
    public void setDateAdded(String dateAdded) {
        this.dateAdded = dateAdded;
    }
}