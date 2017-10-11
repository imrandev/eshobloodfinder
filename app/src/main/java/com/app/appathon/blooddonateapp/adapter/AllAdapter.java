package com.app.appathon.blooddonateapp.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.appathon.blooddonateapp.Config.Config;
import com.app.appathon.blooddonateapp.R;
import com.app.appathon.blooddonateapp.activities.UserProfileActivity;
import com.app.appathon.blooddonateapp.model.Inbox;
import com.app.appathon.blooddonateapp.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

/**
 * Created by IMRAN on 10/22/2016.
 */

public class AllAdapter extends RecyclerView.Adapter<AllAdapter.ListHolder>{

    private List<User> arrayColumns;
    private Activity mContext;

    public AllAdapter(Activity context, List<User> arrayColumns){
        this.arrayColumns= arrayColumns;
        this.mContext = context;
    }
    @Override
    public ListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recycler_all,parent,false);
        return new ListHolder(rootView);
    }

    @Override
    public void onBindViewHolder(final ListHolder holder, final int position) {
        Calendar c = Calendar.getInstance();
        int curMonth = c.get(Calendar.MONTH)+1;
        int donateDATE = arrayColumns.get(position).getLastDonate();

        final Typeface ThemeFont = Typeface.createFromAsset(mContext.getAssets(), "fonts/HelveticaNeue.ttf");
        final String id = arrayColumns.get(position).getId();

        if(donateDATE==0){
            holder.tDonateDate.setText(R.string.last_donated);
            holder.tBloodGroup.setBackgroundResource(R.drawable.round_bg);
        }
        else if(donateDATE==1){
            holder.tDonateDate.setText("Last Donated "+donateDATE+" month ago");
            holder.tBloodGroup.setBackgroundResource(R.drawable.round_red);
        }
        else if(curMonth>donateDATE){
            int interValTime = curMonth - donateDATE;
            int lastDonated = curMonth - interValTime;
            holder.tDonateDate.setText("Last Donated "+ lastDonated +" months ago");
            if (interValTime > 3){
                holder.tBloodGroup.setBackgroundResource(R.drawable.round_bg);
            } else {
                holder.tBloodGroup.setBackgroundResource(R.drawable.round_red);
            }
        } else if(donateDATE>curMonth){
            int interValTime = (donateDATE + curMonth + 2) - donateDATE;
            holder.tDonateDate.setText("Last Donated "+interValTime+" months ago");
            if (interValTime > 3){
                holder.tBloodGroup.setBackgroundResource(R.drawable.round_bg);
            } else {
                holder.tBloodGroup.setBackgroundResource(R.drawable.round_red);
            }
        }

        holder.tName.setText(arrayColumns.get(position).getName());
        holder.tBloodGroup.setText(arrayColumns.get(position).getBloodGroup());
        holder.tArea.setText(arrayColumns.get(position).getAddress());
        holder.proImage.setImageResource(R.drawable.ic_person);

        holder.tDonateDate.setTypeface(ThemeFont);
        holder.tArea.setTypeface(ThemeFont);
        holder.tBloodGroup.setTypeface(ThemeFont);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, UserProfileActivity.class);
                intent.putExtra("id", id);
                mContext.startActivity(intent);
                mContext.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayColumns.size();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(getItemCount() - position - 1);
    }

    public void refreshList(List<User> list) {
        this.arrayColumns = list;
        notifyDataSetChanged();
    }

    final class ListHolder extends RecyclerView.ViewHolder {
        private CoordinatorLayout cardView;
        private final TextView tName;
        private final TextView tBloodGroup;
        private final TextView tArea;
        private final TextView tDonateDate;
        private final ImageView proImage;

        ListHolder(View itemView) {
            super(itemView);

            cardView = (CoordinatorLayout) itemView.findViewById(R.id.card);
            tName = (TextView) itemView.findViewById(R.id.nName);
            tBloodGroup = (TextView) itemView.findViewById(R.id.nBlood);
            tArea = (TextView) itemView.findViewById(R.id.nArea);
            tDonateDate = (TextView) itemView.findViewById(R.id.nLastDonation);
            proImage = (ImageView) itemView.findViewById(R.id.imageView1);
        }
    }
}
