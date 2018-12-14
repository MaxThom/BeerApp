package view.beerapp.contract;

import java.util.ArrayList;

import view.beerapp.entities.Drink;
import view.beerapp.entities.Location;

/**
 * Represent the location repository
 */
public interface ILocationRepository {

    /**
     * Add location to the location repository
     * @param location the location to be added
     * @return the id key of the newly added location
     */
    String addLocation(Location location);

    /**
     * Return the list of locations in the repository
     * @return list of locations
     */
    ArrayList<Location> getLocations();

    /**
     * Get a single location from the repository
     * @param id the id of the location
     * @return the locations
     */
    Location getLocation(String id);

    /**
     * Delete a location from the repository
     * @param id the id of the location to be deleted
     * @return the deleted locations
     */
    Location deleteLocation(String id);

    /**
     * Update a location
     * @param id id of the location to be updated
     * @param location location to be updated
     * @return true if success
     */
    boolean updateLocation(String id, Location location);
}
