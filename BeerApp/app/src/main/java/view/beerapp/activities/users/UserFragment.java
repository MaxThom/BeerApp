package view.beerapp.activities.users;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import view.beerapp.R;
import view.beerapp.entities.User;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * User Fragment Activity base on showing a list View with users(Friends and Not Friends of current user),
 * where it's possible to select a specified user and see his/her profile, or search him/her.
 */
public class UserFragment extends android.support.v4.app.Fragment {

    private ListView listView;
    private ArrayList<User> users = null;
    public HashMap<String, Boolean> userSelected;
    private String filter;

    /**
     * Default constructor
     */
    public UserFragment() {
        filter = "";
    }

    /**
     * On create
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Generate the list when created
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return a view of the list
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);

        //Initialize list View of users
        listView = (ListView) view.findViewById(R.id.listLayout);
        listView.setScrollContainer(false);

        String userType = this.getArguments().getString("USER_TYPE");
        if (users == null) {
            users = this.getArguments().getParcelableArrayList("USER_LIST");
            userSelected = new HashMap<>();

            for (User users : users) {
                userSelected.put(users.idFb, false);
            }
        }

        refreshArray(users);
        setListViewItems(filter);

        return view;
    }

    /**
     * Set List View Items based on the search View usage, where the user can see people whose names
     * or emails start with txtFilter.
     *
     * @param txtFilter text that user write at Search View to find a specific user
     */
    public void setListViewItems(String txtFilter) {
        this.filter = txtFilter.toLowerCase().trim();
        if (users != null) {
            listView.setScrollContainer(false);

            ArrayList<User> usersFiltered = new ArrayList<>();
            for (int i = 0; i < users.size(); i++) {
                if(users.get(i).getFirstName() != null && users.get(i).getLastName() != null) {
                    String fullName = users.get(i).getFirstName().toLowerCase().trim() + " " +
                            users.get(i).getLastName().toLowerCase().trim();
                    if (fullName.contains(filter)) {
                        usersFiltered.add(users.get(i));
                    }
                }
                else if (users.get(i).getEmail().toLowerCase().trim().contains(filter)){
                    usersFiltered.add(users.get(i));
                }
            }

            User[] items = new User[usersFiltered.size()];
            for (int i = 0; i < usersFiltered.size(); i++) {
                items[i] = usersFiltered.get(i);
            }

            setListViewAdapter(listView, items);
        }
    }

    /**
     * Refresh List used whenever a user delete/add a friend and the list must be changed
     * and refreshed.
     *
     * @param users new list of user's (Friends and Not Friends)
     */
    public void refreshArray(ArrayList<User> users) {
        if (users != null) {
            listView.setScrollContainer(false);

            User[] items = new User[users.size()];
            for (int i = 0; i < users.size(); i++) {
                items[i] = users.get(i);
            }

            setListViewAdapter(listView, items);
        }
    }

    /**
     * Set at the list view an adapter and Click event, when the current user select a person from
     * the list, he/she transferred at FriendProfile, to see the details
     *
     * @param listView the list view containing all data
     * @param items the items to display
     */
    private void setListViewAdapter(ListView listView, final User[] items) {
        CustomListAdapter adapter = new CustomListAdapter(getActivity(), items);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
            User selectedItem = items[+position];

            final Context context = getApplicationContext();

            Intent intent = new Intent(context, FriendProfile.class);
            intent.putExtra("USER", selectedItem.idFb);
            startActivityForResult(intent, 1);
            }
        });
    }

    /**
     * List Adapter of the list view that is gonna initialize the items
     */
    private static class CustomListAdapter extends ArrayAdapter<User> {

        private final Activity context;
        private final User[] item;

        /**
         * Constructor of the adapter
         * @param context the parent context
         * @param item the list of items to set
         */
        CustomListAdapter(Activity context, User[] item) {
            super(context, R.layout.listview_user, item);
            this.context = context;
            this.item = item;
        }

        /**
         * GetView set the name or email at the position of the list view
         *
         * @param position of list item
         * @param view this view
         * @param parent the parent activity
         * @return view of current state
         */
        public View getView(int position, View view, ViewGroup parent) {
            LayoutInflater inflater = context.getLayoutInflater();
            View rowView = inflater.inflate(R.layout.listview_user, null, true);

            TextView txtName = (TextView) rowView.findViewById(R.id.textView11);

            if (item[position].getFirstName() != null) {
                if (item[position].getLastName() != null) {
                    //If the user has set a first and last name
                    txtName.setText("   " + item[position].getFirstName() + " " + item[position].getLastName());
                } else {
                    //If the user has set only first name
                    txtName.setText("   " + item[position].getFirstName());
                }
            } else {
                //If the user has not yet set first and last name
                txtName.setText("   " + item[position].getEmail());
            }

            return rowView;
        }
    }
}
