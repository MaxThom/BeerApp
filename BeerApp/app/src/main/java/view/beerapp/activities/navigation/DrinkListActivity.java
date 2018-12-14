package view.beerapp.activities.navigation;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import view.beerapp.R;
import view.beerapp.activities.location_drink.AddDrinkActivity;
import view.beerapp.activities.location_drink.DrinkFragment;
import view.beerapp.entities.Drink;
import view.beerapp.repository.FirebaseDrinkRepository;

/**
 * Class displaying all the drinks in the application database
 */
public class DrinkListActivity extends BaseActivity {
    static final int REQUEST_ADD_DRINK = 2;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;

    /**
     * Starting point of the activity.
     * @param savedInstanceState saved state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beers);
        onCreateDrawer();

        (findViewById(R.id.btnAddDrink)).bringToFront();

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(3);
        setupViewPager(viewPager, FirebaseDrinkRepository.getInstance().getDrinks());

        tabLayout = (TabLayout) findViewById(R.id.tabsDrink);
        tabLayout.setupWithViewPager(viewPager);
    }

    /**
     * When retourning from the add drink activity, refresh the list of drinks
     * @param requestCode which activity its returning
     * @param resultCode the result
     * @param data extras
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ADD_DRINK && resultCode == RESULT_OK) {
           adapter.refreshLists(FirebaseDrinkRepository.getInstance().getDrinks());
        }
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
     * Click listener for the add button. Start the add drink activity
     * @param view the view
     */
    public void onAddButtonClick(View view) {
        Intent addBeerIntent = new Intent(getBaseContext(), AddDrinkActivity.class);
        startActivityForResult(addBeerIntent, REQUEST_ADD_DRINK);
    }

    /**
     * Setup the view pager with a list of drinks
     * @param viewPager the viewpager
     * @param drinks the drinks
     */
    private void setupViewPager(ViewPager viewPager, ArrayList<Drink> drinks) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());

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
        bundleBeer.putString("DRINK_TYPE","Beers");
        bundleBeer.putParcelableArrayList("DRINK_LIST", beers);
        DrinkFragment beer = new DrinkFragment();
        beer.setArguments(bundleBeer);

        Bundle bundleWine = new Bundle();
        bundleWine.putString("DRINK_TYPE","Wines");
        bundleWine.putParcelableArrayList("DRINK_LIST", wines);
        DrinkFragment wine = new DrinkFragment();
        wine.setArguments(bundleWine);

        Bundle bundleCocktail = new Bundle();
        bundleCocktail.putString("DRINK_TYPE","Beers");
        bundleCocktail.putParcelableArrayList("DRINK_LIST", cocktails);
        DrinkFragment cocktail = new DrinkFragment();
        cocktail.setArguments(bundleCocktail);


        adapter.addFragment(beer, getString(R.string.tab_list_beer));
        adapter.addFragment(wine, getString(R.string.tab_list_wine));
        adapter.addFragment(cocktail, getString(R.string.tab_list_cocktail));

        viewPager.setAdapter(adapter);
    }

    /**
     * The view pager adapter for the fragments
     */
    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<DrinkFragment> mFragmentList = new ArrayList<>();
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
         * ADd a fragment to the tab layout
         * @param fragment the fragment to be added
         * @param title the title of the fragment
         */
        void addFragment(DrinkFragment fragment, String title) {
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
         * Refresh List of drinks when coming back to the activity
         *
         * @param drinks list of drinks to be refreshed
         */
        void refreshLists(List<Drink> drinks) {
            ArrayList<Drink> beers = new ArrayList<>();
            ArrayList<Drink> wines = new ArrayList<>();
            ArrayList<Drink> cocktails = new ArrayList<>();

            for (Drink dr: drinks) {
                if (dr.getBeverageType() != null) {
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
            }

            mFragmentList.get(0).setListViewItems(beers);
            mFragmentList.get(1).setListViewItems(wines);
            mFragmentList.get(2).setListViewItems(cocktails);
        }
    }
}
