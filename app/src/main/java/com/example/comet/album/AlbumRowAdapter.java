package com.example.comet.album;

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
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.comet.MusicRepository;
import com.example.comet.databinding.AlbumRowItemBinding;
import com.example.comet.song.SongModel;
import com.example.comet.util.Constants;
import com.example.comet.R;
import com.example.comet.viewmodel.SongListFromAlbumViewModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AlbumRowAdapter extends RecyclerView.Adapter<AlbumRowAdapter.BindingViewHolder> {
    private final List<AlbumModel> albumList;
    private final Context context;
    private final IAlbumRowAdapterInterface mListener;
    private ArrayList<SongModel> songsList;

    public AlbumRowAdapter(ArrayList<AlbumModel> albumList, Context context, IAlbumRowAdapterInterface  mListener){
        this.albumList = albumList;
        this.context = context;
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public BindingViewHolder  onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        AlbumRowItemBinding binding = AlbumRowItemBinding.inflate(inflater, parent, false);
        return new BindingViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull BindingViewHolder  holder, int position) {
        AlbumModel album = albumList.get(position);
        holder.binding.setAlbum(album);
        holder.binding.executePendingBindings();

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            //Creating music repository to query the album for its song list
            MusicRepository musicRepository = new MusicRepository(context);
            songsList = musicRepository.queryAlbum(album);

            //Storing data in the ViewModel to be retrieved later in SongListFromAlbum
            SongListFromAlbumViewModel viewModel = new ViewModelProvider((FragmentActivity) context).get(SongListFromAlbumViewModel.class);
            viewModel.setAlbumData(album.getId(), songsList);

            //sending songs list from AlbumRowAdapter to AlbumListFromArtistFragment
            mListener.toSongsListFromAlbumRow();
            }
        });


    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }


    public void updateAlbums(List<AlbumModel> newAlbums) {
        albumList.clear();
        albumList.addAll(newAlbums);
        notifyDataSetChanged();
    }

    static class BindingViewHolder extends RecyclerView.ViewHolder {
        final AlbumRowItemBinding binding;

        BindingViewHolder(AlbumRowItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public interface IAlbumRowAdapterInterface {
        void toSongsListFromAlbumRow();
    }
}
