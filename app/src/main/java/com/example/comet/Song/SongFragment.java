package com.example.comet.Song;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.comet.Album.AlbumModel;
import com.example.comet.Artist.ArtistAdapter;
import com.example.comet.Constants;
import com.example.comet.R;
import com.example.comet.ViewModel.SongViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class SongFragment extends Fragment {
    private final String TAG = "Looking for errors";
    private RecyclerView songRecyclerView;
    private GridLayoutManager gridLayoutManager;
    private ArrayList<MusicModel> musicList;
    private ArrayList<AlbumModel> albumList;
    private SongViewModel songViewModel;
    private SongAdapter songAdapter;


    public SongFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main_display, container, false);

        //using custom song adapter to set array of songs into layout
        songRecyclerView = view.findViewById(R.id.albumListFromArtistRecyclerView);
        songRecyclerView.setHasFixedSize(true);
        gridLayoutManager = new GridLayoutManager(getContext(), 1);
        songAdapter = new SongAdapter(new ArrayList<>(), getContext());
        songRecyclerView.setAdapter(songAdapter);
        songRecyclerView.setLayoutManager(gridLayoutManager);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        songViewModel = new ViewModelProvider(requireActivity()).get(SongViewModel.class);

        songViewModel.getSongList().observe(getViewLifecycleOwner(), songs -> {
            songAdapter.updateSongs(songs);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void sortSongs(Comparator<MusicModel> comparator, boolean isDescending) {
        songViewModel.sortSongs();
    }

    public void linkScrollBar(SeekBar seekBar) {
        if (songRecyclerView == null || songRecyclerView.getAdapter() == null || songRecyclerView.getAdapter().getItemCount() == 0) {
            return;
        }

        seekBar.setMax(songRecyclerView.getAdapter().getItemCount() - 1);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    songRecyclerView.scrollToPosition(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        songRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                int firstVisiblePosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
                seekBar.setProgress(firstVisiblePosition);
            }
        });

    }



    //    @Override
//    public void onAttach(@NonNull Context context){
//        super.onAttach(context);
//
//        if(context instanceof IMainDisplayFragmentListener){
//            mListener = (IMainDisplayFragmentListener) context;
//        } else{
//            throw new RuntimeException(context.toString() + "must implement IListener");
//        }
//    }
//
//    IMainDisplayFragmentListener mListener;
//
//    public interface IMainDisplayFragmentListener{
//
//    }

}