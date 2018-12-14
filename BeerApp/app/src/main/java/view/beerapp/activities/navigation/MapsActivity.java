package view.beerapp.activities.navigation;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.location.*;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Collections;

import view.beerapp.activities.location_drink.LocationDisplayActivity;
import view.beerapp.R;
import view.beerapp.activities.login.LoginActivity;
import view.beerapp.activities.navigation.newsFeed.DashBoard;
import view.beerapp.contract.IAsyncResponse;
import view.beerapp.entities.Drink;
import view.beerapp.entities.Location;
import view.beerapp.utility.GeoCode;
import view.beerapp.repository.FirebaseDrinkRepository;
import view.beerapp.repository.FirebaseLocationRepository;

/**
 * Maps Activity with google API
 */
public class MapsActivity extends AppCompatActivity
                            implements GoogleMap.InfoWindowAdapter, OnMapReadyCallback,
                                        GoogleMap.OnMarkerClickListener,
                                        GoogleMap.OnInfoWindowClickListener, IAsyncResponse<Location> {
    private static final int LOCATION_GROUP_PERMISSION_REQUEST = 1;

    private GoogleMap mMap;
    private DrawerLayout mDrawerLayout;

    /**
     * Starting point of the activity.
     * @param savedInstanceState saved state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        onCreateDrawer();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Create the navigation drawer
     */
    protected void onCreateDrawer() {

        //Fetch our drawer
        mDrawerLayout = findViewById(R.id.drawer_layout);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
            new NavigationView.OnNavigationItemSelectedListener() {
                public boolean onNavigationItemSelected(MenuItem menuItem) {

                    Intent navigationIntent = null;

                    // set item as selected to persist highlight
                    menuItem.setChecked(true);

                    // close drawer when item is tapped
                    mDrawerLayout.closeDrawers();

                    // Handle navigation view item clicks here.
                    int id = menuItem.getItemId();

                    if (id == R.id.nav_map) {
                        navigationIntent = new Intent(getBaseContext(), MapsActivity.class);
                    }
                    else if (id == R.id.nav_home) {
                        navigationIntent = new Intent(getBaseContext(), DashBoard.class);
                    }
                    else if (id == R.id.nav_friends) {
                        navigationIntent = new Intent(getBaseContext(), FriendsActivity.class);
                    }
                    else if (id == R.id.nav_profile) {
                        navigationIntent = new Intent(getBaseContext(), ProfileActivity.class);
                    }
                    else if (id == R.id.nav_beers) {
                        navigationIntent = new Intent(getBaseContext(), DrinkListActivity.class);
                    }
                    else if (id == R.id.nav_locations) {
                        navigationIntent = new Intent(getBaseContext(), LocationsActivity.class);
                    }
                    else if(id == R.id.nav_settings){
                        navigationIntent = new Intent(getBaseContext(), SettingsActivity.class);
                    }
                    else if(id == R.id.nav_logout){

                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(getBaseContext(), LoginActivity.class));
                        finish();
                    }

                    startActivity(navigationIntent);
                    return true;
                }
            });
    }

    /**
     * Receive result of the async task when the map is ready
     * @param googleMap the map
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Defince Listeners & Adapters
        mMap.setOnMarkerClickListener(this);
        mMap.setInfoWindowAdapter(this);
        mMap.setOnInfoWindowClickListener(this);

        // Initialize data and check for location permission
        initializeData();
        checkLocationPermission();
    }

    /**
     * Result from the async task of fetching the latitude and longitude of a location
     * @param output the result
     */
    @Override
    public void processFinish(Location output) {
        if (output != null) {
            addMarker(output);
        }
    }

    /**
     * Update from the geocode task
     * @param progress the progress indicator
     */
    @Override
    public void progressUpdate(int progress) { }

    /**
     * Add a marker on the map
     * @param loc location of the marker
     */
    public void addMarker(Location loc) {
        LatLng pos = new LatLng(loc.getLat(), loc.getLng());
        Marker marker = mMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.beer))
                .anchor(0.0f, 1.0f) // Anchors the marker on the bottom left
                .position(pos)
                .title(loc.getFormattedAddress())
                .snippet(loc.toString()));
        marker.setTag(loc);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(pos));
    }

    /**
     * Move the camera to the marker when click.
     * Return false make this behavior
     * @param marker the clicker marker
     * @return true if the even is consumed
     */
    @Override
    public boolean onMarkerClick(final Marker marker) {
        return false;
    }

    /**
     * Get the content of a marker
     * @param marker the marker
     * @return the view
     */
    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    /**
     * Get the info window of a marker when its clicked
     * @param marker the clicker marker
     * @return the view of the marker with the display of the drinks
     */
    @Override
    public View getInfoWindow(final Marker marker) {

        final Context context = getApplicationContext();
        Location loc = (Location) marker.getTag();
        for (int i = 0; i < loc.getDrinks().size(); i++) {
            if (loc.getDrinks().get(i).getName() == null) {
                loc.getDrinks().set(i, FirebaseDrinkRepository.getInstance().getDrink(loc.getDrinks().get(i).getIdFb()));
            }
        }
        // Define Layout
        LinearLayout info = new LinearLayout(context);
        info.setOrientation(LinearLayout.VERTICAL);
        info.setBackgroundResource(R.drawable.map_background);

        // Define top row
        TextView title = new TextView(context);
        title.setTextColor(Color.BLACK);
        title.setGravity(Gravity.CENTER);
        title.setTypeface(null, Typeface.BOLD);
        title.setText(loc.getTitle().length() > 45 ? loc.getTitle().substring(0, 42) + "..." : loc.getTitle());
        info.addView(title);

        // Display the first 4 drinks
        Collections.sort(loc.getDrinks(), Drink.ratingComparator);
        for (int i = 0; i < loc.getDrinks().size() && i < 4 ; i++){
            ImageView img = new ImageView(context);
            img.setBackgroundResource(R.drawable.beer_separator);
            info.addView(img);

            info.addView(createDrinkRow(context, loc.getDrinks().get(i)));
        }

        return info;
    }

    /**
     * Click event on the info window
     * @param marker the marker corresponding to the window
     */
    @Override
    public void onInfoWindowClick(Marker marker) {
        final Context context = getApplicationContext();

        // Start the location display
        Intent intent = new Intent(context, LocationDisplayActivity.class);
        intent.putExtra("LOCATION", ((Location) marker.getTag()).getIdFb());
        intent.putExtra("PARENT", "MAP");
        startActivity(intent);
    }

    /**
     * Get the location permission of the user
     */
    private void checkLocationPermission() {
        int perm1 = PermissionChecker.checkSelfPermission(MapsActivity.this, "android.permission.ACCESS_FINE_LOCATION");
        int perm2 = PermissionChecker.checkSelfPermission(MapsActivity.this, "android.permission.ACCESS_COARSE_LOCATION");

        // If the permission is already given, move the camera, else ask for the permission
        if (perm1 == PermissionChecker.PERMISSION_GRANTED || perm2 == PermissionChecker.PERMISSION_GRANTED) {
            setViewToCurrentPosition();

        } else {
            ActivityCompat.requestPermissions(MapsActivity.this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_GROUP_PERMISSION_REQUEST);
        }
    }

    /**
     * Called when the user accept or refuse a permission
     * @param requestCode request code on which permission
     * @param permissions list of permissions
     * @param grantResults list of accepted permission
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case LOCATION_GROUP_PERMISSION_REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PermissionChecker.PERMISSION_GRANTED) {
                    setViewToCurrentPosition();
                } else {
                    // permission denied
                }
                break;
            }
        }
    }

    /**
     * Set the view to the current position if we have the permission
     */
    private void setViewToCurrentPosition() {
        int perm1 = PermissionChecker.checkSelfPermission(MapsActivity.this, "android.permission.ACCESS_FINE_LOCATION");
        int perm2 = PermissionChecker.checkSelfPermission(MapsActivity.this, "android.permission.ACCESS_COARSE_LOCATION");

        if (perm1 == PermissionChecker.PERMISSION_GRANTED || perm2 == PermissionChecker.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);

            FusedLocationProviderClient mFusedLocationClient;
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

            mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<android.location.Location>() {
                    @Override
                    public void onSuccess(android.location.Location location) {
                    // Got last known location. In some rare situations this can be null.
                    if (location != null) {
                        final double currentLatitude = location.getLatitude();
                        final double currentLongitude = location.getLongitude();

                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLatitude, currentLongitude), 15));
                    }
                    }
                });
        }
    }

    /**
     * Create a row of the info window. Each row represent a drink
     * @param context the activity context
     * @param drink the drink to be displayed
     * @return return the row layout
     */
    private LinearLayout createDrinkRow(Context context, Drink drink) {
        LinearLayout beerLayout = new LinearLayout(context);
        beerLayout.setOrientation(LinearLayout.HORIZONTAL);

        ImageView img2 = new ImageView(context);
        img2.setMaxHeight(64);
        img2.setMaxWidth(64);
        img2.setImageResource(drink.getIcon());
        beerLayout.addView(img2);

        TextView snippet = new TextView(context);
        snippet.setTextColor(Color.GRAY);
        snippet.setText(drink.toDisplay());
        //snippet.setWidth(600);
        beerLayout.addView(snippet);

        return beerLayout;
    }

    /**
     * Initialize the data and add marker to every location
     */
    private void initializeData() {
        for (Location loc : FirebaseLocationRepository.getInstance().getLocations()) {
            if (loc.getLat() == 0.0 && loc.getLng() == 0.0) {
                GeoCode geoCode = new GeoCode();
                geoCode.delegate = this;
                geoCode.execute(loc);
            }
            else {
                addMarker(loc);
            }
        }
    }

    /**
     * Used to react on option item selected on the navigation bar
     * @param item the menu item
     * @return true if need to react
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            //Navigation menu button pressed
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}