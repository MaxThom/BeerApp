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
import view.beerapp.contract.IUserRepository;
import view.beerapp.entities.Drink;
import view.beerapp.entities.User;

/**
 * FirebaseUserRepository is the repository of the user, where all the data are saved into
 * the Database of Firebase. Here a new user can be read, created, edited and deleted.
 * Singleton Pattern
 */
public class FirebaseUserRepository implements IUserRepository, IDatabaseData {
    private static FirebaseUserRepository instance;

    private FirebaseDatabase database;
    private DatabaseReference myRef;

    private ArrayList<User> users;
    private User currentUser;
    private boolean gotFirstTimeData;

    /**
     * Constructor
     */
    private FirebaseUserRepository() {
        //Initialize FirebaseDatabase and DatabaseReference
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("users");
        users = new ArrayList<User>();
        gotFirstTimeData = false;

        setListenerOnNode();
    }

    /**
     * Get the current instance of this singleton
     * @return the instance
     */
    public static FirebaseUserRepository getInstance() {
        if (instance == null) {
            instance = new FirebaseUserRepository();
        }
        return instance;
    }

    /**
     * Set the listener on the node from firebase.
     * This will update the list of users in the app everytime there is a change on firebase
     */
    private void setListenerOnNode() {
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                users = new ArrayList<User>();

                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Map<String, Object> map = (Map<String, Object>) child.getValue();
                    User user = new User();

                    if (map.get("idFb") != null)
                        user.idFb = map.get("idFb").toString();
                    if (map.get("firstName") != null)
                        user.setFirstName(map.get("firstName").toString());
                    if (map.get("lastName") != null)
                        user.setLastName(map.get("lastName").toString());
                    if (map.get("email") != null)
                        user.setEmail(map.get("email").toString());
                    if (map.get("address") != null)
                        user.setAddress(map.get("address").toString());
                    if (map.get("phone") != null)
                        user.setPhone(map.get("phone").toString());

                    //Friend list
                    if (map.get("users") != null) {
                        for (String idUsr : (ArrayList<String>) map.get("users")) {
                            User friend = FirebaseUserRepository.getInstance().getUser(idUsr);
                            if (friend != null) {
                                user.friends.add(friend);
                            } else {
                                friend = new User();
                                friend.idFb = idUsr;
                                user.friends.add(friend);
                            }
                        }
                    }

                    //Favorite drinks list
                    if (map.get("favoriteDrinks") != null) {
                        for (String idDrink : (ArrayList<String>) map.get("favoriteDrinks")) {
                            Drink tmp = FirebaseDrinkRepository.getInstance().getDrink(idDrink);
                            if (tmp != null)
                                user.addFavoriteDrink(tmp);
                            else {
                                tmp = new Drink();
                                tmp.setIdFb(idDrink);
                                user.addFavoriteDrink(tmp);
                            }
                        }
                    }

                    users.add(user);
                }

                // Update user list
                for (User usr : users) {
                    for (int i = 0; i < usr.friends.size(); i++) {
                        usr.friends.set(i, FirebaseUserRepository.getInstance().getUser(usr.friends.get(i).idFb));
                    }
                }

                gotFirstTimeData = true;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    /**
     * Add a new user at Database when he/she want to sign up.
     * @param user the user to be added
     * @return the id key of the user
     */
    @Override
    public String addUser(User user) {
        String usersId = myRef.push().getKey();
        user.idFb = usersId;

        Map<String, Object> childUpdates = new HashMap<>();

        childUpdates.put(usersId + "/" + "idFb", user.idFb);
        if (user.getFirstName() != null)
            childUpdates.put(usersId + "/" + "firstName", user.getFirstName());
        if (user.getLastName() != null)
            childUpdates.put(usersId + "/" + "lastName", user.getLastName());
        if (user.getEmail() != null) childUpdates.put(usersId + "/" + "email", user.getEmail());
        if (user.getPhone() != null) childUpdates.put(usersId + "/" + "phone", user.getPhone());
        if (user.getAddress() != null)
            childUpdates.put(usersId + "/" + "address", user.getAddress());

        myRef.updateChildren(childUpdates);

        return usersId;
    }

    /**
     * Get all the users of Database
     * @return the list of users
     */
    @Override
    public ArrayList<User> getUsers() {
        return users;
    }

    /**
     * Get specific user with his/her id.
     * @param id the user id
     * @return the user
     */
    @Override
    public User getUser(String id) {
        for (User usr : users) {
            if (usr.idFb.equals(id)) {
                return usr;
            }
        }
        return null;
    }

    /**
     * Delete a specific user
     * This method is not implemented
     * @param id user id to be deleted
     * @return the deleted user
     */
    @Override
    public User deleteUser(String id) {
        return null;
    }

    /**
     * Update a user, when he/she changes his/her data, like name, phone, friend list, favorite drink
     * @param id the id of the user to be updated
     * @param user the user to be updated
     * @return true if success
     */
    @Override
    public boolean updateUser(String id, User user) {

        myRef.child(id).child("favoriteDrinks").removeValue();
        myRef.child(id).child("users").removeValue();

        Map<String, Object> childUpdates = new HashMap<>();

        if (user.getFirstName() != null)
            childUpdates.put(id + "/" + "firstName", user.getFirstName());
        if (user.getLastName() != null)
            childUpdates.put(id + "/" + "lastName", user.getLastName());
        if (user.getEmail() != null)
            childUpdates.put(id + "/" + "email", user.getEmail());
        if (user.getPhone() != null)
            childUpdates.put(id + "/" + "phone", user.getPhone());
        if (user.getAddress() != null)
            childUpdates.put(id + "/" + "address", user.getAddress());

        for (int i = 0; i < user.getFavoriteDrinks().size(); i++) {
            childUpdates.put(id + "/favoriteDrinks/" + Integer.toString(i), user.getFavoriteDrinks().get(i).getIdFb());
        }
        for (int i = 0; i < user.friends.size(); i++) {
            childUpdates.put(id + "/users/" + Integer.toString(i), user.friends.get(i).idFb);
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

    /**
     * Set the logged in  user
     * @param user the current user
     */
    public void setCurrentUser(User user) {
        this.currentUser = user;
        for (int i = 0; i < currentUser.friends.size(); i++) {
            currentUser.friends.set(i, FirebaseUserRepository.getInstance().getUser(currentUser.friends.get(i).idFb));
        }
    }

    /**
     * Return the current signed in user
     * @return the user
     */
    public User getCurrentUser() {
        return currentUser;
    }
}
