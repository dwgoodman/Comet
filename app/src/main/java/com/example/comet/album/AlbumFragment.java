package com.example.comet.album;

import android.content.Context;
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

import com.example.comet.artist.ArtistAdapter;
import com.example.comet.databinding.FragmentAlbumBinding;
import com.example.comet.song.SongModel;
import com.example.comet.util.Constants;
import com.example.comet.song.SongFragment;
import com.example.comet.R;
import com.example.comet.song.SongAdapter;
import com.example.comet.viewmodel.AlbumViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class AlbumFragment extends Fragment implements AlbumAdapter.IAlbumAdapterInterface{

    private final String TAG = "Looking for errors";
    RecyclerView albumRecyclerView;
    private ArrayList<AlbumModel> albumsList;
    private FragmentAlbumBinding binding;
    private AlbumViewModel albumViewModel;
    private AlbumAdapter albumAdapter;


    public AlbumFragment() {
        // Required empty public constructor
    }

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
        // Use Data Binding to inflate layout
        binding = FragmentAlbumBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Bind ViewModel to layout
        albumViewModel = new ViewModelProvider(requireActivity()).get(AlbumViewModel.class);
        binding.setAlbumViewModel(albumViewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());

        //Setup RecyclerView
        albumAdapter = new AlbumAdapter(new ArrayList<>(), requireContext(), this);
        binding.albumListFromArtistRecyclerView.setHasFixedSize(true);
        binding.albumListFromArtistRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        binding.albumListFromArtistRecyclerView.setAdapter(albumAdapter);

        //Observe LiveData and update adapter
        albumViewModel.getAlbumList().observe(getViewLifecycleOwner(), albums -> {
            albumAdapter.updateAlbums(albums);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
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