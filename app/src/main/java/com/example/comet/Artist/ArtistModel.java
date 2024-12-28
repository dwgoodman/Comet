package com.example.comet.Artist;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class ArtistModel implements Serializable, Parcelable {

    String id;
    String artist;
    String numAlbums;

    public ArtistModel(Parcel in){
        super();
        readFromParcel(in);
    }


    public static final Parcelable.Creator<ArtistModel> CREATOR = new Parcelable.Creator<ArtistModel>() {
        @Override
        public ArtistModel createFromParcel(Parcel in) { return new ArtistModel(in); }

        @Override
        public ArtistModel[] newArray(int size) {
            return new ArtistModel[size];
        }
    };

    public void readFromParcel(Parcel in) {
        id = in.readString();
        artist = in.readString();
        numAlbums = in.readString();
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(artist);
        dest.writeString(numAlbums);
    }

    public ArtistModel(String id, String artist, String numAlbums ) {
        this.id = id;
        this.artist = artist;
        this.numAlbums = numAlbums;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getNumAlbums() {
        return numAlbums;
    }

    public void setNumAlbums(String numAlbums) {
        this.numAlbums = numAlbums;
    }
}
