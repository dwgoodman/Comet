package com.example.comet.playlist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comet.R;
import com.example.comet.databinding.PlaylistRowItemBinding;
import com.example.comet.song.SongModel;

import java.util.ArrayList;
import java.util.List;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.BindingViewHolder>{
    private List<PlaylistModel> playlistList;
    private final Context context;
    private final IPlaylistAdapterInterface mListener;

    public PlaylistAdapter(List<PlaylistModel> playlistList, Context context, IPlaylistAdapterInterface mListener) {
        this.playlistList = playlistList;
        this.context = context;
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
        PlaylistModel playlist = playlistList.get(position);
        holder.binding.setPlaylist(playlist); // Data Binding handles setting text
        holder.binding.executePendingBindings();

        holder.itemView.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.toSongListFromPlaylistFragment(playlist.getSongs());
            }
        });
    }

    @Override
    public int getItemCount() {
        return playlistList.size();
    }

    public void updatePlaylists(List<PlaylistModel> newPlaylists) {
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
        void toSongListFromPlaylistFragment(List<SongModel> songsList);
    }
}
