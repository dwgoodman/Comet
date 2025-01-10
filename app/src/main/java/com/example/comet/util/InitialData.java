package com.example.comet.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class InitialData {
    private static ArrayList<String> categories = new ArrayList<>();
    private static final ArrayList<Song> songs1 = new ArrayList<>();
    private static final ArrayList<Album> albums1 = new ArrayList<>();
    {
        categories = new ArrayList<>();
        categories.add("Songs");
        categories.add("Albums");
        categories.add("Artists");
        categories.add("Playlists");
        songs1.add(new Song("Suisei Hoshimachi", "Stellar Stellar", "5:01", "Still Still Stellar"));
        songs1.add(new Song("Suisei Hoshimachi", "Next Color Planet", "4:19", "Still Still Stellar"));
        songs1.add(new Song("Suisei Hoshimachi", "Her Trail on the Celestial Sphere", "4:16", "Still Still Stellar"));
        songs1.add(new Song("Suisei Hoshimachi", "Ghost", "4:43", "Still Still Stellar"));
        songs1.add(new Song("Suisei Hoshimachi", "Bye Bye Rainy", "3:33", "Still Still Stellar"));
        songs1.add(new Song("Suisei Hoshimachi", "Selfish Dazzling", "3:39", "Still Still Stellar"));
        songs1.add(new Song("Suisei Hoshimachi", "Bluerose", "3:44", "Still Still Stellar"));
        songs1.add(new Song("Suisei Hoshimachi", "Comet", "4:04", "Still Still Stellar"));
        songs1.add(new Song("Suisei Hoshimachi", "Andromeda", "4:54", "Still Still Stellar"));
        songs1.add(new Song("Suisei Hoshimachi", "Je T'aime", "3:21", "Still Still Stellar"));
        songs1.add(new Song("Suisei Hoshimachi", "Starry Jet", "4:10", "Still Still Stellar"));
        songs1.add(new Song("Suisei Hoshimachi", "Run", "3:52", "Still Still Stellar"));
        albums1.add(new Album("Stellar", "Hoshimachi Suisei", songs.get("Songs")));
        albums1.add(new Album("Still Stellar", "Hoshimachi Suisei", songs.get("Songs")));
        albums1.add(new Album("Still Still Stellar", "Hoshimachi Suisei", songs.get("Songs")));
        albums1.add(new Album("Still Still Still Stellar", "Hoshimachi Suisei", songs.get("Songs")));
        albums1.add(new Album("Still Still Still Still Stellar", "Hoshimachi Suisei", songs.get("Songs")));
        albums1.add(new Album("Still Still Still Still Still Stellar", "Hoshimachi Suisei", songs.get("Songs")));
        albums1.add(new Album("Still Still Still Still Still Still Stellar", "Hoshimachi Suisei", songs.get("Songs")));
        albums1.add(new Album("Still Still Still Still Still Still Still Stellar", "Hoshimachi Suisei", songs.get("Songs")));
        albums1.add(new Album("Still Still Still Still Still Still Still Still Stellar", "Hoshimachi Suisei", songs.get("Songs")));
        albums1.add(new Album("Still Still Still Still Still Still Still Still Still Stellar", "Hoshimachi Suisei", songs.get("Songs")));
        albums1.add(new Album("Still Still Still Still Still Still Still Still Still Still Stellar", "Hoshimachi Suisei", songs.get("Songs")));
    }

    private static final HashMap<String, ArrayList<Song>> songs = new HashMap<String, ArrayList<Song>>(){{
        put("Songs", new ArrayList<Song>(){{
            add(new Song("Suisei Hoshimachi", "Stellar Stellar", "5:01", "Still Still Stellar"));
            add(new Song("Suisei Hoshimachi", "Next Color Planet", "4:19", "Still Still Stellar"));
            add(new Song("Suisei Hoshimachi", "Her Trail on the Celestial Sphere", "4:16", "Still Still Stellar"));
            add(new Song("Suisei Hoshimachi", "Ghost", "4:43", "Still Still Stellar"));
            add(new Song("Suisei Hoshimachi", "Bye Bye Rainy", "3:33", "Still Still Stellar"));
            add(new Song("Suisei Hoshimachi", "Selfish Dazzling", "3:39", "Still Still Stellar"));
            add(new Song("Suisei Hoshimachi", "Bluerose", "3:44", "Still Still Stellar"));
            add(new Song("Suisei Hoshimachi", "Comet", "4:04", "Still Still Stellar"));
            add(new Song("Suisei Hoshimachi", "Andromeda", "4:54", "Still Still Stellar"));
            add(new Song("Suisei Hoshimachi", "Je T'aime", "3:21", "Still Still Stellar"));
            add(new Song("Suisei Hoshimachi", "Starry Jet", "4:10", "Still Still Stellar"));
            add(new Song("Suisei Hoshimachi", "Run", "3:52", "Still Still Stellar"));
            add(new Song("Suisei Hoshimachi", "Je T'aime", "3:21", "Still Still Stellar"));
            add(new Song("Suisei Hoshimachi", "Starry Jet", "4:10", "Still Still Stellar"));
            add(new Song("Suisei Hoshimachi", "Run", "3:52", "Still Still Stellar"));
            add(new Song("Suisei Hoshimachi", "Je T'aime", "3:21", "Still Still Stellar"));
            add(new Song("Suisei Hoshimachi", "Starry Jet", "4:10", "Still Still Stellar"));
            add(new Song("Suisei Hoshimachi", "Run", "3:52", "Still Still Stellar"));
            add(new Song("Suisei Hoshimachi", "Je T'aime", "3:21", "Still Still Stellar"));
            add(new Song("Suisei Hoshimachi", "Starry Jet", "4:10", "Still Still Stellar"));
            add(new Song("Suisei Hoshimachi", "Run", "3:52", "Still Still Stellar"));
        }});
    }};

    //for some reason I can't call xxx.get("List") inside this hashmap from the same class
    //may need to rotue data elsewhere or just hardcode differently
    private static final HashMap<String, ArrayList<Artist>> artists = new HashMap<String, ArrayList<Artist>>(){{
        put("Artists", new ArrayList<Artist>(){{
            add(new Artist("Hoshimachi Suisei", songs1, albums1));
            add(new Artist("Hshimachi Suisei", songs1, albums1));
            add(new Artist("Hohimachi Suisei", songs1, albums1));
            add(new Artist("Hosimachi Suisei", songs1, albums1));
            add(new Artist("Hoshmachi Suisei", songs1, albums1));
            add(new Artist("Hoshiachi Suisei", songs1, albums1));
            add(new Artist("Hoshimchi Suisei", songs1, albums1));
            add(new Artist("Hoshimahi Suisei", songs1, albums1));
            add(new Artist("Hoshimaci Suisei", songs1, albums1));
            add(new Artist("Hoshimach Suisei", songs1, albums1));
            add(new Artist("Hoshimachi Sisei", songs1, albums1));
            add(new Artist("Hoshimachi Sisei", songs1, albums1));
            add(new Artist("Hoshimachi Sisei", songs1, albums1));
            add(new Artist("Hoshimachi Sisei", songs1, albums1));
        }});
    }};

    private static final HashMap<String, ArrayList<Album>> albums = new HashMap<String, ArrayList<Album>>(){{
        put("Albums", new ArrayList<Album>(){{
            add(new Album("Stellar", "Hoshimachi Suisei", songs1));
            add(new Album("Still Stellar", "Hoshimachi Suisei", songs1));
            add(new Album("Still Still Stellar", "Hoshimachi Suisei", songs1));
            add(new Album("Still Still Still Stellar", "Hoshimachi Suisei", songs1));
            add(new Album("Still Still Still Still Stellar", "Hoshimachi Suisei", songs1));
            add(new Album("Still Still Still Still Still Stellar", "Hoshimachi Suisei", songs1));
            add(new Album("Still Still Still Still Still Still Stellar", "Hoshimachi Suisei", songs1));
            add(new Album("Still Still Still Still Still Still Still Stellar", "Hoshimachi Suisei", songs1));
            add(new Album("Still Still Still Still Still Still Still Still Stellar", "Hoshimachi Suisei", songs1));
            add(new Album("Still Still Still Still Still Still Still Still Still Stellar", "Hoshimachi Suisei", songs1));
            add(new Album("Still Still Still Still Still Still Still Still Still Still Stellar", "Hoshimachi Suisei", songs1));
        }});
    }};


    public static ArrayList<Song> getSongs(){
        return songs.get("Songs");
    }

    public static ArrayList<Artist> getArtists(){
        return artists.get("Artists");
    }

    public static ArrayList<Album> getAlbums(){
        return albums.get("Albums");
    }

    public static ArrayList<String> getCategories(){
        ArrayList<String> thisStuff = new ArrayList<String>(categories);

        return thisStuff;
    }

    public static class Song implements Serializable{
        String artist, name, length, album;

        public Song (String artist, String name, String length, String album){
            this.artist = artist;
            this.name = name;
            this.length = length;
            this.album = album;
        }

        @Override
        public String toString() {
            return "Song{" +
                    "artist='" + artist + '\'' +
                    ", song='" + name + '\'' +
                    ", length='" + length + '\'' +
                    ", album='" + album + '\'' +
                    '}';
        }

        public String getArtist() {
            return artist;
        }

        public String getName() {
            return name;
        }

        public String getLength() {
            return length;
        }

        public String getAlbum() {
            return album;
        }
    }

    public static class Artist implements Serializable{
        String name;
        ArrayList<Song> songs;
        ArrayList<Album> albums;

        public Artist(String name, ArrayList<Song> songs, ArrayList<Album> albums) {
            this.name = name;
            this.songs = songs;
            this.albums = albums;
        }

        public String getName() {
            return name;
        }

        public ArrayList<Song> getSongs() {
            return songs;
        }

        public ArrayList<Album> getAlbums() {
            return albums;
        }

        @Override
        public String toString() {
            return "Artist{" +
                    "name='" + name + '\'' +
                    ", songs=" + songs +
                    ", albums=" + albums +
                    '}';
        }
    }

    public static class Album{
        String name, artist;
        ArrayList<Song> songs;

        public Album(String name, String artist, ArrayList<Song> songs) {
            this.name = name;
            this.artist = artist;
            this.songs = songs;
        }

        public String getName() {
            return name;
        }

        public String getArtist() {
            return artist;
        }

        public ArrayList<Song> getSongs() {
            return songs;
        }

        @Override
        public String toString() {
            return "Album{" +
                    "name='" + name + '\'' +
                    ", artist='" + artist + '\'' +
                    ", songs=" + songs +
                    '}';
        }
    }

}
