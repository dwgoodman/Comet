package com.example.comet.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface PlaylistSongDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertSongToPlaylist(PlaylistSongEntity song);

    @Query("DELETE FROM playlist_song_table WHERE playlistId = :playlistId AND songId = :songId")
    void removeSongFromPlaylist(int playlistId, String songId);

    @Query("SELECT * FROM playlist_song_table WHERE playlistId = :playlistId ORDER BY date_added ASC")
    LiveData<List<PlaylistSongEntity>> getSongsInPlaylist(int playlistId);

    @Query("SELECT COUNT(*) FROM playlist_song_table WHERE playlistId = :playlistId")
    LiveData<Integer> getSongCountInPlaylist(int playlistId);
}
