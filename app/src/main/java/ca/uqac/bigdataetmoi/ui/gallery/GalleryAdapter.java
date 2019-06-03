package ca.uqac.bigdataetmoi.ui.gallery;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.recyclerview.extensions.ListAdapter;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import ca.uqac.bigdataetmoi.BuildConfig;
import ca.uqac.bigdataetmoi.R;
import ca.uqac.bigdataetmoi.models.Photo;

public class GalleryAdapter extends ListAdapter<Photo, GalleryAdapter.ViewHolder> {

    private Context context;

    public GalleryAdapter(Context context) {
        super(DIFF_CALLBACK);
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ViewHolder(inflater.inflate(R.layout.gallery_photo, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView photoImageView;

        private ViewHolder(View itemView) {
            super(itemView);
            photoImageView = itemView.findViewById(R.id.gallery_photo);
        }

        private void bind(Photo photo) {
            Glide.with(context)
                    .load(BuildConfig.API_URL + photo.path)
                    .into(photoImageView)
                    .waitForLayout();
        }
    }

    private static DiffUtil.ItemCallback<Photo> DIFF_CALLBACK = new DiffUtil.ItemCallback<Photo>() {
        @Override
        public boolean areItemsTheSame(Photo oldItem, Photo newItem) {
            return oldItem.name.equals(newItem.name);
        }

        @Override
        public boolean areContentsTheSame(Photo oldItem, Photo newItem) {
            return oldItem.path.equals(newItem.path);
        }
    };
}
