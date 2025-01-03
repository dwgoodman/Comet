package com.example.comet;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import com.example.comet.song.SongModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MusicRepository {

    private final Context context;

    public MusicRepository(Context context) {
        this.context = context;
    }

    public List<SongModel> querySongs() {
        List<SongModel> songList = new ArrayList<>();
        String[] projection = {
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.DATE_ADDED
        };

        //only taking music from media store
        String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";

        //query the media store for my selected audio parameters
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, selection, null, MediaStore.Audio.Media.TITLE);


        //iterating over selected parameters and adding to the custom model
        while (cursor.moveToNext()) {
            SongModel musicData = new SongModel(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6));
            if (new File(musicData.getPath()).exists()) {
                songList.add(musicData);
            }
        }
        cursor.close();
        return songList;
    }

}
