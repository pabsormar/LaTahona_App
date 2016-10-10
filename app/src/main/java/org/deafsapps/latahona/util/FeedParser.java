/*
 * Copyright (c) 2016 Pablo L. Sordo Martinez
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.deafsapps.latahona.util;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.util.Log;

import org.deafsapps.latahona.BuildConfig;
import org.deafsapps.latahona.R;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FeedParser extends AsyncTask<String, Void, ArrayList<FeedItem>>
{
    private static final String TAG = "In-FeedParser";

    private static final String ITEM = "item";
    private static final String TITLE = "title";
    private static final String LINK = "link";
    private static final String PUBDATE = "pubDate";
    private static final String CATEGORY = "category";
    private static final String DESCRIPTION = "description";
    private static final String CONTENT = "encoded";   //"content:encoded"

    private Context mUiThreadContext;
    //private ProgressDialog mProgDialog;
    private int mFragmentPosition;

    // This interface will allow to return a 'FeedItem' list to the main Activity
    public interface FeedParserCallback {
        void onFeedParserResponse(List<FeedItem> dataItemList, int position, String date);
    }
    // The interface is implemented by the entity which is receiving the response, and a field is created in the "sender"
    private FeedParserCallback mFeedParserCallback;

    public FeedParser(Context context) {
        mUiThreadContext = context;Log.e(TAG, "FeedParser constructor");
        //this.mProgDialog = new ProgressDialog(mContext);
        mFeedParserCallback = (FeedParserCallback) context;
        //this.callingFragment = mFragment;
    }

    // Shows progress dialog to "keep the app alive"
    protected void onPreExecute() {
        /*
        this.mProgDialog.setMessage("Loading...");
        this.mProgDialog.show();
        */
    }

    @Override
    protected ArrayList<FeedItem> doInBackground(String[] params) {
        try {
            // The input arguments are fetched in order
            if (BuildConfig.DEBUG) Log.d(TAG, "URL to be queried: "
                    + params[0]);
            URL myUrl = new URL(params[0]);   // Throws 'MalformedURLException'
            mFragmentPosition = Integer.valueOf(params[1]);

            HttpURLConnection myConnection = (HttpURLConnection) myUrl.openConnection();   // Throws 'IOException'
                myConnection.setRequestMethod("GET");
                myConnection.setDoInput(true);
                // Starting the query
                myConnection.connect();   // Throws 'IOException'

            int respCode = myConnection.getResponseCode();   // Throws 'IOException'
            if (BuildConfig.DEBUG) Log.d(TAG, "The response is: " + respCode);

            if (respCode == HttpURLConnection.HTTP_OK) {
                ArrayList<FeedItem> itemList;

                InputStream inputStream = myConnection.getInputStream();   // Throws 'IOException'

                // The following method does the actual parsing of the feed
                itemList = ParseFeed(inputStream);

                inputStream.close();   // Always close the 'InputStream'

                return itemList;
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(ArrayList<FeedItem> list) {
        super.onPostExecute(list);

        /*
        // 'ProgressDialog' has to be dismissed
        if (this.mProgDialog.isShowing())
            this.mProgDialog.dismiss();
        */

        if (list != null) {
            if (BuildConfig.DEBUG) Log.d(TAG, "Feed "
                    + mFragmentPosition + " loaded");

            // Once finished, 'onResponse' interface is employed to send data back to the 'MainActivity'
            mFeedParserCallback.onFeedParserResponse(list, mFragmentPosition, "Actualizado "
                    + new SimpleDateFormat("d MMM yyyy HH:mm", Locale.getDefault()).format(new Date()));
        }
        else {
            if (BuildConfig.DEBUG) Log.w(TAG, "Error loading feed");
            // This 'Snackbar' will dismiss the one currently on display (saying "Loading...")
            Snackbar.make(((Activity) mUiThreadContext).findViewById(R.id.coordinator_layout_activity_main),
                    "Error loading feed", Snackbar.LENGTH_SHORT).show();
        }
    }

    @Nullable
    private ArrayList<FeedItem> ParseFeed(InputStream myInStream) {
        ArrayList<FeedItem> feedList = new ArrayList<>();

        try {
            XmlPullParserFactory xmlPullParserFactory;
            xmlPullParserFactory = XmlPullParserFactory.newInstance();
            xmlPullParserFactory.setNamespaceAware(true);

            XmlPullParser xmlPullParser;
                xmlPullParser = xmlPullParserFactory.newPullParser();
                xmlPullParser.setInput(myInStream, "UTF-8");   // Including the encoding is CRITICAL to work it out!

            FeedItem mItem = null;
            String eventText = "";
            int eventType = xmlPullParser.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if (xmlPullParser.getName().equals(FeedParser.ITEM)) {
                            mItem = new FeedItem();
                        }
                        break;

                    case XmlPullParser.TEXT:
                        eventText = xmlPullParser.getText();
                        break;

                    case XmlPullParser.END_TAG:
                        if (mItem != null) {
                            if (xmlPullParser.getName().equals(FeedParser.ITEM)) {
                                feedList.add(mItem);
                                mItem = null;
                            }
                            else if (xmlPullParser.getName().equals(FeedParser.TITLE)) {
                                mItem.setItemTitle(eventText);
                            }
                            else if (xmlPullParser.getName().equals(FeedParser.LINK)) {
                                mItem.setItemLink(eventText);
                            }
                            else if (xmlPullParser.getName().equals(FeedParser.PUBDATE)) {
                                mItem.setItemPubDate(eventText);
                            }
                            else if (xmlPullParser.getName().equals(FeedParser.CATEGORY)) {
                                mItem.setItemCategoryElement(eventText);
                            }
                            else if (xmlPullParser.getName().equals(FeedParser.DESCRIPTION)) {
                                mItem.setItemDescription(eventText);
                            }
                            else if (xmlPullParser.getName().equals(FeedParser.CONTENT)) {
                                mItem.setItemContent(eventText);
                            }
                        }
                        break;
                }

                eventType = xmlPullParser.next();
            }
            System.out.println("End document");

        } catch (XmlPullParserException | IOException e1) {
            e1.printStackTrace();
        }
        finally {
            if (feedList.isEmpty())
                feedList = null;
        }

        // The return value is null if 'feedList' is empty or it is null
        return feedList;
    }
}
