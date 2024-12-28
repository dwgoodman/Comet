package com.example.comet;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.ui.PlayerNotificationManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

@UnstableApi public class MyMediaDescriptionAdapter implements PlayerNotificationManager.MediaDescriptionAdapter {

    private final Context context;

    public MyMediaDescriptionAdapter(Context context){
        this.context = context;
    }
    @Override
    public CharSequence getCurrentContentTitle(Player player) {
        // Return the title of the currently playing media item
        MediaItem mediaItem = player.getCurrentMediaItem();
        return mediaItem != null ? mediaItem.mediaMetadata.title : null;
    }

    @Nullable
    @Override
    public PendingIntent createCurrentContentIntent(Player player) {
        Intent openAppIntent = new Intent(context, MusicService.class);

        return PendingIntent.getActivity(context, 0 , openAppIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Nullable
    @Override
    public CharSequence getCurrentContentText(Player player) {
        // Return additional information about the currently playing media item (e.g., artist)
        MediaItem mediaItem = player.getCurrentMediaItem();
        return mediaItem != null ? mediaItem.mediaMetadata.artist : null;
    }

    @Nullable
    @Override
    public Bitmap getCurrentLargeIcon(Player player, PlayerNotificationManager.BitmapCallback callback) {
        // Return a large icon for the currently playing media item
        // You can use a custom method to load the bitmap asynchronously if needed
        MediaItem mediaItem = player.getCurrentMediaItem();
        if (mediaItem != null && mediaItem.mediaMetadata.artworkUri != null) {
            Glide.with(context)
                    .asBitmap()
                    .load(mediaItem.mediaMetadata.artworkUri)
                    .error(R.drawable.hoshi)
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            callback.onBitmap(resource);
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {

                        }
                    });
        }
        return null;
    }
}
