package com.example.comet;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import com.example.comet.album.AlbumModel;
import com.example.comet.artist.ArtistModel;
import com.example.comet.song.SongModel;
import com.example.comet.util.InitialData;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MusicRepository {

    private final Context context;

    public MusicRepository(Context context) {
        this.context = context;
    }

    //List of all songs queried for in MainActivity
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

    //List of all Albums queried for in MainActivity
    public List<AlbumModel> queryAlbums() {
        List<AlbumModel> albumList = new ArrayList<>();
        String[] projection1 = {
                MediaStore.Audio.Albums._ID,
                MediaStore.Audio.Albums.ARTIST,
                MediaStore.Audio.Albums.ALBUM,
                MediaStore.Audio.Albums.ALBUM_ART,
                MediaStore.Audio.Albums.NUMBER_OF_SONGS,
                MediaStore.Audio.Albums.FIRST_YEAR
        };

        //query the media store for my selected audio parameters
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, projection1, null, null, null);

        while (cursor.moveToNext()) {
            AlbumModel albumData = new AlbumModel(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5));
            albumList.add(albumData);
        }
        cursor.close();
        return albumList;
    }

    //List of Songs in a single passed in Album
    public ArrayList<SongModel> queryAlbum(AlbumModel album){
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
        String selection = MediaStore.Audio.Media.ALBUM_ID + "= " + album.getId();

        //query the media store for my selected audio parameters
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, selection, null, null);

        ArrayList<SongModel> songsList = new ArrayList<>();
        //iterating over selected parameters and adding to the custom model
        while(cursor.moveToNext()){
            SongModel musicData = new SongModel(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6));
            if(new File(musicData.getPath()).exists()) {
                songsList.add(musicData);
            }
        }
        cursor.close();
        return songsList;
    }

    //List of all Artists queried for in MainActivity
    public List<ArtistModel> queryArtists(){
        ArrayList<ArtistModel> artistList = new ArrayList<>();
        String[] projection2 = {
                MediaStore.Audio.Artists._ID,
                MediaStore.Audio.Artists.ARTIST,
                MediaStore.Audio.Artists.NUMBER_OF_ALBUMS
        };

        //query the media store for my selected audio parameters
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI, projection2, null, null, null);


        //iterating over selected parameters and adding to the custom model
        while(cursor.moveToNext()){
            ArtistModel artistData = new ArtistModel(cursor.getString(0), cursor.getString(1), cursor.getString(2));
            artistList.add(artistData);
        }
        cursor.close();
        return artistList;
    }

    //List of Albums for a single passed in Artist
    public ArrayList<AlbumModel> queryArtist(ArtistModel artist){
        String[] projection1 = {
                MediaStore.Audio.Albums._ID,
                MediaStore.Audio.Albums.ARTIST,
                MediaStore.Audio.Albums.ALBUM,
                MediaStore.Audio.Albums.ALBUM_ART,
                MediaStore.Audio.Albums.NUMBER_OF_SONGS,
                MediaStore.Audio.Albums.FIRST_YEAR
        };

        //only taking music from media store
        String selection = MediaStore.Audio.Media.ARTIST_ID + "= " + artist.getId();

        //query the media store for my selected audio parameters
        Cursor cursor1 = context.getContentResolver().query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, projection1, selection, null, null);

        ArrayList<AlbumModel> albumsList = new ArrayList<>();

        while(cursor1.moveToNext()){
            AlbumModel albumData = new AlbumModel(cursor1.getString(0), cursor1.getString(1), cursor1.getString(2), cursor1.getString(3), cursor1.getString(4), cursor1.getString(5));
            albumsList.add(albumData);
        }
        cursor1.close();
        return albumsList;
    }


}
