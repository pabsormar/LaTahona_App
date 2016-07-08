package org.deafsapps.latahona.util;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Xml;
import android.widget.Toast;

import org.deafsapps.latahona.activities.MainActivity;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class FeedParser extends AsyncTask<String, Void, ArrayList<FeedItem>>
{
    private static final String TAG_FEED_PARSER = "In-FeedParser";

    private static final String ITEM = "item";
    private static final String TITLE = "title";
    private static final String LINK = "link";
    private static final String PUBDATE = "pubDate";
    private static final String DESCRIPTION = "description";
    private static final String CONTENT = "content:encoded";

    private Context threadContext;

    public FeedParser(Context mContext) { this.threadContext = mContext; }

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
                ArrayList<FeedItem> mFeedItemList = new ArrayList<>();

                InputStream myInStream = myConnection.getInputStream();   // Throws 'IOException'

                XmlPullParser myXmlParser = Xml.newPullParser();
                //myXmlParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);   // Throws 'XmlPullParserException'
                myXmlParser.setInput(myInStream, null);   // Throws 'XmlPullParserException'

                String fullTitleString = "";
                int event = myXmlParser.nextTag();
                // This parsing process is ONLY ensured to be valid for the specific feed accessed
                while (myXmlParser.getEventType() != XmlPullParser.END_DOCUMENT)
                {
                    switch (event)
                    {
                        case XmlPullParser.START_TAG:
                            if (myXmlParser.getName().equals(FeedParser.ITEM))
                            {
                                myXmlParser.nextTag();
                                myXmlParser.next();
                                String mValue = myXmlParser.getText();
                                fullTitleString += "- " + mValue + "\n";

                                mFeedItemList.add(new FeedItem(mValue));
                            }
                            break;
                    }

                    event = myXmlParser.next();
                }
                Log.i(FeedParser.TAG_FEED_PARSER, fullTitleString);

                myInStream.close();   // Always close the 'InputStream'

                return mFeedItemList;
            }
        }
        catch (MalformedURLException e1) { e1.printStackTrace(); }
        catch (IOException e2) { e2.printStackTrace(); }
        catch (XmlPullParserException e3) { e3.printStackTrace(); }

        return null;
    }

    @Override
    protected void onPostExecute(ArrayList<FeedItem> mList)
    {
        super.onPostExecute(mList);

        if (!mList.isEmpty())
        {
            Toast.makeText(this.threadContext, "Feed loaded", Toast.LENGTH_LONG).show();
        }
        else
            Log.i(FeedParser.TAG_FEED_PARSER, "No feed to be loaded");
    }
}
