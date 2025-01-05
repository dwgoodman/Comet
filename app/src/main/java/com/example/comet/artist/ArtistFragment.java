package com.example.comet.artist;

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
import android.widget.SeekBar;

import com.example.comet.album.AlbumModel;
import com.example.comet.databinding.FragmentArtistBinding;
import com.example.comet.util.Constants;
import com.example.comet.R;
import com.example.comet.viewmodel.ArtistViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ArtistFragment extends Fragment implements ArtistAdapter.IArtistAdapterInterface {
    RecyclerView artistRecyclerView;
    private GridLayoutManager gridLayoutManager;

    private ArrayList<ArtistModel> artistList;
    private FragmentArtistBinding binding;
    private ArtistViewModel artistViewModel;
    private ArtistAdapter artistAdapter;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Use Data Binding to inflate layout
        binding = FragmentArtistBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize ViewModel
        artistViewModel = new ViewModelProvider(requireActivity()).get(ArtistViewModel.class);
        binding.setArtistViewModel(artistViewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());

        // Setup RecyclerView
        artistAdapter = new ArtistAdapter(new ArrayList<>(), requireContext(), this);
        binding.albumListFromArtistRecyclerView.setHasFixedSize(true);
        binding.albumListFromArtistRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
        binding.albumListFromArtistRecyclerView.setAdapter(artistAdapter);

        // Observe LiveData and update adapter
        artistViewModel.getArtistList().observe(getViewLifecycleOwner(), artists -> {
            artistAdapter.updateArtists(artists);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
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
