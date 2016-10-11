package org.deafsapps.latahona.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;

/**
 * Created by ${USER} on ${DATE}.
 *
 * This class implements a Content Provider object to facilitate database manipulation
 */
public class LaTahonaProvider extends ContentProvider {

    // Uri matcher possible values
    private static final int FEED_ENTRY = 100;
    private static final int FEED_ENTRIES = 200;
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    // The UriMatcher will match each URI so different actions can be performed
    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(LaTahonaContract.CONTENT_AUTHORITY, LaTahonaContract.PATH_FEED + "/#", FEED_ENTRY);
        matcher.addURI(LaTahonaContract.CONTENT_AUTHORITY, LaTahonaContract.PATH_FEED, FEED_ENTRIES);

        return matcher;
    }

    private LaTahonaDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new LaTahonaDbHelper(getContext());
        return true;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case FEED_ENTRY:
                return LaTahonaContract.FeedItemEntry.CONTENT_ITEM_TYPE;
            case FEED_ENTRIES:
                return LaTahonaContract.FeedItemEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown URI " + uri);
        }
    }

    @Override
    // No need to use the UriMatcher here
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if (selection == null) selection = "1";

        rowsDeleted = db.delete(
                LaTahonaContract.FeedItemEntry.TABLE_NAME,
                selection,
                selectionArgs
        );

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        long regId = db.insert(
                LaTahonaContract.FeedItemEntry.TABLE_NAME,
                null,
                values);
        if (regId > 0) { returnUri = ContentUris.withAppendedId(
                LaTahonaContract.FeedItemEntry.CONTENT_URI, regId); }
        else { throw new SQLException("Failed to insert row into " + uri); }

        // Notify registered observers that a row was updated and attempt to sync
        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] contentValues) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsInserted = 0;

        switch (match) {
            case FEED_ENTRIES:
                db.beginTransaction();
                try {
                    for (ContentValues cv : contentValues) {
                        long newID = db.insert(
                                LaTahonaContract.FeedItemEntry.TABLE_NAME,
                                null,
                                cv
                        );
                        if (newID > 0) {
                            rowsInserted++;
                        }
                        else {
                            throw new SQLException("Failed to insert row into " + uri);
                        }
                    }
                    db.setTransactionSuccessful();
                    getContext().getContentResolver().notifyChange(uri, null);
                } finally {
                    db.endTransaction();
                }

                return rowsInserted;
            default:
                return super.bulkInsert(uri, contentValues);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        final SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor retCursor;

        retCursor = db.query(
                LaTahonaContract.FeedItemEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);

        return retCursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int rowsUpdated;

        rowsUpdated = db.update(
                LaTahonaContract.FeedItemEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs
        );

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }
}
