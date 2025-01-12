package com.example.comet.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "playlist_table")
public class PlaylistEntity {
    @PrimaryKey(autoGenerate = true)
    public int playlistId;

    @ColumnInfo(name = "name")
    public String name; // Playlist name (e.g., Favorites, Recently Played)

    @ColumnInfo(name = "is_system_managed")
    public boolean isSystemManaged; // True if it's a system playlist (e.g., Now Playing)

    @ColumnInfo(name = "date_created")
    public long dateCreated; // Timestamp for when the playlist was created

    public PlaylistEntity(String name, boolean isSystemManaged, long dateCreated) {
        this.name = name;
        this.isSystemManaged = isSystemManaged;
        this.dateCreated = dateCreated;
    }

    public int getPlaylistId() {
        return playlistId;
    }

    public String getName() {
        return name;
    }
}
