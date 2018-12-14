package view.beerapp.activities.location_drink;

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
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import view.beerapp.R;
import view.beerapp.activities.hardware.DefaultCameraActivity;
import view.beerapp.activities.navigation.newsFeed.DashBoard;
import view.beerapp.activities.navigation.LocationsActivity;
import view.beerapp.activities.navigation.MapsActivity;
import view.beerapp.entities.Drink;
import view.beerapp.entities.Location;
import view.beerapp.repository.FirebaseDrinkRepository;
import view.beerapp.repository.FirebaseLocationRepository;

/**
 * Activity that shows a single location.
 */
public class LocationDisplayActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_ADD_DRINK = 2;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private Location loc;
    private String parent;

    private ViewPagerAdapter adapter;

    /**
     * Starting point of the activity.
     * @param savedInstanceState saved state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_display);

        //Sets up the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);

        // Get the Intent that started this activity and extract the string
        Bundle data = getIntent().getExtras();
        //SharedPreferences sharedPref = this.getSharedPreferences("LOC", Context.MODE_PRIVATE);
        String locId = "";
        if (data != null) {
            locId = data.getString("LOCATION");
            parent = data.getString("PARENT");
            //SharedPreferences.Editor editor = sharedPref.edit();
            //editor.putString("LOCID", locId);
            //editor.apply();

        } else {
            //locId = sharedPref.getString("LOCID", "");
        }
        if (locId != "") {
            loc = FirebaseLocationRepository.getInstance().getLocation(locId);
            for (int i = 0; i < loc.getDrinks().size(); i++) {
                if (loc.getDrinks().get(i).getName() == null) {
                    loc.getDrinks().set(i, FirebaseDrinkRepository.getInstance().getDrink(loc.getDrinks().get(i).getIdFb()));
                }
            }
            if (loc != null) initializeView(loc);
        }
    }

    /**
     * Result
     * @param requestCode request code
     * @param resultCode result code
     * @param data data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            String picture = data.getStringExtra("PICTURE");
            SharedPreferences sharedPref = this.getSharedPreferences("LOCATION_PICTURE", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(loc.getIdFb(), picture);
            editor.apply();

            setPic(picture);
            final ScrollView main = (ScrollView) findViewById(R.id.scrollView);
            main.post(new Runnable() {
                public void run() {
                    main.scrollTo(0,0);
                }
            });
        }
        else if (requestCode == REQUEST_ADD_DRINK && resultCode == RESULT_OK ) {
            loc = FirebaseLocationRepository.getInstance().getLocation(loc.getIdFb());
            adapter.refreshLists(loc.getDrinks());
        }
    }

    /**
     * Catch the previous button for lower API
     * @return intent of the previous activity
     */
    @Override
    public Intent getSupportParentActivityIntent() {
        return getParentActivityIntentImpl();
    }

    /**
     * Catch the previous button
     * @return intent of the previous activity
     */
    @Override
    public Intent getParentActivityIntent() {
        return getParentActivityIntentImpl();
    }

    /**
     * Return the good parent activity according to where this activity was called
     * @return the intent of the previous activity
     */
    private Intent getParentActivityIntentImpl() {
        Intent i = null;

        if (parent.equals("MAP")) {
            i = new Intent(this, MapsActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        } else if (parent.equals("DASHBOARD")) {
            i = new Intent(this, DashBoard.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        } else if (parent.equals("LOCATIONS")) {
            i = new Intent(this, LocationsActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        } else if (parent.equals("DRINK")) {
            i = new Intent(this, DrinkDisplayActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        } else {
            i = new Intent(this, DashBoard.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        }

        return i;
    }

    /**
     * Initialize the view (labels, images, etc) with the location data
     * @param loc
     */
    private void initializeView(Location loc) {
        TextView textView = findViewById(R.id.txtTitle);
        textView.setText(loc.getTitle());
        textView = findViewById(R.id.txtAlcoolLevel);
        textView.setText(loc.getFormattedAddress());
        textView = findViewById(R.id.txtRating);
        textView.setText(Double.toString(loc.getRating()) + " / 5.0");
        textView = findViewById(R.id.txtDescription);
        textView.setMovementMethod(new ScrollingMovementMethod());
        textView.setText(loc.getDescription());


        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager, loc);
        viewPager.setOffscreenPageLimit(3);

        tabLayout = (TabLayout) findViewById(R.id.tabsDrink);
        tabLayout.setupWithViewPager(viewPager);

        setupAddBeerButton();
        setInitialPicture();

        final ScrollView main = (ScrollView) findViewById(R.id.scrollView);
        main.post(new Runnable() {
            public void run() {
                main.scrollTo(0,0);
            }
        });
    }

    /**
     * Sets up the click listener to the add beer button
     */
    private void setupAddBeerButton(){
        final Button addBeer = findViewById(R.id.add_beer);
        addBeer.bringToFront();
        addBeer.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            Intent addBeerIntent = new Intent(getBaseContext(), AddExistingDrinkActivity.class);
            addBeerIntent.putExtra("LOCATION", loc.getIdFb());
            startActivityForResult(addBeerIntent, REQUEST_ADD_DRINK);
            }
        });
    }

    /**
     * Sets an already taken picture (if existing on the device) to the location image
     */
    private void setInitialPicture() {
        SharedPreferences sharedPref = this.getSharedPreferences("LOCATION_PICTURE", Context.MODE_PRIVATE);
        String picPath = sharedPref.getString(loc.getIdFb(), "");
        if (!picPath.equals(""))
            setPic(picPath);
    }

    /**
     * Starts the camera activity to take a picture for the drink
     * @param view view
     */
    public void takePicture(View view) {

        Intent intent = new Intent(this, DefaultCameraActivity.class);
        intent.putExtra("LOCATION", loc.getIdFb());
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
    }

    /**
     * Sets the drink picture to one taken with the camera
     * @param picturePath local path to the picture
     */
    private void setPic(String picturePath) {
        ImageView mImageView = findViewById(R.id.imageView);
        // Get the dimensions of the View
        int targetW = Resources.getSystem().getDisplayMetrics().widthPixels;;
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
     * Sets up the viewpager to be able to see drinks in tabs and lists
     * @param viewPager viewpager
     * @param loc location
     */
    private void setupViewPager(ViewPager viewPager, Location loc) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());

        ArrayList<Drink> beers = new ArrayList<>();
        ArrayList<Drink> wines = new ArrayList<>();
        ArrayList<Drink> cocktails = new ArrayList<>();

        for (Drink dr: loc.getDrinks()) {
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

        Bundle bundleBeer = new Bundle();
        bundleBeer.putString("DRINK_TYPE","Beers");
        bundleBeer.putParcelableArrayList("DRINK_LIST", (ArrayList<? extends Parcelable>) beers);
        DrinkFragment beer = new DrinkFragment();
        beer.setArguments(bundleBeer);

        Bundle bundleWine = new Bundle();
        bundleWine.putString("DRINK_TYPE","Wines");
        bundleWine.putParcelableArrayList("DRINK_LIST", (ArrayList<? extends Parcelable>) wines);
        DrinkFragment wine = new DrinkFragment();
        wine.setArguments(bundleWine);

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
     * View pager adapter to have drink fragments in the list
     */
    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<DrinkFragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        /**
         * Constructor of the pager
         * @param manager manager for the super class
         */
        private ViewPagerAdapter(FragmentManager manager) {
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
         * Refreshes the location's drinks list
         * @param drinks drink list
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
