package org.deafsapps.latahona.fragments;

import android.content.Context;
import android.os.Bundle;
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

import org.deafsapps.latahona.R;
import org.deafsapps.latahona.util.FeedItem;

import java.util.List;

public class CardFragment extends Fragment
{
    private static final String TAG_CARD_FRAGMENT = "In-CardFragment";

    private List<FeedItem> mAdapterDataList;
    private String mDataListDate;
    private RecyclerView mRecyclerView;
    private boolean favFragment = false;

    // This interface will allow to add a 'Card' object to the "Favoritos" tab
    public interface OnFragment2Favoritos
    {
        void onFrag2Fav(FeedItem mItem2Update);
    }
    // The interface is implemented by the entity which is receiving the response, and a field is created in the "sender"
    private OnFragment2Favoritos mFrag2FavUpdate;

    // This static constructor is preferred, since it potentially allows to assign a 'Bundle' object and return the 'Fragment' itself
    public static CardFragment newInstance() { return new CardFragment(); }

    // Required empty public constructor
    public CardFragment() { this.mAdapterDataList = null; this.mDataListDate = null; }

    public RecyclerView getmRecyclerView() { return mRecyclerView; }

    public boolean isFavFragment() { return this.favFragment; }
    public void setFavFragment(boolean favFragment) { this.favFragment = favFragment; }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        Log.i(CardFragment.TAG_CARD_FRAGMENT, "CardFragment 'onCreateView'");

        // 'mList' can be null, which means no data is shown on the 'Fragment' object ('getItemCount' = 0)
        // In fact, all 'CardFragment' instances are empty ('mAdapterDataList' = null) at the beginning
        CardContentAdapter mContentAdapter = new CardContentAdapter(this.getContext(), this.mAdapterDataList, this.mDataListDate);

        this.mFrag2FavUpdate = (OnFragment2Favoritos) this.getContext();

        // Inflate the layout for this 'Fragment'
        this.mRecyclerView = (RecyclerView) inflater.inflate(R.layout.recycler_view_layout, container, false);
        this.mRecyclerView.setHasFixedSize(true);
        this.mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        this.mRecyclerView.setAdapter(mContentAdapter);

        return this.mRecyclerView;
    }

    // This method allows to update the 'Fragment' object's content (used when the AsyncTask instance has finished - onPostExecute())
    public void updateFragment(List<FeedItem> mList, String updateDate)
    {
        Log.i(CardFragment.TAG_CARD_FRAGMENT, "'updateFragment'");

        // 'mAdapterDataList' is updated, so that the next time 'onCreateView' is called, the 'RecyclerView.Adapter' does show some info
        this.mAdapterDataList = mList;
        this.mDataListDate = updateDate;
        // Now we update the 'RecyclerView.Adapter' field related to the loaded data, and notify it
        // This makes the 'Fragment' in the foreground to get updated live
        ((CardContentAdapter) this.mRecyclerView.getAdapter()).setItemList(mList);
        ((CardContentAdapter) this.mRecyclerView.getAdapter()).setmDate(updateDate);

        this.mRecyclerView.getAdapter().notifyDataSetChanged();
    }

    // Adapter to display recycler view
    public class CardContentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
    {
        private static final String TAG_CARD_CONTENT_ADAPTER = "In-CardContentAdapter";
        private static final int TYPE_HEADER = 0;
        private static final int TYPE_CARD = 1;

        private Context mContext;
        private List<FeedItem> itemList;
        private String mDate;

        private class MyCardTopMessageViewHolder extends RecyclerView.ViewHolder
        {
            private TextView updateMessage_TxtView;

            public MyCardTopMessageViewHolder(View itemView)
            {
                super(itemView);

                this.updateMessage_TxtView = (TextView) itemView.findViewById(R.id.headerRecyclerViewTextView);
            }
        }

        // Creating a 'ViewHolder' to speed up the performance
        public class MyCardViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
        {
            private TextView title_TxtView;
            private TextView description_TxtView;
            private ImageButton favourite_Btn;
            private ImageButton share_Btn;

            public MyCardViewHolder(View itemView)
            {
                super(itemView);

                this.title_TxtView = (TextView) itemView.findViewById(R.id.card_title);
                this.description_TxtView = (TextView) itemView.findViewById(R.id.card_body);
                this.favourite_Btn = (ImageButton) itemView.findViewById(R.id.favourite_button);
                    this.favourite_Btn.setOnClickListener(this);
                this.share_Btn = (ImageButton) itemView.findViewById(R.id.share_button);
                    this.share_Btn.setOnClickListener(this);
            }

            @Override
            public void onClick(View whichView)
            {
                if (whichView.getId() == R.id.favourite_button)
                {
                    Log.i(CardFragment.TAG_CARD_FRAGMENT, "favourite button clicked");

                    boolean isFavItem = CardContentAdapter.this.getItemList().get(this.getAdapterPosition() - 1).isFavorite();

                    if (isFavItem)
                    {
                        //this.favourite_Btn.setImageResource(R.drawable.ic_favorite_white);
                        Toast.makeText(whichView.getContext(), "Eliminado de \"Favoritos\"", Toast.LENGTH_SHORT).show();
                        isFavItem = false;
                    }
                    else
                    {
                        //this.favourite_Btn.setImageResource(R.drawable.ic_favorite_red);
                        Toast.makeText(whichView.getContext(), "Añadido a \"Favoritos\"", Toast.LENGTH_SHORT).show();
                        isFavItem = true;
                    }

                    // This next line updates the boolean variable associated to the 'Card' object on the current 'CardFragment'
                    CardContentAdapter.this.getItemList().get(this.getAdapterPosition() - 1).setFavorite(isFavItem);
                    CardFragment.this.mFrag2FavUpdate.onFrag2Fav(CardContentAdapter.this.getItemList().get(this.getAdapterPosition() - 1));
                }
                else if (whichView.getId() == R.id.share_button)
                {
                    Log.i(CardContentAdapter.TAG_CARD_CONTENT_ADAPTER, "share button clicked");
                }
            }
        }

        // Provide a suitable constructor (depends on the kind of dataset)
        public CardContentAdapter(Context aContext, List<FeedItem> objectList, String objectDate)
        {
            this.mContext = aContext;
            this.itemList = objectList;
            this.mDate = objectDate;
        }

        public void setItemList(List<FeedItem> itemList) { this.itemList = itemList; }
        public List<FeedItem> getItemList() { return this.itemList; }

        public String getmDate() { return this.mDate; }
        public void setmDate(String mDate) { this.mDate = mDate; }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            if (viewType == CardContentAdapter.TYPE_HEADER)
            {
                // create a new header
                View viewRow = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_top_message, parent, false);
                return new MyCardTopMessageViewHolder(viewRow);
            }
            else if (viewType == CardContentAdapter.TYPE_CARD)
            {
                // create a new view
                View viewRow = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_custom_layout, parent, false);
                viewRow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        Toast.makeText(mContext, "Clicked", Toast.LENGTH_SHORT).show();
                    }
                });

                // set the view's size, margins, padding and layout parameters
                return new MyCardViewHolder(viewRow);
            }

            return null;
        }

        @Override
        public int getItemViewType(int position)
        {
            if (position == 0)
                return CardContentAdapter.TYPE_HEADER;
            else
                return CardContentAdapter.TYPE_CARD;
        }

        // This next method is the one which includes 'ViewHolder' elements into the list as long as the user scrolls down (or back up)
        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
        {
            if (position == CardContentAdapter.TYPE_HEADER)
            {
                ((MyCardTopMessageViewHolder) holder).updateMessage_TxtView.setText(this.getmDate());
            }
            else
            {
                // get element from the dataset at this position and replace the contents of the view with that element
                // The piece 'Html.fromHtml()' allows to deal with HTML 'CDATA' sections
                // 'position - 1' is employed so that the "date TextView" on top of the list is taken into account
                ((MyCardViewHolder) holder).title_TxtView.setText(Html.fromHtml(this.itemList.get(position - 1).getItemTitle()));
                ((MyCardViewHolder) holder).description_TxtView.setText(Html.fromHtml(this.itemList.get(position - 1).getItemDescription()));
                ((MyCardViewHolder) holder).description_TxtView.setText(Html.fromHtml(this.itemList.get(position - 1).getItemDescription()));
                // Just in case we want to try
                //int favDrawable = this.itemList.get(position - 1).isFavorite() ? R.drawable.ic_favorite_red : R.drawable.ic_favorite_white;
                //((MyCardViewHolder) holder).favourite_Btn.setImageResource(favDrawable);
            }
        }

        // Return the size of your data-set (invoked by the layout manager)
        // If the data-set is null, it returns 0 and 'onBindViewHolder' is never called
        // Once again, the size of the list is '+ 1' to take into account the "date TextView" on top
        @Override
        public int getItemCount() { return this.itemList == null ? 0 : this.itemList.size() + 1; }
    }
}
