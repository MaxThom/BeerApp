package view.beerapp.activities.location_drink;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcelable;
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
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import view.beerapp.R;
import view.beerapp.activities.hardware.DefaultCameraActivity;
import view.beerapp.entities.Drink;
import view.beerapp.entities.User;
import view.beerapp.repository.FirebaseDrinkRepository;
import view.beerapp.repository.FirebaseLocationRepository;
import view.beerapp.repository.FirebaseUserRepository;

/**
 * The activity used to show a single drink.
 */
public class DrinkDisplayActivity extends AppCompatActivity {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private Drink drink;
    private boolean isFavorite;
    private User currentUser;

    /**
     * Starting point of the activity.
     * @param savedInstanceState saved state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drink_display);

        isFavorite = false;

        //Sets up the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);

        //Gets the locations where this drink can be found
        Bundle data = getIntent().getExtras();
        drink = FirebaseDrinkRepository.getInstance().getDrink(data.getString("DRINK"));
        for (int i = 0 ; i < drink.getLocations().size() ; i++) {
            if (drink.getLocations().get(i).getFormattedAddress() == null) {
                drink.getLocations().set(i, FirebaseLocationRepository.getInstance().getLocation(drink.getLocations().get(i).getIdFb()));
            }
        }

        //Sets the drink attributes to the UI
        TextView txtTitle = findViewById(R.id.txtTitle);
        TextView txtAlcoolLevel = findViewById(R.id.txtAlcoolLevel);
        TextView txtRating = findViewById(R.id.txtRating);
        TextView txtDescription = findViewById(R.id.txtDescription);
        Button btnFavorite = findViewById(R.id.btnFavorite);
        txtTitle.setText(drink.getName());
        txtAlcoolLevel.setText(Double.toString(drink.getAlcoolLevel()) + " %");
        txtRating.setText(drink.getRating() + " / 5.0");
        txtDescription.setText(drink.getDescription());

        //Sets the favorite icon
        currentUser = FirebaseUserRepository.getInstance().getCurrentUser();
        if (currentUser.isFavoriteDrink(drink)) {
            isFavorite = true;
            btnFavorite.setBackgroundResource(R.drawable.favority_full);
        }

        //Set up for the viewpager to contain the locations to find the drink
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        ViewGroup.LayoutParams params= viewPager.getLayoutParams();
        params.height = drink.getLocations().size() * 260;
        viewPager.setLayoutParams(params);
        setupViewPager(viewPager, drink);

        tabLayout = (TabLayout) findViewById(R.id.tabsDrink);
        tabLayout.setupWithViewPager(viewPager);

        setInitialPicture();

        final ScrollView main = (ScrollView) findViewById(R.id.scrollView);
        main.post(new Runnable() {
            public void run() {
                main.scrollTo(0,0);
            }
        });
    }

    /**
     * Sets up the viewpager to show the locations in the list
     * @param viewPager viewpager
     * @param drink the drink from which the locations have to be extracted
     */
    private void setupViewPager(ViewPager viewPager, Drink drink) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        Bundle bundleBeer = new Bundle();
        bundleBeer.putString("LOCATION_TYPE","Bars");
        bundleBeer.putParcelableArrayList("LOCATION_LIST", (ArrayList<? extends Parcelable>) drink.getLocations());
        LocationFragment bar = new LocationFragment();
        bar.setArguments(bundleBeer);

        adapter.addFragment(bar, getString(R.string.tab_list_bar));

        viewPager.setAdapter(adapter);
    }

    /**
     * The viewpager adapter to show the drinks related locations in the list
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

    /**
     * Starts the camera activity to take a picture
     * @param view view
     */
    public void takePicture(View view) {
        Intent intent = new Intent(this, DefaultCameraActivity.class);
        intent.putExtra("LOCATION", drink.getIdFb());
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
    }

    /**
     * Activity Result of a picture taken with the camera
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
            editor.putString(drink.getIdFb(), picture);
            editor.apply();

            setPic(picture);
            final ScrollView main = (ScrollView) findViewById(R.id.scrollView);
            main.post(new Runnable() {
                public void run() {
                    main.scrollTo(0,0);
                }
            });
        }

    }

    /**
     * Sets the drink picture to one taken with the camera
     * @param picturePath path to the taken picture
     */
    private void setPic(String picturePath) {
        ImageView mImageView = findViewById(R.id.imageView);

        // Get the dimensions of the View
        int targetW = Resources.getSystem().getDisplayMetrics().widthPixels;;
        int targetH = 300;

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
     * Sets an already taken picture (if existing on the device) to the drink image
     */
    private void setInitialPicture() {
        SharedPreferences sharedPref = this.getSharedPreferences("LOCATION_PICTURE", Context.MODE_PRIVATE);
        String picPath = sharedPref.getString(drink.getIdFb(), "");
        if (!picPath.equals(""))
            setPic(picPath);
    }

    /**
     * Changes the heart icon and adds to user's favorites when clicked
     * @param view view
     */
    public void clickAddFavorite(View view) {
        Button btnFavorite = findViewById(R.id.btnFavorite);

        if (!isFavorite) {
            btnFavorite.setBackgroundResource(R.drawable.favority_full);
            currentUser.addFavoriteDrink(drink);
        }
        else {
            btnFavorite.setBackgroundResource(R.drawable.favorite_empty);
            currentUser.deleteFavoriteDrink(drink);
        }

        isFavorite = !isFavorite;
        FirebaseUserRepository.getInstance().updateUser(currentUser.idFb, currentUser);
    }
}
