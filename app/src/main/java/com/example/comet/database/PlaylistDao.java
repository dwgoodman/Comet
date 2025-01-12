package com.example.comet.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface PlaylistDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertPlaylist(PlaylistEntity playlist);

    @Delete
    void deletePlaylist(PlaylistEntity playlist);

    @Query("SELECT * FROM playlist_table")
    LiveData<List<PlaylistEntity>> getAllPlaylists();

    @Query("SELECT * FROM playlist_table WHERE is_system_managed = 1")
    LiveData<List<PlaylistEntity>> getSystemPlaylists();

    @Query("DELETE FROM playlist_table")
    void deleteAllPlaylists();
}