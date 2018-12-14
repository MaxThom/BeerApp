package view.beerapp.activities.navigation;

import android.content.Intent;
import android.os.Parcelable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import view.beerapp.activities.location_drink.AddLocationActivity;
import view.beerapp.activities.location_drink.LocationFragment;
import view.beerapp.R;
import view.beerapp.entities.Location;
import view.beerapp.repository.FirebaseLocationRepository;

/**
 * List of all the locations in the application
 */
public class LocationsActivity extends BaseActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;

    /**
     * Starting point of the activity.
     * @param savedInstanceState saved state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locations);
        onCreateDrawer();
        viewPager = (ViewPager) findViewById(R.id.viewpager);

        setupAddLocationButton();
        setupViewPager(viewPager, FirebaseLocationRepository.getInstance().getLocations());

        tabLayout = (TabLayout) findViewById(R.id.tabsDrink);
        tabLayout.setupWithViewPager(viewPager);
    }

    /**
     * When a item is selected from the menu
     * @param item the selected item
     * @return true if item is good
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    /**
     * Set the click listener on the add location button
     */
    private void setupAddLocationButton(){
        final Button addLocation = findViewById(R.id.add_location);
        addLocation.bringToFront();
        addLocation.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            // Perform action on click
            Intent addLocationIntent = new Intent(getBaseContext(), AddLocationActivity.class);
            startActivity(addLocationIntent);
            }
        });
    }

    /**
     * Setup the tabs layout and the view pager with the list of locations
     * @param viewPager the viewpager holding the lists
     * @param locations all the data
     */
    private void setupViewPager(ViewPager viewPager, ArrayList<Location> locations) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        Bundle bundleBeer = new Bundle();
        bundleBeer.putString("LOCATION_TYPE","Bars");
        bundleBeer.putParcelableArrayList("LOCATION_LIST", (ArrayList<? extends Parcelable>) locations);
        LocationFragment bar = new LocationFragment();
        bar.setArguments(bundleBeer);

        adapter.addFragment(bar, getString(R.string.tab_list_bar));

        viewPager.setAdapter(adapter);
    }

    /**
     * The view pager adapter for the fragments
     */
    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        /**
         * Constructor of the pager
         * @param manager manager for the super class
         */
        ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        /**
         * Get the fragment at a position
         * @param position index
         * @return fragment
         */
        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        /**
         * Get the numbers of fragments
         * @return number of fragment
         */
        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        /**
         * Add a fragment to the tab layout
         * @param fragment the fragment to be added
         * @param title the title of the fragment
         */
        void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        /**
         * Get the title of the fragment
         * @param position index
         * @return the title
         */
        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}