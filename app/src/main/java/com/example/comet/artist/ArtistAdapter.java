package com.example.comet.artist;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.comet.album.AlbumModel;
import com.example.comet.util.Constants;
import com.example.comet.R;

import java.util.ArrayList;

public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.viewHolder> {

    ArrayList<ArtistModel> artistList;
    Context context;
   IArtistAdapterInterface mListener;
    ArrayList<AlbumModel> albumsList;

    public ArtistAdapter(ArrayList<ArtistModel> artistList, Context context, IArtistAdapterInterface mListener) {
        this.artistList = artistList;
        this.context = context;
        this.mListener = mListener;
    }

        @NonNull
        @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.artist_row_item, parent, false);
        return new ArtistAdapter.viewHolder(view);
    }

    public void onBindViewHolder(@NonNull viewHolder holder, int position){
        ArtistModel artistData = artistList.get(position);

        holder.artistNameText.setText(artistData.getArtist());
        holder.albumNumberText.setText(String.format("%s album(s)", artistData.getNumAlbums()));

        //todo image will need to come from somewhere else
        Uri uri = ContentUris.withAppendedId(Constants.sArtworkUri,
                Long.parseLong(artistData.getId()));
        Glide.with(context).asBitmap().load(uri).placeholder(R.drawable.background_for_load).error(R.drawable.hoshi).centerCrop().into(holder.artistImage);



        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //creating new projection/cursor to fill albumModel
                String[] projection1 = {
                        MediaStore.Audio.Albums._ID,
                        MediaStore.Audio.Albums.ARTIST,
                        MediaStore.Audio.Albums.ALBUM,
                        MediaStore.Audio.Albums.ALBUM_ART,
                        MediaStore.Audio.Albums.NUMBER_OF_SONGS,
                        MediaStore.Audio.Albums.FIRST_YEAR
                };

                //only taking music from media store
                String selection = MediaStore.Audio.Media.ARTIST_ID + "= " + artistData.getId();

                //query the media store for my selected audio parameters
                Cursor cursor1 = context.getContentResolver().query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, projection1, selection, null, null);

                albumsList = new ArrayList<>();

                while(cursor1.moveToNext()){
                    AlbumModel albumData = new AlbumModel(cursor1.getString(0), cursor1.getString(1), cursor1.getString(2), cursor1.getString(3), cursor1.getString(4), cursor1.getString(5));
                    albumsList.add(albumData);
                }
                cursor1.close();

                //sending albums list from ArtistAdapter to ArtistFragment
                mListener.toAlbumsListFromArtists(albumsList);
            }
        });
    }

    @Override
    public int getItemCount() { return artistList.size(); }

    public class viewHolder extends RecyclerView.ViewHolder {
        TextView artistNameText;
        TextView albumNumberText;
        ImageView artistImage;
        public viewHolder(View itemView) {
            super(itemView);
            albumNumberText = itemView.findViewById(R.id.albumNumberText);
            artistNameText = itemView.findViewById(R.id.artistNameText);
            artistImage = itemView.findViewById(R.id.artistImage);
        }
    }

    public interface IArtistAdapterInterface {
        //method to pass list of songs in album back to fragment
        void toAlbumsListFromArtists(ArrayList<AlbumModel> albumsList);
    }

}
