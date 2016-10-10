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

package org.deafsapps.latahona.activities;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.Nullable;
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

import org.deafsapps.latahona.BuildConfig;
import org.deafsapps.latahona.R;
import org.deafsapps.latahona.fragments.CardFragment;
import org.deafsapps.latahona.util.FeedItem;
import org.deafsapps.latahona.util.PatchedTextView;

/**
 *
 */
public class DetailActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = DetailActivity.class.getSimpleName();

    private FeedItem mItem;
    private boolean mToggleFavState = false;   // This field indicates whether the "Favourite" condition is swapped

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mItem = getIntent().getParcelableExtra(CardFragment.TAG_INTENT_ARGUMENTS);

        final ImageButton btnShare = (ImageButton) findViewById(R.id.imbt_activity_detail_share);
            btnShare.setOnClickListener(this);
        final ImageButton btnFavourite = (ImageButton) findViewById(R.id.imbt_activity_detail_favourite);
            btnFavourite.setOnClickListener(this);

        /**
         * Toolbar
         * Gets a reference to the 'Toolbar' and adding it as ActionBar for the 'Activity'
         */
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_activity_detail);
        // This coming line makes the trick, replacing the 'ActionBar' with the 'Toolbar'
        setSupportActionBar(toolbar);//toolbar.setNavigationOnClickListener(this);

        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            /**
             * This line actually shows a 'Button' on the top left corner of the 'ActionBar'/'Toolbar',
             * whose behaviour can be defined through the 'onOptionsItemSelected' method
             */
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        //--------------------------------------

        // The following line allows to set up the text fields from the current piece of news
        setUpTextFields();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem whichItem) {
        if (whichItem.getItemId() == android.R.id.home)
            onBackPressed();
        else
            return false;

        return true;
    }

    private void setUpTextFields() {
        final TextView tvDetailActivTitle = (TextView) findViewById(R.id.tv_activity_detail_title);
            tvDetailActivTitle.setText(Html.fromHtml(mItem.getItemTitle()));
        final PatchedTextView tvDetailActivBody = (PatchedTextView) findViewById(R.id.tv_activity_detail_body);
            // The next line allows to make clickable any link in the text
            tvDetailActivBody.setMovementMethod(LinkMovementMethod.getInstance());
            // This next line sets the 'TextView' object text excluding any image tag
            tvDetailActivBody.setText(Html.fromHtml(mItem.getItemContent().replaceAll("<img.+/(img)*>", "")));
    }

    @Override
    public void onClick(View whichView) {
        if (whichView.getId() == R.id.imbt_activity_detail_favourite) {
            if (BuildConfig.DEBUG) Log.d(TAG, "FAV Button clicked");

            // Toggles if the article is favourite
            mToggleFavState = !mToggleFavState;

            // Shows a 'Toast' message (logic XOR gate)
            Toast.makeText(whichView.getContext(), (mToggleFavState ^ mItem.isFavorite() ? "Añadido a " : "Eliminado de ") + " \"Favoritos\"", Toast.LENGTH_SHORT).show();
        }
        else if (whichView.getId() == R.id.imbt_activity_detail_share) {
            if (BuildConfig.DEBUG) Log.d(TAG, "SHARE Button clicked");
        }
    }

    @Override
    public void onBackPressed() {
        if (BuildConfig.DEBUG) Log.d(TAG, "BACK pressed");

        // Checks whether the "favourite" condition of the item has changed
        if (mToggleFavState) {
            mItem.setFavorite(!mItem.isFavorite());

            Intent detailReturnIntent = new Intent();
                detailReturnIntent.putExtra(CardFragment.TAG_INTENT_ARGUMENTS, mItem);
                detailReturnIntent.putExtra("articlePosition", getIntent().getIntExtra("articlePosition", -1));
            setResult(Activity.RESULT_OK, detailReturnIntent);
        }
        else {
            setResult(Activity.RESULT_CANCELED);
        }

        finish();

        // In this case, 'super.onBackPressed()' needs to be the last line of the method
        super.onBackPressed();
    }
}
