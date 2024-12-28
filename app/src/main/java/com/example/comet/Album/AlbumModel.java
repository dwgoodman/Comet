package com.example.comet.Album;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class AlbumModel implements Serializable, Parcelable {
    String albumArtist;
    String album;
    String numberOfSongs;
    String albumArt;
    String id;
    String firstYear;

    public AlbumModel(Parcel in) {
        super();
        readFromParcel(in);
    }

    public static final Parcelable.Creator<AlbumModel> CREATOR = new Parcelable.Creator<AlbumModel>() {
        public AlbumModel createFromParcel(Parcel in) {
            return new AlbumModel(in);
        }

        public AlbumModel[] newArray(int size) {

            return new AlbumModel[size];
        }

    };

    public void readFromParcel(Parcel in) {
        id = in.readString();
        albumArtist = in.readString();
        album = in.readString();
        albumArt = in.readString();
        numberOfSongs = in.readString();
        firstYear = in.readString();
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(albumArtist);
        dest.writeString(album);
        dest.writeString(albumArt);
        dest.writeString(numberOfSongs);
        dest.writeString(firstYear);
    }

    public AlbumModel(String id, String albumArtist, String album, String albumArt, String numberOfSongs, String firstYear) {
        this.id = id;
        this.albumArtist = albumArtist;
        this.album = album;
        this.albumArt = albumArt;
        this.numberOfSongs = numberOfSongs;
        this.firstYear = firstYear;
    }

    public String getAlbumArtist() {
        return albumArtist;
    }

    public void setAlbumArtist(String albumArtist) {
        this.albumArtist = albumArtist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getNumberOfSongs() {
        return numberOfSongs;
    }

    public void setNumberOfSongs(String numberOfSongs) {
        this.numberOfSongs = numberOfSongs;
    }

    public String getAlbumArt() {
        return albumArt;
    }

    public void setAlbumArt(String albumArt) {
        this.albumArt = albumArt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstYear() {
        return firstYear;
    }

    public void setFirstYear(String firstYear) {
        this.firstYear = firstYear;
    }
}
