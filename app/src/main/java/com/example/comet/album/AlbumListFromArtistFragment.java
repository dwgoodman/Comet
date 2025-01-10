package com.example.comet.album;

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
import androidx.lifecycle.ViewModelProvider;
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
import com.example.comet.databinding.FragmentAlbumListFromArtistBinding;
import com.example.comet.song.SongModel;
import com.example.comet.util.Constants;
import com.example.comet.R;
import com.example.comet.viewmodel.AlbumListFromArtistViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AlbumListFromArtistFragment extends Fragment implements AlbumRowAdapter.IAlbumRowAdapterInterface {

    private FragmentAlbumListFromArtistBinding binding;
    private AlbumListFromArtistViewModel viewModel;
    private AlbumRowAdapter adapter;

    public AlbumListFromArtistFragment() {
        // Required empty public constructor
    }

    public static AlbumListFromArtistFragment newInstance() {
        AlbumListFromArtistFragment fragment = new AlbumListFromArtistFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAlbumListFromArtistBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(AlbumListFromArtistViewModel.class);
        binding.setAlbumListFromArtistViewModel(viewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());

        adapter = new AlbumRowAdapter(new ArrayList<>(), requireContext(), this);
        binding.albumListFromArtistRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
        binding.albumListFromArtistRecyclerView.setAdapter(adapter);

        // Observe album list updates
        viewModel.getArtistAlbums().observe(getViewLifecycleOwner(), adapter::updateAlbums);
        viewModel.getAlbumCountAndSongCount().observe(getViewLifecycleOwner(), countAndTime -> {
            binding.songsNumberAndTimeAlbum.setText(countAndTime);
        });

        //Observe updates to albums list
        viewModel.getArtistAlbums().observe(getViewLifecycleOwner(), adapter::updateAlbums);

        //observe updates to albumId
        viewModel.getAlbumId().observe(getViewLifecycleOwner(), albumId -> {
        });

        binding.backButtonAlbum.setOnClickListener(v -> moveBackfromArtistAlbum());
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    public void moveBackfromArtistAlbum(){
        mListener.moveBackFromArtistAlbum();
    }
    @Override
    public void toSongsListFromAlbumRow(){
        //gets argument from the adapter and calls the method to take back to the main activity
        mListener.toSongsListFromAlbumRowFragment();
    }

    AlbumListFromArtistFragmentListener mListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mListener = (AlbumListFromArtistFragmentListener) context;
    }

    public interface AlbumListFromArtistFragmentListener{
        //method to send songsList back to the main activity
        void toSongsListFromAlbumRowFragment();
        void moveBackFromArtistAlbum();
    }

}