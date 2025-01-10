package com.example.comet.playlist;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.comet.database.PlaylistEntity;
import com.example.comet.databinding.FragmentPlaylistBinding;
import com.example.comet.song.SongModel;
import com.example.comet.viewmodel.PlaylistViewModel;

import java.util.ArrayList;
import java.util.List;

public class PlaylistFragment extends Fragment implements PlaylistAdapter.IPlaylistAdapterInterface {
    RecyclerView playlistRecyclerView;
    private GridLayoutManager gridLayoutManager;
    private ArrayList<PlaylistModel> playlistList;
    private PlaylistAdapter playlistAdapter;
    private FragmentPlaylistBinding binding;
    private PlaylistViewModel playlistViewModel;

    public PlaylistFragment() {
        // Required empty public constructor
    }

    public static PlaylistFragment newInstance(ArrayList<PlaylistModel> playlistList) {
        PlaylistFragment fragment = new PlaylistFragment();
        Bundle args = new Bundle();
//        args.putParcelableArrayList(Constants.PLAYLISTS_PARAM, playlistList);
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
        binding = FragmentPlaylistBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        playlistViewModel = new ViewModelProvider(requireActivity()).get(PlaylistViewModel.class);
        binding.setPlaylistViewModel(playlistViewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());

        //todo this new Array will need to be changed to actually pass in the data from the DB
        playlistAdapter = new PlaylistAdapter(new ArrayList<>(), requireContext(), playlistViewModel, this);
        binding.songListFromPlaylist.setLayoutManager(new GridLayoutManager(getContext(), 1));
        binding.songListFromPlaylist.setAdapter(playlistAdapter);

        playlistViewModel.getAllPlaylists().observe(getViewLifecycleOwner(), playlists -> {
            playlistAdapter.updatePlaylists(playlists);
        });

        playlistViewModel.getShowAddPlaylistDialog().observe(getViewLifecycleOwner(), shouldShow -> {
            if (Boolean.TRUE.equals(shouldShow)) {
                showCreatePlaylistDialog();
                playlistViewModel.resetAddPlaylistDialog();
            }
        });

//        playlistViewModel.loadDummyPlaylists();
    }

    private void showCreatePlaylistDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Create New Playlist");

        final EditText input = new EditText(requireContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("Create", (dialog, which) -> {
            String playlistName = input.getText().toString().trim();
            if (!playlistName.isEmpty()) {
                createPlaylist(playlistName);
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void createPlaylist(String name) {
        PlaylistEntity newPlaylist = new PlaylistEntity(name, false, System.currentTimeMillis());
        playlistViewModel.insertPlaylist(newPlaylist);
    }


    PlaylistFragmentListener mListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mListener = (PlaylistFragmentListener) context;
    }

    @Override
    public void toSongListFromPlaylistFragment() {
        //gets argument from the adapter and calls the method to take back to the main activity
        mListener.toSongListFromPlaylistFragment();
    }

    public interface PlaylistFragmentListener{
        void toSongListFromPlaylistFragment();
    }
}