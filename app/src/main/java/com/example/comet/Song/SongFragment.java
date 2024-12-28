package com.example.comet.Song;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SongFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SongFragment extends Fragment {
    private final String TAG = "Looking for errors";

    HorizontalScrollView topBarScrollView;
//    ListView mainBodyListView;
    private RecyclerView songRecyclerView;
    private GridLayoutManager gridLayoutManager;
    Button playlistButton;
    Button artistButton;
    Button genreButton;
    Button songButton;
    Button albumButton;
    TextView songNameText;
    TextView songArtistText;
    TextView songDurationText;
    SongAdapter songAdapter;
    ArtistAdapter artistAdapter;

    private ArrayList<MusicModel> musicList;
    private ArrayList<AlbumModel> albumList;


    public SongFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters
     * @return A new instance of fragment MainDisplayFragment.
     */
    public static SongFragment newInstance(ArrayList<MusicModel> musicList, ArrayList<AlbumModel> albumsList) {
        SongFragment fragment = new SongFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(Constants.SONGS_PARAM, musicList);
        args.putParcelableArrayList(Constants.ALBUMS_PARAM, albumsList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            musicList = (ArrayList<MusicModel>) getArguments().get(Constants.SONGS_PARAM);
            albumList = (ArrayList<AlbumModel>) getArguments().get(Constants.ALBUMS_PARAM);
        }
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
        songRecyclerView.setAdapter(new SongAdapter(musicList, getContext()));
        songRecyclerView.setLayoutManager(gridLayoutManager);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(songRecyclerView != null){
            songRecyclerView.setAdapter(new SongAdapter(musicList, getContext()));
        }
    }

    public void sortSongs(Comparator<MusicModel> comparator, boolean isDescending) {
        if (musicList != null && !musicList.isEmpty()) {
            // Reverse comparator if ascending
            if (!isDescending) {
                comparator = comparator.reversed();
            }

            Collections.sort(musicList, comparator);
            songRecyclerView.getAdapter().notifyDataSetChanged(); // Refresh the RecyclerView
        }
    }

    public void linkScrollBar(SeekBar seekBar) {
        if (songRecyclerView != null) {
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