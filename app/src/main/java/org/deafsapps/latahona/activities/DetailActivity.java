package org.deafsapps.latahona.activities;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.deafsapps.latahona.R;
import org.deafsapps.latahona.util.FeedItem;
import org.deafsapps.latahona.util.PatchedTextView;


public class DetailActivity extends AppCompatActivity implements View.OnClickListener
{
    private static final String TAG_DETAIL_ACTIVITY = "In-DetailActivity";

    private FeedItem mItem;
    private boolean toggleFavState = false;   // This field indicates whether the "Favourite" condition is swapped

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        this.mItem = this.getIntent().getParcelableExtra("feedItem");

        final ImageButton detailShare_Btn = (ImageButton) this.findViewById(R.id.detailShareButton);
            detailShare_Btn.setOnClickListener(this);
        final ImageButton detailFavourite_Btn = (ImageButton) this.findViewById(R.id.detailFavouriteButton);
            detailFavourite_Btn.setOnClickListener(this);

        //----- TOOLBAR -----
        // Getting a reference to the 'Toolbar' and adding it as ActionBar for the 'Activity'
        final Toolbar mToolbar = (Toolbar) this.findViewById(R.id.detailActToolbar);
        // This coming line makes the magic, replacing the 'ActionBar' with the 'Toolbar'
        this.setSupportActionBar(mToolbar);

        final ActionBar mActionBar = this.getSupportActionBar();
        if (mActionBar != null)
        {
            // This line actually shows a 'Button' on the top left corner of the 'ActionBar'/'Toolbar',
            // whose behaviour can be defined through the 'onOptionsItemSelected' method
            mActionBar.setDisplayHomeAsUpEnabled(true);
        }
        //--------------------------------------

        // The following line allows to set up the text fields from the current piece of news
        this.setUpTextFields();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem whichItem)
    {
        if (whichItem.getItemId() == android.R.id.home)
            this.onBackPressed();
        else
            return false;

        return true;
    }

    private void setUpTextFields()
    {
        final TextView detailActivTitle = (TextView) this.findViewById(R.id.detailTitle);
            detailActivTitle.setText(Html.fromHtml(this.mItem.getItemTitle()));
        final PatchedTextView detailActivBody = (PatchedTextView) this.findViewById(R.id.detailBody);
            // The next line allows to make clickable any link in the text
            detailActivBody.setMovementMethod(LinkMovementMethod.getInstance());
            // This next line sets the 'TextView' object text excluding any image tag
            detailActivBody.setText(Html.fromHtml(this.mItem.getItemContent().replaceAll("<img.+/(img)*>", "")));
    }

    @Override
    public void onClick(View whichView)
    {
        if (whichView.getId() == R.id.detailFavouriteButton)
        {
            Log.i(DetailActivity.TAG_DETAIL_ACTIVITY, "FAV Button clicked");

            // Toggling whether the article is favourite
            this.toggleFavState = this.toggleFavState ? false : true;

            // This next lines try to enhance UX by showing a 'Toast' message (logic XOR gate)
            Toast.makeText(whichView.getContext(), (this.toggleFavState ^ this.mItem.isFavorite() ? "Añadido a " : "Eliminado de ") + " \"Favoritos\"", Toast.LENGTH_SHORT).show();
        }
        else if (whichView.getId() == R.id.detailShareButton)
        {
            Log.i(DetailActivity.TAG_DETAIL_ACTIVITY, "SHARE Button clicked");
        }
    }

    @Override
    public void onBackPressed()
    {
        Log.i(DetailActivity.TAG_DETAIL_ACTIVITY, "BACK pressed");

        // If the "favourite" condition of the item has changed...
        if (this.toggleFavState)
        {
            this.mItem.setFavorite(this.mItem.isFavorite() ? false : true);

            Intent detailReturnIntent = new Intent();
                detailReturnIntent.putExtra("feedItem", this.mItem);
                detailReturnIntent.putExtra("articlePosition", this.getIntent().getIntExtra("articlePosition", -1));
            this.setResult(Activity.RESULT_OK, detailReturnIntent);
        }
        // Otherwise no action is performed
        else
            this.setResult(Activity.RESULT_CANCELED);

        this.finish();

        // In this case, 'super.onBackPressed()' needs to be the last line of the method
        super.onBackPressed();
    }
}
