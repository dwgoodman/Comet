package com.example.comet.Album;

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
import com.example.comet.Constants;
import com.example.comet.R;
import com.example.comet.Song.MusicModel;

import java.util.ArrayList;
import java.util.Locale;

public class AlbumListFromArtistFragment extends Fragment implements AlbumRowAdapter.IAlbumRowAdapterInterface {

    private RecyclerView albumRecyclerView;
    private ImageView albumArtistImage;
    private ImageView backButton;
    private TextView songsNumberAndTimeAlbum;
    private ConstraintLayout midBarLayout;
    private GridLayoutManager gridLayoutManager;
    private ArrayList<AlbumModel> albumsList;

    public AlbumListFromArtistFragment() {
        // Required empty public constructor
    }

    public static AlbumListFromArtistFragment newInstance(ArrayList<AlbumModel> albumsList) {
        AlbumListFromArtistFragment fragment = new AlbumListFromArtistFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(Constants.ALBUMS_PARAM, albumsList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            albumsList = (ArrayList<AlbumModel>) getArguments().get(Constants.ALBUMS_PARAM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_album_list_from_artist, container, false);

        albumArtistImage = view.findViewById(R.id.artistAlbumImageAlbum);
        songsNumberAndTimeAlbum = view.findViewById(R.id.songsNumberAndTimeAlbum);
        midBarLayout = view.findViewById(R.id.midbarLayoutAlbum);
        backButton = view.findViewById(R.id.backButtonAlbum);

        Uri uri = ContentUris.withAppendedId(Constants.sArtworkUri,
                Long.parseLong(albumsList.get(0).getId()));
        Glide.with(getContext()).asBitmap().load(uri).placeholder(R.drawable.background_for_load)
                .error(R.drawable.hoshi).centerCrop().into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        albumArtistImage.setImageBitmap(resource);
                        Palette.from(resource).generate(new Palette.PaletteAsyncListener() {
                            public void onGenerated(Palette palette) {
                                Palette.Swatch swatch = palette.getVibrantSwatch();
                                GradientDrawable gd;
                                if (swatch != null) {
                                    //swatch.getRgb in both slots will create a solid effect, can find a better color for gradient if wanted
                                    gd = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, new int[]{swatch.getRgb(), swatch.getRgb()});
                                    songsNumberAndTimeAlbum.setTextColor(swatch.getTitleTextColor());
                                }else {
                                    gd = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, new int[]{0x38393838, 0x38393838});
                                    songsNumberAndTimeAlbum.setTextColor(Color.BLACK);
                                }
                                midBarLayout.setBackground(gd);
                            }

                        });
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });

        long numAlbumSongs = 0;
        for(AlbumModel album : albumsList){
            numAlbumSongs += Long.parseLong(album.getNumberOfSongs());
        }
        String songsText;
        String albumsText;

        //setting variable text
        albumsText = (albumsList.size() < 2) ? "album" : "albums";
        songsText = (numAlbumSongs < 2) ? "song" : "songs";
        songsNumberAndTimeAlbum.setText(String.format(Locale.getDefault(), "%d %s, %d %s", albumsList.size(), albumsText, numAlbumSongs, songsText));

        //using custom adapter to set array of albums into layout
        albumRecyclerView = view.findViewById(R.id.albumListFromArtistRecyclerView);
        albumRecyclerView.setHasFixedSize(true);
        gridLayoutManager = new GridLayoutManager(getContext(), 1);
        albumRecyclerView.setAdapter(new AlbumRowAdapter(albumsList, getContext(), this));
        albumRecyclerView.setLayoutManager(gridLayoutManager);

        backButton.setOnClickListener(v -> moveBackfromArtistAlbum());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(albumRecyclerView != null){
            albumRecyclerView.setAdapter(new AlbumRowAdapter(albumsList, getContext(), this));
        }
    }


    public void moveBackfromArtistAlbum(){
        mListener.moveBackFromArtistAlbum();
    }
    @Override
    public void toSongsListFromAlbumRow(ArrayList<MusicModel> songsList){
        //gets argument from the adapter and calls the method to take back to the main activity
        mListener.toSongsListFromAlbumRowFragment(songsList);
    }

    AlbumListFromArtistFragmentListener mListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mListener = (AlbumListFromArtistFragmentListener) context;
    }

    public interface AlbumListFromArtistFragmentListener{
        //method to send songsList back to the main activity
        void toSongsListFromAlbumRowFragment(ArrayList<MusicModel> songsList);
        void moveBackFromArtistAlbum();
    }

}