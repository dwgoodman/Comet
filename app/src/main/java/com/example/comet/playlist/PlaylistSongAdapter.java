package com.example.comet.playlist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comet.databinding.SongRowItemBinding;
import com.example.comet.song.SongModel;
import com.example.comet.viewmodel.SongListFromPlaylistViewModel;

import java.util.List;

public class PlaylistSongAdapter extends RecyclerView.Adapter<PlaylistSongAdapter.BindingViewHolder>{
    private final List<SongModel> songList;
    private final Context context;
    private final SongListFromPlaylistViewModel viewModel;

    public PlaylistSongAdapter(List<SongModel> songList, Context context, SongListFromPlaylistViewModel viewModel) {
        this.songList = songList;
        this.context = context;
        this.viewModel = viewModel;
    }

    @NonNull
    @Override
    public BindingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        SongRowItemBinding binding = SongRowItemBinding.inflate(inflater, parent, false);
        return new BindingViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull BindingViewHolder holder, int position) {
        SongModel song = songList.get(position);
        holder.binding.setSong(song);
        holder.binding.executePendingBindings();


    }

    @Override
    public int getItemCount() {
        return songList.size();
    }

    public void updateSongs(List<SongModel> newSongs) {
        songList.clear();
        songList.addAll(newSongs);
        notifyDataSetChanged();
    }

    static class BindingViewHolder extends RecyclerView.ViewHolder {
        final SongRowItemBinding binding;

        BindingViewHolder(SongRowItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
