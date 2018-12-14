package view.beerapp.activities.users;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import view.beerapp.R;
import view.beerapp.activities.location_drink.DrinkFragment;
import view.beerapp.entities.Drink;
import view.beerapp.entities.User;
import view.beerapp.repository.FirebaseDrinkRepository;
import view.beerapp.repository.FirebaseUserRepository;

/**
 * FriendProfile Activity base on a user profile.
 * When the current user select a person from Friend Activity, transferred here
 * to see personals information of the selected user like name, email and favorite drink list
 */
public class FriendProfile extends AppCompatActivity {
    private TextView firstName,email, phone, address;
    private Button add_friend;
    private User user, currentUser;

    private TabLayout tabLayout;
    private ViewPager viewPager;

    /**
     * OnCreate, starting point of the activity
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_profile);

        //Initialize .xml objects
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);

        firstName = (TextView) findViewById(R.id.firstName_text);
        email = (TextView) findViewById(R.id.email_text);
        phone = (TextView) findViewById(R.id.phone_text);
        address = (TextView) findViewById(R.id.address_text);
        add_friend = (Button) findViewById(R.id.add_friend);

        currentUser = FirebaseUserRepository.getInstance().getCurrentUser();

        //Get selected user
        Bundle data = getIntent().getExtras();
        user = FirebaseUserRepository.getInstance().getUser(data.getString("USER"));

        for (int i = 0 ; i < user.friends.size(); i++ ) {
            if (user.friends.get(i).idFb == null) {
                user.friends.set(i, FirebaseUserRepository.getInstance().getUser(user.friends.get(i).idFb));
            }
        }

        //Complete the fields to show the personal data of selected user, if they are exist
        if (user.getFirstName() != null && user.getLastName() != null) {
            firstName.setText(user.getFirstName() + " " + user.getLastName());
        }

        email.setText(user.getEmail());

        if (user.getPhone() != null) {
            phone.setText(user.getPhone());
        }
        if (user.getAddress() != null) {
            address.setText(user.getAddress());
        }

        //If the selected person is already friend, current user can only delete him/her
        if(currentUser.isUserFriends(user)){
            // User is not your friend
            add_friend.setText(getString(R.string.friend_profile_delete_friend));
        }

        initializeTabsLayout();
    }

    /**
     * Corresponding to current user choice, selected person added or removed from user's friend list
     * @param view
     */
    public void onChangeFriendStatus(View view) {
        if(!currentUser.isUserFriends(user)){
            // User is not your friend
            currentUser.friends.add(user);

            Toast.makeText(getApplicationContext(), getString(R.string.friend_profile_added),Toast.LENGTH_LONG).show();
            add_friend.setText(getString(R.string.friend_profile_delete_friend));
        }
        else {
            // User is your friend
            currentUser.deleteFriend(user);

            Toast.makeText(getApplicationContext(), getString(R.string.friend_profile_removed),Toast.LENGTH_LONG).show();
            add_friend.setText(getString(R.string.friend_profile_add_friend));
        }

        //Update current user info
        FirebaseUserRepository.getInstance().updateUser(currentUser.idFb, currentUser);
    }

    /**
     * Initialize View Pager and TabLayout according to selected user favorite
     * drink list
     */
    private void initializeTabsLayout() {
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        ViewGroup.LayoutParams params= viewPager.getLayoutParams();

        ArrayList<Drink> drinks = user.getFavoriteDrinks();
        for (int i = 0; i < drinks.size(); i++) {
            if (drinks.get(i).getName() == null) {
                drinks.set(i, FirebaseDrinkRepository.getInstance().getDrink(drinks.get(i).getIdFb()));
            }
        }

        params.height = drinks.size() == 0 ? 100 : drinks.size() * 260;
        viewPager.setLayoutParams(params);

        setupViewPager(viewPager, drinks);

        tabLayout = (TabLayout) findViewById(R.id.tabsDrink);
        tabLayout.setupWithViewPager(viewPager);

        final ScrollView main = (ScrollView) findViewById(R.id.scrollView);
        main.post(new Runnable() {
            public void run() {
                main.scrollTo(0,0);
            }
        });
    }

    /**
     * Add at the TabLayout, based on selected user favorite drink list, his/her favority drinks
     * wines and cocktails.
     *
     * @param viewPager
     * @param drinks , favorite drink list of selected user
     */
    private void setupViewPager(ViewPager viewPager, ArrayList<Drink> drinks) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        ArrayList<Drink> beers = new ArrayList<>();
        ArrayList<Drink> wines = new ArrayList<>();
        ArrayList<Drink> cocktails = new ArrayList<>();

        //According to the Type of the drink, is added to the corresponding list
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

        //Beers Tab
        Bundle bundleBeer = new Bundle();
        bundleBeer.putString("DRINK_TYPE","Beers");
        bundleBeer.putParcelableArrayList("DRINK_LIST", (ArrayList<? extends Parcelable>) beers);
        DrinkFragment beer = new DrinkFragment();
        beer.setArguments(bundleBeer);

        //Wines Tab
        Bundle bundleWine = new Bundle();
        bundleWine.putString("DRINK_TYPE","Wines");
        bundleWine.putParcelableArrayList("DRINK_LIST", (ArrayList<? extends Parcelable>) wines);
        DrinkFragment wine = new DrinkFragment();
        wine.setArguments(bundleWine);

        //Cocktails Tab
        Bundle bundleSweet = new Bundle();
        bundleSweet.putString("DRINK_TYPE","Cocktails");
        bundleSweet.putParcelableArrayList("DRINK_LIST", (ArrayList<? extends Parcelable>) cocktails);
        DrinkFragment sweetDrink = new DrinkFragment();
        sweetDrink.setArguments(bundleSweet);

        adapter.addFragment(beer, getString(R.string.tab_list_beer));
        adapter.addFragment(wine, getString(R.string.tab_list_wine));
        adapter.addFragment(sweetDrink, getString(R.string.tab_list_cocktail));

        viewPager.setAdapter(adapter);
    }

    /**
     * Class for the view page adapter holding the fragment list with the tabs
     */
    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        /**
         * Constructor
         * @param manager
         */
        ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        /**
         * Get the fragment at the index position
         * @param position index
         * @return the fragment
         */
        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        /**
         * Get the number of fragment
         * @return the number of fragments
         */
        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        /**
         * Add a new fragment to the list with his title
         * @param fragment the fragment
         * @param title the title of the frament
         */
        void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        /**
         * Get the title of a specific fragment
         * @param position index of the fragment to get
         * @return the title of the fragment
         */
        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

}
