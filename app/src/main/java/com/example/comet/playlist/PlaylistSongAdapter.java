package com.example.comet.playlist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comet.database.PlaylistEntity;
import com.example.comet.database.PlaylistSongEntity;
import com.example.comet.databinding.SongFromPlaylistRowItemBinding;
import com.example.comet.databinding.SongRowItemBinding;
import com.example.comet.song.SongModel;
import com.example.comet.viewmodel.SongListFromPlaylistViewModel;

import java.util.List;

public class PlaylistSongAdapter extends RecyclerView.Adapter<PlaylistSongAdapter.BindingViewHolder>{
    private final List<PlaylistSongEntity> songList;
    private final Context context;
    private final SongListFromPlaylistViewModel viewModel;

    public PlaylistSongAdapter(List<PlaylistSongEntity> songList, Context context, SongListFromPlaylistViewModel viewModel) {
        this.songList = songList;
        this.context = context;
        this.viewModel = viewModel;
    }

    @NonNull
    @Override
    public BindingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        SongFromPlaylistRowItemBinding binding = SongFromPlaylistRowItemBinding .inflate(inflater, parent, false);
        return new BindingViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull BindingViewHolder holder, int position) {
        PlaylistSongEntity song = songList.get(position);
        holder.binding.setPlaylistSong(song);
        holder.binding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }

    public void updateSongs(List<PlaylistSongEntity> newSongs) {
        songList.clear();
        songList.addAll(newSongs);
        notifyDataSetChanged();
    }

    static class BindingViewHolder extends RecyclerView.ViewHolder {
        final SongFromPlaylistRowItemBinding  binding;

        BindingViewHolder(SongFromPlaylistRowItemBinding  binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
