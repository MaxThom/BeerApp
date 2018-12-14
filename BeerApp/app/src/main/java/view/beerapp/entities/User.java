package view.beerapp.entities;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Entity user represent the profile of the person that login. A user can set his/her personal data
 * , edit them and have a list of friends and favorite drinks
 */

public class User implements Parcelable {

    public String idFb;
    public int id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String address;

    private ArrayList<Drink> favoriteDrinks;
    public ArrayList<User> friends;

    /**
     * Default constructor
     */
    public User(){
        friends = new ArrayList<>();
        favoriteDrinks = new ArrayList<>();
    }

    /**
     * Constructor with email
     * @param email email of the user
     */
    public User(String email){
        this.email = email;
        friends = new ArrayList<>();
        favoriteDrinks = new ArrayList<>();
    }

    /**
     * Constructor with full list of params
     * @param firstName first name of the user
     * @param lastName last name of the user
     * @param email email of the user
     * @param phone phone of the user
     * @param address address of the user
     */
    public User(String firstName, String lastName, String email, String phone, String address){
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.address = address;
        friends = new ArrayList<>();
        favoriteDrinks = new ArrayList<>();
    }

    /**
     * Clone a user
     * @param value the user to be cloned
     */
    public void clone(User value) {
        this.firstName = value.firstName;
        this.lastName = value.lastName;
        this.email = value.email;
        this.phone = value.phone;
        this.address = value.address;
        this.friends = value.friends;
        this.favoriteDrinks = value.favoriteDrinks;
    }

    //
    // Getters and Setters
    //

    /**
     * Set the first name
     * @param firstName first name
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Set the last name
     * @param lastName last name
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Set the email
     * @param email the email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Set the phone
     * @param phone the phone
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * Set the address
     * @param address the address
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Get the first name
     * @return the first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Get the last name
     * @return the last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Get the email
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Get the phone
     * @return the phone
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Get the address
     * @return the address
     */
    public String getAddress() {
        return address;
    }

    /**
     * Get the list of friends
     * @return list of users
     */
    public ArrayList<User> getFriends() {return friends;}

    /**
     * Delete a friend from the list
     * @param user the user to be deleted
     */
    public void deleteFriend(User user) {
        for (int i = 0 ; i < friends.size() ; i++) {
            if (friends.get(i).idFb.equals(user.idFb)) {
                friends.remove(i);
                break;
            }
        }
    }

    /**
     * Check if a user is on the buddy list
     * @param user the user
     * @return true if present
     */
    public boolean isUserFriends(User user) {
        for (User usr: friends) {
            if (usr.idFb.equals(user.idFb)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Add a drink at favorite drink list
     * @param dr the drink to be added
     */
    public void addFavoriteDrink(Drink dr) {
        this.favoriteDrinks.add(dr);
    }

    /**
     * Remove a drink from the favorite drink list
     * @param dr the drink to be removed
     */
    public void deleteFavoriteDrink(Drink dr) {
        for (int i = 0 ; i < favoriteDrinks.size() ; i++) {
            if (favoriteDrinks.get(i).getIdFb().equals(dr.getIdFb())) {
                favoriteDrinks.remove(i);
                break;
            }
        }
    }

    /**
     * Check if a drink is on the favorite drink list
     * @param dr the drink to check
     * @return true if present
     */
    public boolean isFavoriteDrink(Drink dr) {
        for (int i = 0 ; i < favoriteDrinks.size() ; i++) {
            if (favoriteDrinks.get(i).getIdFb().equals(dr.getIdFb())) {
                return true;
            }
        }
        return false;
    }

    /**
     * get the list of favorites drinks
     * @return list of drinks
     */
    public ArrayList<Drink> getFavoriteDrinks() {
        return this.favoriteDrinks;
    }

    //
    // Parcelling part
    //

    /**
     * Constructor for a parcel user
     * @param in parcel of the user
     */
    public User(Parcel in){
        this.id = in.readInt();
        this.firstName = in.readString();
        this.lastName = in.readString();
        this.phone = in.readString();
        this.address = in.readString();
        this.friends = new ArrayList<>();
    }

    /**
     * Creator of the parcel
     */
    public static final Parcelable.Creator<User> CREATOR
            = new Parcelable.Creator<User>() {
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        public User[] newArray(int size) {
            return new User[size];
        }
    };

    /**
     * Used to parcel child object of drink. Not used in this case
     * @return nothing
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Write a user into parcel format
     * @param out the out parcel
     * @param flags options with the parcel
     */
    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(id);
        out.writeString(firstName);
        out.writeString(lastName);
        out.writeString(email);
        out.writeString(phone);
        out.writeString(address);
    }
}
