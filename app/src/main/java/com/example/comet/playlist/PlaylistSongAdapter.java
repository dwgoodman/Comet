package com.example.comet.playlist;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.media3.common.util.UnstableApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comet.ExoMusicPlayer;
import com.example.comet.MusicService;
import com.example.comet.MyMediaPlayer;
import com.example.comet.R;
import com.example.comet.database.PlaylistEntity;
import com.example.comet.database.PlaylistSongEntity;
import com.example.comet.databinding.SongFromPlaylistRowItemBinding;
import com.example.comet.databinding.SongRowItemBinding;
import com.example.comet.song.SongModel;
import com.example.comet.util.Constants;
import com.example.comet.viewmodel.PlaylistViewModel;
import com.example.comet.viewmodel.SongListFromPlaylistViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class PlaylistSongAdapter extends RecyclerView.Adapter<PlaylistSongAdapter.BindingViewHolder>{
    private final List<PlaylistSongEntity> songList;
    private final Context context;
    private final SongListFromPlaylistViewModel viewModel;
    private final int playlistId;

    public PlaylistSongAdapter(List<PlaylistSongEntity> songList, Context context, SongListFromPlaylistViewModel viewModel, int playlistId) {
        this.songList = songList;
        this.context = context;
        this.viewModel = viewModel;
        this.playlistId = playlistId;
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

        //todo there is a bug where if you sort the list the special coloring will follow the same row item (not the same song)
        //i.e. if I have song 1, 2, 3, 4 and 2 is playing and highlighted. After sorting I have 4, 3, 2 and 1 now 3 will be highlighted, but 2 will still be playing
        if(MyMediaPlayer.currentIndex == position){
            holder.binding.playlistSongNameText.setTextColor(Color.parseColor("#DCD0FF"));
        }else {
            holder.binding.playlistSongNameText.setTextColor((Color.parseColor("white")));
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @OptIn(markerClass = UnstableApi.class)
            @Override
            public void onClick(View view) {
                MyMediaPlayer.currentIndex = holder.getBindingAdapterPosition();

                // Convert all PlaylistSongEntity to SongModel
                ArrayList<SongModel> songModelList = new ArrayList<>();
                for (PlaylistSongEntity entity : songList) {
                    songModelList.add(new SongModel(
                            entity.path,
                            entity.title,
                            entity.duration,
                            entity.artist,
                            entity.album,
                            entity.albumId,
                            String.valueOf(entity.dateAdded),
                            entity.songId
                    ));
                }

                Intent intentService = new Intent(context, MusicService.class);
                intentService.setAction(Constants.ACTION_PLAY);
                intentService.putExtra("SONGS", songModelList);
                intentService.putExtra("POS", holder.getBindingAdapterPosition());
                context.startService(intentService);

                Intent intent = new Intent(context, ExoMusicPlayer.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("isFromPlaylist", true);
                intent.putExtra("playlistId", playlistId);
                context.startActivity(intent);
                notifyDataSetChanged();
            }
        });

        //Adding options button to songList
        holder.binding.playlistSongOptionsButton.setOnClickListener(view -> showPopupMenu(view, song));
    }

    private void showPopupMenu(View view, PlaylistSongEntity song) {
        PopupMenu popup = new PopupMenu(view.getContext(), view);
        popup.getMenuInflater().inflate(R.menu.playlist_song_options_menu, popup.getMenu());

        popup.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.remove_from_playlist) {
                removeSongFromPlaylist(view.getContext(), song);
                return true;
            }
            return false;
        });

        popup.show();
    }

    private void removeSongFromPlaylist(Context context, PlaylistSongEntity song) {
        PlaylistViewModel playlistViewModel = new ViewModelProvider((FragmentActivity) context).get(PlaylistViewModel.class);
        playlistViewModel.removeSongFromPlaylist(song.playlistId, song.songId);
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
