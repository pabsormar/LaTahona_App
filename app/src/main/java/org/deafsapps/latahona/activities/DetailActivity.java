package org.deafsapps.latahona.activities;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.MenuItem;
import android.widget.TextView;

import org.deafsapps.latahona.R;
import org.deafsapps.latahona.util.PatchedTextView;


public class DetailActivity extends AppCompatActivity
{
    private static final String TAG_DETAIL_ACTIVITY = "In-DetailActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

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
        {
            this.finish();
        }
        else
            return false;

        return true;
    }

    private void setUpTextFields()
    {
        final TextView detailActivTitle = (TextView) this.findViewById(R.id.detailTitle);
            detailActivTitle.setText(Html.fromHtml(this.getIntent().getStringExtra("detailTitle")));
        final PatchedTextView detailActivBody = (PatchedTextView) this.findViewById(R.id.detailBody);
            // The next line allows to make clickable any link in the text
            detailActivBody.setMovementMethod(LinkMovementMethod.getInstance());

            //detailActivBody.setText(Html.fromHtml(this.getIntent().getStringExtra("detailBody"), new ImageGetter(), null));

            // This next line sets the 'TextView' object text excluding any image tag
            detailActivBody.setText(Html.fromHtml(this.getIntent().getStringExtra("detailBody").replaceAll("<img.+/(img)*>", "")));
            //detailActivBody.setText(Html.fromHtml(this.getIntent().getStringExtra("detailBody")));
    }
}
