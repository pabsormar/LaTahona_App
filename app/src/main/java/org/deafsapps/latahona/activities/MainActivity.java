package org.deafsapps.latahona.activities;

import android.app.Activity;
import android.content.Context;
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

import org.deafsapps.latahona.R;
import org.deafsapps.latahona.fragments.CardFragment;
import org.deafsapps.latahona.util.FeedItem;
import org.deafsapps.latahona.util.FeedParser;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener,
                                                                    TabLayout.OnTabSelectedListener, FeedParser.OnAsyncResponse,
                                                                    CardFragment.OnFragment2Favoritos
{
    private static final String TAG_MAIN_ACTIVITY = "In-MainActivity";
    private static final String LA_TAHONA_FEED_URL = "http://www.revistalatahona.com/category/";
    private static int[] categoryArray = {R.string.feed_category_favoritos, R.string.feed_category_actualidad, R.string.feed_category_formacion, R.string.feed_category_recetas, R.string.feed_category_nacional,
            R.string.feed_category_internacional, R.string.feed_category_asociaciones, R.string.feed_category_ferias};

    private CoordinatorLayout mCoordLayout;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private ViewPager mViewPager;
    private static Snackbar mLoadingSnackbar;
    private List<FeedItem> mFavItemList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // This 'CoordinatorLayout' reference is used to display 'Snackbar' messages (employed as 'View' object)
        this.mCoordLayout = (CoordinatorLayout) this.findViewById(R.id.appCoordLayout);

        //----- DRAWERLAYOUT -----
        // The following lines link the 'DrawerLayout' object and the 'NavigationView' object and their behaviours
        this.mDrawerLayout = (DrawerLayout) this.findViewById(R.id.appDrawerLayout);
        this.mNavigationView = (NavigationView) this.findViewById(R.id.appNavView);
            // This next line checks on the first 'NavigationView' item (initially selected by default)
        if (this.mNavigationView != null)
            this.mNavigationView.getMenu().getItem(1).setChecked(true);
        // This next line makes 'NavigationView' items react to interaction (defined in 'onNavigationItemSelected' method)
        if (this.mNavigationView != null)
            this.mNavigationView.setNavigationItemSelectedListener(this);
        //--------------------------------------

        //----- TOOLBAR -----
        // Getting a reference to the 'Toolbar' and adding it as ActionBar for the 'Activity'
        final Toolbar mToolbar = (Toolbar) this.findViewById(R.id.appToolbar);
        // This coming line makes the magic, replacing the 'ActionBar' with the 'Toolbar'
        this.setSupportActionBar(mToolbar);

        final ActionBar mActionBar = this.getSupportActionBar();
        if (mActionBar != null)
        {
            // Allows to use another 'drawable' on the 'ActionBar' (in the 'Toolbar' in this case)
            mActionBar.setHomeAsUpIndicator(R.drawable.ic_menu_24dp);   // Corresponds to the 'Hamburger' icon
            // This line actually shows a 'Button' on the top left corner of the 'ActionBar'/'Toolbar',
            // whose behaviour can be defined through the 'onOptionsItemSelected' method
            mActionBar.setDisplayHomeAsUpEnabled(true);
        }
        //--------------------------------------

        //----- VIEWPAGER -----
        // A 'ViewPager' object allows to include swipe gesture to move across pages or fragments
        this.mViewPager = (ViewPager) this.findViewById(R.id.appViewPager);
        final PagerAdapter mPagerAdapter = new MyPagerAdapter(this.getSupportFragmentManager(), this);
            this.mViewPager.setAdapter(mPagerAdapter);
            this.mViewPager.setCurrentItem(1);
        //--------------------------------------

        //----- TABLAYOUT -----
        // Getting a reference to the 'TabLayout' to add 'Tab' elements through a 'ViewPager' object
        final TabLayout appTabLayout = (TabLayout) this.findViewById(R.id.appTabLayout);
            // 'setupWithViewPager' requires to override the 'getPageTitle' method from the 'FragmentPageAdapter' class
            // The return value of the latter must be a List or array with the titles of the distinct tabs
        if (appTabLayout != null)
        {
            appTabLayout.setupWithViewPager(this.mViewPager);
            appTabLayout.setOnTabSelectedListener(this);
        }
        //--------------------------------------

        //----- FAB -----
        final FloatingActionButton mFAB = (FloatingActionButton) this.findViewById(R.id.appFAB);
        if (mFAB != null)
            mFAB.setOnClickListener(this);
        //--------------------------------------
    }

    @Override
    public void onClick(View whichView)
    {
        if (whichView.getId() == R.id.appFAB)
        {
            Snackbar.make(this.mCoordLayout, "FAB clicked", Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater mInflater = this.getMenuInflater();
            mInflater.inflate(R.menu.menu_options, menu);

        return super.onCreateOptionsMenu(menu);
    }

    // Since the 'Toolbar' is enabled and working with the 'ActionBar', this method is called when the 'Hamburger' or a Toolbar option is clicked
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // The 'android.R.id.home' identifier refers to the 'Hamburger'
        if (item.getItemId() == android.R.id.home)
        {
            // 'GravityCompat.START' makes the trick in order to see the 'DrawerLayout' opening from left to right
            this.mDrawerLayout.openDrawer(GravityCompat.START);
        }
        else if (item.getItemId() == R.id.menu_option_refresh)
        {
            Log.i(MainActivity.TAG_MAIN_ACTIVITY, "Refresh option button tapped, page: " + this.mViewPager.getCurrentItem());

            int refreshPage = this.mViewPager.getCurrentItem();

            // the 'Refresh' option does not make sense for the first page ("Favoritos")
            if (refreshPage != 0)
            {
                mLoadingSnackbar = Snackbar.make(this.findViewById(R.id.appCoordLayout), "Cargando \"" + this.getResources().getString(this.categoryArray[refreshPage]) + "\"...", Snackbar.LENGTH_INDEFINITE);
                mLoadingSnackbar.show();

                this.parseFeed(MainActivity.LA_TAHONA_FEED_URL + getResources().getString(MainActivity.categoryArray[refreshPage]) + "/feed/", String.valueOf(refreshPage));
            }
        }
        else if (item.getItemId() == R.id.menu_option_settings)
        {
            Log.i(MainActivity.TAG_MAIN_ACTIVITY, "Settings option button tapped");
        }
        else
            return false;

        return true;
    }

    // This next method is overridden from the 'NavigationView.OnNavigationItemSelectedListener' interface
    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {
        if (item.getItemId() == R.id.navigation_option_favoritos)
            this.mViewPager.setCurrentItem(0);
        else if (item.getItemId() == R.id.navigation_option_actualidad)
            this.mViewPager.setCurrentItem(1);
        else if (item.getItemId() == R.id.navigation_option_formacion)
            this.mViewPager.setCurrentItem(2);
        else if (item.getItemId() == R.id.navigation_option_recetas)
            this.mViewPager.setCurrentItem(3);
        else if (item.getItemId() == R.id.navigation_option_nacional)
            this.mViewPager.setCurrentItem(4);
        else if (item.getItemId() == R.id.navigation_option_internacional)
            this.mViewPager.setCurrentItem(5);
        else if (item.getItemId() == R.id.navigation_option_asociaciones)
            this.mViewPager.setCurrentItem(6);
        else if (item.getItemId() == R.id.navigation_option_ferias)
            this.mViewPager.setCurrentItem(7);

        // The selected item is highlighted
        item.setChecked(true);
        // Closing drawer on item click
        this.mDrawerLayout.closeDrawers();

        return true;
    }

    // These next 3 methods are overridden from the 'TabLayout.OnTabSelectedListener' interface
    @Override
    public void onTabSelected(TabLayout.Tab tab)
    {
        Log.w(MainActivity.TAG_MAIN_ACTIVITY, "Tab selected");

        // 'ViewPager' is updated according to the selected tab
        this.mViewPager.setCurrentItem(tab.getPosition());
        // The corresponding 'NavigationView' item is checked on according to the selected tab
        this.mNavigationView.getMenu().getItem(tab.getPosition()).setChecked(true);
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {  }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {  }

    // This method is overridden from the 'FeedParser.OnAsyncResponse' interface
    @Override
    public void onResponse(List<FeedItem> updatedItemList, int fragmentPosition, String updateDate)
    {
        Log.i(MainActivity.TAG_MAIN_ACTIVITY, "'onResponse'");

        // This snippet just updates the "favorite" condition of the item within the current visible page
        for (FeedItem favItem : this.mFavItemList)
        {
            for (FeedItem newItem : updatedItemList)
            {
                if (favItem.getItemTitle().equals(newItem.getItemTitle()))
                    newItem.setFavorite(true);
            }
        }

        // This next line is where the 'Fragment' in the foreground gets updated.
        ((CardFragment) ((MyPagerAdapter) this.mViewPager.getAdapter()).getmFragmentList().get(fragmentPosition)).updateFragment(updatedItemList, updateDate);
        // This next line is where 'MyPagerAdapter' data list gets updated, so that future queries can be locally loaded
        ((CardFragment.CardContentAdapter) ((CardFragment) ((MyPagerAdapter) this.mViewPager.getAdapter()).getmFragmentList().get(fragmentPosition)).getmRecyclerView().getAdapter()).setItemList(updatedItemList);
        // 'Snackbar' object is dismissed
        mLoadingSnackbar.dismiss();
    }

    public void parseFeed(String urlFeed, String fragmentPosition)
    {
        // The information is queried from the Internet
        FeedParser mParser = new FeedParser(MainActivity.this);
        mParser.execute(urlFeed, fragmentPosition);
    }

    // This next method updates the "Favoritos" 'Fragment' (position 0) once the "heart" 'Button' is clicked
    @Override
    public void onFrag2Fav(FeedItem mItem2Fav)
    {
        if (mItem2Fav.isFavorite())
            this.mFavItemList.add(mItem2Fav);
        else
        {
            for (FeedItem favItem : this.mFavItemList)
            {
                if (favItem.getItemTitle().equals(mItem2Fav.getItemTitle()))
                    this.mFavItemList.remove(favItem);
            }
        }

        ((CardFragment) ((MyPagerAdapter) this.mViewPager.getAdapter()).getmFragmentList().get(0)).updateFragment(this.mFavItemList, "");
    }

    // This 'FragmentStatePageAdapter' instance will be used with the 'ViewPager' object
    // It makes the system not to save every single page, but the current, the one right after, and the one right before (difference with 'FragmentPagerAdapter'
    // That means that 'getItem' is called rather often, so it is required to program the logic which makes the loading happen
    private class MyPagerAdapter extends FragmentStatePagerAdapter
    {
        private static final String TAG_MY_PAGER_ADAPTER = "In-MyPagerAdapter";
        private static final int NUMBER_OF_TABS = 7;

        // This next 'SparseArray' will store the 'Fragment' objects already downloaded from the Internet, so that they can be loaded locally later
        private SparseArrayCompat<Fragment> mRegisteredFragments;
        private Context mainUIContext;

        public MyPagerAdapter(FragmentManager mManager, Context mContext)
        {
            super(mManager);
            this.mainUIContext = mContext;
            this.mRegisteredFragments = new SparseArrayCompat<>();

            Log.i(MyPagerAdapter.TAG_MY_PAGER_ADAPTER, "'MyPagerAdapter' instance created");
        }

        public Context getMainUIContext() { return this.mainUIContext; }

        @Override
        public Fragment getItem(int position)
        {
            Log.i(MyPagerAdapter.TAG_MY_PAGER_ADAPTER, "getItem, Page: " + position);
            Log.i(MyPagerAdapter.TAG_MY_PAGER_ADAPTER, "Previously loaded?: " + (this.getmFragmentList().get(position) != null));


            if (this.getmFragmentList().get(position) == null)
            {
                Log.i(MyPagerAdapter.TAG_MY_PAGER_ADAPTER, "Loading from the Internet");

                // This new 'CardFragment' instance will have no data to load by default (it won't show any info on screen)
                Fragment mFragment = CardFragment.newInstance();
                // The 'Fragment' instance is saved in the 'SparseArray' field for later
                this.getmFragmentList().put(position, mFragment);

                if (position != 0)
                {
                    // This next line does parse the feed in a different thread (AsyncTask)
                    parseFeed(MainActivity.LA_TAHONA_FEED_URL + getResources().getString(MainActivity.categoryArray[position]) + "/feed/", String.valueOf(position));

                    mLoadingSnackbar = Snackbar.make(((Activity) this.getMainUIContext()).findViewById(R.id.appCoordLayout), "Cargando...", Snackbar.LENGTH_INDEFINITE);
                    mLoadingSnackbar.show();
                }
            } else
                Log.i(MyPagerAdapter.TAG_MY_PAGER_ADAPTER, "Loading from local");

            return this.getmFragmentList().get(position);
        }

        @Override
        public int getItemPosition(Object object) { return super.getItemPosition(object); }

        @Override
        public int getCount() { return MyPagerAdapter.NUMBER_OF_TABS + 1; }   // '+1' since "Favoritos" category is at the beginning

        @Override
        public CharSequence getPageTitle(int position) { return getResources().getString(MainActivity.categoryArray[position]); }

        public SparseArrayCompat<Fragment> getmFragmentList() { return this.mRegisteredFragments; }
        public void setmRegisteredFragments(SparseArrayCompat<Fragment> mRegisteredFragments) { this.mRegisteredFragments = mRegisteredFragments; }
    }
}
