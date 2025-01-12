package com.example.comet.database;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.Executors;

@Database(entities = {PlaylistEntity.class, PlaylistSongEntity.class}, version = 2)
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
                            .addMigrations(MIGRATION_1_2)
                            .fallbackToDestructiveMigration()
                            .build();
                    // Debug Query: Check if songs exist in the database
                    Executors.newSingleThreadExecutor().execute(() -> {
                        Cursor cursor = INSTANCE.getOpenHelper().getReadableDatabase()
                                .query("SELECT * FROM playlist_song_table");

                        if (cursor != null) {
                            Log.d("DB_DEBUG", "Query executed. Total songs: " + cursor.getCount());
                            while (cursor.moveToNext()) {
                                String songId = cursor.getString(cursor.getColumnIndexOrThrow("songId"));
                                Log.d("DB_DEBUG", "Song ID in DB: " + songId);
                            }
                            cursor.close();
                        } else {
                            Log.d("DB_DEBUG", "Query failed. No songs found in database.");
                        }
                    });
                }
            }
        }
        return INSTANCE;
    }

    public static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // Add new columns while preserving existing data
            database.execSQL("ALTER TABLE playlist_song_table ADD COLUMN title TEXT DEFAULT ''");
            database.execSQL("ALTER TABLE playlist_song_table ADD COLUMN artist TEXT DEFAULT ''");
            database.execSQL("ALTER TABLE playlist_song_table ADD COLUMN albumId TEXT DEFAULT ''");
            database.execSQL("ALTER TABLE playlist_song_table ADD COLUMN duration TEXT DEFAULT '0'");
        }
    };
}