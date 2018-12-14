package view.beerapp.entities;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import view.beerapp.R;

/**
 * Represent a drink in the application
 */
public class Drink implements Parcelable {
    // Display of text in the ui
    private static final int MAX_LENGTH = 25;

    private String idFb;
    private String name;
    private String description;
    private String beverageType;
    private double alcoolLevel;
    private double rating;

    private List<Location> locations;

    /**
     * Default Constructor
     */
    public Drink() {
        this.locations = new ArrayList<>();
    }

    /**
     * Constructor with all params
     * @param name name of the drink
     * @param description description of the drink
     * @param alcoolLevel alcohol level of the drink
     * @param rating rating of the drink
     * @param beverageType beverage type of the drink
     */
    public Drink(String name, String description, double alcoolLevel, double rating, String beverageType) {
        this.name = name;
        this.description = description;
        this.alcoolLevel = alcoolLevel;
        this.rating = rating;
        this.beverageType = beverageType;
        this.locations = new ArrayList<>();
    }

    /**
     * Clone constructor to copy a drink
     * @param value drink to be copied
     */
    public void clone(Drink value) {
        this.name = value.name;
        this.description = value.description;
        this.alcoolLevel = value.alcoolLevel;
        this.rating = value.rating;
        this.locations = value.locations;
        this.beverageType = value.beverageType;
        this.idFb = value.idFb;
    }

    /**
     * Print the drink information
     * @return string containing the drink information
     */
    public String toDisplay() {
        StringBuilder message = new StringBuilder();

        message.append(name.length() > MAX_LENGTH ? name.substring(0, MAX_LENGTH-3) + "..." : name);
        message.append("\n");
        message.append(description.length() > MAX_LENGTH ? description.substring(0, MAX_LENGTH-3) + "..." : description);
        message.append("\n");
        message.append(alcoolLevel + "%");
        message.append("\n");
        message.append( rating + " / 5");

        return message.toString();
    }

    /**
     * Get the correct icon according to the drink type
     * @return the drawable
     */
    public int getIcon() {
        switch (beverageType) {
            case "Beer":
                return R.drawable.beer_placeholder;
            case "Wine":
                return R.drawable.wine_placeholder;
            case "Cocktail":
                return R.drawable.cocktail_placeholder;
            default:
                return R.drawable.beer_placeholder;
        }
    }

    /**
     * Comparator on the rating to sort a list of drink
     */
    public static Comparator<Drink> ratingComparator = new Comparator<Drink>() {
        public int compare(Drink b1, Drink b2) {
            if (b1.rating > b2.rating)
                return -1;
            else if (b1.rating < b2.rating)
                return 1;
            else
                return 0;
        }
    };

    /**
     * Constructor for receiving a drink in parcel
     * @param in
     */
    public Drink(Parcel in){
        this.name = in.readString();
        this.description = in.readString();
        this.alcoolLevel = in.readDouble();
        this.rating = in.readDouble();
        this.beverageType = in.readString();
        this.idFb = in.readString();

        this.locations = new ArrayList<>();
    }

    /**
     * Used to parcel child object of drink. Not used in this case
     * @return nothing
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Write a drink into parcel format
     * @param out the out parcel
     * @param flags options with the parcel
     */
    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(name);
        out.writeString(description);
        out.writeDouble(alcoolLevel);
        out.writeDouble(rating);
        out.writeString(beverageType);
        out.writeString(idFb);
    }

    /**
     * Creator of a drink parcel
     */
    public static final Parcelable.Creator<Drink> CREATOR
            = new Parcelable.Creator<Drink>() {
        public Drink createFromParcel(Parcel in) {
            return new Drink(in);
        }

        public Drink[] newArray(int size) {
            return new Drink[size];
        }
    };

    //
    // Getters and Setters
    //

    /**
     * Get drink key
     * @return drink key
     */
    public String getIdFb() {
        return idFb;
    }

    /**
     * Set drink key
     * @param idFb drink key
     */
    public void setIdFb(String idFb) {
        this.idFb = idFb;
    }

    /**
     * Get name
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Set name
     * @param name name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get description
     * @return description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set description
     * @param description description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Get beverage type
     * @return beverate type
     */
    public String getBeverageType() {
        return beverageType;
    }

    /**
     * Set beverage type
     * @param beverageType beverage type
     */
    public void setBeverageType(String beverageType) {
        this.beverageType = beverageType;
    }

    /**
     * Get Alcohol level
     * @return Alcohol level
     */
    public double getAlcoolLevel() {
        return alcoolLevel;
    }

    /**
     * Set alcohol level
     * @param alcoolLevel alcohol level
     */
    public void setAlcoolLevel(double alcoolLevel) {
        this.alcoolLevel = alcoolLevel;
    }

    /**
     * Get rating
     * @return rating
     */
    public double getRating() {
        return rating;
    }

    /**
     * Set rating
     * @param rating rating
     */
    public void setRating(double rating) {
        this.rating = rating;
    }

    /**
     * Return the list of locations
     * @return locations list
     */
    public List<Location> getLocations() {
        return locations;
    }
}
