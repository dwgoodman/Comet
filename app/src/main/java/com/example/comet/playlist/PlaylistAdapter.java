package com.example.comet.playlist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comet.R;
import com.example.comet.database.PlaylistEntity;
import com.example.comet.databinding.PlaylistRowItemBinding;
import com.example.comet.song.SongModel;
import com.example.comet.viewmodel.PlaylistViewModel;

import java.util.ArrayList;
import java.util.List;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.BindingViewHolder>{
    private List<PlaylistEntity> playlistList;
    private final Context context;
    private final IPlaylistAdapterInterface mListener;
    private final PlaylistViewModel playlistViewModel;

    public PlaylistAdapter(List<PlaylistEntity> playlistList, Context context, PlaylistViewModel playlistViewModel, IPlaylistAdapterInterface mListener) {
        this.playlistList = playlistList;
        this.context = context;
        this.playlistViewModel = playlistViewModel;
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public BindingViewHolder  onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        PlaylistRowItemBinding binding = PlaylistRowItemBinding.inflate(inflater, parent, false);
        return new BindingViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull BindingViewHolder  holder, int position) {
        PlaylistEntity playlist = playlistList.get(position);
        holder.binding.setPlaylist(playlist); // Data Binding handles setting text
        holder.binding.setPlaylistViewModel(playlistViewModel);
        holder.binding.executePendingBindings();

        holder.itemView.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.toSongListFromPlaylistFragment();
            }
        });
    }

    @Override
    public int getItemCount() {
        return playlistList.size();
    }

    public void updatePlaylists(List<PlaylistEntity> newPlaylists) {
        this.playlistList.clear();
        this.playlistList.addAll(newPlaylists);
        notifyDataSetChanged();
    }

    static class BindingViewHolder extends RecyclerView.ViewHolder {
        final PlaylistRowItemBinding binding;

        BindingViewHolder(PlaylistRowItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public interface IPlaylistAdapterInterface {
        void toSongListFromPlaylistFragment();
    }
}
