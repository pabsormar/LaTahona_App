package org.deafsapps.latahona.util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.util.Log;

import org.deafsapps.latahona.R;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class FeedParser extends AsyncTask<String, Void, ArrayList<FeedItem>>
{
    private static final String TAG_FEED_PARSER = "In-FeedParser";

    private static final String ITEM = "item";
    private static final String TITLE = "title";
    private static final String LINK = "link";
    private static final String PUBDATE = "pubDate";
    private static final String CATEGORY = "category";
    private static final String DESCRIPTION = "description";
    private static final String CONTENT = "content:encoded";

    private Context threadContext;
    private ProgressDialog mProgDialog;

    public FeedParser(Context mContext)
    {
        this.threadContext = mContext;
        this.mProgDialog = new ProgressDialog(mContext);
    }

    // Shows progress dialog to "keep the app alive"
    protected void onPreExecute()
    {
        this.mProgDialog.setMessage("Loading...");
        this.mProgDialog.show();
    }

    @Override
    protected ArrayList<FeedItem> doInBackground(String[] params)
    {
        try
        {
            // The input arguments are fetched in order
            URL myUrl = new URL(params[0]);   // Throws 'MalformedURLException'
            HttpURLConnection myConnection = (HttpURLConnection) myUrl.openConnection();   // Throws 'IOException'
                myConnection.setRequestMethod("GET");
                myConnection.setDoInput(true);
                // Starting the query
                myConnection.connect();   // Throws 'IOException'

            int respCode = myConnection.getResponseCode();   // Throws 'IOException'
                Log.i(FeedParser.TAG_FEED_PARSER, "The response is: " + respCode);

            if (respCode == HttpURLConnection.HTTP_OK)
            {
                ArrayList<FeedItem> mFeedItemList;

                InputStream myInStream = myConnection.getInputStream();   // Throws 'IOException'

                // The following method does the actual parsing of the feed
                mFeedItemList = ParseFeed(myInStream);

                myInStream.close();   // Always close the 'InputStream'

                return mFeedItemList;
            }
        }
        catch (IOException e1) { e1.printStackTrace(); }

        return null;
    }

    @Override
    protected void onPostExecute(ArrayList<FeedItem> mList)
    {
        super.onPostExecute(mList);

        // 'ProgressDialog' has to be dismissed
        if (this.mProgDialog.isShowing())
            this.mProgDialog.dismiss();

        if (mList != null)
        {
            Log.i(FeedParser.TAG_FEED_PARSER, "Feed loaded");
            Snackbar.make(((Activity) this.threadContext).findViewById(R.id.appCoordLayout), "Feed loaded", Snackbar.LENGTH_SHORT).show();
        }
        else
        {
            Log.w(FeedParser.TAG_FEED_PARSER, "Error loading feed");
            Snackbar.make(((Activity) this.threadContext).findViewById(R.id.appCoordLayout), "Error loading feed", Snackbar.LENGTH_SHORT).show();
        }
    }

    @Nullable
    private ArrayList<FeedItem> ParseFeed(InputStream myInStream)
    {
        ArrayList<FeedItem> feedList = new ArrayList<>();

        try
        {
            XmlPullParserFactory mFactory;
                mFactory = XmlPullParserFactory.newInstance();
                mFactory.setNamespaceAware(true);

            XmlPullParser myXmlParser;
                myXmlParser = mFactory.newPullParser();
                myXmlParser.setInput(myInStream, "UTF-8");   // Including the encoding is CRITICAL to work it out!

            FeedItem mItem = null;
            String eventText = "";
            int eventType = myXmlParser.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT)
            {
                switch (eventType)
                {
                    case XmlPullParser.START_TAG:
                        if (myXmlParser.getName().equals(FeedParser.ITEM)) { mItem = new FeedItem(); }
                        break;

                    case XmlPullParser.TEXT:
                        eventText = myXmlParser.getText();
                        break;

                    case XmlPullParser.END_TAG:
                        if (mItem != null)
                        {
                            if (myXmlParser.getName().equals(FeedParser.ITEM))
                            {
                                feedList.add(mItem);
                                mItem = null;
                            }
                            else if (myXmlParser.getName().equals(FeedParser.TITLE)) { mItem.setItemTitle(eventText); }
                            else if (myXmlParser.getName().equals(FeedParser.LINK)) { mItem.setItemLink(eventText); }
                            else if (myXmlParser.getName().equals(FeedParser.PUBDATE)) { mItem.setItemPubDate(eventText); }
                            else if (myXmlParser.getName().equals(FeedParser.CATEGORY)) { mItem.setItemCategoryElement(eventText); }
                            else if (myXmlParser.getName().equals(FeedParser.DESCRIPTION)) { mItem.setItemDescription(eventText); }
                            else if (myXmlParser.getName().equals(FeedParser.CONTENT)) { mItem.setItemContent(eventText); }
                        }
                        break;
                }

                eventType = myXmlParser.next();
            }
            System.out.println("End document");

        }
        catch (XmlPullParserException | IOException e1) { e1.printStackTrace(); }
        finally
        {
            if (feedList.isEmpty())
                feedList = null;
        }

        // The return value is null if 'feedList' is empty or it is null
        return feedList;
    }
}
