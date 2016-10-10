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
import android.content.Context;
import android.os.AsyncTask;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.util.SparseArrayCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import org.deafsapps.latahona.BuildConfig;
import org.deafsapps.latahona.R;
import org.deafsapps.latahona.fragments.CardFragment;
import org.deafsapps.latahona.util.FeedItem;
import org.deafsapps.latahona.util.FeedParser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        NavigationView.OnNavigationItemSelectedListener, TabLayout.OnTabSelectedListener,
        FeedParser.FeedParserCallback, CardFragment.CardFragmentCallback {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String LA_TAHONA_FEED_URL = "http://www.revistalatahona.com/category/";
    private static final int[] LA_TAHONA_CATEGORIES = {R.string.feed_category_favoritos, R.string.feed_category_actualidad, R.string.feed_category_formacion,
            R.string.feed_category_recetas, R.string.feed_category_nacional, R.string.feed_category_internacional, R.string.feed_category_asociaciones, R.string.feed_category_ferias};

    private CoordinatorLayout mCoordLayout;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private ViewPager mViewPager;
    private static Snackbar mLoadingSnackbar;
    private List<FeedItem> mFavItemList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /**
         * Gets a 'CoordinatorLayout' object reference in order to display 'Snackbar' messages
         */
        mCoordLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout_activity_main);

        /**
         * Drawer Layout
         * Links the 'DrawerLayout' object and the 'NavigationView' object and their behaviours
         */
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_activity_main);

        mNavigationView = (NavigationView) findViewById(R.id.nav_view_activity_main);

        if (mNavigationView != null) {
            mNavigationView.getMenu().getItem(1).setChecked(true);
            mNavigationView.setNavigationItemSelectedListener(this);
        }

        /**
         * Toolbar
         */
        // Gets a reference to the 'Toolbar' and adding it as ActionBar for the 'Activity'
        final Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar_activity_main);
        // Replaces the 'ActionBar' with the 'Toolbar'
        setSupportActionBar(mToolbar);

        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            // Allows to use another 'drawable' on the 'ActionBar' (in the 'Toolbar' in this case)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_24dp);   // Corresponds to the 'Hamburger' icon
            // This line actually shows a 'Button' on the top left corner of the 'ActionBar'/'Toolbar',
            // whose behaviour can be defined through the 'onOptionsItemSelected' method
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        /**
         * ViewPager
         * Allows to include swipe gesture to move across pages or fragments
         */
        mViewPager = (ViewPager) findViewById(R.id.view_pager_activity_main);
        final PagerAdapter mPagerAdapter = new MyPagerAdapter(getSupportFragmentManager(), this);
            mViewPager.setAdapter(mPagerAdapter);
            mViewPager.setCurrentItem(1);

        /**
         * TabLayout
         * Getting a reference to the 'TabLayout' to add 'Tab' elements through a 'ViewPager' object
         */
        final TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout_activity_main);
            // 'setupWithViewPager' requires to override the 'getPageTitle' method from the 'FragmentPageAdapter' class
            // The return value of the latter must be a List or array with the titles of the distinct tabs
        if (tabLayout != null)
        {
            tabLayout.setupWithViewPager(mViewPager);
            tabLayout.setOnTabSelectedListener(this);
        }

        /**
         * FAB
         */
        final FloatingActionButton mFAB = (FloatingActionButton) findViewById(R.id.fab_activity_main);
        if (mFAB != null) {
            mFAB.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View whichView)
    {
        if (whichView.getId() == R.id.fab_activity_main)
        {
            Snackbar.make(mCoordLayout, "FAB clicked", Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater mInflater = getMenuInflater();
            mInflater.inflate(R.menu.main_activity, menu);

        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Method called when the 'Hamburger' or a 'Toolbar' option is clicked. Take into account that
     * the 'Toolbar' is enabled and working with the 'ActionBar'.
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The 'android.R.id.home' identifier refers to the 'Hamburger'
        if (item.getItemId() == android.R.id.home) {
            // Makes the trick in order to see the 'DrawerLayout' opening from left to right
            mDrawerLayout.openDrawer(GravityCompat.START);
        }
        else if (item.getItemId() == R.id.menu_option_refresh) {
            if (BuildConfig.DEBUG) Log.d(TAG, "Refresh option button tapped, page: " + mViewPager.getCurrentItem());

            int refreshPage = mViewPager.getCurrentItem();

            // Excludes firs page ("Favoritos")
            if (refreshPage != 0) {
                mLoadingSnackbar = Snackbar.make(findViewById(R.id.coordinator_layout_activity_main), "Cargando \""
                        + getResources().getString(LA_TAHONA_CATEGORIES[refreshPage])
                        + "\"...", Snackbar.LENGTH_INDEFINITE);
                mLoadingSnackbar.show();

                parseFeed(LA_TAHONA_FEED_URL + getResources().getString(LA_TAHONA_CATEGORIES[refreshPage])
                        + "/feed/", String.valueOf(refreshPage));
            }
        }
        else if (item.getItemId() == R.id.menu_option_settings) {
            if (BuildConfig.DEBUG) Log.d(TAG, "Settings option button tapped");
        }
        else
            return false;

        return true;
    }

    // This next method is overridden from the 'NavigationView.OnNavigationItemSelectedListener' interface
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.navigation_option_favoritos) {
            mViewPager.setCurrentItem(0);
        }
        else if (item.getItemId() == R.id.navigation_option_actualidad) {
            mViewPager.setCurrentItem(1);
        }
        else if (item.getItemId() == R.id.navigation_option_formacion) {
            mViewPager.setCurrentItem(2);
        }
        else if (item.getItemId() == R.id.navigation_option_recetas) {
            mViewPager.setCurrentItem(3);
        }
        else if (item.getItemId() == R.id.navigation_option_nacional) {
            mViewPager.setCurrentItem(4);
        }
        else if (item.getItemId() == R.id.navigation_option_internacional) {
            mViewPager.setCurrentItem(5);
        }
        else if (item.getItemId() == R.id.navigation_option_asociaciones) {
            mViewPager.setCurrentItem(6);
        }
        else if (item.getItemId() == R.id.navigation_option_ferias) {
            mViewPager.setCurrentItem(7);
        }

        // Highlights the selected item
        item.setChecked(true);
        // Closes the Navigation Drawer on item click
        mDrawerLayout.closeDrawers();

        return true;
    }

    /**
     * These next 3 methods are overridden from the 'TabLayout.OnTabSelectedListener' interface
     */
    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        Log.w(TAG, "Tab selected");

        // Updates the 'ViewPager' according to the selected tab
        mViewPager.setCurrentItem(tab.getPosition());
        // The corresponding 'NavigationView' item is checked on according to the selected tab
        mNavigationView.getMenu().getItem(tab.getPosition()).setChecked(true);
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {}

    @Override
    public void onTabReselected(TabLayout.Tab tab) {}

    /**
     *
     * Overridden method from the 'FeedParser.OnAsyncResponse' interface
     *
     * @param updatedItemList       a
     * @param fragmentPosition      a
     * @param updateDate        a
     */
    @Override
    public void onFeedParserResponse(List<FeedItem> updatedItemList, int fragmentPosition, String updateDate) {
        if (BuildConfig.DEBUG) Log.d(TAG, "'onResponse'");

        // Updates the "favorite" condition of the item within the current visible page
        for (FeedItem favItem : mFavItemList) {
            for (FeedItem newItem : updatedItemList) {
                if (favItem.getItemTitle().equals(newItem.getItemTitle())) {
                    newItem.setFavorite(true);
                }
            }
        }

        // This next line is where the 'Fragment' in the foreground gets updated.
        ((CardFragment) ((MyPagerAdapter) mViewPager.getAdapter()).getmFragmentList().get(fragmentPosition)).updateFragment(updatedItemList, updateDate);
        // This next line is where 'MyPagerAdapter' data list gets updated, so that future queries can be locally loaded
        ((CardFragment.CardContentAdapter) ((CardFragment) ((MyPagerAdapter) mViewPager.getAdapter()).getmFragmentList().get(fragmentPosition)).getmRecyclerView().getAdapter()).setItemList(updatedItemList);
        // 'Snackbar' object is dismissed
        mLoadingSnackbar.dismiss();
    }

    /**
     * Queries information from the Internet
     *
     * @param urlFeed       a
     * @param fragmentPosition      a
     */
    public void parseFeed(String urlFeed, String fragmentPosition) {
        FeedParser feedParser = new FeedParser(MainActivity.this);
        feedParser.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, urlFeed, fragmentPosition);
        //feedParser.execute(urlFeed, fragmentPosition);
    }

    /**
     * Updates the "Favoritos" 'Fragment' (position 0) once the "heart" 'Button' is clicked
     *
     * @param item2Fav      a
     */
    @Override
    public void onCardFragment2Favoritos(FeedItem item2Fav)
    {
        if (item2Fav.isFavorite()) {
            mFavItemList.add(item2Fav);
        }
        else {
            // To safely remove from a collection while iterating over it, we need to use an 'Iterator'
            Iterator<FeedItem> iterator = mFavItemList.iterator();

            while (iterator.hasNext())
            {
                FeedItem mItem = iterator.next();

                if (mItem.getItemTitle().equals(item2Fav.getItemTitle())) {
                    iterator.remove();
                }
            }
        }

        ((CardFragment) ((MyPagerAdapter) mViewPager.getAdapter()).getmFragmentList().get(0))
                .updateFragment(mFavItemList, "");
    }

    /**
     * This 'FragmentStatePageAdapter' instance will be used with the 'ViewPager' object
     * It makes the system not to save every single page, but the current, the one right after, and
     * the one right before (difference with 'FragmentPagerAdapter'. That means that 'getItem' is
     * called rather often, so it is required to program the logic which makes the loading happen
     */
    private class MyPagerAdapter extends FragmentStatePagerAdapter {
        private static final String TAG_MY_PAGER_ADAPTER = "In-MyPagerAdapter";
        private static final int NUMBER_OF_TABS = 7;
        private static final int OFFSET = 1;

        /**
         * This next 'SparseArray' will store the 'Fragment' objects already downloaded from the
         Internet, so that they can be loaded locally later
         */
        private SparseArrayCompat<Fragment> mRegisteredFragments;
        private Context mUiContext;

        MyPagerAdapter(FragmentManager fm, Context context) {
            super(fm);
            mUiContext = context;
            mRegisteredFragments = new SparseArrayCompat<>();

            if (BuildConfig.DEBUG) Log.d(TAG_MY_PAGER_ADAPTER, "'MyPagerAdapter' instance created");
        }

        Context getMainUIContext() {
            return mUiContext;
        }

        @Override
        public Fragment getItem(int position) {
            if (BuildConfig.DEBUG) Log.d(MyPagerAdapter.TAG_MY_PAGER_ADAPTER, "getItem, Page: " + position);
            if (BuildConfig.DEBUG) {
                Log.d(MyPagerAdapter.TAG_MY_PAGER_ADAPTER, "Previously loaded?: "
                        + (getmFragmentList().get(position) != null));
            }

            if (getmFragmentList().get(position) == null) {
                if (BuildConfig.DEBUG) Log.d(MyPagerAdapter.TAG_MY_PAGER_ADAPTER, "Loading from the Internet");

                // This new 'CardFragment' instance will have no data to load by default (it won't show any info on screen)
                Fragment fragment = CardFragment.newInstance();
                // Saves the 'Fragment' instance in the 'SparseArray' to be used later
                getmFragmentList().put(position, fragment);

                if (position != 0) {
                    // Parses the feed in a different thread (AsyncTask)
                    parseFeed(LA_TAHONA_FEED_URL + getResources().getString(LA_TAHONA_CATEGORIES[position])
                            + "/feed/", String.valueOf(position));

                    mLoadingSnackbar = Snackbar.make(((Activity) getMainUIContext())
                            .findViewById(R.id.coordinator_layout_activity_main), "Cargando...",
                            Snackbar.LENGTH_INDEFINITE);
                    mLoadingSnackbar.show();
                }
            } else {
                if (BuildConfig.DEBUG) Log.d(MyPagerAdapter.TAG_MY_PAGER_ADAPTER, "Loading from local");
            }

            return getmFragmentList().get(position);
        }

        @Override
        public int getItemPosition(Object object) {
            return super.getItemPosition(object);
        }

        @Override
        public int getCount() {
            // '+1' since "Favoritos" category is at the beginning
            return NUMBER_OF_TABS + OFFSET;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return getResources().getString(LA_TAHONA_CATEGORIES[position]);
        }

        SparseArrayCompat<Fragment> getmFragmentList() {
            return mRegisteredFragments;
        }
        public void setmRegisteredFragments(SparseArrayCompat<Fragment> registeredFragments) {
            mRegisteredFragments = registeredFragments;
        }
    }
}
