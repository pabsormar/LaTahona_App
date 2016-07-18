package org.deafsapps.latahona.activities;

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
import org.deafsapps.latahona.util.FeedParser;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener,
                                                                    TabLayout.OnTabSelectedListener, FeedParser.OnAsyncResponse
{
    private static final String TAG_MAIN_ACTIVITY = "In-MainActivity";
    private static final String LA_TAHONA_FEED_URL = "http://www.revistalatahona.com/category/";

    private CoordinatorLayout mCoordLayout;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
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
        if (this.mNavigationView != null)
            this.mNavigationView.getMenu().getItem(0).setChecked(true);
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
        final PagerAdapter mPagerAdapter = new MyPagerAdapter(this.getSupportFragmentManager());
            this.mViewPager.setAdapter(mPagerAdapter);
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
            Log.i(MainActivity.TAG_MAIN_ACTIVITY, "Refresh option button tapped");
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
    public void onResponse(Fragment updatedFragment, int fragmentPosition)
    {
        if (updatedFragment != null)
        {
            Log.i(MainActivity.TAG_MAIN_ACTIVITY, "At AsyncTask 'onResponse', list NOT NULL");

            this.updatePagerFragment(fragmentPosition, updatedFragment);
        }
        else
            Log.w(MainActivity.TAG_MAIN_ACTIVITY, "At AsyncTask 'onResponse', list NULL");
    }

    private void updatePagerFragment(int fragmentPosition, Fragment updatedFragment)
    {
        Log.i(MainActivity.TAG_MAIN_ACTIVITY, "'updatePagerFragment'");

        ((MyPagerAdapter) this.mViewPager.getAdapter()).getmFragmentList().put(fragmentPosition, updatedFragment);
    }

    // This 'FragmentStatePageAdapter' instance will be used with the 'ViewPager' object
    // It makes the system not to save every single page, but the current, the one right after, and the one right before (difference with 'FragmentPagerAdapter'
    // That means that 'getItem' is called rather often, so it is required to program the logic which makes the loading happen
    private class MyPagerAdapter extends FragmentStatePagerAdapter
    {
        private static final String TAG_MY_PAGER_ADAPTER = "In-MyPagerAdapter";
        private static final int NUMBER_OF_TABS = 7;

        private int[] categoryArray = {R.string.feed_category_actualidad, R.string.feed_category_formacion, R.string.feed_category_recetas, R.string.feed_category_nacional,
                R.string.feed_category_internacional, R.string.feed_category_asociaciones, R.string.feed_category_ferias};
        // This next 'SparseArray' will store the 'Fragment' objects already downloaded from the Internet, so that they can be loaded locally later
        private SparseArrayCompat<Fragment> mRegisteredFragments;

        public MyPagerAdapter(FragmentManager mManager)
        {
            super(mManager);
            this.mRegisteredFragments = new SparseArrayCompat<>();

            Log.i(MyPagerAdapter.TAG_MY_PAGER_ADAPTER, "'MyPagerAdapter' instance created");
        }

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

                // The information is queried from the Internet
                FeedParser mParser = new FeedParser(MainActivity.this, this.getmFragmentList().get(position));
                    mParser.execute(MainActivity.LA_TAHONA_FEED_URL + getResources().getString(categoryArray[position]) + "/feed/", String.valueOf(position));
            }
            else
                Log.i(MyPagerAdapter.TAG_MY_PAGER_ADAPTER, "Loading from local");

                return this.getmFragmentList().get(position);
        }

        @Override
        public int getItemPosition(Object object)
        {
            return super.getItemPosition(object);
        }

        @Override
        public int getCount() { return MyPagerAdapter.NUMBER_OF_TABS; }

        @Override
        public CharSequence getPageTitle(int position) { return getResources().getString(this.categoryArray[position]); }

        public SparseArrayCompat<Fragment> getmFragmentList() { return this.mRegisteredFragments; }
    }
}
