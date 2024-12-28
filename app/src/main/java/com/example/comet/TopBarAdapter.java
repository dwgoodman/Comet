package com.example.comet;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class TopBarAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    //todo delete class during cleanup
    public static final int ITEM_TYPE_SONG = 0;
    public static final int ITEM_TYPE_ALBUM = 2;
    public static final int ITEM_TYPE_ARTIST = 1;
    private static Context mContext;
    private int VIEW_TYPE = 0;
    ArrayList<InitialData.Song> songs;
    ArrayList<InitialData.Artist> artists;
    ArrayList<InitialData.Album> albums;
    int size;

    public TopBarAdapter(@NonNull Context context, int viewType, List<?> input) {
        mContext = context;
        VIEW_TYPE = viewType;
        if(input.get(0).getClass() == InitialData.Song.class) {
            songs = (ArrayList<InitialData.Song>) input;
            size = songs.size();
        } else if (input.get(0).getClass() == InitialData.Artist.class) {
            artists = (ArrayList<InitialData.Artist>) input;
            size = artists.size();
        } else if(input.get(0).getClass() == InitialData.Album.class) {
            albums = (ArrayList<InitialData.Album>) input;
            size = albums.size();
        }
    }


    public void setVIEW_TYPE(int viewType) {
        VIEW_TYPE = viewType;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        // check here the viewType and return RecyclerView.ViewHolder based on view type
        switch (VIEW_TYPE) {
            case ITEM_TYPE_SONG:
                // if VIEW_TYPE is song then return GridViewHolder
                view = LayoutInflater.from(mContext).inflate(R.layout.song_row_item, parent, false);
                return new SongViewHolder(view);
            case ITEM_TYPE_ARTIST:
                // if VIEW_TYPE is artist then return CardListViewHolder
                view = LayoutInflater.from(mContext).inflate(R.layout.artist_row_item, parent, false);
                return new ArtistViewHolder(view);
            case ITEM_TYPE_ALBUM:
                // if VIEW_TYPE is album then return TitleListViewHolder
                view = LayoutInflater.from(mContext).inflate(R.layout.album_item, parent, false);
                return new AlbumViewHolder(view);
        }
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final int itemType = getItemViewType(position);
        // First check here the View Type
        // then set data based on View Type to your recyclerview item
        if (VIEW_TYPE == 0) {
                SongViewHolder viewHolder = (SongViewHolder) holder;
                // write here code for your grid list
                InitialData.Song song = songs.get(position);
                //setting all items
                viewHolder.songNameText.setText(song.getName());
                viewHolder.songArtistText.setText(song.getArtist());
                viewHolder.songDurationText.setText(song.getLength());
                viewHolder.songImage.setImageResource(R.drawable.chiaki);

        } else if (VIEW_TYPE == 1) {
                ArtistViewHolder viewHolder = (ArtistViewHolder) holder;
                // write here code for your grid list
                InitialData.Artist artist = artists.get(position);
                viewHolder.artistNameText.setText(artist.getName());
                viewHolder.albumText.setText(artist.getAlbums().size() + " album(s)");
                viewHolder.artistImage.setImageResource(R.drawable.chiaki);


        } else if (VIEW_TYPE == 2) {
                AlbumViewHolder viewHolder = (AlbumViewHolder) holder;
                // write here code for your TitleListViewHolder
                InitialData.Album album = albums.get(position);
                viewHolder.albumName.setText(album.getName());
                viewHolder.albumImage.setImageResource(R.drawable.chiaki);


        }
    }

    @Override
    public int getItemCount() {
        return size;
    }

    // RecyclerView.ViewHolder class for gridLayoutManager
    public static class SongViewHolder extends RecyclerView.ViewHolder {

        TextView songNameText;
        TextView songArtistText;
        TextView songDurationText;
        ImageView songImage;

        public SongViewHolder(@NonNull View itemView) {
            super(itemView);
            //getting all items needed to be set
            songNameText = itemView.findViewById(R.id.songNameText);
            songArtistText = itemView.findViewById(R.id.songArtistText);
            songDurationText = itemView.findViewById(R.id.songDurationText);
            songImage = itemView.findViewById(R.id.songImage);
        }
    }

    // RecyclerView.ViewHolder class for Card list View
    public class ArtistViewHolder extends RecyclerView.ViewHolder {
        //getting all items needed to be set
        TextView artistNameText;
        TextView albumText;
        ImageView artistImage;
        public ArtistViewHolder(@NonNull View itemView) {
            super(itemView);
            artistNameText = itemView.findViewById(R.id.artistNameText);
            albumText = itemView.findViewById(R.id.albumNumberText);
            artistImage = itemView.findViewById(R.id.artistImage);
        }
    }

    // RecyclerView.ViewHolder class for Title list View
    public class AlbumViewHolder extends RecyclerView.ViewHolder {
        TextView albumName;
        ImageView albumImage;
        public AlbumViewHolder(@NonNull View itemView) {
            super(itemView);
            albumName = itemView.findViewById(R.id.albumName);
            albumImage = itemView.findViewById(R.id.albumImage);
        }
    }

}
