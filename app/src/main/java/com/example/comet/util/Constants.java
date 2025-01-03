package com.example.comet.util;

import android.net.Uri;

import androidx.palette.graphics.Target;

public class Constants {
    final public static Uri sArtworkUri = Uri
            .parse("content://media/external/audio/albumart");

    public static final String SONGS_PARAM = "songs_param";
    public static final String ALBUMS_PARAM = "albums_param";
    public static final String ARTISTS_PARAM = "artists_param";
    public static final String PLAYLISTS_PARAM = "playlists_param";

    public static final String ACTION_PLAY = "com.example.musicplayer.action.PLAY";
    public static final String ACTION_PAUSE = "com.example.musicplayer.action.PAUSE";
    public static final String ACTION_SKIP_NEXT = "com.example.musicplayer.action.SKIP_NEXT";
    public static final String ACTION_SKIP_PREVIOUS = "com.example.musicplayer.action.SKIP_PREVIOUS";
    public static final String NOTIFICATION_ID = "notification_id";

    public static final String ALBUM_FRAG = "album_fragment";
    public static final String ALBUM_LIST_ARTIST = "album_list_artist";

    public static final Target DARK;
    public static final Target LIGHT;
    public static final Target NEUTRAL;

    public static final String MUSIC_LAST_PLAYED = "LAST_PLAYED";
    public static final String MUSIC_FILE = "STORED_MUSIC";
    public static final String ALBUM_ID = "ALBUM_ID";
    public static final String ARTIST = "SONG_ARTIST";
    public static final String TITLE = "SONG_TITLE";


    static {
        DARK = new Target.Builder().setMinimumLightness(0f)
                .setTargetLightness(0.26f)
                .setMaximumLightness(0.5f)
                .setMinimumSaturation(0.1f)
                .setTargetSaturation(0.6f)
                .setMaximumSaturation(1f)
                .setPopulationWeight(0.18f)
                .setSaturationWeight(0.22f)
                .setLightnessWeight(0.60f)
                .setExclusive(false)
                .build();

        LIGHT = new Target.Builder().setMinimumLightness(0.50f)
                .setTargetLightness(0.74f)
                .setMaximumLightness(1.0f)
                .setMinimumSaturation(0.1f)
                .setTargetSaturation(0.7f)
                .setMaximumSaturation(1f)
                .setPopulationWeight(0.18f)
                .setSaturationWeight(0.22f)
                .setLightnessWeight(0.60f)
                .setExclusive(false)
                .build();

        NEUTRAL = new Target.Builder().setMinimumLightness(0.20f)
                .setTargetLightness(0.5f)
                .setMaximumLightness(0.8f)
                .setMinimumSaturation(0.1f)
                .setTargetSaturation(0.6f)
                .setMaximumSaturation(1f)
                .setPopulationWeight(0.18f)
                .setSaturationWeight(0.22f)
                .setLightnessWeight(0.60f)
                .setExclusive(false)
                .build();
    }

}
