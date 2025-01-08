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
import com.example.comet.databinding.AlbumItemBinding;
import com.example.comet.song.SongModel;
import com.example.comet.util.Constants;
import com.example.comet.R;
import com.example.comet.viewmodel.SongListFromAlbumViewModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.BindingViewHolder> {

    private final ArrayList<AlbumModel> albumList;
    private final Context context;
    private final IAlbumAdapterInterface mListener;
    private ArrayList<SongModel> songsList;

    public AlbumAdapter(ArrayList<AlbumModel> albumList, Context context, IAlbumAdapterInterface mListener){
        this.albumList = albumList;
        this.context = context;
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public BindingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        AlbumItemBinding binding = AlbumItemBinding.inflate(inflater, parent, false);
        return new BindingViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull BindingViewHolder holder, int position) {
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

                //sending songs list from AlbumAdapter to AlbumFragment
                mListener.toSongsListFromAlbums();
            }
        });

    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }

    public void updateAlbums(List<AlbumModel> albums) {
        this.albumList.clear();
        this.albumList.addAll(albums);
        notifyDataSetChanged();
    }

    static class BindingViewHolder extends RecyclerView.ViewHolder {
        final AlbumItemBinding binding;

        BindingViewHolder(AlbumItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public interface IAlbumAdapterInterface {
        void toSongsListFromAlbums();
    }

}
