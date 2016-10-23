package com.app.appathon.blooddonateapp.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.appathon.blooddonateapp.OnItemClickListener;
import com.app.appathon.blooddonateapp.R;

/**
 * Created by Sunny on 10/22/2016.
 */

public class CustomListAdapter extends RecyclerView.Adapter<CustomListAdapter.ListHolder> {

    private Cursor arrayColumns;
    private Context mContext;
    private OnItemClickListener onItemClickListener;

    public CustomListAdapter(Context context, Cursor arrayColumns){
        this.arrayColumns= arrayColumns;
        this.mContext = context;
    }
    @Override
    public ListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item,parent,false);
        this.mContext = parent.getContext();
        return new ListHolder(rootView,onItemClickListener);
    }

    @Override
    public void onBindViewHolder(ListHolder holder, int position) {
        arrayColumns.moveToPosition(position);
        holder.tName.setText(arrayColumns.getString(1));
        holder.tEmail.setText(arrayColumns.getString(2));
        holder.tBloodGroup.setText("Blood Type: "+arrayColumns.getString(3));
        holder.tArea.setText(arrayColumns.getString(6)+","+arrayColumns.getString(5));
        holder.tDonateDate.setText("Last Donated "+arrayColumns.getString(7)+" month(s) ago");
        holder.proImage.setImageResource(R.drawable.account);

        holder.tCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayColumns.getCount();
    }

    final class ListHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private CardView cardView;
        private final TextView tName;
        private final TextView tEmail;
        private final TextView tBloodGroup;
        private final TextView tArea;
        private final TextView tDonateDate;
        private final ImageView tCallButton;
        private final ImageView proImage;
        private final OnItemClickListener onItemClickListener;

        public ListHolder(View itemView, OnItemClickListener onItemClickListener) {
            super(itemView);
            this.onItemClickListener = onItemClickListener;

            cardView = (CardView) itemView.findViewById(R.id.card);
            tName = (TextView) itemView.findViewById(R.id.nName);
            tEmail = (TextView) itemView.findViewById(R.id.nEmail);
            tBloodGroup = (TextView) itemView.findViewById(R.id.nBlood);
            tArea = (TextView) itemView.findViewById(R.id.nArea);
            tDonateDate = (TextView) itemView.findViewById(R.id.nLastDonation);
            tCallButton = (ImageView) itemView.findViewById(R.id.imageView2);
            proImage = (ImageView) itemView.findViewById(R.id.imageView1);
        }

        @Override
        public void onClick(View v) {
            onItemClickListener.onClick(v,getAdapterPosition());
        }
    }
}
