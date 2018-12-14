package view.beerapp.activities.location_drink;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import view.beerapp.R;
import view.beerapp.entities.Drink;
import view.beerapp.entities.Location;
import view.beerapp.repository.FirebaseDrinkRepository;
import view.beerapp.repository.FirebaseLocationRepository;

/**
 * The activity in which the user can search existing drinks to add them to a location or decide
 * to create one himself.
 */
public class AddExistingDrinkActivity extends AppCompatActivity {

    static final int REQUEST_ADD_DRINK = 2;

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private AddExistingDrinkActivity.ViewPagerAdapter adapter;
    private String locIdFb;

    /**
     * Starting point of the activity.
     * @param savedInstanceState saved state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_existing_beer);

        //Setup the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);

        (findViewById(R.id.btnAddNewDrink)).bringToFront();
        Bundle data = getIntent().getExtras();
        locIdFb = data.getString("LOCATION");

        initialiseSearchView();

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        Location loc = FirebaseLocationRepository.getInstance().getLocation(locIdFb);

        //Fetches all the drinks on the firebase repo
        ArrayList<Drink> drinksToDisplay = new ArrayList<>();
        for (Drink drAll : FirebaseDrinkRepository.getInstance().getDrinks()) {
            boolean isAlreadyThere = false;
            for (Drink drLoc : loc.getDrinks()) {
                if (drLoc.getName().equals(drAll.getName())) {
                    isAlreadyThere = true;
                    break;
                }
            }

            if (!isAlreadyThere) {
                drinksToDisplay.add(drAll);
            }
        }

        //Setup of the tablayout viewpager
        setupViewPager(viewPager, drinksToDisplay, "");
        tabLayout = (TabLayout) findViewById(R.id.location_tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    /**
     * Initializes the search bar to be able to search in all the drinks
     */
    private void initialiseSearchView() {
        SearchView sv = (SearchView) findViewById(R.id.searchText);

        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            //Searches when text is submitted
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.setSearchFilter(query);
                return true;
            }

            //Searches dynamically when text changes
            @Override
            public boolean onQueryTextChange(String query) {
                adapter.setSearchFilter(query);

                return false;
            }
        });
    }

    /**
     * Launches the activity to create a new drink when the add button is clicked
     * @param view
     */
    public void onAddButtonClick(View view) {
        Intent addBeerIntent = new Intent(getBaseContext(), AddDrinkActivity.class);
        addBeerIntent.putExtra("LOCATION", locIdFb);
        startActivityForResult(addBeerIntent, REQUEST_ADD_DRINK);
    }

    /**
     * Adds the drink(s) to the location when the confirm button is clicked
     * @param view
     */
    public void addDrinkClick(View view) {
        ArrayList<Drink> newDrinks = adapter.getNewDrinks();
        Location loc = FirebaseLocationRepository.getInstance().getLocation(locIdFb);

        for (Drink dr : newDrinks) {

            loc.getDrinks().add(dr);
            dr.getLocations().add(loc);

            FirebaseDrinkRepository.getInstance().updateDrink(dr.getIdFb(), dr);
            FirebaseLocationRepository.getInstance().updateLocation(loc.getIdFb(), loc);
        }
        if (newDrinks.size() > 0)
            Toast.makeText(getApplicationContext(), getString(R.string.drink_added), Toast.LENGTH_SHORT).show();

        setResult(RESULT_OK);
        finish();
    }

    /**
     * Activity result when a drink has been created successfully
     * @param requestCode requestCode
     * @param resultCode resultCode
     * @param data data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ADD_DRINK && resultCode == RESULT_OK) {
            setResult(RESULT_OK);
            finish();
        }
    }

    /**
     * Sets up the view pager by fetching all the drinks and sorting them by types to put them
     * in the appropriate tab.
     * @param viewPager viewPager
     * @param drinks drink list
     * @param searchText searchText
     */
    private void setupViewPager(ViewPager viewPager, ArrayList<Drink> drinks, String searchText) {

        adapter = new AddExistingDrinkActivity.ViewPagerAdapter(getSupportFragmentManager());
        ArrayList<Drink> beers = new ArrayList<>();
        ArrayList<Drink> wines = new ArrayList<>();
        ArrayList<Drink> cocktails = new ArrayList<>();

        for (Drink dr: drinks) {
            switch (dr.getBeverageType()) {
                case "Beer":
                    beers.add(dr);
                    break;
                case "Wine":
                    wines.add(dr);
                    break;
                case "Cocktail":
                    cocktails.add(dr);
                    break;
            }
        }

        Bundle bundleBeer = new Bundle();
        bundleBeer.putString("LOCATION", locIdFb);
        bundleBeer.putString("DRINK_TYPE","Beers");
        bundleBeer.putParcelableArrayList("DRINK_LIST", beers);
        DrinkSelectFragment beer = new DrinkSelectFragment();
        beer.setArguments(bundleBeer);

        Bundle bundleWine = new Bundle();
        bundleWine.putString("LOCATION", locIdFb);
        bundleWine.putString("DRINK_TYPE","Wines");
        bundleWine.putParcelableArrayList("DRINK_LIST", wines);
        DrinkSelectFragment wine = new DrinkSelectFragment();
        wine.setArguments(bundleWine);

        Bundle bundleCocktail = new Bundle();
        bundleCocktail.putString("LOCATION", locIdFb);
        bundleCocktail.putString("DRINK_TYPE","Beers");
        bundleCocktail.putParcelableArrayList("DRINK_LIST", cocktails);
        DrinkSelectFragment cocktail = new DrinkSelectFragment();
        cocktail.setArguments(bundleCocktail);

        adapter.addFragment(beer, getString(R.string.tab_list_beer));
        adapter.addFragment(wine, getString(R.string.tab_list_wine));
        adapter.addFragment(cocktail, getString(R.string.tab_list_cocktail));

        viewPager.setAdapter(adapter);
    }

    /**
     * View pager adapter to be able to show all the items in tabs
     */
    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<DrinkSelectFragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();
        private String filter = "";

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
            mFragmentList.get(position).setListViewItems(filter);
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
        private void addFragment(DrinkSelectFragment fragment, String title) {
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

        /**
         * Refreshes the drinks list according to the search filter
         * @param txtFilter the search text
         */
        void setSearchFilter(String txtFilter) {
            this.filter = txtFilter;
            for (DrinkSelectFragment fragment : mFragmentList) {
                fragment.setListViewItems(filter);
            }
        }

        /**
         * Gets the drinks selected by the user to be added to the location
         * @return The selected drinks
         */
        private ArrayList<Drink> getNewDrinks() {
            ArrayList<Drink> newDrinks = new ArrayList<>();
            for (DrinkSelectFragment fragment : mFragmentList) {
                if (fragment.drinkSelected != null) {
                    for (Map.Entry<String, Boolean> entry : fragment.drinkSelected.entrySet()) {
                        if (entry.getValue())
                            newDrinks.add(FirebaseDrinkRepository.getInstance().getDrink(entry.getKey()));
                    }
                }
            }

            return newDrinks;
        }
    }
}
