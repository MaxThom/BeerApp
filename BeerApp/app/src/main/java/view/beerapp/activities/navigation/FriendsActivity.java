package view.beerapp.activities.navigation;

import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.widget.SearchView;

import java.util.ArrayList;
import java.util.List;

import view.beerapp.R;
import view.beerapp.activities.users.UserFragment;
import view.beerapp.entities.User;
import view.beerapp.repository.FirebaseUserRepository;

/**
 * FriendsActivity class represent the list of users who use this app, and the current user can
 * see which of them his/her friend is and add or delete them.
 * Also the current user has the opportunity to search at the tabs for a specific user
 */
public class FriendsActivity extends BaseActivity {

    private ConstraintLayout activity_friend;
    private SearchView searchView;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private FriendsActivity.ViewPagerAdapter adapter;
    private User currentUser;

    /**
     * Starting point of the activity.
     * @param savedInstanceState saved state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        onCreateDrawer();

        //Initialize .xml objects
        tabLayout = (TabLayout) findViewById(R.id.user_tabs);
        activity_friend = (ConstraintLayout) findViewById(R.id.activity_friend);
        viewPager = (ViewPager) findViewById(R.id.viewpager);

        currentUser = FirebaseUserRepository.getInstance().getCurrentUser();

        setupViewPager(viewPager, FirebaseUserRepository.getInstance().getUsers());

        tabLayout.setupWithViewPager(viewPager);

        initialiseSearchView();
    }

    /**
     * Refresh the list of friend every time when the user add-remove someone.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        adapter.refreshList(FirebaseUserRepository.getInstance().getUsers());
    }

    /**
     * Initialize searchView and get the text to transfer it at setSearchFilter(UserFragment)
     * method to see the specific users
     */
    private void initialiseSearchView() {
        searchView = (SearchView) findViewById(R.id.searchText);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.setSearchFilter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                adapter.setSearchFilter(query);
                return false;
            }
        });
    }

    /**
     * Initialize TabLayout based of the user friends and separate them to
     * Friends and not Friends
     * @param viewPager the Viewpager to add all the data
     * @param data list of data to be displayed
     */
    private void setupViewPager(ViewPager viewPager, ArrayList<User> data) {
        adapter = new FriendsActivity.ViewPagerAdapter(getSupportFragmentManager());

        ArrayList<User> friends = new ArrayList<>();
        ArrayList<User> notFriends = new ArrayList<>();

        for (User usr : data) {
            if (!usr.idFb.equals(currentUser.idFb)) {
                //If a user is at friend list of the current user, add him/her at friend list
                if (currentUser.isUserFriends(usr))
                    friends.add(usr);
                //If not add him/her at not Friend list
                else
                    notFriends.add(usr);
            }
        }

        //Friend Tab
        Bundle bundleFriend = new Bundle();
        bundleFriend.putString("USER_TYPE", "Friends");
        bundleFriend.putParcelableArrayList("USER_LIST", friends);
        UserFragment friend = new UserFragment();
        friend.setArguments(bundleFriend);

        //Not Friend Tab
        Bundle bundleNotFriend = new Bundle();
        bundleNotFriend.putString("USER_TYPE", "Not Friends");
        bundleNotFriend.putParcelableArrayList("USER_LIST", notFriends);
        UserFragment notFriend = new UserFragment();
        notFriend.setArguments(bundleNotFriend);

        adapter.addFragment(friend, getString(R.string.tab_list_friends));
        adapter.addFragment(notFriend, getString(R.string.tab_list_not_friends));

        viewPager.setAdapter(adapter);
    }

    /**
     * The view pager adapter for the fragments
     */
    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<UserFragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();
        private String filter = "";

        /**
         * Constructor of the pager
         * @param manager manager for the super class
         */
        ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        /**
         * Get the fragment at a position
         * @param position index
         * @return fragment
         */
        @Override
        public Fragment getItem(int position) {
            mFragmentList.get(position).setListViewItems(filter);
            return mFragmentList.get(position);
        }

        /**
         * Set the search filter on the different tabs
         * @param txtFilter the string filter
         */
        void setSearchFilter(String txtFilter) {
            this.filter = txtFilter;
            for (UserFragment fragment : mFragmentList) {
                fragment.setListViewItems(filter);
            }
        }

        /**
         * Get the numbers of fragments
         * @return number of fragment
         */
        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        /**
         * ADd a fragment to the tab layout
         * @param fragment the fragment to be added
         * @param title the title of the fragment
         */
        void addFragment(UserFragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        /**
         * Get the title of the fragment
         * @param position index
         * @return the title
         */
        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }

        /**
         * Refresh List whenever the current user add or remove a user
         *
         * @param users new user's friend list
         */
        void refreshList(ArrayList<User> users) {
            ArrayList<User> friends = new ArrayList<>();
            ArrayList<User> notFriends = new ArrayList<>();

            for (User usr : users) {
                if (!usr.idFb.equals(currentUser.idFb)) {
                    if (currentUser.isUserFriends(usr))
                        friends.add(usr);
                    else
                        notFriends.add(usr);
                }
            }

            mFragmentList.get(0).refreshArray(friends);
            mFragmentList.get(1).refreshArray(notFriends);
        }
    }
}

