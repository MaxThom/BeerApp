package view.beerapp.activities.navigation;

/**
 * Created by christophe on 26/03/18.
 */

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.google.firebase.auth.FirebaseAuth;

import view.beerapp.R;
import view.beerapp.activities.login.LoginActivity;
import view.beerapp.activities.navigation.newsFeed.DashBoard;

/**
 * Contains the basics to use the navigation drawer. All the activities that use it will
 * inherit this class, except the map activity.
 */
public class BaseActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;

    /**
     * The method to create the navigation drawer. This method will be called in every onCreate
     * method in activities which have to use the navigation drawer.
     */
    protected void onCreateDrawer() {

        //Fetch our drawer
        mDrawerLayout = findViewById(R.id.drawer_layout);

        //Sets up the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);

        //Goes to the right activity depending on the clicked item menu
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(

                new NavigationView.OnNavigationItemSelectedListener() {
                    public boolean onNavigationItemSelected(MenuItem menuItem) {

                        Intent navigationIntent = null;

                        // set item as selected to persist highlight
                        menuItem.setChecked(true);

                        // close drawer when item is tapped
                        mDrawerLayout.closeDrawers();

                        // Handle navigation view item clicks here.
                        int id = menuItem.getItemId();

                        if (id == R.id.nav_map) {
                            navigationIntent = new Intent(getBaseContext(), MapsActivity.class);
                        }
                        else if (id == R.id.nav_home) {
                            navigationIntent = new Intent(getBaseContext(), DashBoard.class);
                        }
                        else if (id == R.id.nav_friends) {
                            navigationIntent = new Intent(getBaseContext(), FriendsActivity.class);
                        }
                        else if (id == R.id.nav_profile) {
                            navigationIntent = new Intent(getBaseContext(), ProfileActivity.class);
                        }
                        else if (id == R.id.nav_beers) {
                            navigationIntent = new Intent(getBaseContext(), DrinkListActivity.class);
                        }
                        else if (id == R.id.nav_locations) {
                            navigationIntent = new Intent(getBaseContext(), LocationsActivity.class);
                        }
                        else if(id == R.id.nav_settings){
                            navigationIntent = new Intent(getBaseContext(), SettingsActivity.class);
                        }
                        else if(id == R.id.nav_logout){
                            FirebaseAuth.getInstance().signOut();
                            navigationIntent = new Intent(getBaseContext(), LoginActivity.class);
                        }

                        startActivity(navigationIntent);
                        finish();

                        return true;
                    }
                });
    }

    /**
     * Opens the navigation drawer when the menu button is clicked in the toolbar
     * @param item menu item
     * @return selected item
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        closeVirtualKeyboard();

        switch (item.getItemId()) {
            //Navigation menu button pressed
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Closes the virtual keyboard if it is opened
     */
    private void closeVirtualKeyboard(){
        //Closes the virtual keyboard if it is opened
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
