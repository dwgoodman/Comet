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
    public String songId; // Unique song ID (from MediaStore)

    @ColumnInfo(name = "date_added")
    public long dateAdded; // Timestamp when the song was added to the playlist

    @ColumnInfo(name = "play_count")
    public int playCount; // Number of times played (optional)

    public PlaylistSongEntity(int playlistId, String songId, long dateAdded, int playCount) {
        this.playlistId = playlistId;
        this.songId = songId;
        this.dateAdded = dateAdded;
        this.playCount = playCount;
    }
}