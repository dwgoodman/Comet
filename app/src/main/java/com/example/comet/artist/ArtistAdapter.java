package com.example.comet.artist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comet.MusicRepository;
import com.example.comet.album.AlbumListFromArtistFragment;
import com.example.comet.album.AlbumModel;
import com.example.comet.databinding.ArtistRowItemBinding;
import com.example.comet.viewmodel.AlbumListFromArtistViewModel;

import java.util.ArrayList;
import java.util.List;

public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.BindingViewHolder> {

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
    public BindingViewHolder  onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            ArtistRowItemBinding binding = ArtistRowItemBinding.inflate(inflater, parent, false);
            return new BindingViewHolder(binding);
    }

    public void onBindViewHolder(@NonNull BindingViewHolder  holder, int position){
        ArtistModel artist = artistList.get(position);
        holder.binding.setArtist(artist);
        holder.binding.executePendingBindings();

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Creating music repository to query the artist for their album list
                MusicRepository musicRepository = new MusicRepository(context);
                albumsList = musicRepository.queryArtist(artist);

                //Storing data in the ViewModel to be retrieved later in AlbumListFromArtist
                AlbumListFromArtistViewModel viewModel = new ViewModelProvider((FragmentActivity) context).get(AlbumListFromArtistViewModel.class);
                viewModel.setAlbumData(albumsList != null ? albumsList.get(0).getId() : "", albumsList);

                //Changing fragment from artists list to album list
                mListener.toAlbumsListFromArtists();
            }
        });
    }

    @Override
    public int getItemCount() { return artistList.size(); }

    public void updateArtists(List<ArtistModel> artists) {
        this.artistList.clear();
        this.artistList.addAll(artists);
        notifyDataSetChanged();
    }

    static class BindingViewHolder extends RecyclerView.ViewHolder {
        final ArtistRowItemBinding binding;

        BindingViewHolder(ArtistRowItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public interface IArtistAdapterInterface {
        //method to pass list of songs in album back to fragment
        void toAlbumsListFromArtists();
    }

}
