package com.app.appathon.blooddonateapp.adapter;

import android.app.Activity;
import java.util.List;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.app.appathon.blooddonateapp.R;
import com.app.appathon.blooddonateapp.activities.UserProfileActivity;
import com.app.appathon.blooddonateapp.database.FirebaseDatabaseHelper;
import com.app.appathon.blooddonateapp.interfaces.ActionCallToUser;
import com.app.appathon.blooddonateapp.model.User;

/**
 * Created by IMRAN on 10/22/2016.
 */

public class AvailableAdapter extends RecyclerView.Adapter<AvailableAdapter.ListHolder>
        implements FirebaseDatabaseHelper.RequestToUser {

    private List<User> arrayColumns;
    private Activity mContext;
    private ActionCallToUser callToUser;

    public void setCallToUser(ActionCallToUser callToUser){
        this.callToUser = callToUser;
    }

    public AvailableAdapter(Activity context, List<User> arrayColumns) {
        this.arrayColumns = arrayColumns;
        this.mContext = context;
    }

    @Override
    public ListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler, parent, false);
        return new ListHolder(rootView, callToUser);
    }

    public void refreshList(List<User> todoList) {
        this.arrayColumns = todoList;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(final ListHolder holder, final int position) {

        final Typeface ThemeFont = Typeface.createFromAsset(mContext.getAssets(), "fonts/HelveticaNeue.ttf");

        final String id = arrayColumns.get(position).getId();

        if (arrayColumns.get(position).getLastDonate() == 0) {
            holder.tDonateDate.setText(R.string.last_donated);
        } else if (arrayColumns.get(position).lastDonate == 1) {
            holder.tDonateDate.setText("Last Donated " +
                    arrayColumns.get(position).getLastDonate() + " month ago");
        } else {
            holder.tDonateDate.setText("Last Donated " +
                    arrayColumns.get(position).getLastDonate() + " months ago");
        }

        holder.tName.setText(arrayColumns.get(position).getName());
        holder.proImage.setText(arrayColumns.get(position).getBloodGroup());
        holder.tArea.setText(arrayColumns.get(position).getAddress());
        holder.tBloodGroup.setImageResource(R.drawable.ic_person);

        holder.tDonateDate.setTypeface(ThemeFont);
        holder.tArea.setTypeface(ThemeFont);
        holder.proImage.setTypeface(ThemeFont);

        holder.tBloodGroup.setBackgroundResource(R.drawable.round_bg);

        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, UserProfileActivity.class);
                intent.putExtra("id", id);
                mContext.startActivity(intent);
                mContext.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
            }
        });

        holder.op1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callToUser.onCall(v, holder.getAdapterPosition());
            }
        });

        holder.op2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = arrayColumns.get(holder.getAdapterPosition()).getId();
                String NotificationId = arrayColumns.get(holder.getAdapterPosition()).getNotificationId();
                String name = arrayColumns.get(holder.getAdapterPosition()).getName();
                String bloodGroup = arrayColumns.get(holder.getAdapterPosition()).getBloodGroup();

                FirebaseDatabaseHelper firebaseDatabaseHelper =
                        new FirebaseDatabaseHelper(mContext, AvailableAdapter.this);
                firebaseDatabaseHelper.SendRequestMsgToUser(id, NotificationId, name, bloodGroup);
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

    @Override
    public void SendRequestMsgToUser(String userId, String email, String name, String blood) {

    }

    final class ListHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView tName;
        private final ImageView tBloodGroup;
        private final TextView tArea;
        private final TextView tDonateDate;
        private final TextView proImage;
        private TextView op1;
        private TextView op2;
        private CoordinatorLayout card;
        private ActionCallToUser callToUser;

        ListHolder(View itemView, ActionCallToUser callToUser) {
            super(itemView);
            this.callToUser = callToUser;
            tName = (TextView) itemView.findViewById(R.id.nName);
            tBloodGroup = (ImageView) itemView.findViewById(R.id.nBlood);
            tArea = (TextView) itemView.findViewById(R.id.nArea);
            tDonateDate = (TextView) itemView.findViewById(R.id.nLastDonation);
            proImage = (TextView) itemView.findViewById(R.id.imageView1);
            op1 = (TextView) itemView.findViewById(R.id.op1);
            op2 = (TextView) itemView.findViewById(R.id.op2);
            card = (CoordinatorLayout) itemView.findViewById(R.id.card);
        }

        @Override
        public void onClick(View v) {
            callToUser.onCall(v, getAdapterPosition());
        }
    }
}
