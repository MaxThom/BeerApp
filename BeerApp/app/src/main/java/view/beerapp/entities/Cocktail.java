package view.beerapp.entities;

import view.beerapp.R;

/**
 * Represent a Cocktail, a drink type
 */
public class Cocktail extends Drink {

    /**
     * Default constructor
     */
    public Cocktail() {
        super();
    }

    /**
     * Constructor with all params
     * @param name name of the drink
     * @param description description of the drink
     * @param alcoolLevel alcohol level of the drink
     * @param rating rating of the drink
     * @param beverageType beverage type of the drink
     */
    public Cocktail(String name, String description, double alcoolLevel, double rating, String beverageType) {
        super(name, description, alcoolLevel, rating, beverageType);
    }

    /**
     * Return the cocktail icon
     * @return Drawable of the icon
     */
    @Override
    public int getIcon() {
        return R.drawable.cocktail_placeholder;
    }
}
