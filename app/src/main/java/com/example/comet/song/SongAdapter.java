package com.example.comet.song;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.media3.common.util.UnstableApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comet.R;
import com.example.comet.database.PlaylistEntity;
import com.example.comet.util.Constants;
import com.example.comet.ExoMusicPlayer;
import com.example.comet.MusicService;
import com.example.comet.MyMediaPlayer;
import com.example.comet.databinding.SongRowItemBinding;
import com.example.comet.viewmodel.PlaylistViewModel;

import java.util.ArrayList;
import java.util.List;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.BindingViewHolder> {

    private final ArrayList<SongModel> songsList;
    private final Context context;

    public SongAdapter(ArrayList<SongModel> songsList, Context context) {
        this.songsList = songsList;
        this.context = context;
    }

    @NonNull
    @Override
    public BindingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        SongRowItemBinding binding = SongRowItemBinding.inflate(inflater, parent, false);
        return new BindingViewHolder(binding);
    }

    public void onBindViewHolder(BindingViewHolder holder, int position) {
        SongModel song = songsList.get(position);
        holder.binding.setSong(song); // Bind the SongModel to the layout
        holder.binding.executePendingBindings();

        //todo there is a bug where if you sort the list the special coloring will follow the same row item (not the same song)
        //i.e. if I have song 1, 2, 3, 4 and 2 is playing and highlighted. After sorting I have 4, 3, 2 and 1 now 3 will be highlighted, but 2 will still be playing
        if(MyMediaPlayer.currentIndex == position){
            holder.binding.songNameText.setTextColor(Color.parseColor("#DCD0FF"));
        }else {
            holder.binding.songNameText.setTextColor((Color.parseColor("white")));
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @OptIn(markerClass = UnstableApi.class)
            @Override
            public void onClick(View view) {
                MyMediaPlayer.currentIndex = holder.getBindingAdapterPosition();

                Intent intentService = new Intent(context, MusicService.class);
                intentService.setAction(Constants.ACTION_PLAY);
                intentService.putExtra("SONGS", songsList);
                intentService.putExtra("POS", holder.getBindingAdapterPosition());
                context.startService(intentService);

                Intent intent = new Intent(context, ExoMusicPlayer.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                notifyDataSetChanged();
            }
        });

        holder.binding.songOptionsButton.setOnClickListener(view -> showPopupMenu(view, song));
    }

    private void showPopupMenu(View view, SongModel song) {
        PopupMenu popup = new PopupMenu(view.getContext(), view);
        popup.getMenuInflater().inflate(R.menu.song_options_menu, popup.getMenu());

        popup.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.add_to_playlist) {
                showPlaylistSelectionDialog(view.getContext(), song);
                return true;
            }
            return false;
        });

        popup.show();
    }

    private void showPlaylistSelectionDialog(Context context, SongModel song) {
        PlaylistViewModel viewModel = new ViewModelProvider((FragmentActivity) context).get(PlaylistViewModel.class);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Select a Playlist");

        viewModel.getAllPlaylists().observe((FragmentActivity) context, playlists -> {
            if (playlists == null || playlists.isEmpty()) {
                Toast.makeText(context, "No playlists available", Toast.LENGTH_SHORT).show();
                return;
            }

            String[] playlistNames = playlists.stream().map(PlaylistEntity::getName).toArray(String[]::new);
            builder.setItems(playlistNames, (dialog, which) -> {
                PlaylistEntity selectedPlaylist = playlists.get(which);
                viewModel.addSongToPlaylist(selectedPlaylist.getPlaylistId(), song);
            });

            builder.show();
        });
    }

    public void updateSongs(List<SongModel> newSongs) {
        this.songsList.clear();
        this.songsList.addAll(newSongs);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return songsList.size();
    }

    static class BindingViewHolder extends RecyclerView.ViewHolder {
        final SongRowItemBinding binding;

        BindingViewHolder(SongRowItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
