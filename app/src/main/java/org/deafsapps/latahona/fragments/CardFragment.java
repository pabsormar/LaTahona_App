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

    /*public RecyclerView getmRecyclerView() {
        return mRecyclerView;
    }*/

    private RecyclerView mRecyclerView;

    // Required empty public constructor
    public CardFragment() { this.mAdapterDataList = null; }

    // This static constructor is preferred, since it potentially allows to assign a 'Bundle' object and return the 'Fragment' itself
    public static CardFragment newInstance() { return new CardFragment(); }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        Log.i(CardFragment.TAG_CARD_FRAGMENT, "CardFragment 'onCreateView'");

        // 'mList' can be null, which means no data is shown on the 'Fragment' object ('getItemCount' = 0)
        // In fact, all 'CardFragment' instances are empty ('mAdapterDataList' = null) at the beginning
        CardContentAdapter mContAdapter = new CardContentAdapter(this.getContext(), this.mAdapterDataList);

        // Inflate the layout for this 'Fragment'
        this.mRecyclerView = (RecyclerView) inflater.inflate(R.layout.recycler_view_layout, container, false);
        this.mRecyclerView.setHasFixedSize(true);
        this.mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        this.mRecyclerView.setAdapter(mContAdapter);

        return this.mRecyclerView;
    }

    // This method allows to update the 'Fragment' object's content (used when the AsyncTask instance has finished - onPostExecute())
    public void updateFragment(List<FeedItem> mList)
    {
        Log.i(CardFragment.TAG_CARD_FRAGMENT, "'updateFragment'");

        // 'mAdapterDataList' is updated, so that the next time 'onCreateView' is called, the 'RecyclerView.Adapter' does show some info
        this.mAdapterDataList = mList;
        // Now we update the 'RecyclerView.Adapter' field related to the loaded data, and notify it
        // This makes the 'Fragment' in the foreground to get updated live
        ((CardContentAdapter) this.mRecyclerView.getAdapter()).setItemList(mList);
        this.mRecyclerView.getAdapter().notifyDataSetChanged();
    }

    // Adapter to display recycler view
    private class CardContentAdapter extends RecyclerView.Adapter<CardContentAdapter.MyCardViewHolder>
    {
        private static final String TAG_CARD_CONTENT_ADAPTER = "In-CardContentAdapter";

        private Context mContext;
        private List<FeedItem> itemList;

        // Creating a 'ViewHolder' to speed up the performance
        public class MyCardViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
        {
            private TextView title_TxtView;
            private TextView description_TxtView;
            private ImageButton favourite_Btn;
            private ImageButton share_Btn;

            private boolean ic_favorite_pressed = false;

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

            public boolean isIc_favorite_pressed() { return this.ic_favorite_pressed; }
            public void setIc_favorite_pressed(boolean ic_favorite_pressed) { this.ic_favorite_pressed = ic_favorite_pressed; }

            @Override
            public void onClick(View whichView)
            {
                if (whichView.getId() == R.id.favourite_button)
                {
                    Log.i(CardFragment.TAG_CARD_FRAGMENT, "favourite button clicked");

                    // The next snippet toggles the 'Drawable' of the 'ImageButton' object
                    if (!this.ic_favorite_pressed)
                    {
                        ((ImageButton) whichView).setImageResource(R.drawable.ic_favorite_red);
                        this.setIc_favorite_pressed(true);
                    }
                    else
                    {
                        ((ImageButton) whichView).setImageResource(R.drawable.ic_favorite_white);
                        this.setIc_favorite_pressed(false);
                    }
                }
                else if (whichView.getId() == R.id.share_button)
                {
                    Log.i(CardContentAdapter.TAG_CARD_CONTENT_ADAPTER, "share button clicked");
                }
            }
        }

        // Provide a suitable constructor (depends on the kind of dataset)
        public CardContentAdapter(Context aContext, List<FeedItem> objectList)
        {
            this.mContext = aContext;
            this.itemList = objectList;
        }

        public void setItemList(List<FeedItem> itemList) { this.itemList = itemList; }

        @Override
        public MyCardViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            // create a new view
            View viewRow = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_custom_layout, parent, false);
            viewRow.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v) { Toast.makeText(mContext, "Clicked", Toast.LENGTH_SHORT).show(); }
            });
            // set the view's size, margins, padding and layout parameters

            return new MyCardViewHolder(viewRow);
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(MyCardViewHolder holder, int position)
        {
            // get element from the dataset at this position and replace the contents of the view with that element
            // The piece 'Html.fromHtml()' allows to deal with HTML 'CDATA' sections
            holder.title_TxtView.setText(Html.fromHtml(this.itemList.get(position).getItemTitle()));
            holder.description_TxtView.setText(Html.fromHtml(this.itemList.get(position).getItemDescription()));
        }

        // Return the size of your data-set (invoked by the layout manager)
        // If the data-set is null, it returns 0 and 'onBindViewHolder' is never called
        @Override
        public int getItemCount() { return this.itemList == null ? 0 : this.itemList.size(); }
    }
}
