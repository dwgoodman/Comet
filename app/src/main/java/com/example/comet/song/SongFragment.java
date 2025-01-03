package com.example.comet.song;

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

import com.example.comet.viewmodel.SongViewModel;
import com.example.comet.databinding.FragmentMainDisplayBinding;

import java.util.ArrayList;

public class SongFragment extends Fragment {
    private final String TAG = "Looking for errors";
    private RecyclerView songRecyclerView;
    private SongViewModel songViewModel;
    private SongAdapter songAdapter;
    private FragmentMainDisplayBinding binding;


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
        // Use Data Binding to inflate the layout
        binding = FragmentMainDisplayBinding.inflate(inflater, container, false);
        return binding.getRoot(); // Return the root view of the binding
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        songViewModel = new ViewModelProvider(requireActivity()).get(SongViewModel.class);

        // Bind ViewModel to layout
        binding.setSongViewModel(songViewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());

        // RecyclerView setup
        songAdapter = new SongAdapter(new ArrayList<>(), requireContext());
        binding.albumListFromArtistRecyclerView.setHasFixedSize(true);
        binding.albumListFromArtistRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
        binding.albumListFromArtistRecyclerView.setAdapter(songAdapter);

        songViewModel.getSongList().observe(getViewLifecycleOwner(), songs -> {
            songAdapter.updateSongs(songs);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
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

}