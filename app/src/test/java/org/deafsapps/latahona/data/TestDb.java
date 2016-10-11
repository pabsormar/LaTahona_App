package org.deafsapps.latahona.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.deafsapps.latahona.BuildConfig;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

/**
 * Created by ${USER} on ${DATE}.
 *
 * This test class checks the correct functionality when manipulating the database
 */
@RunWith(RobolectricTestRunner.class)   // 'Robolectric allows to use a Context instance
@Config(constants = BuildConfig.class)
public class TestDb {

    private Context mContext;
    private LaTahonaDbHelper mDbHelper;
    private SQLiteDatabase mDb;

    @Before
    public void setUp() throws Exception {
        mContext = RuntimeEnvironment.application;
        assertNotNull("Context is null!", mContext);
        mDbHelper = new LaTahonaDbHelper(mContext);
        assertNotNull("LaTahonaDbHelper is null!", mDbHelper);
        mDb = mDbHelper.getWritableDatabase();
        assertNotNull("SQLiteDatabase is null!", mDb);
    }

    @After
    public void tearDown() throws Exception {
        mDb.close();
        mDbHelper.close();
    }

    @Test
    public void testCreateDb() throws Exception {
        // Checks whether the database was created
        assertTrue("DB did not open", mDb.isOpen());
    }

    @Test
    public void testDbColumns() {
        // Query the database and receive a Cursor back
        Cursor cursorQuery = mDb.query(
                LaTahonaContract.FeedItemEntry.TABLE_NAME,  // Table to Query
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null  // sort order
        );

        String[] queriedColumnNames = cursorQuery.getColumnNames();
        String[] actualColumnNames = {LaTahonaContract.FeedItemEntry.COLUMN_ITEM_TITLE,
                LaTahonaContract.FeedItemEntry.COLUMN_ITEM_LINK,
                LaTahonaContract.FeedItemEntry.COLUMN_ITEM_PUB_DATE,
                LaTahonaContract.FeedItemEntry.COLUMN_ITEM_CATEGORY,
                LaTahonaContract.FeedItemEntry.COLUMN_ITEM_DESCRIPTION,
                LaTahonaContract.FeedItemEntry.COLUMN_ITEM_CONTENT,
                LaTahonaContract.FeedItemEntry.COLUMN_ITEM_FAVOURITE};

        int dbColumnOffset = 1;
        for (int idx = dbColumnOffset; idx < queriedColumnNames.length; idx++) {
            assertTrue("Column name with index " + idx + " does not match",
                    queriedColumnNames[idx].equals(actualColumnNames[idx - dbColumnOffset]));
        }

        cursorQuery.close();
    }

    @Test
    public void testInsertDbEntry() {
        // Create feed values
        ContentValues feedEntryValues = TestUtilities.createDummyContentValuesObject();
        // Insert ContentValues into database and get a row ID back
        long feedEntryRowId = mDb.insert(
                LaTahonaContract.FeedItemEntry.TABLE_NAME,
                null,
                feedEntryValues
        );
        assertTrue(feedEntryRowId != -1);

        // Query the database and receive a Cursor back
        Cursor cursorQuery = mDb.query(
                LaTahonaContract.FeedItemEntry.TABLE_NAME,  // Table to Query
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null  // sort order
        );
        assertTrue("The Cursor instance is empty", cursorQuery.moveToFirst());
        assertTrue("The Cursor instance has more than one row", !cursorQuery.moveToNext());

        cursorQuery.close();
    }

    @Test
    public void testDeleteDbEntry() {
        // Create feed values
        ContentValues feedEntryValues = TestUtilities.createDummyContentValuesObject();
        // Insert ContentValues into database and get a row ID back
        long feedEntryRowId = mDb.insert(
                LaTahonaContract.FeedItemEntry.TABLE_NAME,
                null,
                feedEntryValues
        );
        assertTrue(feedEntryRowId != -1);

        int rowsAffected = mDb.delete(
                LaTahonaContract.FeedItemEntry.TABLE_NAME,
                "_id=?",
                new String[] { String.valueOf(feedEntryRowId) }
        );
        assertTrue("Wrong number of rows affected by the deletion", rowsAffected == 1);
    }

    @Test
    public void testDeleteDb(){
        // To test whether the database is successfully deleted, all connections to it must be
        // removed first
        mDb.close();
        assertTrue("DB could not be deleted",
                mContext.deleteDatabase(LaTahonaDbHelper.DATABASE_NAME));
    }
}
