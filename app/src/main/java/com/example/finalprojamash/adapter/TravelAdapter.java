package com.example.finalprojamash.adapter;



import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalprojamash.R;
import com.example.finalprojamash.model.Travel;
import com.example.finalprojamash.utils.ImageUtil;


import java.util.List;

public class TravelAdapter extends RecyclerView.Adapter<TravelAdapter.TravelViewHolder> {

    private Context context;
    private List<Travel> travelList;
    private OnTravelClickListener listener;

    public interface OnTravelClickListener {
        void onEditClick(Travel travel, int position);
        void onDeleteClick(Travel travel, int position);
        void onItemClick(Travel travel, int position);
    }

    public TravelAdapter(Context context, List<Travel> travelList, OnTravelClickListener listener) {
        this.context = context;
        this.travelList = travelList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TravelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.itemtravelamash, parent, false);
        return new TravelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TravelViewHolder holder, int position) {
        Travel travel = travelList.get(position);

        holder.tvDestination.setText(travel.getName()); // או שם הטיול
        holder.tvDetails.setText(travel.getDetails());
        holder.ivTravelPic.setImageBitmap(ImageUtil.convertFrom64base(travel.getAttractionList().get(0).getPic()));


        holder.btnEdit.setOnClickListener(v -> {
            if(listener != null){
                listener.onEditClick(travel, position);
            }
        });

        holder.btnDelete.setOnClickListener(v -> {
            if(listener != null){
                listener.onDeleteClick(travel, position);
            }
        });

        holder.itemView.setOnClickListener(v -> {
            if(listener != null){
                listener.onItemClick(travel, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return travelList.size();
    }

    public static class TravelViewHolder extends RecyclerView.ViewHolder {

        ImageView ivTravelPic;
        TextView tvDestination, tvDetails;
        ImageButton btnEdit, btnDelete;

        public TravelViewHolder(@NonNull View itemView) {
            super(itemView);

            tvDestination = itemView.findViewById(R.id.tvTripDestination);
            tvDetails = itemView.findViewById(R.id.tvTripDetails);
            btnEdit = itemView.findViewById(R.id.btnEditTrip);
            btnDelete = itemView.findViewById(R.id.btnDeleteTrip);
            ivTravelPic = itemView.findViewById(R.id.ivTripPic);
        }
    }
}