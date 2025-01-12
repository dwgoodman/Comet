package com.example.comet.util;

import android.content.ContentUris;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.BindingAdapter;
import androidx.lifecycle.LiveData;
import androidx.palette.graphics.Palette;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.comet.R;

public class BindingAdapters {

    //placed used and tested SongAdapter, AlbumAdapter
    @BindingAdapter("albumArt")
    public static void loadAlbumArt(ImageView view, String albumId) {
        if(albumId.equals("")){
            albumId = "0";
        }
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

    @BindingAdapter({"albumArt", "midbarView", "textView"})
    public static void loadAlbumArtWithSwatch(ImageView view, String albumId, View midbarView, TextView textView) {
        if (albumId != null) {
            Uri uri = ContentUris.withAppendedId(Constants.sArtworkUri, Long.parseLong(albumId));

            Glide.with(view.getContext())
                    .asBitmap()
                    .load(uri)
                    .placeholder(R.drawable.background_for_load)
                    .error(R.drawable.hoshi)
                    .centerCrop()
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            view.setImageBitmap(resource);

                            // Extract color using Palette
                            Palette.from(resource).generate(palette -> {
                                if (palette != null) {
                                    Palette.Swatch swatch = palette.getVibrantSwatch();
                                    GradientDrawable gd;

                                    if (swatch != null) {
                                        //swatch.getRgb in both slots will create a solid effect, can find a better color for gradient if wanted
                                        gd = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                                                new int[]{swatch.getRgb(), swatch.getRgb()});
                                        textView.setTextColor(swatch.getTitleTextColor());
                                    } else {
                                        gd = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                                                new int[]{0x38393838, 0x38393838});
                                        textView.setTextColor(Color.BLACK);
                                    }

                                    // Apply gradient as background
                                    midbarView.setBackground(gd);
                                }
                            });
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                            view.setImageDrawable(placeholder);
                        }
                    });
        } else {
            view.setImageResource(R.drawable.chiaki);
        }
    }

    @BindingAdapter("playlistSongCountText")
    public static void setPlaylistSongCount(TextView textView, LiveData<Integer> numSongsLiveData) {
        numSongsLiveData.observeForever(numSongs -> {
            textView.setText(numSongs + (numSongs == 1 ? " song" : " songs"));
        });
    }



}
