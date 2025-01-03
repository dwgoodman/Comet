package com.example.comet.playlist;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.comet.util.Constants;
import com.example.comet.R;

import java.util.ArrayList;

public class PlaylistFragment extends Fragment implements PlaylistAdapter.IPlaylistAdapterInterface {


    RecyclerView playlistRecyclerView;
    private GridLayoutManager gridLayoutManager;
    private ArrayList<PlaylistModel> playlistList;
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
        if (getArguments() != null) {
            playlistList = (ArrayList<PlaylistModel>) getArguments().get(Constants.PLAYLISTS_PARAM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_artist, container, false);

        playlistRecyclerView = view.findViewById(R.id.albumListFromArtistRecyclerView);
        playlistRecyclerView.setHasFixedSize(true);
        gridLayoutManager = new GridLayoutManager(getContext(), 1);
        playlistRecyclerView.setAdapter(new PlaylistAdapter(playlistList, getContext(), this));
        playlistRecyclerView.setLayoutManager(gridLayoutManager);

        return view;
    }

    @Override
    public void onPlaylistSelected(ArrayList<PlaylistModel> playlist) {

    }
}