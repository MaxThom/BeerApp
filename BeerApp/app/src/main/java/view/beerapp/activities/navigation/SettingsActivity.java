package view.beerapp.activities.navigation;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


import java.math.BigDecimal;

import view.beerapp.R;

/**
 * Display all the settings for the user to be modifier
 */
public class SettingsActivity extends BaseActivity {

    //In Kilometers
    public static double drinkDiscoveryDistance = 2;
    public static float locationDiscoveryDistance = 10;
    public static int numberOfFriendsFavoritesToShow = 5;
    public static double topRatedLocationTreshold = 4.5;
    public static double topRatedDrinkTreshold = 4.5;
    public static int NumberOfTopRatedLocations = 10;
    public static int NumberOfTopRatedDrinks = 10;

    private EditText locationDiscoveryDistanceET;
    private EditText friendsFavoriteDrinksToShowET;

    private static boolean isSettingsFetch = false;

    /**
     * Starting point of the activity.
     * @param savedInstanceState saved state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        super.onCreateDrawer();


        locationDiscoveryDistanceET = findViewById(R.id.location_discovery_distance);
        friendsFavoriteDrinksToShowET = findViewById(R.id.friends_favorite_drinks_to_show);

        fetchSavedSettings(getApplicationContext());
        updateSettingsFields();
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
     * Update the settings when the save settings button is pressed
     * @param view the view display of the button
     */
    public void saveSettings(View view){
        String ldd = locationDiscoveryDistanceET.getText().toString();
        String ffdts = friendsFavoriteDrinksToShowET.getText().toString();

        if (!ldd.equals("")){
            locationDiscoveryDistance = Float.parseFloat(ldd);
            locationDiscoveryDistance = round(locationDiscoveryDistance, 4);
        }
        if (!ffdts.equals("")){
            numberOfFriendsFavoritesToShow = Integer.parseInt(ffdts);
        }

        SharedPreferences sharedPref = this.getSharedPreferences("SETTINGS", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putFloat("DISTANCE_DISCOVERY", locationDiscoveryDistance);
        editor.putInt("FRIENDS_FAVORITE", numberOfFriendsFavoritesToShow);
        editor.apply();

        Toast.makeText(getApplicationContext(), getString(R.string.settings_save), Toast.LENGTH_SHORT).show();
        updateSettingsFields();
    }

    /**
     * Simple method to update parameters fields
     */
    private void updateSettingsFields(){
        locationDiscoveryDistanceET.setText(Float.toString(locationDiscoveryDistance));
        friendsFavoriteDrinksToShowET.setText(Integer.toString(numberOfFriendsFavoritesToShow));
    }

    /**
     * Get the number of drinks from friend's favorite to display
     * @param context context to know where to get the key/value pair
     * @return the number of friends favorite to show
     */
    public static int getNumberOfFriendsFavoritesToShow(Context context) {
        if (!isSettingsFetch)
            fetchSavedSettings(context);
        return numberOfFriendsFavoritesToShow;
    }

    /**
     * Get the location discovery distance
     * @param context context to know where to get the key/value pair
     * @return the distance
     */
    public static float getLocationDiscoveryDistance(Context context) {
        if (!isSettingsFetch)
            fetchSavedSettings(context);
        return locationDiscoveryDistance;
    }

    /**
     * Get the saved settings in the mobile phone
     * @param context context to know where to save the key/value pair
     */
    private static void fetchSavedSettings(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences("SETTINGS", Context.MODE_PRIVATE);
        locationDiscoveryDistance = sharedPref.getFloat("DISTANCE_DISCOVERY", 10);
        numberOfFriendsFavoritesToShow = sharedPref.getInt("FRIENDS_FAVORITE", 5);
        isSettingsFetch = true;
    }

    /**
     * Round a float value to is decimal place
     * @param d the float value
     * @param decimalPlace number of decimal
     * @return the new float
     */
    private float round(float d, int decimalPlace) {
        return BigDecimal.valueOf(d).setScale(decimalPlace,BigDecimal.ROUND_HALF_UP).floatValue();

    }
}
