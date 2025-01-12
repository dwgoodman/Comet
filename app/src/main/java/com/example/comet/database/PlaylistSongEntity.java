package com.example.comet.database;

import static androidx.room.ForeignKey.CASCADE;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;

@Entity(tableName = "playlist_song_table",
        primaryKeys = {"playlistId", "songId"},
        foreignKeys = {
                @ForeignKey(entity = PlaylistEntity.class,
                        parentColumns = "playlistId",
                        childColumns = "playlistId",
                        onDelete = CASCADE)
        })
public class PlaylistSongEntity {
    @ColumnInfo(name = "playlistId")
    public int playlistId;
    @NonNull
    @ColumnInfo(name = "songId")
    public String songId;

    @ColumnInfo(name = "title")
    public String title;

    @ColumnInfo(name = "artist")
    public String artist;

    @ColumnInfo(name = "albumId")
    public String albumId;

    @ColumnInfo(name = "duration")
    public String duration;

    @ColumnInfo(name = "date_added")
    public long dateAdded;

    @ColumnInfo(name = "play_count")
    public int playCount;

    public PlaylistSongEntity(int playlistId, String songId, String title, String artist, String albumId, String duration, long dateAdded, int playCount) {
        this.playlistId = playlistId;
        this.songId = songId;
        this.title = title;
        this.artist = artist;
        this.albumId = albumId;
        this.duration = duration;
        this.dateAdded = dateAdded;
        this.playCount = playCount;
    }
}