package com.app.appathon.blooddonateapp.adapter;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.app.appathon.blooddonateapp.R;
import com.app.appathon.blooddonateapp.activities.UserProfileActivity;
import com.app.appathon.blooddonateapp.model.Inbox;

import java.util.ArrayList;

/**
 * Created by IMRAN on 8/20/2017.
 */

public class InboxAdapter extends RecyclerView.Adapter<InboxAdapter.ViewHolder> {

    private static final int REQUEST_PHONE_CALL = 1;
    private ArrayList<Inbox> inboxArrayList;
    private Activity activity;
    public InboxAdapter(ArrayList<Inbox> inboxArrayList, Activity activity) {
        this.inboxArrayList = inboxArrayList;
        this.activity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_inbox, parent, false);
        return new ViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Typeface ThemeFont = Typeface.createFromAsset(activity.getAssets(), "fonts/HelveticaNeue.ttf");

        holder.sender_name.setText(inboxArrayList.get(position).getSenderName());
        holder.msg_thumb.setText(String.valueOf(inboxArrayList.get(position).getSenderName().charAt(0)));
        holder.msg_time.setText(inboxArrayList.get(position).getSendTime());
        holder.msg_count.setText(String.valueOf(inboxArrayList.get(position).getCount()));
        holder.msg_body.setText(inboxArrayList.get(position).getMessage());

        holder.op1.setText(R.string.accept);
        holder.op2.setText(R.string.profile);

        final String phone = inboxArrayList.get(holder.getAdapterPosition()).getSenderPhone();

        holder.op1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    showDialogWindow(phone);
            }
        });

        holder.msg_body.setTypeface(ThemeFont);
        holder.msg_time.setTypeface(ThemeFont);

        holder.op2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void showDialogWindow(final String phone) {
        new MaterialDialog.Builder(activity)
                .title(phone)
                .icon(ContextCompat.getDrawable(activity, R.drawable.ic_phone_round))
                .positiveText("Call")
                .backgroundColorRes(R.color.dialog_color)
                .titleColorRes(android.R.color.white)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        try {
                            Intent phoneIntent = new Intent(Intent.ACTION_CALL);
                            phoneIntent.setData(Uri.parse("tel:" + phone));
                            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_PHONE_CALL);
                            } else {
                                activity.startActivity(phoneIntent);
                            }
                        } catch (android.content.ActivityNotFoundException | SecurityException ex) {
                            Toast.makeText(activity,
                                    "Call failed, please try again later!", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .show();
    }

    @Override
    public int getItemCount() {
        return inboxArrayList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView msg_body;
        private TextView sender_name;
        private TextView msg_thumb;
        private TextView msg_time;
        private TextView msg_count;
        private CardView cardView;
        private TextView op1;
        private TextView op2;

        ViewHolder(View itemView) {
            super(itemView);

            msg_body = (TextView) itemView.findViewById(R.id.msg_body);
            sender_name = (TextView) itemView.findViewById(R.id.sender_name);
            msg_thumb = (TextView) itemView.findViewById(R.id.msg_thumb);
            msg_time = (TextView) itemView.findViewById(R.id.sending_time);
            msg_count = (TextView) itemView.findViewById(R.id.msg_count);
            op1 = (TextView) itemView.findViewById(R.id.op1);
            op2 = (TextView) itemView.findViewById(R.id.op2);
            cardView = (CardView) itemView.findViewById(R.id.inbox_card);
        }
    }
}
