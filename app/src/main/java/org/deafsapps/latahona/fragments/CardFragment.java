package org.deafsapps.latahona.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.deafsapps.latahona.R;
import org.deafsapps.latahona.util.FeedItem;

import java.util.ArrayList;

public class CardFragment extends Fragment
{
    private static final String TAG_CARD_FRAGMENT = "In-CardFragment";

    // Required empty public constructor
    public CardFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Retrieving data 'Bundle' sent when the 'Fragment' object was instantiated
        final ArrayList<FeedItem> mList = this.getArguments().getParcelableArrayList(this.getResources().getResourceName(R.string.feed_data_name));

        // Inflate the layout for this fragment
        RecyclerView recyclerView = (RecyclerView) inflater.inflate(R.layout.recycler_view_layout, container, false);
        CardContentAdapter mContAdapter = new CardContentAdapter(this.getContext(), mList);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerView.setAdapter(mContAdapter);

        return recyclerView;
    }

    // Adapter to display recycler view
    private class CardContentAdapter extends RecyclerView.Adapter<CardContentAdapter.MyCardViewHolder>
    {
        Context mContext;
        ArrayList<FeedItem> itemList;

        // Creating a 'ViewHolder' to speed up the performance
        public class MyCardViewHolder extends RecyclerView.ViewHolder
        {
            private TextView title_TxtView;
            private TextView description_TxtView;

            public MyCardViewHolder(View itemView)
            {
                super(itemView);

                this.title_TxtView = (TextView) itemView.findViewById(R.id.card_title);
                this.description_TxtView = (TextView) itemView.findViewById(R.id.card_body);
            }
        }

        // Provide a suitable constructor (depends on the kind of dataset)
        public CardContentAdapter(Context context, ArrayList<FeedItem> objects)
        {
            this.mContext = context;
            this.itemList = objects;
        }

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
            // set the view's size, margins, paddings and layout parameters

            return new MyCardViewHolder(viewRow);
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(MyCardViewHolder holder, int position)
        {
            // get element from your dataset at this position
            // replace the contents of the view with that element
            holder.title_TxtView.setText(this.itemList.get(position).getItemTitle());
            holder.description_TxtView.setText(this.itemList.get(position).getItemDescription());
        }

        // Return the size of your data-set (invoked by the layout manager)
        @Override
        public int getItemCount() { return this.itemList == null ? 0 : this.itemList.size(); }
    }
}
