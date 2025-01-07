package com.example.comet.util;

import android.widget.TextView;

import androidx.databinding.BindingAdapter;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class UtilMethods {
    public static long getMinutes(long milliseconds){
        long minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds);
        return minutes;
    }

    public static long getSeconds(long milliseconds){
        long seconds = (TimeUnit.MILLISECONDS.toSeconds(milliseconds) % 60);
        return seconds;
    }

    //Style in minute:second format
    public static String prettyDuration(long milliseconds){
        if(getSeconds(milliseconds) < 10){
            return String.format(Locale.getDefault(), "%s:0%s", getMinutes(milliseconds), getSeconds(milliseconds));
        }else {
            return String.format(Locale.getDefault(), "%s:%s", getMinutes(milliseconds), getSeconds(milliseconds));
        }

    }

    @BindingAdapter("albumCountText")
    public static void formatAlbumCount(TextView textView, String numAlbums) {
        if(Integer.parseInt(numAlbums) > 1){
            textView.setText(String.format("%s albums", numAlbums));
        }else{
            textView.setText(String.format("%s album", numAlbums));
        }
    }

    @BindingAdapter("songCountText")
    public static void formatSongCount(TextView textView, String numSongs) {
        if(Integer.parseInt(numSongs) > 1){
            textView.setText(String.format("%s songs", numSongs));
        }else{
            textView.setText(String.format("%s song", numSongs));
        }
    }

}
