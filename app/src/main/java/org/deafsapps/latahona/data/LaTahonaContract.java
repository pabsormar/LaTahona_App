package org.deafsapps.latahona.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by ${USER} on ${DATE}.
 *
 * Class recommended by the official Android documentation, which defines all publicly available
 * elements, like the authority, the content URIs of the tables, the columns, etc.
 */
public final class LaTahonaContract {
    // The "Content authority" is a name for the entire content provider
    public static final String CONTENT_AUTHORITY = "org.deafsapps.latahona";
    // Use CONTENT_AUTHORITY as the base of all URI's which apps will use to contact the provider
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    // Possible paths (appended to base content URI for possible URI's)
    public static final String PATH_FEED = "feed";

    /* Inner class that defines the table contents of the feed item table */
    public static final class FeedItemEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FEED).build();
        // Strings that will be used by the UriMatcher
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FEED;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FEED;
        // Table name
        public static final String TABLE_NAME = "feed";
        // Feed item data
        public static final String COLUMN_ITEM_TITLE = "item_title";
        public static final String COLUMN_ITEM_LINK = "item_link";
        public static final String COLUMN_ITEM_PUB_DATE = "pub_date";
        public static final String COLUMN_ITEM_CATEGORY = "item_category";
        public static final String COLUMN_ITEM_CATEGORIES = "item_categories";
        public static final String COLUMN_ITEM_DESCRIPTION = "item_description";
        public static final String COLUMN_ITEM_CONTENT = "item_content";
        public static final String COLUMN_ITEM_FAVOURITE = "item_favourite";
    }
}
