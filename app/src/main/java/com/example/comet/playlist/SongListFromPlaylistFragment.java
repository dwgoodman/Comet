package com.example.comet.playlist;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.comet.ApplicationClass;
import com.example.comet.R;
import com.example.comet.databinding.FragmentSongsListFromPlaylistBinding;
import com.example.comet.song.SongListFromPlaylistViewModelFactory;
import com.example.comet.song.SongModel;
import com.example.comet.util.Constants;
import com.example.comet.viewmodel.PlaylistViewModel;
import com.example.comet.viewmodel.SongListFromPlaylistViewModel;

import java.util.ArrayList;
import java.util.List;


public class SongListFromPlaylistFragment extends Fragment {
    private FragmentSongsListFromPlaylistBinding binding;
    private SongListFromPlaylistViewModel viewModel;
    private PlaylistSongAdapter adapter;
    private ArrayList<SongModel> selectedPlaylist;
    private SongListFromPlaylistViewModel songListFromPlaylistViewModel;



    public SongListFromPlaylistFragment() {
        // Required empty public constructor
    }

    public static SongListFromPlaylistFragment newInstance() {
        SongListFromPlaylistFragment fragment = new SongListFromPlaylistFragment();
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
        binding = FragmentSongsListFromPlaylistBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        PlaylistViewModel playlistViewModel = new ViewModelProvider(requireActivity()).get(PlaylistViewModel.class);
        ApplicationClass app = (ApplicationClass) requireActivity().getApplication();

        playlistViewModel.getSelectedPlaylistId().observe(getViewLifecycleOwner(), playlistId -> {
            if (playlistId != null && playlistId != -1) {
                Log.d("DB_DEBUG", "Fetching songs for Playlist ID: " + playlistId);

                //Now initialize SongListFromPlaylistViewModel with the correct Playlist ID
                songListFromPlaylistViewModel = app.getSongListFromPlaylistViewModelProvider(playlistId)
                        .get(SongListFromPlaylistViewModel.class);

                //Set the songslist and albumId so the UI updates accordingly
                songListFromPlaylistViewModel.setPlaylistSongs(playlistId);
                songListFromPlaylistViewModel.setAlbumId();

                binding.setSongListFromPlaylistViewModel(songListFromPlaylistViewModel); //Set SongListFromPlaylistViewModel in Binding
                binding.setLifecycleOwner(getViewLifecycleOwner());

                adapter = new PlaylistSongAdapter(new ArrayList<>(), requireContext(), songListFromPlaylistViewModel, playlistId);
                binding.playlistSongRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                binding.playlistSongRecyclerView.setAdapter(adapter);

                //Observe playlist songs
                songListFromPlaylistViewModel.getPlaylistSongs().observe(getViewLifecycleOwner(), songs -> {
                    Log.d("DB_DEBUG", "Observed Playlist Songs: " + (songs != null ? songs.size() : "null"));
                    adapter.updateSongs(songs);
                });
            } else {
                Log.d("DB_DEBUG", "Invalid Playlist ID, skipping song fetch.");
            }
        });

        //Moves back to the tab list
        binding.backButtonPlaylist.setOnClickListener(v -> moveBackFromPlaylist());
    }

    public void moveBackFromPlaylist(){
        mListener.moveBackFromPlaylist();
    }

    SongListFromPlaylistFragmentListener mListener;

    public void onAttach(@NonNull Context context){
        super.onAttach(context);
        mListener = (SongListFromPlaylistFragmentListener) context;
    }

    public interface SongListFromPlaylistFragmentListener{
        void moveBackFromPlaylist();
    }
}