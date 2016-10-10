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

package org.deafsapps.latahona.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.deafsapps.latahona.BuildConfig;
import org.deafsapps.latahona.R;
import org.deafsapps.latahona.activities.DetailActivity;
import org.deafsapps.latahona.util.FeedItem;

import java.util.List;

/**
 *
 */
public class CardFragment extends Fragment {
    private static final String TAG = CardFragment.class.getSimpleName();
    private static final int DETAIL_ACTIVITY_REQUEST = 1;
    private static final int LIST_OFFSET = 1;
    public static final String TAG_INTENT_ARGUMENTS = "tagIntentArguments";

    private List<FeedItem> mAdapterDataList;
    private String mDataListDate;
    private RecyclerView mRecyclerView;
    private boolean isFavFragment = false;

    /**
     * This interface allows to add a 'Card' object to the "Favoritos" tab. The interface will be
     * implemented by the entity which is receiving the response. Furthermore, an instance is
     * created in the "sender".
     */
    public interface CardFragmentCallback
    {
        void onCardFragment2Favoritos(FeedItem item2Update);
    }
    private CardFragmentCallback mCardFragmentCallback;

    /**
     * This static constructor is preferred, since it potentially allows to assign a 'Bundle'
     * object and return the 'Fragment' itself
     */
    public static CardFragment newInstance() {
        return new CardFragment();
    }

    // Required empty public constructor
    public CardFragment() {
        mAdapterDataList = null;
        mDataListDate = null;
    }

    public RecyclerView getmRecyclerView() {
        return mRecyclerView;
    }

    public boolean isFavFragment() {
        return isFavFragment;
    }
    public void setFavFragment(boolean favFragment) {
        this.isFavFragment = favFragment;
    }

    /**
     * Holds a reference to the host Activity so it can report task's results.
     * The Android framework will pass it a reference to the newly created
     * Activity after each configuration change.
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mCardFragmentCallback = (CardFragmentCallback) context;
    }

    /**
     * Sets the callback to null, avoiding any accidental leak.
     */
    @Override
    public void onDetach() {
        super.onDetach();

        mCardFragmentCallback = null;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retains this fragment across configuration changes.
        setRetainInstance(true);

        /**
         * 'mList' can be null, which means no data is shown on the 'Fragment' object ('getItemCount' = 0)
         * In fact, all 'CardFragment' instances are empty ('mAdapterDataList' = null) at the beginning
         */
        //CardContentAdapter cardContentAdapter = new CardContentAdapter(getContext(), mAdapterDataList, mDataListDate);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (BuildConfig.DEBUG) Log.d(TAG, "CardFragment 'onCreateView'");

        // Inflates the layout for this 'Fragment'
        View rootView = inflater.inflate(R.layout.card_fragment, container, false);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view_card_fragment);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(new CardContentAdapter(mAdapterDataList, mDataListDate));

        return rootView;
    }

    /**
     * Allows to update the 'Fragment' object's content. It is used when the 'AsyncTask' instance
     * has finished, in other words, in 'onPostExecute'.
     *
     * @param list      a
     * @param updateDate        a
     */
    public void updateFragment(List<FeedItem> list, String updateDate) {
        if (BuildConfig.DEBUG) Log.d(TAG, "'updateFragment'");

        /**
         * Updates 'mAdapterDataList', so that the next time 'onCreateView' is called, the
         * 'RecyclerView.Adapter' does show some info
         */
        mAdapterDataList = list;
        mDataListDate = updateDate;
        /**
         * Updates the 'RecyclerView.Adapter' field related to the loaded data, and notifies it.
         * This makes the 'Fragment' in the foreground to get updated live.
         */
        ((CardContentAdapter) mRecyclerView.getAdapter()).setItemList(list);
        ((CardContentAdapter) mRecyclerView.getAdapter()).setmDate(updateDate);

        mRecyclerView.getAdapter().notifyDataSetChanged();
    }

    /**
     * Adapter to display recycler view
     */
    public class CardContentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private static final String TAG_CARD_CONTENT_ADAPTER = "CardContentAdapter";
        private static final int TYPE_HEADER = 0;
        private static final int TYPE_CARD = 1;

        private List<FeedItem> mItemList;
        private String mDate;

        private class MyCardTopMessageViewHolder extends RecyclerView.ViewHolder {
            private TextView mTvUpdateMessage;

            MyCardTopMessageViewHolder(View itemView) {
                super(itemView);

                mTvUpdateMessage = (TextView) itemView.findViewById(R.id.tv_card_fragment_top_message);
            }
        }

        // Creating a 'ViewHolder' to speed up the performance
        class MyCardViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            private View mCardView;
            private TextView mTextViewTitle;
            private TextView mTextViewDescription;
            private ImageButton mButtonFavourite;
            private ImageButton mButtonShare;
            private TextView mTextViewPubDate;

            MyCardViewHolder(View itemView) {
                super(itemView);

                mCardView = itemView;
                    mCardView.setOnClickListener(this);
                mTextViewTitle = (TextView) itemView.findViewById(R.id.tv_card_content_adapter_title);
                mTextViewDescription = (TextView) itemView.findViewById(R.id.tv_card_content_adapter_body);
                mButtonFavourite = (ImageButton) itemView.findViewById(R.id.imbt_card_content_adapter_favourite);
                    mButtonFavourite.setOnClickListener(this);
                mButtonShare = (ImageButton) itemView.findViewById(R.id.imbt_card_content_adapter_share);
                    mButtonShare.setOnClickListener(this);
                mTextViewPubDate = (TextView) itemView.findViewById(R.id.tv_card_content_adapter_pub_date);
            }

            @Override
            public void onClick(View whichView) {
                if (whichView.equals(mCardView)) {
                    if (BuildConfig.DEBUG) Log.d(TAG_CARD_CONTENT_ADAPTER, "Clicked " + String.valueOf(getAdapterPosition() - LIST_OFFSET));

                    Intent cardViewIntent = new Intent(getContext(), DetailActivity.class);
                        cardViewIntent.putExtra(TAG_INTENT_ARGUMENTS, mAdapterDataList.get(getAdapterPosition() - LIST_OFFSET));
                        cardViewIntent.putExtra("articlePosition", getAdapterPosition() - LIST_OFFSET);
                    startActivityForResult(cardViewIntent, CardFragment.DETAIL_ACTIVITY_REQUEST);
                }
                else if (whichView.getId() == R.id.imbt_card_content_adapter_favourite) {
                    if (BuildConfig.DEBUG) Log.d(TAG_CARD_CONTENT_ADAPTER, "favourite button clicked");

                    FeedItem feedItem = CardContentAdapter.this.getItemList().get(getAdapterPosition() - LIST_OFFSET);

                    // Toggling favourite state and showing an informative 'Toast' on screen
                    feedItem.setFavorite(!feedItem.isFavorite());
                    updateFavouriteState(feedItem, getAdapterPosition() - LIST_OFFSET);

                    Toast.makeText(getContext(), (feedItem.isFavorite() ? "Añadido a" : "Eliminado de")
                            + " \"Favoritos\"", Toast.LENGTH_SHORT).show();
                }
                else if (whichView.getId() == R.id.imbt_card_content_adapter_share) {
                    if (BuildConfig.DEBUG) Log.d(TAG_CARD_CONTENT_ADAPTER, "share button clicked");
                }
            }
        }

        /**
         * Provides a suitable constructor (depends on the kind of dataset
         *
         * @param objectList        a
         * @param objectDate        a
         */
        CardContentAdapter(List<FeedItem> objectList, String objectDate) {
            mItemList = objectList;
            mDate = objectDate;
        }

        List<FeedItem> getItemList() {
            return mItemList;
        }
        public void setItemList(List<FeedItem> itemList) {
            mItemList = itemList;
        }

        String getmDate() {
            return mDate;
        }
        void setmDate(String date) {
            mDate = date;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == TYPE_HEADER) {
                // Creates a new header
                View viewRow = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.card_fragment_top_message, parent, false);
                return new MyCardTopMessageViewHolder(viewRow);
            }
            else if (viewType == TYPE_CARD) {
                // Creates a new view
                final View viewRow = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.card_content_adapter, parent, false);
                // set the view's size, margins, padding and layout parameters
                return new MyCardViewHolder(viewRow);
            }

            return null;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0) {
                return TYPE_HEADER;
            }
            else {
                return TYPE_CARD;
            }
        }

        /**
         * Includes 'ViewHolder' elements into the list as long as the user scrolls down (or up)
         *
         * @param holder        a
         * @param position      a
         */
        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (position == TYPE_HEADER) {
                ((MyCardTopMessageViewHolder) holder).mTvUpdateMessage.setText(getmDate());
            }
            else {
                /**
                 * Gets an element from the dataset at this position and replaces the contents of
                 * the view with that element. The piece 'Html.fromHtml' allows to deal with HTML
                 * 'CDATA' sections. 'position - OFFSET' is employed so that the "date TextView"
                 * on top of the list is taken into account
                 */
                ((MyCardViewHolder) holder).mTextViewTitle.setText(Html.fromHtml(mItemList.get(position - LIST_OFFSET).getItemTitle()));
                ((MyCardViewHolder) holder).mTextViewDescription.setText(Html.fromHtml(mItemList.get(position - 1).getItemDescription()));
                ((MyCardViewHolder) holder).mTextViewPubDate.setText(Html.fromHtml("<i>" + mItemList.get(position - 1).getItemPubDate().substring(5, 22) + "</i>"));
            }
        }

        /**
         * Returns the size of the data-set (invoked by the layout manager). If the data-set is
         * null, it returns 0 and 'onBindViewHolder' is never called. The size of the list is '+1'
         * to take into account the "date TextView" on top.
         *
         * @return      a
         */
        @Override
        public int getItemCount() {
            return mItemList == null ? 0 : mItemList.size() + LIST_OFFSET;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == DETAIL_ACTIVITY_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                if (BuildConfig.DEBUG) Log.d(TAG, "'onActivityResult'");

                FeedItem feedItem = data.getParcelableExtra("feedItem");
                int pos = data.getIntExtra("articlePosition", -1);

                updateFavouriteState(feedItem, pos);
            }
        }
    }

    private void updateFavouriteState(FeedItem feedItem, int itemPos) {
        if (BuildConfig.DEBUG) Log.d(TAG, "updateFavouriteState");

        ((CardContentAdapter) mRecyclerView.getAdapter()).getItemList().set(itemPos, feedItem);
        mCardFragmentCallback.onCardFragment2Favoritos(
                ((CardContentAdapter) mRecyclerView.getAdapter()).getItemList().get(itemPos));
    }
}
