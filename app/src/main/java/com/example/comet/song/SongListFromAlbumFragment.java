package com.example.comet.song;

import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.comet.util.Constants;
import com.example.comet.R;
import com.example.comet.util.UtilMethods;

import java.util.ArrayList;
import java.util.Locale;

public class SongListFromAlbumFragment extends Fragment {

    private RecyclerView songRecyclerView;
    private ImageView songAlbumImage;
    private  ImageView backButton;
    private TextView songsNumberAndTime;
    private ConstraintLayout midBarLayout;
    private GridLayoutManager gridLayoutManager;
    private ArrayList<SongModel> songsList;


    public SongListFromAlbumFragment() {
        // Required empty public constructor
    }


    public static SongListFromAlbumFragment newInstance(ArrayList<SongModel> songsList) {
        SongListFromAlbumFragment fragment = new SongListFromAlbumFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(Constants.SONGS_PARAM, songsList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            songsList = (ArrayList<SongModel>) getArguments().get(Constants.SONGS_PARAM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout. fragment_song_list_from_album, container, false);

        songAlbumImage = view.findViewById(R.id.artistAlbumImageSong);
        songsNumberAndTime = view.findViewById(R.id.songsNumberAndTime);
        midBarLayout = view.findViewById(R.id.midbarLayoutSong);
        backButton = view.findViewById(R.id.backButtonSong);



        Uri uri = ContentUris.withAppendedId(Constants.sArtworkUri,
                Long.parseLong(songsList.get(0).getAlbumId()));
        Glide.with(getContext()).asBitmap().load(uri).placeholder(R.drawable.background_for_load)
                .error(R.drawable.hoshi).centerCrop().into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        songAlbumImage.setImageBitmap(resource);
                        Palette.from(resource).generate(new Palette.PaletteAsyncListener() {
                            public void onGenerated(Palette palette) {
                                Palette.Swatch swatch = palette.getVibrantSwatch();
                                GradientDrawable gd;
                                if (swatch != null) {
                                    //swatch.getRgb in both slots will create a solid effect, can find a better color for gradient if wanted
                                    gd = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, new int[]{swatch.getRgb(), swatch.getRgb()});
                                    songsNumberAndTime.setTextColor(swatch.getTitleTextColor());
                                }else {
                                    gd = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, new int[]{0x38393838, 0x38393838});
                                    songsNumberAndTime.setTextColor(Color.BLACK);
                                }
                                midBarLayout.setBackground(gd);
                            }

                        });
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });

        long totalSongLength = 0;
        for(SongModel song : songsList){
            totalSongLength += Long.parseLong(song.getDuration());
        }

        //setting variable text
        String songsText = (songsList.size() < 2) ? "song" : "songs";

        songsNumberAndTime.setText(String.format(Locale.getDefault(), "%d %s (%s)", songsList.size(), songsText, UtilMethods.prettyDuration(totalSongLength)));

        //using custom adapter to set array of songs into layout
        songRecyclerView = view.findViewById(R.id.songListFromAlbumRecyclerView);
        songRecyclerView.setHasFixedSize(true);
        gridLayoutManager = new GridLayoutManager(getContext(), 1);
        songRecyclerView.setAdapter(new SongAdapter(songsList, getContext()));
        songRecyclerView.setLayoutManager(gridLayoutManager);

        backButton.setOnClickListener(v -> moveBackFromAlbum());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(songRecyclerView != null){
            songRecyclerView.setAdapter(new SongAdapter(songsList, getContext()));
        }
    }

    public void moveBackFromAlbum(){
        //gets argument from the adapter and calls the method to take back to the main activity
        mListener.moveBackFromAlbum();
    }

    SongListFromAlbumFragmentListener mListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mListener = (SongListFromAlbumFragmentListener) context;
    }

    public interface SongListFromAlbumFragmentListener{
        //moves back from the songs list inside an album
        void moveBackFromAlbum();
    }
}