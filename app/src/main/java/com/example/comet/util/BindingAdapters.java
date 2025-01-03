package com.example.comet.util;

import android.content.ContentUris;
import android.net.Uri;
import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

import com.bumptech.glide.Glide;
import com.example.comet.R;

public class BindingAdapters {

    //placed used and tested SongAdapter
    @BindingAdapter("albumArt")
    public static void loadAlbumArt(ImageView view, String albumId) {
        if (albumId != null) {
            Uri uri = ContentUris.withAppendedId(Constants.sArtworkUri, Long.parseLong(albumId));
            Glide.with(view.getContext())
                    .asBitmap()
                    .load(uri)
                    .placeholder(R.drawable.background_for_load)
                    .error(R.drawable.hoshi)
                    .centerCrop()
                    .into(view);
        } else {
            //default image
            view.setImageResource(R.drawable.chiaki);
        }
    }





}
