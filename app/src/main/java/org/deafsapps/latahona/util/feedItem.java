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

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.deafsapps.latahona.BuildConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ${USER} on ${DATE}.
 *
 * This class defines the object which will mainly compose the application
 */
public class FeedItem implements Parcelable {
    private static final String TAG_FEED_ITEM = "In-FeedItem";

    private String mItemTitle;
    private String mItemLink;
    private String mItemPubDate;
    private List<String> mItemCategory = new ArrayList<>();
    private String mItemDescription;
    private String mItemContent;
    private boolean isFavorite = false;

    public FeedItem() {}

    // This constructor relates to the 'Parcelable' condition of this class
    public FeedItem(Parcel pc) {
        mItemTitle = pc.readString();
        mItemLink = pc.readString();
        mItemPubDate = pc.readString();
        mItemCategory = pc.readArrayList(String.class.getClassLoader());
        mItemDescription = pc.readString();
        mItemContent = pc.readString();
        isFavorite = pc.readInt() == 1;
    }

    public String getItemTitle() {
        return mItemTitle;
    }
    public void setItemTitle(String itemTitle) {
        this.mItemTitle = itemTitle;
    }

    public String getItemLink() {
        return mItemLink;
    }
    public void setItemLink(String itemLink) {
        this.mItemLink = itemLink;
    }

    public String getItemPubDate() {
        return mItemPubDate;
    }
    public void setItemPubDate(String itemPubDate) {
        this.mItemPubDate = itemPubDate;
    }

    public List<String> getItemCategory() {
        return mItemCategory;
    }
    public void setItemCategory(List<String> itemCategory) {
        this.mItemCategory = itemCategory;
    }
    public void setItemCategoryElement(String itemCategoryElement) {
        this.mItemCategory.add(itemCategoryElement);
    }

    public String getItemDescription() {
        return mItemDescription;
    }
    public void setItemDescription(String itemDescription) {
        this.mItemDescription = itemDescription;
    }

    public String getItemContent() {
        return mItemContent;
    }
    public void setItemContent(String itemContent) {
        this.mItemContent = itemContent;
    }

    public boolean isFavorite() {
        return isFavorite;
    }
    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (BuildConfig.DEBUG) Log.d(FeedItem.TAG_FEED_ITEM, "writeToParcel... " + flags);

        dest.writeString(mItemTitle);
        dest.writeString(mItemLink);
        dest.writeString(mItemPubDate);
        dest.writeList(mItemCategory);
        dest.writeString(mItemDescription);
        dest.writeString(mItemContent);
        dest.writeInt(isFavorite ? 1 : 0);
    }

    /**
     * Static field used to regenerate object, individually or as arrays. Its name must be 'CREATOR'
     */
    public static final Parcelable.Creator<FeedItem> CREATOR = new Creator<FeedItem>() {
        @Override
        public FeedItem createFromParcel(Parcel source) {
            return new FeedItem(source);
        }

        @Override
        public FeedItem[] newArray(int size) {
            return new FeedItem[size];
        }
    };
}
