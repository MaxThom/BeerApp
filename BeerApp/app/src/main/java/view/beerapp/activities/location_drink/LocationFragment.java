package view.beerapp.activities.location_drink;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import view.beerapp.R;
import view.beerapp.entities.Location;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Fragment to display a list of location
 */
public class LocationFragment extends Fragment {

    /**
     * Default constructor
     */
    public LocationFragment() { }

    /**
     * On create
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
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
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_drink, container, false);

        String drinkType = this.getArguments().getString("LOCATION_TYPE");
        ArrayList<Location> locations = this.getArguments().getParcelableArrayList("LOCATION_LIST");
        if (locations != null) {
            ListView listView = (ListView) view.findViewById(R.id.listLayout);
            listView.setScrollContainer(false);

            Location[] items = new Location[locations.size()];
            for (int i = 0; i < locations.size(); i++) {
                items[i] = locations.get(i);
            }

            setListViewAdapter(listView, items);
        }

        return view;
    }

    /**
     * Set the view list with the data and set the click listener
     * @param listView the list view that contain the data
     * @param items the items list to display
     */
    private void setListViewAdapter(ListView listView, final Location[] items) {
        CustomListAdapter adapter = new CustomListAdapter(getActivity(), items);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
            Location selectedItem = items[+position];

            final Context context = getApplicationContext();

            Intent intent = new Intent(context, LocationDisplayActivity.class);
            intent.putExtra("LOCATION", selectedItem.getIdFb());
            intent.putExtra("PARENT", "LOCATIONS");
            startActivityForResult(intent, 1);
            }
        });
    }

    /**
     * Custom adapter to put a custom list inside a listview
     */
    private class CustomListAdapter extends ArrayAdapter<Location> {

        private final Activity context;
        private final Location[] item;

        /**
         * Constructor of the adapter
         * @param context the parent context
         * @param item the list of items to set
         */
        CustomListAdapter(Activity context, Location[] item) {
            super(context, R.layout.listview_locations, item);
            this.context = context;
            this.item = item;
        }

        /**
         * Set the text for the view of a single row
         * @param position position of the row
         * @param view the view
         * @param parent the parent of the list view
         * @return the view
         */
        public View getView(int position, View view, ViewGroup parent) {
            LayoutInflater inflater = context.getLayoutInflater();
            View rowView = inflater.inflate(R.layout.listview_locations, null, true);

            TextView txtTitle = (TextView) rowView.findViewById(R.id.item);
            TextView tvAddress = (TextView) rowView.findViewById(R.id.address);
            TextView tvRating = (TextView) rowView.findViewById(R.id.rating);

            txtTitle.setText(item[position].getTitle());
            tvAddress.setText(item[position].getFormattedAddress().length() > 30 ? item[position].getFormattedAddress().substring(0, 27) + "..." : item[position].getFormattedAddress());
            tvRating.setText(item[position].getRating() + " / 5.0");

            return rowView;
        }
    }
}


