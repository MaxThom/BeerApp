package view.beerapp.contract;

import java.util.ArrayList;

import view.beerapp.entities.User;

/**
 * Represent the user repository
 */
public interface IUserRepository {

    /**
     * Add a user to the user repository
     * @param user the user to be added
     * @return the id key of the added user
     */
    String addUser(User user);

    /**
     * Get the list of users in the repository
     * @return list of users
     */
    ArrayList<User> getUsers();

    /**
     * Get a single user in the repository
     * @param id id the user to get
     * @return the user
     */
    User getUser(String id);

    /**
     * Delete a user in the repository
     * @param id id of the user to delete
     * @return the deleted user
     */
    User deleteUser(String id);

    /**
     * Update a user in the repository
     * @param id id of the user to be updated
     * @param user the user to be updated
     * @return true if success
     */
    boolean updateUser(String id, User user);

}
