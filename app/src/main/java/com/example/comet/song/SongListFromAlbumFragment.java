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
import androidx.lifecycle.ViewModelProvider;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.comet.databinding.FragmentSongListFromAlbumBinding;
import com.example.comet.util.Constants;
import com.example.comet.R;
import com.example.comet.util.UtilMethods;
import com.example.comet.viewmodel.SongListFromAlbumViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SongListFromAlbumFragment extends Fragment {

    private FragmentSongListFromAlbumBinding binding;
    private SongListFromAlbumViewModel viewModel;
    private SongAdapter adapter;
    private GridLayoutManager gridLayoutManager;
//    private ArrayList<SongModel> songsList;


    public SongListFromAlbumFragment() {
        // Required empty public constructor
    }

    public static SongListFromAlbumFragment newInstance() {
        SongListFromAlbumFragment fragment = new SongListFromAlbumFragment();
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
        binding = FragmentSongListFromAlbumBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(SongListFromAlbumViewModel.class);
        binding.setSongListFromAlbumViewModel(viewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());

        adapter = new SongAdapter(new ArrayList<>(), requireContext());
        binding.songListFromAlbumRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.songListFromAlbumRecyclerView.setAdapter(adapter);

        // Observe song list updates
        viewModel.getAlbumSongs().observe(getViewLifecycleOwner(), adapter::updateSongs);

        // Observe LiveData and update UI
        viewModel.getAlbumId().observe(getViewLifecycleOwner(), albumId -> {
        });

        // Observe song count and duration updates
        viewModel.getSongCountAndDuration().observe(getViewLifecycleOwner(), countAndTime -> {
            binding.songsNumberAndTime.setText(countAndTime);
        });

        //Moves back to the tab list
        binding.backButtonSong.setOnClickListener(v -> moveBackFromAlbum());

    }

    @Override
    public void onResume() {
        super.onResume();
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