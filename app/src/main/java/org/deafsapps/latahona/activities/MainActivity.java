package org.deafsapps.latahona.activities;

import android.content.Context;
import android.os.Parcelable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
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
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener,
                                                                    TabLayout.OnTabSelectedListener, SwipeRefreshLayout.OnRefreshListener
{
    private static final String TAG_MAIN_ACTIVITY = "In-MainActivity";
    private static final String LA_TAHONA_FEED_URL = "http://www.revistalatahona.com/category/";

    private static int[] categoryArray = {R.string.feed_category_actualidad, R.string.feed_category_formacion, R.string.feed_category_recetas, R.string.feed_category_nacional,
            R.string.feed_category_internacional, R.string.feed_category_asociaciones, R.string.feed_category_ferias};

    private CoordinatorLayout mCoordLayout;
    private MyPagerAdapter mPagerAdapter;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private SwipeRefreshLayout mSwipeRefresh;
    private ViewPager mViewPager;

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
            this.mNavigationView.getMenu().getItem(0).setChecked(true);
            // This next line makes 'NavigationView' items react to interaction (defined in 'onNavigationItemSelected' method)
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

        //----- SWIPE-REFRESH-LAYOUT -----
        this.mSwipeRefresh = (SwipeRefreshLayout) this.findViewById(R.id.swipeContainer);
            this.mSwipeRefresh.setOnRefreshListener(this);
        //--------------------------------------

        //----- VIEWPAGER -----
        // A 'ViewPager' object allows to include swipe gesture to move across pages or fragments
        this.mPagerAdapter = new MyPagerAdapter(this.getSupportFragmentManager(), this.mSwipeRefresh);
        this.mViewPager = (ViewPager) this.findViewById(R.id.appViewPager);
            this.mViewPager.setAdapter(mPagerAdapter);
        //--------------------------------------

        //----- TABLAYOUT -----
        // Getting a reference to the 'TabLayout' to add 'Tab' elements through a 'ViewPager' object
        final TabLayout appTabLayout = (TabLayout) this.findViewById(R.id.appTabLayout);
            // 'setupWithViewPager' requires to override the 'getPageTitle' method from the 'FragmentPageAdapter' class
            // The return value of the latter must be a List or array with the titles of the distinct tabs
            appTabLayout.setupWithViewPager(this.mViewPager);
            appTabLayout.setOnTabSelectedListener(this);
        //--------------------------------------

        //----- FAB -----
        final FloatingActionButton mFAB = (FloatingActionButton) this.findViewById(R.id.appFAB);
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
            Log.i(MainActivity.TAG_MAIN_ACTIVITY, "Refresh option button tapped");

            // The refreshing animation is enabled
            this.mSwipeRefresh.setRefreshing(true);
            // The next line calls 'getItem(position)' so that just the current 'Fragment' object is updated (feed is queried again)
            this.mPagerAdapter.getItem(this.mViewPager.getCurrentItem());
            // The refreshing animation is dismissed once we have finished
            this.mSwipeRefresh.setRefreshing(false);

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
        if (item.getItemId() == R.id.navigation_option_actualidad)
            this.mViewPager.setCurrentItem(0);
        else if (item.getItemId() == R.id.navigation_option_formacion)
            this.mViewPager.setCurrentItem(1);
        else if (item.getItemId() == R.id.navigation_option_recetas)
            this.mViewPager.setCurrentItem(2);
        else if (item.getItemId() == R.id.navigation_option_nacional)
            this.mViewPager.setCurrentItem(3);
        else if (item.getItemId() == R.id.navigation_option_internacional)
            this.mViewPager.setCurrentItem(4);
        else if (item.getItemId() == R.id.navigation_option_asociaciones)
            this.mViewPager.setCurrentItem(5);
        else if (item.getItemId() == R.id.navigation_option_ferias)
            this.mViewPager.setCurrentItem(6);

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
        Log.i(MainActivity.TAG_MAIN_ACTIVITY, "Tab selected");

        // 'ViewPager' is updated according to the selected tab
        this.mViewPager.setCurrentItem(tab.getPosition());
        // The corresponding 'NavigationView' item is checked on according to the selected tab
        this.mNavigationView.getMenu().getItem(tab.getPosition()).setChecked(true);
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {  }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {  }

    // These next method is overridden from the 'SwipeRefreshLayout.OnRefreshListener' interface
    @Override
    public void onRefresh()
    {
        Log.i(MainActivity.TAG_MAIN_ACTIVITY, "Refresh gesture detected");
        // The next line calls 'getItem(position)' so that just the current 'Fragment' object is updated (feed is queried again)
        this.mPagerAdapter.getItem(this.mViewPager.getCurrentItem());
        // The refreshing animation is dismissed once we have finished
        this.mSwipeRefresh.setRefreshing(false);
    }

    // This 'FragmentPageAdapter' instance will be used with the 'ViewPager' object
    private class MyPagerAdapter extends FragmentPagerAdapter
    {
        private static final int NUMBER_OF_TABS = 7;

        private SwipeRefreshLayout mSwipeRefreshLayout;

        public MyPagerAdapter(FragmentManager mFragManager, SwipeRefreshLayout aSwipeRefreshLayout)
        {
            super(mFragManager);
            this.mSwipeRefreshLayout = aSwipeRefreshLayout;
        }

        @Override
        public Fragment getItem(final int position)
        {
            switch (position)
            {
                case 0:
                    return retrieveFeedCategoryFragment(getResources().getString(categoryArray[0]));
                case 1:
                    return retrieveFeedCategoryFragment(getResources().getString(categoryArray[1]));
                case 2:
                    return retrieveFeedCategoryFragment(getResources().getString(categoryArray[2]));
                case 3:
                    return retrieveFeedCategoryFragment(getResources().getString(categoryArray[3]));
                case 4:
                    return retrieveFeedCategoryFragment(getResources().getString(categoryArray[4]));
                case 5:
                    return retrieveFeedCategoryFragment(getResources().getString(categoryArray[5]));
                case 6:
                    return retrieveFeedCategoryFragment(getResources().getString(categoryArray[6]));
                default:
                    return new Fragment();   // If no case is reached, an "empty" 'Fragment' object is returned
            }
        }

        @Override
        public int getCount() { return MyPagerAdapter.NUMBER_OF_TABS; }

        @Override
        public CharSequence getPageTitle(int position) { return getResources().getString(MainActivity.categoryArray[position]); }

        // This method queries the feed (according to the 'aCateogry' section) and returns a 'Fragment' object to be published
        private Fragment retrieveFeedCategoryFragment(String aCategory)
        {
            List<FeedItem> mFeedItemList;

            try
            {
                FeedParser mParser = new FeedParser(mDrawerLayout.getContext());

                // Querying 'La Tahona' feed for 'aCategory' section
                mFeedItemList = mParser.execute(MainActivity.LA_TAHONA_FEED_URL + aCategory + "/feed/").get();

                if (mFeedItemList != null)
                {
                    Bundle mBundle = new Bundle();
                        mBundle.putParcelableArrayList(getResources().getResourceName(R.string.feed_data_name), (ArrayList<? extends Parcelable>) mFeedItemList);

                    Fragment mFragment = new CardFragment();
                        mFragment.setArguments(mBundle);

                    Log.i(MainActivity.TAG_MAIN_ACTIVITY, "Feed from " + aCategory + " category loaded");
                    return mFragment;
                }
            }
            catch (InterruptedException | ExecutionException e) { e.printStackTrace(); }

            // If the feed has not been successfully queried, a 'dummy' Fragment object is returned
            return new Fragment();
        }
    }
}
