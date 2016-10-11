package org.deafsapps.latahona.data;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.Map;
import java.util.Set;

import static org.deafsapps.latahona.data.LaTahonaContract.FeedItemEntry.COLUMN_ITEM_CATEGORY;
import static org.deafsapps.latahona.data.LaTahonaContract.FeedItemEntry.COLUMN_ITEM_CONTENT;
import static org.deafsapps.latahona.data.LaTahonaContract.FeedItemEntry.COLUMN_ITEM_DESCRIPTION;
import static org.deafsapps.latahona.data.LaTahonaContract.FeedItemEntry.COLUMN_ITEM_FAVOURITE;
import static org.deafsapps.latahona.data.LaTahonaContract.FeedItemEntry.COLUMN_ITEM_LINK;
import static org.deafsapps.latahona.data.LaTahonaContract.FeedItemEntry.COLUMN_ITEM_PUB_DATE;
import static org.deafsapps.latahona.data.LaTahonaContract.FeedItemEntry.COLUMN_ITEM_TITLE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Created by Pablo L on 11/10/2016.
 */

public class TestUtilities {
    public static ContentValues createDummyContentValuesObject() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_ITEM_TITLE, "Dummy title");
        contentValues.put(COLUMN_ITEM_LINK, "Dummy link");
        contentValues.put(COLUMN_ITEM_PUB_DATE, "Dummy publication date");
        contentValues.put(COLUMN_ITEM_CATEGORY, "Dummy category");
        contentValues.put(COLUMN_ITEM_DESCRIPTION, "Dummy description");
        contentValues.put(COLUMN_ITEM_CONTENT, "Dummy content");
        contentValues.put(COLUMN_ITEM_FAVOURITE, 1);

        return contentValues;
    }

    public static ContentValues[] createDummyContentValuesSomeObjects(int numInsertions) {
        ContentValues[] returnContentValues = new ContentValues[numInsertions];

        for (int iter = 0; iter < numInsertions; iter++) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(COLUMN_ITEM_TITLE, "Dummy title " + iter);
            contentValues.put(COLUMN_ITEM_LINK, "Dummy link " + iter);
            contentValues.put(COLUMN_ITEM_PUB_DATE, "Dummy publication date " + iter);
            contentValues.put(COLUMN_ITEM_CATEGORY, "Dummy category " + iter);
            contentValues.put(COLUMN_ITEM_DESCRIPTION, "Dummy description " + iter);
            contentValues.put(COLUMN_ITEM_CONTENT, "Dummy content " + iter);
            contentValues.put(COLUMN_ITEM_FAVOURITE, 1);

            returnContentValues[iter] = contentValues;
        }

        return returnContentValues;
    }

    // Checks whether the Cursor values are equal to those expected
    public static void validateCursor(Cursor cursorFromQuery, ContentValues feedEntryValues) {
        Set<Map.Entry<String, Object>> expectedValues = feedEntryValues.valueSet();
        for (Map.Entry<String, Object> entry : expectedValues) {
            String columnName = entry.getKey();
            int idx = cursorFromQuery.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found ", idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + cursorFromQuery.getString(idx) +
                    "' did not match the expected value '" +
                    expectedValue + "'", expectedValue, cursorFromQuery.getString(idx));
        }
    }
}
