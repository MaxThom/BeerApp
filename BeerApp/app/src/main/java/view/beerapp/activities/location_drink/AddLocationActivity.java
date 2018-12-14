package view.beerapp.activities.location_drink;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import view.beerapp.activities.navigation.LocationsActivity;
import view.beerapp.R;
import view.beerapp.contract.IAsyncResponse;
import view.beerapp.entities.Location;
import view.beerapp.utility.GeoCode;
import view.beerapp.repository.FirebaseLocationRepository;

/**
 * The activity in which the user creates a new location to add to the application.
 */
public class AddLocationActivity extends AppCompatActivity implements IAsyncResponse<Location> {

    private boolean isAddressVerified;
    private Location lastLoc;

    /**
     * Starting point of the activity.
     * @param savedInstanceState saved state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_location);
        isAddressVerified = false;

        //Sets up buttons
        createLocationButton();
        cancelCreationButton();

        //Sets up the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
    }

    /**
     * Sets up the create location button. Shows a message if the fields are not well filled.
     */
    private void createLocationButton(){
        Button create = findViewById(R.id.create_location);
        create.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(verifyFieldAndCreateLocation()){
                    Intent addLocationIntent = new Intent(getBaseContext(), LocationsActivity.class);
                    startActivity(addLocationIntent);
                }
                else{
                    showMessageDialog(getString(R.string.dialog_missing_field), getString(R.string.dialog_fill_info));
                }
            }
        });
    }

    /**
     * Shows a message dialog in the application.
     * @param title message title
     * @param message message to show
     */
    private void showMessageDialog(String title, String message){
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.dialog_ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    /**
     * Sets up the cancel button to change the activity when clicked
     */
    private void cancelCreationButton(){
        Button cancel = findViewById(R.id.cancel_location_creation);
        cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                Intent addBeerIntent = new Intent(getBaseContext(), LocationsActivity.class);
                startActivity(addBeerIntent);
            }
        });
    }

    /**
     * Verifies if the address really exists using the geocode class
     * @param v view
     */
    public void onClickVerifyAddress(View v) {

        GeoCode geoCode = new GeoCode();
        geoCode.delegate = this;

        String addressToVerify = ((EditText) findViewById(R.id.location_address)).getText().toString();
        if (!addressToVerify.trim().equals("")) {
            Location tempLoc = new Location();
            tempLoc.setFormattedAddress(addressToVerify);

            geoCode.execute(tempLoc);
        }
    }

    /**
     * Method that will create the location and add it to the application's total location list*
     * @return a boolean to specifiy if the location as correctly been created
     */
    private boolean verifyFieldAndCreateLocation() {
        boolean allFieldsFilled = true;

        //Fetch the beer infos entered by the user
        String locationName = ((EditText) findViewById(R.id.location_name)).getText().toString();
        String address = ((EditText) findViewById(R.id.location_address)).getText().toString();
        double rating = (double) ((RatingBar) findViewById(R.id.location_rating)).getRating();
        String locationDescription = ((EditText) findViewById(R.id.location_description)).getText().toString();

        if(!isAddressVerified || address.isEmpty() || rating == 0 || locationName.isEmpty() || locationDescription.isEmpty()){
            allFieldsFilled = false;
        }

        else{
            Location newLocation = new Location(locationName, lastLoc.getFormattedAddress(), locationDescription, rating, lastLoc.getLat(), lastLoc.getLng());
            FirebaseLocationRepository.getInstance().addLocation(newLocation);
        }

        return allFieldsFilled;
    }

    /**
     * Informs the user with toasts if the address has been found or not
     * @param output output from the address verification
     */
    @Override
    public void processFinish(Location output) {
        if (output == null) {
            isAddressVerified = false;
            Toast.makeText(this, getString(R.string.verify_adress_not_found), Toast.LENGTH_LONG).show();
        }
        else {
            isAddressVerified = true;
            Toast.makeText(this, getString(R.string.verify_adress_found), Toast.LENGTH_LONG).show();
            lastLoc = output;
            ((EditText) findViewById(R.id.location_address)).setText(output.getFormattedAddress());
        }
    }

    /**
     * Respecting the interface
     * @param progress
     */
    @Override
    public void progressUpdate(int progress) {
        //No override needed
    }
}
