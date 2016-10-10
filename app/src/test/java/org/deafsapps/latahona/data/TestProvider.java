package org.deafsapps.latahona.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;

import org.deafsapps.latahona.BuildConfig;
import org.junit.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.deafsapps.latahona.data.LaTahonaContract.FeedItemEntry.CONTENT_ITEM_TYPE;
import static org.deafsapps.latahona.data.LaTahonaContract.FeedItemEntry.CONTENT_TYPE;
import static org.deafsapps.latahona.data.LaTahonaContract.FeedItemEntry.CONTENT_URI;

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
        String dirType = mResolver.getType(CONTENT_URI);
        // vnd.android.cursor.dir/org.deafsapps.latahona/feed
        Assert.assertEquals("Error: the FeedEntry CONTENT_URI should return FeedItemEntry.CONTENT_TYPE",
                CONTENT_TYPE, dirType);

        // content://org.deafsapps.latahona/feed/1
        long regId = 1;
        String itemType = mResolver.getType(ContentUris.withAppendedId(CONTENT_URI, regId));
        // vnd.android.cursor.item/org.deafsapps.latahona/feed/1
        Assert.assertEquals("Error: the FeedEntry CONTENT_URI should return FeedItemEntry.CONTENT_ITEM_TYPE",
                CONTENT_ITEM_TYPE, itemType);
    }

    @Test
    public void testDelete() {
        // Deletes all records from the database (where = null)
        mResolver.delete(CONTENT_URI, null, null);
        // Makes sure the deletion has been successful
        Cursor cursor = mResolver.query(CONTENT_URI,
                null,
                null,
                null,
                null
        );
        Assert.assertTrue("Error: not all records were deleted", cursor.getCount() == 0);
        cursor.close();
    }

    @Test
    public void testInsert() {

    }

    @Test
    public void testBulkInsert() {

    }

    @Test
    public void testQuery() {

    }

    @Test
    public void testQueries() {

    }

    @Test
    public void testUpdate() {

    }
}
