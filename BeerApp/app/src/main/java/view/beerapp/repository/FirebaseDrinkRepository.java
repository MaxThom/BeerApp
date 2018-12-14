package view.beerapp.repository;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import view.beerapp.contract.IDatabaseData;
import view.beerapp.contract.IDrinkRepository;
import view.beerapp.entities.Drink;
import view.beerapp.entities.DrinkFactory;
import view.beerapp.entities.Location;

/**
 * Use to do CRUD operation on the drink node in Firebase.
 * Singleton Pattern
 */
public class FirebaseDrinkRepository implements IDrinkRepository, IDatabaseData {

    private static FirebaseDrinkRepository instance;

    private FirebaseDatabase database;
    private ArrayList<Drink> drinks;
    private DatabaseReference myRef;
    private boolean gotFirstTimeData;

    /**
     * Constructors
     */
    private FirebaseDrinkRepository() {
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("drinks").getRef();
        drinks = new ArrayList<Drink>();
        gotFirstTimeData = false;

        setListenerOnNode();
    }

    /**
     * Get instance of the singleton
     * @return the instance
     */
    public static FirebaseDrinkRepository getInstance() {
        if (instance == null) {
            instance = new FirebaseDrinkRepository();
        }
        return instance;
    }

    /**
     * Set the listener on the node from firebase.
     * This will update the list of drink in the app everytime there is a change on firebase
     */
    private void setListenerOnNode() {
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                drinks = new ArrayList<Drink>();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Map<String, Object> map = (Map<String, Object>) child.getValue();
                    Drink dr = new Drink();

                    if (map.get("beverageType") != null)
                        dr = DrinkFactory.createDrink(map.get("beverageType").toString());
                    if (map.get("description") != null)
                        dr.setDescription(map.get("description").toString());
                    if (map.get("idFb") != null)
                        dr.setIdFb(map.get("idFb").toString());
                    if (map.get("rating") != null)
                        dr.setRating(Double.valueOf(map.get("rating").toString()));
                    if (map.get("alcoolLevel") != null)
                        dr.setAlcoolLevel(Double.valueOf(map.get("alcoolLevel").toString()));
                    if (map.get("name") != null)
                        dr.setName(map.get("name").toString());
                    if (map.get("beverageType") != null)
                        dr.setBeverageType(map.get("beverageType").toString());

                    if (map.get("locations") != null) {
                        for (String idLoc : (ArrayList<String>) map.get("locations")) {
                            Location tmp = FirebaseLocationRepository.getInstance().getLocation(idLoc);
                            if (tmp != null)
                                dr.getLocations().add(tmp);
                            else {
                                tmp = new Location();
                                tmp.setIdFb(idLoc);
                                dr.getLocations().add(tmp);
                            }
                        }
                    }
                    drinks.add(dr);
                }
                gotFirstTimeData = true;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    /**
     * Add drink to firebase
     * @param drink drink to be added
     * @return the id key of the newly added drink
     */
    @Override
    public String addDrink(Drink drink) {
        String drinksId = myRef.push().getKey();
        drink.setIdFb(drinksId);
        this.drinks.add(drink);

        Map<String, Object> childUpdates = new HashMap<>();

        childUpdates.put(drinksId + "/" + "idFb", drink.getIdFb());
        childUpdates.put(drinksId + "/" + "name", drink.getName());
        childUpdates.put(drinksId + "/" + "description", drink.getDescription());
        childUpdates.put(drinksId + "/" + "alcoolLevel", drink.getAlcoolLevel());
        childUpdates.put(drinksId + "/" + "rating", drink.getRating());
        childUpdates.put(drinksId + "/" + "beverageType", drink.getBeverageType());

        for (int i = 0; i < drink.getLocations().size(); i++) {
            childUpdates.put(drinksId + "/locations/" + Integer.toString(i), drink.getLocations().get(i).getIdFb());
        }

        myRef.updateChildren(childUpdates);

        return drinksId;
    }

    /**
     * Return the drinks list
     * @return list of drink
     */
    @Override
    public ArrayList<Drink> getDrinks() {
        return drinks;
    }

    /**
     * Get a single drink
     * @param id id of the drink
     * @return the drink
     */
    @Override
    public Drink getDrink(String id) {
        for (Drink dr : drinks) {
            if (dr.getIdFb().equals(id)) {
                return dr;
            }
        }

        return null;
    }

    /**
     * Delete a drink in firebase.
     * This method is not implemented
     * @param id id of the drink
     * @return the deleted drink
     */
    @Override
    public Drink deleteDrink(String id) {
        return null;
    }

    /**
     * Update a drink to fire base
     * @param id id of the drink
     * @param drink the drink
     * @return true is correctly updated
     */
    @Override
    public boolean updateDrink(String id, Drink drink) {
        Map<String, Object> childUpdates = new HashMap<>();

        if (drink.getName() != null)
            childUpdates.put(id + "/" + "name", drink.getName());
        if (drink.getDescription() != null)
            childUpdates.put(id + "/" + "description", drink.getDescription());
        if (drink.getAlcoolLevel() != 0.0)
            childUpdates.put(id + "/" + "alcoolLevel", drink.getAlcoolLevel());
        if (drink.getRating() != 0.0)
            childUpdates.put(id + "/" + "rating", drink.getRating());
        if (drink.getBeverageType() != null)
            childUpdates.put(id + "/" + "beverageType", drink.getBeverageType());

        for (int i = 0; i < drink.getLocations().size(); i++) {
            childUpdates.put(id + "/locations/" + Integer.toString(i), drink.getLocations().get(i).getIdFb());
        }

        myRef.updateChildren(childUpdates);

        return true;
    }

    /**
     * Used to see if the data is populated for the first time
     * @return true if data is present
     */
    @Override
    public boolean isDataReady() {
        return gotFirstTimeData;
    }
}
