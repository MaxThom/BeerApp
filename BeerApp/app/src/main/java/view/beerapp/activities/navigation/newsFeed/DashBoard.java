package view.beerapp.activities.navigation.newsFeed;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import view.beerapp.R;
import view.beerapp.activities.navigation.BaseActivity;
import view.beerapp.activities.navigation.SettingsActivity;
import view.beerapp.entities.Drink;
import view.beerapp.entities.Location;
import view.beerapp.entities.User;
import view.beerapp.repository.FirebaseDrinkRepository;
import view.beerapp.repository.FirebaseLocationRepository;
import view.beerapp.repository.FirebaseUserRepository;
import view.beerapp.utility.MapsCalculations;

/**
 * The newsfeed activity. Locations and drinks are listed and filtered by top ratings or fetched
 * in the friends favorites.
 */
public class DashBoard extends BaseActivity {
    private static final int LOCATION_GROUP_PERMISSION_REQUEST = 1;
    public static double currentLongitude;
    public static double currentLatitude;

    /**
     * Starting point of the activity.
     * @param savedInstanceState saved state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);
        onCreateDrawer();

        checkLocationPermission();
    }

    /**
     * Informs the user that localization permission is needed to show nearest recommendations
     */
    private void checkLocationPermission() {
        int perm1 = PermissionChecker.checkSelfPermission(DashBoard.this, "android.permission.ACCESS_FINE_LOCATION");
        int perm2 = PermissionChecker.checkSelfPermission(DashBoard.this, "android.permission.ACCESS_COARSE_LOCATION");

        if (perm1 == PermissionChecker.PERMISSION_GRANTED || perm2 == PermissionChecker.PERMISSION_GRANTED) {
            updateCurrentPositionCoordinates();
        } else {
            AlertDialog alertDialog = new AlertDialog.Builder(DashBoard.this).create();
            alertDialog.setTitle(getString(R.string.dialog_permission));
            alertDialog.setMessage(getString(R.string.dialog_location));
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.dialog_ok),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            ActivityCompat.requestPermissions(DashBoard.this,
                                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                                    LOCATION_GROUP_PERMISSION_REQUEST);
                        }
                    });
            alertDialog.show();
        }
    }

    /**
     * Updates the localisation if the user accepts the permission asked or does nothing if not
     * @param requestCode request code
     * @param permissions string array containing permissions
     * @param grantResults grant results
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case LOCATION_GROUP_PERMISSION_REQUEST: {
                if (grantResults.length > 0
                        && grantResults[0] == PermissionChecker.PERMISSION_GRANTED) {
                    updateCurrentPositionCoordinates();
                } else {
                    // permission denied
                }
                break;
            }
        }
    }

    /**
     * Will use the parent method to open the navigation drawer menu
     * @param item menu item
     * @return selected item
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    /**
     *
     * @param drinks the top rated drinks in the nearest locations
     */
    private void setupDrinksRVAdapter(ArrayList<Drink> drinks){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        RecyclerView recyclerView = findViewById(R.id.top_rated_beers);
        recyclerView.setLayoutManager(layoutManager);

        TopRatedDrinksRVAdapter adapter = new TopRatedDrinksRVAdapter(this, drinks);
        recyclerView.setAdapter(adapter);
    }

    /**
     * Sets up the adapter to show the nearest locations in the recyclerview
     * @param locations nearest locations
     */
    private void setupLocationsRVAdapter(ArrayList<Location> locations){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        RecyclerView recyclerView = findViewById(R.id.top_rated_locations);
        recyclerView.setLayoutManager(layoutManager);
        TopRatedLocationRVAdapter adapter = new TopRatedLocationRVAdapter(this, locations);
        recyclerView.setAdapter(adapter);
    }

    /**
     * Sets up the adapter to show the friends favorites drinks
     * @param drinks friends favorites drinks
     */
    private void setupFriendsFavoritesRVAdapter(ArrayList<Drink> drinks){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        RecyclerView recyclerView = findViewById(R.id.friends_favorites);
        recyclerView.setLayoutManager(layoutManager);
        TopRatedDrinksRVAdapter adapter = new TopRatedDrinksRVAdapter(this, drinks);
        recyclerView.setAdapter(adapter);
    }

    /**
     * Get a specified number of users's friends favorite drinks to show in the newsfeed
     */
    private ArrayList<Drink> getFriendsFavorites(){

        ArrayList<Drink> friendsFavorites = new ArrayList<>();
        User currentUser = FirebaseUserRepository.getInstance().getCurrentUser();
        List<User> friends = currentUser.getFriends();

        //Will go fetch a specified number of friends favorites
        for (int i = 0; i < friends.size(); i++){
            for (int j = 0; j < friends.get(i).getFavoriteDrinks().size() && j < SettingsActivity.getNumberOfFriendsFavoritesToShow(getApplicationContext()); j++){
                boolean isFound = false;
                for (Drink dr: friendsFavorites) {
                    if (dr.getIdFb().equals(friends.get(i).getFavoriteDrinks().get(j).getIdFb())) {
                        isFound = true;
                        break;
                    }
                }
                if(!isFound) {
                    friendsFavorites.add(friends.get(i).getFavoriteDrinks().get(j));
                }
            }
        }

        for (int i = 0; i < friendsFavorites.size(); i++) {
            if (friendsFavorites.get(i).getName() == null) {
                friendsFavorites.set(i, FirebaseDrinkRepository.getInstance().getDrink(friendsFavorites.get(i).getIdFb()));
            }
        }

        return friendsFavorites;
    }

    /**
     * Get the top rated drinks from the nearest locations to show in the newsfeed
     */
    private ArrayList<Drink> getTopNearestDrinks(){

        ArrayList<Location> nearestLocations = getTopNearestLocations();
        ArrayList<Drink> topDrinks = new ArrayList<>();
        int showedDrinks = 0;

        for (int i = 0; i < nearestLocations.size(); i++){

            List<Drink> currentLocDrinks = nearestLocations.get(i).getDrinks();

            //Sorts drinks by rating
            Collections.sort(currentLocDrinks, Drink.ratingComparator);
            for (int j = 0; j < currentLocDrinks.size() && showedDrinks < SettingsActivity.NumberOfTopRatedDrinks; j++){

                Drink currentDrink = currentLocDrinks.get(j);
                if(!topDrinks.contains(currentDrink)){
                    topDrinks.add(currentDrink);
                    showedDrinks++;
                }
            }
        }

        return topDrinks;
    }

    /**
     * Get the top rated nearest locations to show in the newsfeed
     */
    private ArrayList<Location> getTopNearestLocations(){

        ArrayList<Location> nearestLocations = new ArrayList();
        ArrayList<Location> allLocations = FirebaseLocationRepository.getInstance().getLocations();
        for (Location loc : allLocations) {
            for (int i = 0; i < loc.getDrinks().size(); i++) {
                if (loc.getDrinks().get(i) != null && loc.getDrinks().get(i).getName() == null) {
                    loc.getDrinks().set(i, FirebaseDrinkRepository.getInstance().getDrink(loc.getDrinks().get(i).getIdFb()));
                }
            }
        }

        //Sorts the locations by rating
        Collections.sort(allLocations, Location.ratingComparator);

        //Shows a specified number of locations
        for (int i = 0; i < allLocations.size() && i < SettingsActivity.NumberOfTopRatedLocations; i++){

            Location currentLoc = allLocations.get(i);
            double lon = currentLoc.getLng();
            double lat = currentLoc.getLat();

            double distanceFromCurrentLocation = MapsCalculations.distanceBetweenTwoPoints(lon, lat, currentLongitude, currentLatitude);

            if (distanceFromCurrentLocation <= SettingsActivity.getLocationDiscoveryDistance(getApplicationContext())){
                nearestLocations.add(currentLoc);
            }
        }

        return nearestLocations;
    }

    /**
     * Fetches the user's current location with the google map api to find the nearest locations.
     */
    public void updateCurrentPositionCoordinates(){

        int perm1 = PermissionChecker.checkSelfPermission(DashBoard.this, "android.permission.ACCESS_FINE_LOCATION");
        int perm2 = PermissionChecker.checkSelfPermission(DashBoard.this, "android.permission.ACCESS_COARSE_LOCATION");

        if (perm1 == PermissionChecker.PERMISSION_GRANTED || perm2 == PermissionChecker.PERMISSION_GRANTED) {
            FusedLocationProviderClient mFusedLocationClient;
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

            //Try to fetch the last known location
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<android.location.Location>() {
                        @Override
                        public void onSuccess(android.location.Location location) {
                            // GPS location can be null if GPS is switched off
                            if (location != null) {
                                //Current location found : updates the current coordinates
                                setCurrentLatitude(location.getLatitude());
                                setCurrentLongitude(location.getLongitude());
                                setupDrinksRVAdapter(getTopNearestDrinks());
                                setupLocationsRVAdapter(getTopNearestLocations());
                                setupFriendsFavoritesRVAdapter(getFriendsFavorites());
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            e.printStackTrace();
                        }
                    });
        }
    }

    /**
     * Current longitude setter
     * @param lon longitude
     */
    public static void setCurrentLongitude(double lon){
        currentLongitude = lon;
    }

    /**
     * Current latitude setter
     * @param lat latitude
     */
    public static void setCurrentLatitude(double lat){
        currentLatitude = lat;
    }
}