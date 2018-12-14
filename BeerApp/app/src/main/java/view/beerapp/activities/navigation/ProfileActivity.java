package view.beerapp.activities.navigation;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import view.beerapp.R;
import view.beerapp.activities.hardware.DefaultCameraActivity;
import view.beerapp.activities.location_drink.DrinkFragment;
import view.beerapp.entities.Drink;
import view.beerapp.entities.User;
import view.beerapp.repository.FirebaseDrinkRepository;
import view.beerapp.repository.FirebaseUserRepository;

/**
 * ProfileActivity class represent the profile of the user, who can edit his/her personal information,
 * see and edit his/her favorite drink list
 */
public class ProfileActivity extends BaseActivity {
    static final int REQUEST_IMAGE_CAPTURE = 1;

    private EditText profile_firstName,profile_lastName, profile_address, profile_phone;
    private TextView profile_email,textFirstName,textLastName,textPhone,textAddress;
    private Button profile_save;

    private User currentUser = FirebaseUserRepository.getInstance().getCurrentUser();
    private Toolbar toolbar;
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
        setContentView(R.layout.activity_profile);
        super.onCreateDrawer();

        //Initialize .xml objects
        profile_email = (TextView) findViewById(R.id.profile_email);
        textFirstName = (TextView) findViewById(R.id.textFirstName);
        profile_firstName = (EditText) findViewById(R.id.profile_first);
        textLastName = (TextView) findViewById(R.id.textLastName);
        profile_lastName = (EditText) findViewById(R.id.profile_last);
        textPhone = (TextView) findViewById(R.id.textPhone);
        profile_phone = (EditText)findViewById(R.id.profile_phone);
        textAddress = (TextView) findViewById(R.id.textAddress);
        profile_address = (EditText)findViewById(R.id.profile_address);
        profile_save = (Button)findViewById(R.id.profile_save);

        //Sets up the toolbar
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //If a current user exists
        if (currentUser != null) {
            //Set user's email address, without can edit it
            profile_email.setText(currentUser.getEmail());

            profile_firstName.setText(currentUser.getFirstName());
            profile_lastName.setText(currentUser.getLastName());
            profile_phone.setText(currentUser.getPhone());
            profile_address.setText(currentUser.getAddress());

            //Save button listener
            profile_save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Get data from Edit text
                    String first = profile_firstName.getText().toString();
                    String last = profile_lastName.getText().toString();
                    String phone = profile_phone.getText().toString();
                    String address = profile_address.getText().toString();

                    //If the user didn't fill anything
                    if (phone.matches("") && address.matches("") && first.matches("") && last.matches("")) {
                        Toast.makeText(getApplicationContext(), getString(R.string.dialog_missing_field), Toast.LENGTH_SHORT).show();

                    } else { //All the data that user has completed is stored and information is up to date
                        currentUser.setFirstName(first);
                        currentUser.setLastName(last);
                        currentUser.setPhone(phone);
                        currentUser.setAddress(address);
                        FirebaseUserRepository.getInstance().updateUser(currentUser.idFb, currentUser);

                        Toast.makeText(getApplicationContext(), getString(R.string.toast_saved), Toast.LENGTH_LONG).show();
                        }
                    }
                });

            setInitialPicture();
            initializeTabsLayout();

        }
    }

    /**
     * Sets the picture if a picture was taken with the camera or updates the favorites list if
     * the user changed favorite(s) drink(s)
     * @param requestCode request code
     * @param resultCode result code
     * @param data intent data
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            String picture = data.getStringExtra("PICTURE");
            SharedPreferences sharedPref = this.getSharedPreferences("LOCATION_PICTURE", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(currentUser.idFb, picture);
            editor.apply();

            setPic(picture);
            final ScrollView main = (ScrollView) findViewById(R.id.scrollView);
            main.post(new Runnable() {
                public void run() {
                    main.scrollTo(0,0);
                }
            });
        }

        //Updates the drinks list if the user changed his favorites and came back
        else {
            ArrayList<Drink> drinks = currentUser.getFavoriteDrinks();
            for (int i = 0; i < drinks.size(); i++) {
                if (drinks.get(i).getName() == null) {
                    drinks.set(i, FirebaseDrinkRepository.getInstance().getDrink(drinks.get(i).getIdFb()));
                }
            }
            adapter.refreshLists(drinks);
        }
    }

    /**
     * Will use the parent method to open the navigation drawer menu
     * @param item menu item
     * @return the selected item
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    /**
     * Sets an already taken picture (if existing on the device) to the user icon
     */
    private void setInitialPicture() {
        SharedPreferences sharedPref = this.getSharedPreferences("LOCATION_PICTURE", Context.MODE_PRIVATE);
        String picPath = sharedPref.getString(currentUser.idFb, "");
        if (!picPath.equals(""))
            setPic(picPath);
    }

    /**
     * Starts the camera activity to take a picture for the user avatar
     * @param view view
     */
    public void takePicture(View view) {
        Intent intent = new Intent(this, DefaultCameraActivity.class);
        intent.putExtra("LOCATION", currentUser.idFb);
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
    }

    /**
     * Sets the user's picture to one taken with the camera
     * @param picturePath local path to the picture
     */
    private void setPic(String picturePath) {
        ImageView mImageView = findViewById(R.id.imageView);
        // Get the dimensions of the View
        int targetW = Resources.getSystem().getDisplayMetrics().widthPixels;
        int targetH = 150;

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(picturePath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(picturePath, bmOptions);
        mImageView.setImageBitmap(bitmap);
    }


    /**
     * Initialize tabs and view pager according to the size of the favorite drink list
     */
    private void initializeTabsLayout() {
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        ViewGroup.LayoutParams params= viewPager.getLayoutParams();

        //Gets the users favorites drinks
        ArrayList<Drink> drinks = currentUser.getFavoriteDrinks();
        for (int i = 0; i < drinks.size(); i++) {
            if (drinks.get(i).getName() == null) {
                drinks.set(i, FirebaseDrinkRepository.getInstance().getDrink(drinks.get(i).getIdFb()));
            }
        }
        params.height = drinks.size() == 0 ? 100 : drinks.size() * 260;
        viewPager.setLayoutParams(params);
        viewPager.setOffscreenPageLimit(3);
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
     * Add at the tabs of TabLayout, based on the favorite list of user, favorite beers
     * wines and cocktails
     *
     * @param viewPager viewpager
     * @param drinks , list of user's favorite list
     */
    private void setupViewPager(ViewPager viewPager, ArrayList<Drink> drinks) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());

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

        //Wines tab
        Bundle bundleWine = new Bundle();
        bundleWine.putString("DRINK_TYPE","Wines");
        bundleWine.putParcelableArrayList("DRINK_LIST", (ArrayList<? extends Parcelable>) wines);
        DrinkFragment wine = new DrinkFragment();
        wine.setArguments(bundleWine);

        //Cocktails tab
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
     * View pager adapter to show drinks in tabs and lists
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
         * Add a fragment to the tab layout
         * @param fragment the fragment to be added
         * @param title the title of the fragment
         */
        private void addFragment(DrinkFragment fragment, String title) {
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
         * Refresh List used whenever a user delete/add a drink and the list must be changed
         * and refreshed.
         *
         * @param drinks new favorite user's drink list
         */
        private void refreshLists(List<Drink> drinks) {
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
            int size = beers.size() > wines.size() ? beers.size() > cocktails.size() ? beers.size() : cocktails.size() : wines.size() > cocktails.size() ? wines.size() : cocktails.size();

            ViewGroup.LayoutParams params = viewPager.getLayoutParams();
            params.height = size == 0 ? 100 : size * 260;
            viewPager.setLayoutParams(params);
            final ScrollView main = (ScrollView) findViewById(R.id.scrollView);
            main.post(new Runnable() {
                public void run() {
                    main.scrollTo(0,0);
                }
            });

            mFragmentList.get(0).setListViewItems(beers);
            mFragmentList.get(1).setListViewItems(wines);
            mFragmentList.get(2).setListViewItems(cocktails);
        }
    }

}
