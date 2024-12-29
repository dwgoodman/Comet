package com.example.comet.Playlist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comet.R;

import java.util.ArrayList;
import java.util.List;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.ViewHolder>{
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
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.playlist_row_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //todo create the playlist holding database
        //todo populate the playlist with songs
        //todo fetch the songs in each playlist here unless I do something different with LiveData
//        PlaylistModel playlist = playlistList.get(position);
//
//        holder.playlistNameText.setText(playlist.getName());
//        holder.songCountText.setText(String.format("%d songs", playlist.getSongs().size()));
//
//        holder.itemView.setOnClickListener(v -> {
//            if (mListener != null) {
//                mListener.onPlaylistSelected(playlist);
//            }
//        });

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

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView playlistNameText;
        TextView songCountText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            playlistNameText = itemView.findViewById(R.id.playlistNameText);
            songCountText = itemView.findViewById(R.id.playlistSongCountText);
        }
    }

    public interface IPlaylistAdapterInterface {
        void onPlaylistSelected(ArrayList<PlaylistModel> playlist);
    }
}
