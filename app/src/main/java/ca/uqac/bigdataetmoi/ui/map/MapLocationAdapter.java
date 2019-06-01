package ca.uqac.bigdataetmoi.adapters;

import android.support.annotation.NonNull;
import android.support.v7.recyclerview.extensions.ListAdapter;
import android.support.v7.util.DiffUtil.ItemCallback;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import ca.uqac.bigdataetmoi.R;
import ca.uqac.bigdataetmoi.models.City;
import ca.uqac.bigdataetmoi.ui.map.CityClickListener;

public class MapLocationAdapter extends ListAdapter<City, MapLocationAdapter.ViewHolder> {

    private CityClickListener clickListener;

    public MapLocationAdapter(CityClickListener clickListener) {
        super(DIFF_CALLBACK);
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ViewHolder(inflater.inflate(R.layout.coordinate, parent, false), clickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView placeNameTextView;
        ImageButton dropdownImageButton;
        CityClickListener cityClickListener;

        private ViewHolder(View itemView, CityClickListener clickListener) {
            super(itemView);
            placeNameTextView = itemView.findViewById(R.id.place_name);
            dropdownImageButton = itemView.findViewById(R.id.show_info);
            cityClickListener = clickListener;
        }

        private void bind(City city) {
            placeNameTextView.setText(city.name + " (" + city.numberOfCoordinatesLogged + "\uD83D\uDCCD)");
            dropdownImageButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            cityClickListener.onClick(view, getItem(getAdapterPosition()));
        }
    }

    private static ItemCallback<City> DIFF_CALLBACK = new ItemCallback<City>() {
        @Override
        public boolean areItemsTheSame(City oldItem, City newItem) {
            return oldItem.name.equals(newItem.name);
        }

        @Override
        public boolean areContentsTheSame(City oldItem, City newItem) {
            return oldItem.latitude.equals(newItem.latitude) &&
                    oldItem.longitude.equals(newItem.longitude) &&
                    oldItem.numberOfCoordinatesLogged == newItem.numberOfCoordinatesLogged;
        }
    };
}
