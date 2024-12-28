package com.example.comet.Album;

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
import com.example.comet.Constants;
import com.example.comet.R;
import com.example.comet.Song.MusicModel;

import java.io.File;
import java.util.ArrayList;

public class AlbumRowAdapter extends RecyclerView.Adapter<AlbumRowAdapter.viewHolder> {
    private final ArrayList<AlbumModel> albumList;
    private final Context context;
    private final AlbumRowAdapter.IAlbumRowAdapterInterface mListener;
    private ArrayList<MusicModel> songsList;

    public AlbumRowAdapter(ArrayList<AlbumModel> albumList, Context context, AlbumRowAdapter.IAlbumRowAdapterInterface mListener){
        this.albumList = albumList;
        this.context = context;
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.album_row_item, parent, false);
        return new AlbumRowAdapter.viewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        AlbumModel albumData = albumList.get(position);

        holder.albumNameText.setText(albumData.getAlbum());
        holder.albumYear.setText(albumData.getFirstYear());

        Uri uri = ContentUris.withAppendedId(Constants.sArtworkUri,
                Long.parseLong(albumData.getId()));
        Glide.with(context).asBitmap().load(uri).placeholder(R.drawable.background_for_load).error(R.drawable.hoshi).centerCrop().into(holder.albumArt);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(context, SongListFromAlbumFragment.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                context.startActivity(intent);
                String[] projection = {
                        MediaStore.Audio.Media.DATA,
                        MediaStore.Audio.Media.TITLE,
                        MediaStore.Audio.Media.DURATION,
                        MediaStore.Audio.Media.ARTIST,
                        MediaStore.Audio.Media.ALBUM,
                        MediaStore.Audio.Media.ALBUM_ID,
                        MediaStore.Audio.Media.DATE_ADDED
                };

                //only taking music from media store
                String selection = MediaStore.Audio.Media.ALBUM_ID + "= " + albumData.getId();

                //query the media store for my selected audio parameters
                Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, selection, null, null);

                songsList = new ArrayList<>();
                //iterating over selected parameters and adding to the custom model
                while(cursor.moveToNext()){
                    MusicModel musicData = new MusicModel(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6));
                    if(new File(musicData.getPath()).exists()) {
                        songsList.add(musicData);
                    }
                }
                cursor.close();



                //sending songs list from AlbumRowAdapter to AlbumListFromArtistFragment
                mListener.toSongsListFromAlbumRow(songsList);
            }
        });


    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }


    public class viewHolder extends RecyclerView.ViewHolder {
        TextView albumNameText;
        TextView albumYear;
        ImageView albumArt;
        public viewHolder(View itemView, AlbumRowAdapter.IAlbumRowAdapterInterface mListener) {
            super(itemView);
            albumNameText = itemView.findViewById(R.id.albumNameText);
            albumArt = itemView.findViewById(R.id.albumImage);
            albumYear = itemView.findViewById(R.id.albumYear);
        }
    }

    public interface IAlbumRowAdapterInterface {
        void toSongsListFromAlbumRow(ArrayList<MusicModel> songsList);
    }
}
