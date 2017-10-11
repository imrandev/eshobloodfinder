package com.app.appathon.blooddonateapp.database;

import android.app.Activity;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;
import com.app.appathon.blooddonateapp.Config.Config;
import com.app.appathon.blooddonateapp.interfaces.TrackUserLocation;
import com.app.appathon.blooddonateapp.model.Inbox;
import com.app.appathon.blooddonateapp.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.onesignal.OSPermissionSubscriptionState;
import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


/**
 * Created by IMRAN on 9/27/2017.
 */

public class FirebaseDatabaseHelper implements TrackUserLocation{

    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS =0 ;
    private Activity context;
    private AvailableDonorInterface availableDonorInterface;
    private AllDonorInterface allDonorInterface;
    private IncomingInboxInterface incomingInboxInterface;
    private OutgoingInboxInterface outgoingInboxInterface;
    private DatabaseReference mDatabase;
    private FirebaseUser firebaseUser;
    private String userId, userPhone;
    private int c = 1;

    public FirebaseDatabaseHelper(Activity context, AvailableDonorInterface availableDonorInterface) {
        this.context = context;
        this.availableDonorInterface = availableDonorInterface;
        initFirebase();
    }

    public FirebaseDatabaseHelper(Activity context, AllDonorInterface allDonorInterface) {
        this.context = context;
        this.allDonorInterface = allDonorInterface;
        initFirebase();
    }

    public FirebaseDatabaseHelper(Activity context, IncomingInboxInterface incomingInboxInterface) {
        this.context = context;
        this.incomingInboxInterface = incomingInboxInterface;
        initFirebase();
    }

    public FirebaseDatabaseHelper(Activity context, OutgoingInboxInterface outgoingInboxInterface) {
        this.context = context;
        this.outgoingInboxInterface = outgoingInboxInterface;
        initFirebase();
    }

    public FirebaseDatabaseHelper(Activity context, RequestToUser requestToUser) {
        this.context = context;
        initFirebase();
    }

    private void initFirebase() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.keepSynced(false);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();

        assert firebaseUser != null;
        userId = firebaseUser.getUid();
        userPhone = firebaseUser.getPhoneNumber();
    }

    @Override
    public void trackLocation(Location location) {
        mDatabase.child("users").child(userId).child("lat").setValue(location.getLatitude());
        mDatabase.child("users").child(userId).child("lng").setValue(location.getLongitude());
        Toast.makeText(context, "Location updated", Toast.LENGTH_SHORT).show();
    }


    public interface AvailableDonorInterface {
        void getAvailableDonorInfo(String id, String email, List<User> users);
        void onFirebaseInternalError(String error);
    }

    public interface AllDonorInterface {
        void getAllDonorInfo(String id, String email, List<User> users);
        void onFirebaseInternalError(String error);
    }

    public interface IncomingInboxInterface {
        void getIncomingInboxData(String id, String email, List<Inbox> inboxes);
        void onFirebaseInternalError(String error);
    }

    public interface OutgoingInboxInterface {
        void getOutgoingInboxData(String id, String email, List<Inbox> inboxes, List<User> users);
        void onFirebaseInternalError(String error);
    }

    public interface RequestToUser {
        void SendRequestMsgToUser(String userId, String email, String name, String blood);
    }

    public void getAvailableUserListData(){
        final List<User> availableUserList= new ArrayList<>();
        mDatabase.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (availableUserList.size() > 0)
                    availableUserList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    User user = snapshot.getValue(User.class);
                    user.setId(snapshot.getKey());
                    Calendar c = Calendar.getInstance();
                    if (!firebaseUser.getUid().equals(snapshot.getKey())){
                        int curMonth = c.get(Calendar.MONTH)+1;
                        int donateDATE = user.lastDonate;

                        if(donateDATE==0){
                            availableUserList.add(user);
                        }
                        else if(curMonth>donateDATE){
                            int interValTime = curMonth - donateDATE;
                            int lastDonated = curMonth - interValTime;
                            if(lastDonated > 3){
                                availableUserList.add(user);
                            }
                        } else if(donateDATE>curMonth){
                            int interValTime = (donateDATE + curMonth + 2) - donateDATE;
                            if(interValTime > 3){
                                availableUserList.add(user);
                            }
                        }
                    }
                }
                availableDonorInterface.getAvailableDonorInfo(userId, userPhone, availableUserList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                availableDonorInterface.onFirebaseInternalError(databaseError.getMessage());
            }
        });
    }

    public void getAllUserListData(){
        final List<User> allUserList = new ArrayList<>();

        mDatabase.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (allUserList.size() > 0)
                    allUserList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    User user = snapshot.getValue(User.class);
                    user.setId(snapshot.getKey());
                    if (!firebaseUser.getUid().equals(snapshot.getKey())){
                        allUserList.add(user);
                    }
                }

                allDonorInterface.getAllDonorInfo(userId, userPhone, allUserList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                allDonorInterface.onFirebaseInternalError(databaseError.getMessage());
            }
        });
    }

    public void getUserIncomingInboxData(){
        final List<Inbox> incomingList = new ArrayList<>();
        mDatabase.child("users").child(firebaseUser.getUid()).child("inbox").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (incomingList.size() > 0)
                    incomingList.clear();
                for (DataSnapshot s : dataSnapshot.getChildren()){
                    Inbox msg = s.getValue(Inbox.class);
                    incomingList.add(msg);
                }
                incomingInboxInterface.getIncomingInboxData(userId, userPhone, incomingList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                incomingInboxInterface.onFirebaseInternalError(databaseError.getMessage());
            }
        });
    }

    public void getUserOutgoingInboxData(){
        final List<Inbox> outgoingList = new ArrayList<>();
        final List<String> msg_count = new ArrayList<>();
        final List<User> outMsgList = new ArrayList<>();
        mDatabase.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (outgoingList.size() > 0)
                    outgoingList.clear();
                if (msg_count.size() > 0)
                    msg_count.clear();
                if (outMsgList.size() > 0)
                    outMsgList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    User user = snapshot.getValue(User.class);
                    for (DataSnapshot snap : snapshot.getChildren()){
                        String key = "inbox";
                        if (snap.getKey().equals(key)){
                            for (DataSnapshot sn : snap.getChildren()){
                                String userId = firebaseUser.getUid();
                                if (sn.getKey().equals(userId)){
                                    Inbox msg = sn.getValue(Inbox.class);
                                    outgoingList.add(msg);
                                    outMsgList.add(user);
                                }
                            }
                        }
                    }
                }
                outgoingInboxInterface.getOutgoingInboxData(userId, userPhone, outgoingList, outMsgList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                outgoingInboxInterface.onFirebaseInternalError(databaseError.getMessage());
            }
        });
    }

    public void SendRequestMsgToUser(final String userId, final String SendNotificationId, final String name, final String blood){
        mDatabase.child("users").child(userId).child("inbox").child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Inbox inbox = dataSnapshot.getValue(Inbox.class);
                if (inbox != null){
                    c = inbox.count;
                    c++;
                }
                ActionToSendInboxData(c, userId, SendNotificationId, name, blood);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void ActionToSendInboxData(int count, String userId, String sendNotificationId, String name, String blood) {
        final String message = "Hi, I'm " + Config.CURRENT_USERNAME + ". I have just checked your profile, I need " + blood
                + " blood urgent. If you are interested to donate, please contact with me";
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yy hh.mm aa", Locale.getDefault());
        String sendTime = dateFormat.format(new Date());
        Inbox msg = new Inbox(message, sendTime, Config.CURRENT_USERNAME, userPhone, count);

        mDatabase.child("users").child(userId).child("inbox").child(firebaseUser.getUid()).setValue(msg);
        sendNotification(sendNotificationId,blood);
        Toast.makeText(context,
                "Request message send to " + name,
                Toast.LENGTH_SHORT).show();
    }

    private void sendNotification(String SendNotificationId, String blood)
    {
        OSPermissionSubscriptionState status = OneSignal.getPermissionSubscriptionState();
        boolean isSubscribed = status.getSubscriptionStatus().getSubscribed();

        if (isSubscribed) {
            try {
                OneSignal.postNotification(new JSONObject("{'contents': {'en':'I have just checked your profile, I need "+blood+
                                " blood urgent. If you are interested to donate, please contact with me.'}, " +
                                "'include_player_ids': ['" + SendNotificationId + "'], " +
                                "'headings': {'en': '"+ Config.CURRENT_USERNAME +"' } }"),
                        new OneSignal.PostNotificationResponseHandler() {
                            @Override
                            public void onSuccess(JSONObject response) {
                                Log.i("OneSignalExample", "postNotification Success: " + response.toString());
                            }
                            @Override
                            public void onFailure(JSONObject response) {
                                Log.e("OneSignalExample", "postNotification Failure: " + response.toString());
                            }
                        });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
