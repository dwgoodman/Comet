package com.example.comet.playlist;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.comet.R;
import com.example.comet.song.SongModel;
import com.example.comet.util.Constants;

import java.util.ArrayList;
import java.util.List;


public class SongListFromPlaylistFragment extends Fragment {


    public SongListFromPlaylistFragment() {
        // Required empty public constructor
    }

    public static SongListFromPlaylistFragment newInstance(List<SongModel> songsList) {
        SongListFromPlaylistFragment fragment = new SongListFromPlaylistFragment();
        Bundle args = new Bundle();
        ArrayList<SongModel> songsArray = new ArrayList<>(songsList);
        args.putParcelableArrayList(Constants.SONGS_PARAM, songsArray);
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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_songs_list_from_playlist, container, false);
    }
}