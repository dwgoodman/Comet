package com.example.comet.album;

import android.content.Context;
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

import com.example.comet.artist.ArtistAdapter;
import com.example.comet.song.SongModel;
import com.example.comet.util.Constants;
import com.example.comet.song.SongFragment;
import com.example.comet.R;
import com.example.comet.song.SongAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AlbumFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AlbumFragment extends Fragment implements AlbumAdapter.IAlbumAdapterInterface{

    private final String TAG = "Looking for errors";

    HorizontalScrollView topBarScrollView;
    //    ListView mainBodyListView;
    RecyclerView albumRecyclerView;
    private LinearLayoutManager linearLayoutManager;
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
    private ArrayList<AlbumModel> albumsList;


    public AlbumFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters
     * @return A new instance of fragment MainDisplayFragment.
     */
    public static SongFragment newInstance(ArrayList<AlbumModel> albumsList) {
        SongFragment fragment = new SongFragment();
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
        View view = inflater.inflate(R.layout.fragment_album, container, false);

        //using custom song adapter to set a hardcoded array of songs into
        albumRecyclerView = view.findViewById(R.id.albumListFromArtistRecyclerView);
        albumRecyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(getContext());
        gridLayoutManager = new GridLayoutManager(getContext(), 2);
        albumRecyclerView.setAdapter(new AlbumAdapter(albumsList, getContext(), this));
        albumRecyclerView.setLayoutManager(gridLayoutManager);

        return view;
    }

    //I forget why this was set but I think it was to prevent a null pointer when refreshing the page
    @Override
    public void onResume() {
        super.onResume();
        if(albumRecyclerView != null){
            albumRecyclerView.setAdapter(new AlbumAdapter(albumsList, getContext(), this));
        }
    }

    public void sortAlbums(Comparator<AlbumModel> comparator, boolean isDescending) {
        if (albumsList != null && !albumsList.isEmpty()) {
            // Reverse comparator if ascending
            if (!isDescending) {
                comparator = comparator.reversed();
            }

            Collections.sort(albumsList, comparator);
            albumRecyclerView.getAdapter().notifyDataSetChanged();
        }
    }

    public void linkScrollBar(SeekBar seekBar) {
        if (albumRecyclerView != null) {
            seekBar.setMax(albumRecyclerView.getAdapter().getItemCount() - 1);

            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) {
                        albumRecyclerView.scrollToPosition(progress);
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {}

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {}
            });

            albumRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    int firstVisiblePosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
                    seekBar.setProgress(firstVisiblePosition);
                }
            });
        }
    }

    @Override
    public void toSongsListFromAlbums(ArrayList<SongModel> songsList){
        //gets argument from the adapter and calls the method to take back to the main activity
        mListener.toSongListFromAlbumFragment(songsList);
    }

    AlbumFragmentListener mListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mListener = (AlbumFragmentListener) context;
    }

    public interface AlbumFragmentListener{
        //method to send songsList back to the main activity
        void toSongListFromAlbumFragment(ArrayList<SongModel> songsList);
    }

}