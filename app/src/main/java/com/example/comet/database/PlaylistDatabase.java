package com.example.comet.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {PlaylistEntity.class, PlaylistSongEntity.class}, version = 1)
public abstract class PlaylistDatabase extends RoomDatabase {
    public abstract PlaylistDao playlistDao();
    public abstract PlaylistSongDao playlistSongDao();

    private static volatile PlaylistDatabase INSTANCE;

    public static PlaylistDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (PlaylistDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    PlaylistDatabase.class, "playlist_database")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}