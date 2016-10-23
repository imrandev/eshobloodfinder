package com.app.appathon.blooddonateapp.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Sunny on 10/15/2016.
 */

public class DBAdapter {

    public static final String KEY_ROWID = "_id";
    public static final String KEY_NAME = "Name";
    public static final String KEY_EMAIL = "Email";
    public static final String KEY_PHONE = "PhoneNumber";
    public static final String KEY_BLOOD = "BloodGroup";
    public static final String KEY_DISTRICT = "District";
    public static final String KEY_AREA = "Area";
    public static final String KEY_DONATEDATE = "LastDonated";



    private static final String TAG = "DBAdapter";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    private static final String DATABASE_NAME = "DonerInformation";
    private static final String DONER_INFO = "Doner";
    private static final int DATABASE_VERSION = 1;

    private final Context mCtx;

    private static final String CREATE_DONER_INFO =
            "CREATE TABLE if not exists " + DONER_INFO + " (" +
                    KEY_ROWID + " integer PRIMARY KEY autoincrement," +
                    KEY_NAME + "," +
                    KEY_EMAIL +"," +
                    KEY_BLOOD +"," +
                    KEY_PHONE +"," +
                    KEY_DISTRICT +"," +
                    KEY_AREA +"," +
                    KEY_DONATEDATE +");";


    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }


        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.w(TAG, CREATE_DONER_INFO);
            db.execSQL(CREATE_DONER_INFO);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + DONER_INFO);
            onCreate(db);
        }
    }

    public DBAdapter(Context ctx) {

        this.mCtx = ctx;
    }

    public DBAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        if (mDbHelper != null) {
            mDbHelper.close();
        }
    }

    public long insertInfo(String name,String email, String blood, String phone, String district, String area, String donateDate) {

        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_NAME, name);
        initialValues.put(KEY_EMAIL, email);
        initialValues.put(KEY_BLOOD, blood);
        initialValues.put(KEY_PHONE, phone);
        initialValues.put(KEY_DISTRICT, district);
        initialValues.put(KEY_AREA, area);
        initialValues.put(KEY_DONATEDATE, donateDate);

        return mDb.insert(DONER_INFO, null, initialValues);
    }

    public int updateInfo(long id, String name, String email, String blood, String phone, String district, String area, String donateDate) {

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name);
        values.put(KEY_EMAIL, email);
        values.put(KEY_BLOOD, blood);
        values.put(KEY_PHONE, phone);
        values.put(KEY_DISTRICT, district);
        values.put(KEY_AREA, area);
        values.put(KEY_DONATEDATE, donateDate);

        // updating row
        return mDb.update(DONER_INFO, values, KEY_ROWID + "=" + id, null);
    }


    public void deleteOneInfo(long rowId) {
        mDb.delete(DONER_INFO, KEY_ROWID + "=" + rowId, null);
    }

//    public boolean deleteAllCountries() {
//
//        int doneDelete = 0;
//        doneDelete = mDb.delete(PERSON_INFO, null , null);
//        Log.w(TAG, Integer.toString(doneDelete));
//        return doneDelete > 0;
//
//    }

    public Cursor fetchOneInfo(long rowId) throws SQLException {
        Cursor mCursor = mDb.query(true, DONER_INFO, new String[] {
                        KEY_ROWID, KEY_NAME, KEY_EMAIL, KEY_BLOOD, KEY_PHONE, KEY_DISTRICT, KEY_AREA, KEY_DONATEDATE }, KEY_ROWID + "=" + rowId,
                null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }


    public Cursor fetchAllInfo() {

        Cursor mCursor = mDb.query(DONER_INFO, new String[] {KEY_ROWID, KEY_NAME, KEY_EMAIL, KEY_BLOOD, KEY_PHONE, KEY_DISTRICT, KEY_AREA, KEY_DONATEDATE}, null, null, null, null, null);

        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

//    public void insertSomeCountries() {
//
//        createCountry("AFG","Afghanistan","Asia","Southern and Central Asia");
//        createCountry("ALB","Albania","Europe","Southern Europe");
//        createCountry("DZA","Algeria","Africa","Northern Africa");
//        createCountry("ASM","American Samoa","Oceania","Polynesia");
//        createCountry("AND","Andorra","Europe","Southern Europe");
//        createCountry("AGO","Angola","Africa","Central Africa");
//        createCountry("AIA","Anguilla","North America","Caribbean");
//
//    }

}
