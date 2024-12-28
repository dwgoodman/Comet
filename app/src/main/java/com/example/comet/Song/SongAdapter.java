package com.example.comet.Song;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.comet.Constants;
import com.example.comet.ExoMusicPlayer;
import com.example.comet.MusicService;
import com.example.comet.MyMediaPlayer;
import com.example.comet.R;
import com.example.comet.UtilMethods;

import java.util.ArrayList;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.viewHolder> {

    ArrayList<MusicModel> songsList;
    Context context;

    public SongAdapter(ArrayList<MusicModel> songsList, Context context) {
        this.songsList = songsList;
        this.context = context;
    }

    @NonNull
    @Override
    public SongAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.song_row_item, parent, false);

        return new SongAdapter.viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        MusicModel songData = songsList.get(position);

        holder.songNameText.setText(songData.getTitle());
        holder.songArtistText.setText(songData.getArtist());
        holder.songDurationText.setText(UtilMethods.prettyDuration(Long.parseLong(songData.getDuration())));
        holder.songImage.setImageResource(R.drawable.chiaki);

        //takes the constant artwork Uri and appends the ablumId to retrieve the album artwork
        Uri uri = ContentUris.withAppendedId(Constants.sArtworkUri,
                Long.parseLong(songData.getAlbumId()));
        Glide.with(context).asBitmap().load(uri).placeholder(R.drawable.background_for_load).error(R.drawable.hoshi).centerCrop().into(holder.songImage);

        if(MyMediaPlayer.currentIndex == position){
            holder.songNameText.setTextColor(Color.parseColor("#DCD0FF"));
        }else {
            holder.songNameText.setTextColor((Color.parseColor("white")));
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //start player service
                //todo work on later, have Mr. GPT help guide me through this
//                if(!isServiceRunning(MusicService.class)){
//                    context.startService(new Intent(context.getApplicationContext(), MusicService.class));
//                }
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
            }
        });
    }

    @Override
    public int getItemCount() {
        return songsList.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        TextView songNameText;
        TextView songArtistText;
        TextView songDurationText;
        ImageView songImage;
        public viewHolder(View itemView) {
            super(itemView);
            songNameText = itemView.findViewById(R.id.songNameText);
            songArtistText = itemView.findViewById(R.id.songArtistText);
            songDurationText = itemView.findViewById(R.id.songDurationText);
            songImage = itemView.findViewById(R.id.songImage);
        }
    }
}
