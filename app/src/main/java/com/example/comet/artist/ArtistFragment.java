package com.example.comet.artist;

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
import android.widget.SeekBar;

import com.example.comet.album.AlbumModel;
import com.example.comet.util.Constants;
import com.example.comet.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ArtistFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ArtistFragment extends Fragment implements ArtistAdapter.IArtistAdapterInterface {
    RecyclerView artistRecyclerView;
    private GridLayoutManager gridLayoutManager;

    private ArrayList<ArtistModel> artistList;

    public ArtistFragment() {
        // Required empty public constructor
    }

    public static ArtistFragment newInstance(ArrayList<ArtistModel> artistList) {
        ArtistFragment fragment = new ArtistFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(Constants.ARTISTS_PARAM, artistList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            artistList = (ArrayList<ArtistModel>) getArguments().get(Constants.ARTISTS_PARAM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_artist, container, false);

        artistRecyclerView = view.findViewById(R.id.albumListFromArtistRecyclerView);
        artistRecyclerView.setHasFixedSize(true);
        gridLayoutManager = new GridLayoutManager(getContext(), 1);
        artistRecyclerView.setAdapter(new ArtistAdapter(artistList, getContext(), this));
        artistRecyclerView.setLayoutManager(gridLayoutManager);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(artistRecyclerView != null){
            artistRecyclerView.setAdapter(new ArtistAdapter(artistList, getContext(), this));
        }
    }

    public void sortAlbums(Comparator<ArtistModel> comparator, boolean isDescending) {
        if (artistList != null && !artistList.isEmpty()) {
            // Reverse comparator if ascending
            if (!isDescending) {
                comparator = comparator.reversed();
            }

            Collections.sort(artistList, comparator);
            artistRecyclerView.getAdapter().notifyDataSetChanged();
        }
    }

    public void linkScrollBar(SeekBar seekBar) {
        if (artistRecyclerView != null) {
            seekBar.setMax(artistRecyclerView.getAdapter().getItemCount() - 1);

            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) {
                        artistRecyclerView.scrollToPosition(progress);
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {}

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {}
            });

            artistRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    int firstVisiblePosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
                    seekBar.setProgress(firstVisiblePosition);
                }
            });
        }
    }

    ArtistFragmentListener mListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mListener = (ArtistFragmentListener) context;
    }

    @Override
    public void toAlbumsListFromArtists(ArrayList<AlbumModel> albumsList) {
        //gets argument from the adapter and calls the method to take back to the main activity
        mListener.toAlbumListFromArtistFragment(albumsList);
    }

    public interface ArtistFragmentListener{

        //method to send songsList back to the main activity
        void toAlbumListFromArtistFragment(ArrayList<AlbumModel> albumsList);
    }
}
