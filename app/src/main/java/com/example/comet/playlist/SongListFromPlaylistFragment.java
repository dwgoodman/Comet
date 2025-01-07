package com.example.comet.playlist;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.comet.R;
import com.example.comet.databinding.FragmentSongsListFromPlaylistBinding;
import com.example.comet.song.SongModel;
import com.example.comet.util.Constants;
import com.example.comet.viewmodel.SongListFromPlaylistViewModel;

import java.util.ArrayList;
import java.util.List;


public class SongListFromPlaylistFragment extends Fragment {
    private FragmentSongsListFromPlaylistBinding binding;
    private SongListFromPlaylistViewModel viewModel;
    private PlaylistSongAdapter adapter;
    private ArrayList<SongModel> selectedPlaylist;


    public SongListFromPlaylistFragment() {
        // Required empty public constructor
    }

    public static SongListFromPlaylistFragment newInstance(PlaylistModel playlist) {
        SongListFromPlaylistFragment fragment = new SongListFromPlaylistFragment();
        Bundle args = new Bundle();
        args.putParcelable("selectedPlaylist", playlist);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            selectedPlaylist = (ArrayList<SongModel>) getArguments().get(Constants.SONGS_PARAM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSongsListFromPlaylistBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(SongListFromPlaylistViewModel.class);
        binding.setSongListFromPlaylistViewModel(viewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());

        adapter = new PlaylistSongAdapter(new ArrayList<>(), requireContext(), viewModel);
        binding.playlistSongRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.playlistSongRecyclerView.setAdapter(adapter);

        // Observe song list updates
        viewModel.getPlaylistSongs().observe(getViewLifecycleOwner(), adapter::updateSongs);

        // Load playlist songs if passed through Bundle
        if (getArguments() != null) {
            PlaylistModel selectedPlaylist = getArguments().getParcelable("selectedPlaylist");

            if (selectedPlaylist != null) {
                viewModel.loadPlaylistSongs(selectedPlaylist.getSongs());
            }
        }
    }
}