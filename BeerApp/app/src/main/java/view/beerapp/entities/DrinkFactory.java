package view.beerapp.entities;

/**
 * Used to create the good type of drink according to the beverage type
 * Factory Pattern
 */
public class DrinkFactory {

    /**
     * Create drink with no params
     * @param beverageType the drink type
     * @return the newly created drink
     */
    public static Drink createDrink(String beverageType) {
        switch (beverageType) {
            case "Beer":
                return new Beer();
            case "Wine":
                return new Wine();
            case "Cocktail":
                return new Cocktail();
            default:
                return null;
        }
    }

    /**
     * Create a drink with full parameters list
     * @param name name of the drink
     * @param description description of the drink
     * @param alcoolLevel alcohol level of the drink
     * @param rating rating of the drink
     * @param beverageType beverage type of the drink
     * @return the newly created drink
     */
    public static Drink createDrink(String name, String description, double alcoolLevel, double rating, String beverageType) {
        switch (beverageType) {
            case "Beer":
                return new Beer(name, description, alcoolLevel, rating, beverageType);
            case "Wine":
                return new Wine(name, description, alcoolLevel, rating, beverageType);
            case "Cocktail":
                return new Cocktail(name, description, alcoolLevel, rating, beverageType);
            default:
                return null;
        }
    }

}
