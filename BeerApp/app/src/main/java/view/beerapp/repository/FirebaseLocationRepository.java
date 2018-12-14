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
import view.beerapp.contract.ILocationRepository;
import view.beerapp.entities.Drink;
import view.beerapp.entities.Location;

/**
 * Use to do CRUD operation on the location node in Firebase.
 * Singleton Pattern
 */
public class FirebaseLocationRepository implements ILocationRepository, IDatabaseData {
    private static FirebaseLocationRepository instance;

    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private ArrayList<Location> locations;
    private boolean gotFirstTimeData;

    /**
     * Constructor
     */
    private FirebaseLocationRepository() {
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("locations").getRef();
        locations = new ArrayList<Location>();
        gotFirstTimeData = false;

        setListenerOnNode();
    }

    /**
     * Return the instance of the singleton or create it
     * @return the instance
     */
    public static FirebaseLocationRepository getInstance() {
        if (instance == null) {
            instance = new FirebaseLocationRepository();
        }
        return instance;
    }

    /**
     * Set the listener on the node from firebase.
     * This will update the list of locations in the app everytime there is a change on firebase
     */
    private void setListenerOnNode() {
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                locations = new ArrayList<Location>();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Map<String, Object> map = (Map<String, Object>) child.getValue();
                    Location loc = new Location();

                    if (map.get("formattedAddress") != null)
                        loc.setFormattedAddress(map.get("formattedAddress").toString());
                    if (map.get("idFb") != null)
                        loc.setIdFb(map.get("idFb").toString());
                    if (map.get("lng") != null)
                        loc.setLng(Double.valueOf(map.get("lng").toString()));
                    if (map.get("lat") != null)
                        loc.setLat(Double.valueOf(map.get("lat").toString()));
                    if (map.get("title") != null)
                        loc.setTitle(map.get("title").toString());
                    if (map.get("rating") != null)
                        loc.setRating(Double.valueOf(map.get("rating").toString()));
                    if (map.get("description") != null)
                        loc.setDescription(map.get("description").toString());

                    if (map.get("drinks") != null) {
                        for (String idDrink : (ArrayList<String>) map.get("drinks")) {
                            Drink tmp = FirebaseDrinkRepository.getInstance().getDrink(idDrink);
                            if (tmp != null)
                                loc.getDrinks().add(tmp);
                            else {
                                tmp = new Drink();
                                tmp.setIdFb(idDrink);
                                loc.getDrinks().add(tmp);
                            }
                        }
                    }
                    locations.add(loc);
                }
                gotFirstTimeData = true;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    /**
     * Add a new location to Firebase
     * @param location the location to be added
     * @return the id key of the newly added location
     */
    @Override
    public String addLocation(Location location) {
        String locationId = myRef.push().getKey();
        location.setIdFb(locationId);
        this.locations.add(location);

        Map<String, Object> childUpdates = new HashMap<>();

        childUpdates.put(locationId + "/" + "idFb", location.getIdFb());
        childUpdates.put(locationId + "/" + "title", location.getTitle());
        childUpdates.put(locationId + "/" + "formattedAddress", location.getFormattedAddress());
        childUpdates.put(locationId + "/" + "lat", location.getLat());
        childUpdates.put(locationId + "/" + "lng", location.getLng());
        childUpdates.put(locationId + "/" + "description", location.getDescription());
        childUpdates.put(locationId + "/" + "rating", location.getRating());

        for (int i = 0; i < location.getDrinks().size(); i++) {
            childUpdates.put(locationId + "/drinks/" + Integer.toString(i), location.getDrinks().get(i).getIdFb());
        }

        myRef.updateChildren(childUpdates);

        return locationId;
    }

    /**
     * Get the list of locations
     * @return list of locations
     */
    @Override
    public ArrayList<Location> getLocations() {
        return locations;
    }

    /**
     * Return a single location according to the id
     * @param id id of the location
     * @return the location
     */
    @Override
    public Location getLocation(String id) {

        for (Location loc : locations) {
            if (loc.getIdFb().equals(id)) {
                return loc;
            }
        }

        return null;
    }

    /**
     * Delete a location from Firebase
     * This method is not implemented
     * @param id id of the location to be deleted
     * @return the deleted location
     */
    @Override
    public Location deleteLocation(String id) {
        return null;
    }

    /**
     * Update a location to firebase
     * @param id id of the location
     * @param location the location
     * @return true if success
     */
    @Override
    public boolean updateLocation(String id, Location location) {
        Map<String, Object> childUpdates = new HashMap<>();

        if (location.getTitle() != null)
            childUpdates.put(id + "/" + "title", location.getTitle());
        if (location.getFormattedAddress() != null)
            childUpdates.put(id + "/" + "formattedAddress", location.getFormattedAddress());
        if (location.getLat() != 0.0)
            childUpdates.put(id + "/" + "lat", location.getLat());
        if (location.getLng() != 0.0)
            childUpdates.put(id + "/" + "lng", location.getLng());
        if (location.getDescription() != null)
            childUpdates.put(id + "/" + "description", location.getDescription());
        if (location.getRating() != 0.0)
            childUpdates.put(id + "/" + "rating", location.getRating());

        for (int i = 0; i < location.getDrinks().size(); i++) {
            childUpdates.put(id + "/drinks/" + Integer.toString(i), location.getDrinks().get(i).getIdFb());
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
