package com.example.finalprojamash.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalprojamash.R;
import com.example.finalprojamash.model.Attraction;
import com.example.finalprojamash.utils.ImageUtil;

import java.util.List;

public class AttractionAdapter  extends RecyclerView.Adapter<AttractionAdapter.ViewHolder> {



    public interface OnAttrctionClickListener {
        void onAttractionClick(Attraction attraction);
        void onLongAttractionClick(Attraction attraction);
    }


    private final OnAttrctionClickListener onAttrctionClickListener;


    /// list of attractions
    /// @see Attraction
    private final List<Attraction> attractionList;

    public AttractionAdapter( List<Attraction> attractionList, OnAttrctionClickListener onAttrctionClickListener) {
        this.onAttrctionClickListener = onAttrctionClickListener;
        this.attractionList = attractionList;
    }

    /// create a view holder for the adapter
    /// @param parent the parent view group
    /// @param viewType the type of the view
    /// @return the view holder
    /// @see ViewHolder
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        /// inflate the item_selected_attraction layout
        /// @see R.layout.item_selected_attraction
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.one_attraction, parent, false);
        return new ViewHolder(view);
    }

    /// bind the view holder with the data
    /// @param holder the view holder
    /// @param position the position of the item in the list
    /// @see ViewHolder
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Attraction attraction = attractionList.get(position);
        if (attraction == null) return;

        holder.attractionNameTextView.setText(attraction.getName());
        holder.tvAttCity.setText(attraction.getCity());
        holder.tvAttCountry.setText(attraction.getCountry());
        holder.attractionImageView.setImageBitmap(ImageUtil.convertFrom64base(attraction.getPic()));
        holder.itemView.setOnClickListener(v -> {
            if (onAttrctionClickListener != null) {
                onAttrctionClickListener.onAttractionClick(attraction);
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (onAttrctionClickListener != null) {
                onAttrctionClickListener.onLongAttractionClick(attraction);
            }
            return true;
        });



    }

    /// get the number of items in the list
    /// @return the number of items in the list
    @Override
    public int getItemCount() {
        return attractionList.size();
    }

    /// View holder for the attractions adapter
    /// @see RecyclerView.ViewHolder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView attractionNameTextView,tvAttCountry,tvAttCity;
        public final ImageView attractionImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            attractionNameTextView = itemView.findViewById(R.id.tvAttName);
            tvAttCountry = itemView.findViewById(R.id.tvAttCountry);
            tvAttCity = itemView.findViewById(R.id.tvAttCity);
            attractionImageView = itemView.findViewById(R.id.ivAttPic);
        }
    }
}

