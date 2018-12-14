package view.beerapp.activities.location_drink;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.Toast;

import view.beerapp.R;
import view.beerapp.activities.hardware.BarcodeCameraActivity;
import view.beerapp.entities.Drink;
import view.beerapp.entities.DrinkFactory;
import view.beerapp.entities.Location;
import view.beerapp.repository.FirebaseDrinkRepository;
import view.beerapp.repository.FirebaseLocationRepository;

/**
 * The activity in which the user creates a new beer to add to the application.
 */
public class AddDrinkActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;

    private Spinner beverageTypeSpinner;
    private String locIdFb;

    /**
     * Starting point of the activity.
     * @param savedInstanceState saved state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_beer);

        createBeerButton();
        cancelCreationButton();

        beverageTypeSpinner = findViewById(R.id.beverage_type_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.beverage_type_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        beverageTypeSpinner.setAdapter(adapter);

        //Toolbar setup
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);

        Bundle data = getIntent().getExtras();

        locIdFb = data != null ? data.getString("LOCATION") : null;
    }

    /**
     * Result of a properly scanned barcode returning to the activity.
     * @param requestCode request Code
     * @param resultCode resultCode
     * @param data data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            //Get Barcode scan
            EditText description = findViewById(R.id.beer_description);
            String barCode = data.getStringExtra("BARCODE");

            if(barCode != null){
                description.setText("Barcode : " +barCode);
                Toast.makeText(getApplicationContext(), getString(R.string.barcode_scanned_success), Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Starts the barcode scanning camera activity
     * @param view view
     */
    public void scanBarcode(View view) {
        Intent intent = new Intent(this, BarcodeCameraActivity.class);
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
    }

    /**
     * Setting up the create beer button and its listener
     */
    private void createBeerButton(){
        Button create = findViewById(R.id.create_beer);
        create.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(verifyFieldsAndCreateBeer()){
                    setResult(RESULT_OK);
                    finish();
                }
                else{

                }
            }
        });
    }

    /**
     * Setting up the cancel button and its listener to go back to the previous page.
     */
    private void cancelCreationButton(){
        Button cancel = findViewById(R.id.cancel_beer_creation);
        cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }

    /**
     * Shows a message dialog
     * @param message the message to show
     */
    private void showMessageDialog(String message){

        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle(getString(R.string.dialog_error));
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.dialog_ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    //Method that will create the beer and add it to the application's total beerlist
    // and the appropriate location drink list

    /**
     * Method that will create the beer and add it to the application's total beerlist
     * and the appropriate location drink list
     *
     * @return a boolean to specifiy if the beer as correctly been created
     */
    private boolean verifyFieldsAndCreateBeer(){

        boolean isCreated = false, drinkAlreadyExist = false;

        //Fetch the beer infos entered by the user
        String beerName = ((EditText) findViewById(R.id.beer_name)).getText().toString().trim().toLowerCase();
        if (beerName.length() > 0)
            beerName = beerName.replaceFirst(Character.toString(beerName.charAt(0)), Character.toString(beerName.charAt(0)).toUpperCase());
        String alcoholLevelField = ((EditText) findViewById(R.id.beer_alcool_level)).getText().toString();
        double rating = (double) ((RatingBar) findViewById(R.id.location_rating)).getRating();
        String beerDescription = ((EditText) findViewById(R.id.beer_description)).getText().toString().trim();
        int beverageIndex = beverageTypeSpinner.getSelectedItemPosition();
        String beverageType = "";
        switch (beverageIndex) {
            case 0:
                beverageType = "Beer";
                break;
            case 1:
                beverageType = "Wine";
                break;
            case 2:
                beverageType = "Cocktail";
                break;
        }

        for (Drink dr : FirebaseDrinkRepository.getInstance().getDrinks()) {
            if (dr.getName().equals(beerName)) {
                drinkAlreadyExist = true;
            }
        }

        if(alcoholLevelField.isEmpty() || rating == 0 || beerName.isEmpty() || beerDescription.isEmpty()){
            showMessageDialog(getString(R.string.dialog_fill_info));
        }
        else if (drinkAlreadyExist) {
            showMessageDialog(getString(R.string.dialog_already_exist));
        }
        //Creates the drink if no errors
        else {
            double alcoholLevel = Double.parseDouble(alcoholLevelField);
            Drink newBeer = DrinkFactory.createDrink(beerName, beerDescription, alcoholLevel, rating, beverageType);

            if (locIdFb != null) {
                Location loc = FirebaseLocationRepository.getInstance().getLocation(locIdFb);
                newBeer.getLocations().add(loc);
                newBeer.setIdFb(FirebaseDrinkRepository.getInstance().addDrink(newBeer));
                loc.getDrinks().add(newBeer);
                FirebaseLocationRepository.getInstance().updateLocation(loc.getIdFb(), loc);
            }
            else {
                newBeer.setIdFb( FirebaseDrinkRepository.getInstance().addDrink(newBeer));
            }

            isCreated = true;
        }

        return isCreated;
    }
}
