package com.example.comet.song;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comet.util.Constants;
import com.example.comet.ExoMusicPlayer;
import com.example.comet.MusicService;
import com.example.comet.MyMediaPlayer;
import com.example.comet.databinding.SongRowItemBinding;

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
        holder.binding.setSong(song); // Bind the MusicModel to the layout
        holder.binding.executePendingBindings();

        if(MyMediaPlayer.currentIndex == position){
            holder.binding.songNameText.setTextColor(Color.parseColor("#DCD0FF"));
        }else {
            holder.binding.songNameText.setTextColor((Color.parseColor("white")));
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyMediaPlayer.currentIndex = holder.getBindingAdapterPosition();

                Intent intentService = new Intent(context, MusicService.class);
                intentService.setAction(Constants.ACTION_PLAY);
                intentService.putExtra("SONGS", songsList);
                intentService.putExtra("POS", holder.getBindingAdapterPosition());
                context.startService(intentService);

                Intent intent = new Intent(context, ExoMusicPlayer.class);
//                intent.putExtra("SONGS", songsList);
//                intent.putExtra("POS", holder.getAdapterPosition());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                notifyDataSetChanged();
            }
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
