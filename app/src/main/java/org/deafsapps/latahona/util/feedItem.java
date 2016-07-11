package org.deafsapps.latahona.util;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class FeedItem implements Parcelable
{
    private static final String TAG_FEED_ITEM = "In-FeedItem";

    private String itemTitle;
    private String itemLink;
    private String itemPubDate;
    private List<String> itemCategory = new ArrayList<>();
    private String itemDescription;
    private String itemContent;

    public FeedItem() { }

    // This constructor relates to the 'Parcelable' condition of this class
    public FeedItem(Parcel pc)
    {
        this.itemTitle = pc.readString();
        this.itemLink = pc.readString();
        this.itemPubDate = pc.readString();
        this.itemCategory = pc.readArrayList(String.class.getClassLoader());
        this.itemDescription = pc.readString();
        this.itemContent = pc.readString();
    }

    public String getItemTitle() { return itemTitle; }
    public void setItemTitle(String itemTitle) { this.itemTitle = itemTitle; }

    public String getItemLink() { return itemLink; }
    public void setItemLink(String itemLink) { this.itemLink = itemLink; }

    public String getItemPubDate() { return itemPubDate; }
    public void setItemPubDate(String itemPubDate) { this.itemPubDate = itemPubDate; }

    public List<String> getItemCategory() { return itemCategory; }
    public void setItemCategory(List<String> itemCategory) { this.itemCategory = itemCategory; }
    public void setItemCategoryElement(String itemCategoryElement) { this.itemCategory.add(itemCategoryElement); }

    public String getItemDescription() { return itemDescription; }
    public void setItemDescription(String itemDescription) { this.itemDescription = itemDescription; }

    public String getItemContent() { return itemContent; }
    public void setItemContent(String itemContent) { this.itemContent = itemContent; }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        Log.i(FeedItem.TAG_FEED_ITEM, "writeToParcel... " + flags);

        dest.writeString(this.itemTitle);
        dest.writeString(this.itemLink);
        dest.writeString(this.itemPubDate);
        dest.writeList(this.itemCategory);
        dest.writeString(this.itemDescription);
        dest.writeString(this.itemContent);
    }

    // Static field used to regenerate object, individually or as arrays
    // Its name must be 'CREATOR'
    public static final Parcelable.Creator<FeedItem> CREATOR = new Creator<FeedItem>()
    {
        @Override
        public FeedItem createFromParcel(Parcel source)
        {
            return new FeedItem(source);
        }

        @Override
        public FeedItem[] newArray(int size)
        {
            return new FeedItem[size];
        }
    };
}
