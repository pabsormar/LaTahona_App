package org.deafsapps.latahona.activities;

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
                                                                    TabLayout.OnTabSelectedListener
{
    private static final String TAG_MAIN_ACTIVITY = "In-MainActivity";
    private static final String LA_TAHONA_FEED_URL = "http://www.revistalatahona.com/feed/";

    private CoordinatorLayout mCoordLayout;
    private DrawerLayout mDrawerLayout;
    private ViewPager mViewPager;
    private ArrayList<FeedItem> mFeedItemList;
    private int[] categoryArray = {R.string.feed_category_inicio, R.string.feed_category_actualidad, R.string.feed_category_formacion,
                                   R.string.feed_category_revistas_publicadas, R.string.feed_category_recetas, R.string.feed_category_nacional,
                                   R.string.feed_category_internacional, R.string.feed_category_asociaciones, R.string.feed_category_ferias};

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
        NavigationView mNavigationView = (NavigationView) this.findViewById(R.id.appNavView);
        // This next line makes 'NavigationView' items react to interaction (defined in 'onNavigationItemSelected' method)
        mNavigationView.setNavigationItemSelectedListener(this);
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

        //----- LOADING FEED -----
        // The first parameter refers to the 'Context', the second one to the 'OnFeedListener' interface
        FeedParser mParser = new FeedParser(this);
        try {
            this.mFeedItemList = mParser.execute(MainActivity.LA_TAHONA_FEED_URL).get();
        } catch (InterruptedException | ExecutionException e) { e.printStackTrace(); }
        //--------------------------------------

        //----- VIEWPAGER -----
        // A 'ViewPager' object allows to include swipe gesture to move across pages or fragments
        this.mViewPager = (ViewPager) this.findViewById(R.id.appViewPager);
        MyPageAdapter mPageAdapter = new MyPageAdapter(this.getSupportFragmentManager());

        // If the feed has been properly loaded, a 'Fragment' object is added to the 'PageAdapter'
        if (this.mFeedItemList != null && !this.mFeedItemList.isEmpty())
        {
            // Showing different 'CardFragment' objects according to feed 'category' fields
            ArrayList<FeedItem> categoryList;
            for (int aCategory : this.categoryArray)
            {
                categoryList = (ArrayList<FeedItem>) getCategoryList(aCategory);

                if (!categoryList.isEmpty())
                {
                    Bundle mBundle = new Bundle();
                    mBundle.putParcelableArrayList(this.getResources().getResourceName(R.string.feed_data_name), categoryList);

                    Fragment mFragment = new CardFragment();
                    mFragment.setArguments(mBundle);

                    mPageAdapter.addFragment(mFragment, this.getResources().getString(aCategory));
                }
            }
        }

        this.mViewPager.setAdapter(mPageAdapter);
        //--------------------------------------

        //----- TABLAYOUT -----
        // Getting a reference to the 'TabLayout' to add 'Tab' elements through a 'ViewPager' object
        final TabLayout appTabLayout = (TabLayout) this.findViewById(R.id.appTabLayout);
        // 'setupWithViewPager' requires to override the 'getPageTitle' method from the 'FragmentPageAdapter' class
        // The return value of the latter must be a List or array with the titles of the distinct tabs
        appTabLayout.setupWithViewPager(this.mViewPager);
        //--------------------------------------

        //----- FAB -----
        final FloatingActionButton mFAB = (FloatingActionButton) this.findViewById(R.id.appFAB);
            mFAB.setOnClickListener(this);
        //--------------------------------------
    }

    private List<FeedItem> getCategoryList(int feedCategory)
    { Log.i(MainActivity.TAG_MAIN_ACTIVITY, "feedCategory: " + this.getResources().getString(feedCategory));
        List<FeedItem> mList = new ArrayList<>();

        for (FeedItem mItem : this.mFeedItemList)
        {
            for (String aString : mItem.getItemCategory())
            {
                // If 'feedCategory' value is found among the categories of the 'FeedItem' object, the latter is added to the 'List'
                if (aString.equals(this.getResources().getString(feedCategory))) { mList.add(mItem); }
            }
        }
Log.i(MainActivity.TAG_MAIN_ACTIVITY, "List size: " + String.valueOf(mList.size()));
        return mList;
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
            this.mDrawerLayout.openDrawer(GravityCompat.START);
        }
        else
            return false;

        return true;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {
        // Item is checked
        item.setChecked(true);

        // Closing drawer on item click
        this.mDrawerLayout.closeDrawers();
        return true;
    }

    // The following methods correspond to 'TabLayout.OnTabSelectedListener'
    @Override
    public void onTabSelected(TabLayout.Tab tab) { this.mViewPager.setCurrentItem(tab.getPosition()); }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) { }

    @Override
    public void onTabReselected(TabLayout.Tab tab) { }

    // This 'FragmentPageAdapter' instance will be used with the 'ViewPager' object
    private class MyPageAdapter extends FragmentPagerAdapter
    {
        //private FragmentManager mManager;
        private List<Fragment> mFragmentList = new ArrayList<>();
        private List<String> mFragmentTitleList = new ArrayList<>();

        public MyPageAdapter(FragmentManager mFragManager) { super(mFragManager); }

        public void addFragment(Fragment aFragment, String aFragTitle)
        {
            this.mFragmentList.add(aFragment);
            this.mFragmentTitleList.add(aFragTitle);
        }

        @Override
        public Fragment getItem(int position) { return this.mFragmentList.get(position); }

        public String getFragTitle(int position) { return this.mFragmentTitleList.get(position); }

        @Override
        public int getCount() { return this.mFragmentList.size(); }

        @Override
        public CharSequence getPageTitle(int position) { return this.mFragmentTitleList.get(position); }

        public List<Fragment> getFragmentList() { return mFragmentList; }

        public List<String> getFragmentTitleList() { return mFragmentTitleList; }
    }
}
