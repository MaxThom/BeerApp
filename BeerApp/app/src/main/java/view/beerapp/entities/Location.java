package view.beerapp.entities;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Represent a location in the application
 */
public class Location implements Parcelable {

    private String idFb;
    private String title;
    private String formattedAddress;
    private double lat;
    private double lng;
    private String description;
    private double rating;

    private List<Drink> drinks;

    /**
     * Default constructor
     */
    public Location() {
        drinks = new ArrayList<>();
    }

    /**
     * Constructor with all params
     * @param title title of the location
     * @param address address  of the location
     * @param description description of the location
     * @param rating rating of the location
     * @param lat latitude of the location
     * @param lng longitude of the location
     */
    public Location(String title, String address, String description, double rating, double lat, double lng){
        this.title = title;
        this.formattedAddress = address;
        this.description = description;
        this.rating = rating;
        this.lat = lat;
        this.lng = lng;
        drinks = new ArrayList<>();
    }

    /**
     * Transform the location in a readable string
     * @return string containing all the information
     */
    public String toString() {
        StringBuilder text = new StringBuilder();
        text.append(title + "\n");
        text.append(formattedAddress + "\n");
        text.append(description + "\n");
        text.append(rating + " / 5.0\n");
        return text.toString();
    }

    /**
     * Comparator on the rating of the location to sort a list of locations
     */
    public static Comparator<Location> ratingComparator = new Comparator<Location>() {
        public int compare(Location b1, Location b2) {
            if (b1.rating > b2.rating)
                return -1;
            else if (b1.rating < b2.rating)
                return 1;
            else
                return 0;
        }
    };

    //
    // Parcelling part
    //

    /**
     * Constructor for a parcel of a location
     * @param in in parcel
     */
    public Location(Parcel in){
        this.title = in.readString();
        this.formattedAddress = in.readString();
        this.lat = in.readDouble();
        this.lng = in.readDouble();
        this.description = in.readString();
        this.rating = in.readDouble();
        this.idFb = in.readString();

        this.drinks = new ArrayList<Drink>();
        in.readTypedList(drinks, Drink.CREATOR);
    }

    /**
     * Clone a location
     * @param value the location to be cloned
     */
    public void clone(Location value) {
       this.title = value.title;
       this.formattedAddress = value.formattedAddress;
       this.lat = value.lat;
       this.lng = value.lng;
       this.description = value.description;
       this.rating = value.rating;
       this.drinks = value.drinks;
       this.idFb = value.idFb;
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
     * Write a location into parcel format
     * @param out the out parcel
     * @param flags options with the parcel
     */
    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(title);
        out.writeString(formattedAddress);
        out.writeDouble(lat);
        out.writeDouble(lng);
        out.writeString(description);
        out.writeDouble(rating);
        out.writeTypedList(drinks);
        out.writeString(idFb);
    }

    /**
     * Creator of a drink parcel
     */
    public static final Parcelable.Creator<Location> CREATOR
            = new Parcelable.Creator<Location>() {
        public Location createFromParcel(Parcel in) {
            return new Location(in);
        }

        public Location[] newArray(int size) {
            return new Location[size];
        }
    };

    //
    // Getters and Setters
    //

    /**
     * Get location key
     * @return key
     */
    public String getIdFb() {
        return idFb;
    }

    /**
     * Set location key
     * @param idFb key
     */
    public void setIdFb(String idFb) {
        this.idFb = idFb;
    }

    /**
     * Get title
     * @return title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Set title
     * @param title title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Get formatted address
     * @return formatted address
     */
    public String getFormattedAddress() {
        return formattedAddress;
    }

    /**
     * Set formatted address
     * @param formattedAddress formatted address
     */
    public void setFormattedAddress(String formattedAddress) {
        this.formattedAddress = formattedAddress;
    }

    /**
     * Get the latitude
     * @return latitude
     */
    public double getLat() {
        return lat;
    }

    /**
     * Set latitude
     * @param lat latitude to be setb
     */
    public void setLat(double lat) {
        this.lat = lat;
    }

    /**
     * Get longitude
     * @return longitude
     */
    public double getLng() {
        return lng;
    }

    /**
     * Set longitude
     * @param lng longitude
     */
    public void setLng(double lng) {
        this.lng = lng;
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
     * Return the list of drinks
     * @return list of drinks
     */
    public List<Drink> getDrinks() {
        return drinks;
    }
}
