package view.beerapp.contract;

import java.util.ArrayList;

import view.beerapp.entities.Drink;

/**
 * Represent the drink repository
 */
public interface IDrinkRepository {

    /**
     * Add drink to the repository
     * @param drink drink to be added
     * @return the idkey of the newly added drink
     */
    String addDrink(Drink drink);

    /**
     * Get the list of drinks from the repository
     * @return the list of drinks
     */
    ArrayList<Drink> getDrinks();

    /**
     * Get a single drink
     * @param id id of the drink
     * @return the drink
     */
    Drink getDrink(String id);

    /**
     * Delete a drink from the repository
     * @param id the drink to be deleted
     * @return the deleted drink
     */
    Drink deleteDrink(String id);

    /**
     * Updated a drink in the repository
     * @param id id of the drink
     * @param drink the drink to be updated
     * @return true if success
     */
    boolean updateDrink(String id, Drink drink);
}
