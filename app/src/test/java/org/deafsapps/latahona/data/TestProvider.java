package org.deafsapps.latahona.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import org.deafsapps.latahona.BuildConfig;
import org.junit.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by ${USER} on ${DATE}.
 *
 * This test class checks the correct functionality when manipulating the Content Provider
 */
@RunWith(RobolectricTestRunner.class)   // 'Robolectric allows to use a Context instance
@Config(constants = BuildConfig.class)
public class TestProvider {

    private ContentResolver mResolver;

    @Before
    public void setUp() {
        mResolver = RuntimeEnvironment.application.getContentResolver();
    }

    @After
    public void tearDown() {}

    @Test
    public void testGetType() {
        // content://org.deafsapps.latahona/feed/
        String dirType = mResolver.getType(LaTahonaContract.FeedItemEntry.CONTENT_URI);
        // vnd.android.cursor.dir/org.deafsapps.latahona/feed
        Assert.assertEquals("Error: the FeedEntry CONTENT_URI should return FeedItemEntry.CONTENT_TYPE",
                LaTahonaContract.FeedItemEntry.CONTENT_TYPE, dirType);

        // content://org.deafsapps.latahona/feed/1
        long regId = 1;
        String itemType = mResolver.getType(ContentUris.withAppendedId(
                LaTahonaContract.FeedItemEntry.CONTENT_URI, regId));
        // vnd.android.cursor.item/org.deafsapps.latahona/feed/1
        Assert.assertEquals("Error: the FeedEntry CONTENT_URI should return FeedItemEntry.CONTENT_ITEM_TYPE",
                LaTahonaContract.FeedItemEntry.CONTENT_ITEM_TYPE, itemType);
    }

    @Test
    public void testDelete() {
        // Deletes all records from the database (where = null)
        mResolver.delete(LaTahonaContract.FeedItemEntry.CONTENT_URI,
                null,
                null);
        // Makes sure the deletion has been successful
        Cursor cursor = mResolver.query(LaTahonaContract.FeedItemEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertTrue("Error: not all records were deleted", cursor.getCount() == 0);

        cursor.close();
    }

    @Test
    public void testInsert() {
        ContentValues feedEntryValues = TestUtilities.createDummyContentValuesObject();

        Uri feedInsertUri = mResolver.insert(
                LaTahonaContract.FeedItemEntry.CONTENT_URI,
                feedEntryValues
        );
        long insertedRowId = ContentUris.parseId(feedInsertUri);
        // Verify we got a row back
        assertTrue(insertedRowId != -1);

        // A cursor is the primary interface to the query results
        Cursor cursorFromQuery = mResolver.query(
                LaTahonaContract.FeedItemEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );
        // Checks that the Cursor is not empty
        assertTrue("Empty cursor returned", cursorFromQuery.moveToFirst());
        // Checks that the Cursor values match
        TestUtilities.validateCursor(cursorFromQuery, feedEntryValues);

        cursorFromQuery.close();
    }

    @Test
    public void testBulkInsert() {
        final int numInsertions = 10;
        ContentValues[] bulkInsertContentValues = TestUtilities
                .createDummyContentValuesSomeObjects(numInsertions);

        int insertCount = mResolver.bulkInsert(
                LaTahonaContract.FeedItemEntry.CONTENT_URI,
                bulkInsertContentValues
        );
        assertEquals(insertCount, numInsertions);

        // A cursor is your primary interface to the query results.
        Cursor cursorFromQuery = mResolver.query(
                LaTahonaContract.FeedItemEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );
        // we should have as many records in the database as we've inserted
        assertEquals(cursorFromQuery.getCount(), numInsertions);

        // and let's make sure they match the ones we created
        cursorFromQuery.moveToFirst();
        for (int idx = 0; idx < numInsertions; idx++, cursorFromQuery.moveToNext() ) {
            TestUtilities.validateCursor(cursorFromQuery, bulkInsertContentValues[idx]);
        }

        cursorFromQuery.close();
    }

    @Test
    public void testQuery() {
        ContentValues feedEntryValues = TestUtilities.createDummyContentValuesObject();

        Uri feedInsertUri = mResolver.insert(
                LaTahonaContract.FeedItemEntry.CONTENT_URI,
                feedEntryValues);
        long insertedRowId = ContentUris.parseId(feedInsertUri);
        // Verify we got a row back
        assertTrue(insertedRowId != -1);

        // Test the basic content provider query
        Cursor cursorFromQuery = mResolver.query(
                LaTahonaContract.FeedItemEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        // Checks that the number of rows returned matches
        assertTrue("Number of registers returned should be 1", cursorFromQuery.getCount() == 1);
        // Checks that the Cursor is not empty
        assertTrue("Empty cursor returned", cursorFromQuery.moveToFirst());
        // Make sure we get the correct cursor out of the database
        TestUtilities.validateCursor(cursorFromQuery, feedEntryValues);

        cursorFromQuery.close();
    }

    @Test
    public void testQueries() {
        final int numIter = 10;
        ContentValues feedEntryValues = TestUtilities.createDummyContentValuesObject();
        // Performs several insertions into the database
        for (int iter = 0; iter < numIter; iter++) {
            Uri feedInsertUri = mResolver.insert(
                    LaTahonaContract.FeedItemEntry.CONTENT_URI,
                    feedEntryValues);
            long insertedRowId = ContentUris.parseId(feedInsertUri);
            // Verify we got a row back
            assertTrue(insertedRowId != -1);
        }

        // Test the basic content provider query
        Cursor cursorFromQuery = mResolver.query(
                LaTahonaContract.FeedItemEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        // Checks that the number of rows returned matches
        assertTrue("Number of registers returned should be " + numIter,
                cursorFromQuery.getCount() == numIter);
        // Checks that the Cursor is not empty
        assertTrue("Empty cursor returned", cursorFromQuery.moveToFirst());
        // Make sure we get the correct cursor out of the database
        TestUtilities.validateCursor(cursorFromQuery, feedEntryValues);

        cursorFromQuery.close();
    }

    @Test
    public void testUpdate() {
        ContentValues feedEntryValues = TestUtilities.createDummyContentValuesObject();

        Uri feedInsertUri = mResolver.insert(
                LaTahonaContract.FeedItemEntry.CONTENT_URI,
                feedEntryValues);
        long locationRowId = ContentUris.parseId(feedInsertUri);
        // Verify we got a row back
        assertTrue(locationRowId != -1);

        // Creates new values to be updated
        ContentValues updatedValues = new ContentValues(feedEntryValues);
        updatedValues.put(LaTahonaContract.FeedItemEntry._ID, locationRowId);   // Same row
        updatedValues.put(LaTahonaContract.FeedItemEntry.COLUMN_ITEM_TITLE, "Another dummy title");

        Cursor cursorFromQuery = mResolver.query(
                LaTahonaContract.FeedItemEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );
        // Updates the register and checks that only 1 row has been affected
        int count = mResolver.update(
                LaTahonaContract.FeedItemEntry.CONTENT_URI,
                updatedValues,
                LaTahonaContract.FeedItemEntry._ID + "= ?",
                new String[] { Long.toString(locationRowId)});
        assertEquals(count, 1);

        cursorFromQuery.close();
    }
}
