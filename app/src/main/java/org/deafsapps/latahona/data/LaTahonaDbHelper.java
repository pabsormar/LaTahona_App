package org.deafsapps.latahona.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by ${USER} on ${DATE}.
 *
 * This class implements a SQLiteOpenHelper-based object to create and operate against databases
 */
public class LaTahonaDbHelper extends SQLiteOpenHelper{
    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "latahona.db";

    public LaTahonaDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a table to hold feed items
        final String SQL_CREATE_FEED_ITEM_TABLE = "CREATE TABLE " +
                LaTahonaContract.FeedItemEntry.TABLE_NAME + " (" +
                LaTahonaContract.FeedItemEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                LaTahonaContract.FeedItemEntry.COLUMN_ITEM_TITLE + " TEXT NOT NULL, " +
                LaTahonaContract.FeedItemEntry.COLUMN_ITEM_LINK + " TEXT NOT NULL, " +
                LaTahonaContract.FeedItemEntry.COLUMN_ITEM_PUB_DATE + " TEXT NOT NULL, " +
                LaTahonaContract.FeedItemEntry.COLUMN_ITEM_CATEGORY + " TEXT NOT NULL, " +
                LaTahonaContract.FeedItemEntry.COLUMN_ITEM_DESCRIPTION + " TEXT NOT NULL, " +
                LaTahonaContract.FeedItemEntry.COLUMN_ITEM_CONTENT + " TEXT NOT NULL, " +
                LaTahonaContract.FeedItemEntry.COLUMN_ITEM_FAVOURITE + " BOOL NOT NULL" + ")";

        db.execSQL(SQL_CREATE_FEED_ITEM_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for the database.
        db.execSQL("DROP TABLE IF EXISTS " + LaTahonaContract.FeedItemEntry.TABLE_NAME);
        onCreate(db);
    }
}
