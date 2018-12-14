package view.beerapp.activities.location_drink;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import view.beerapp.R;
import view.beerapp.entities.Drink;

/**
 * Fragment display a list of drinks that can be selected (as in the addExistingBeerActivity)
 */
public class DrinkSelectFragment extends Fragment {

    private View view;
    private ArrayList<Drink> drinks;
    public HashMap<String, Boolean> drinkSelected;
    private String filter;
    private String locIdFb;

    /**
     * Default constructor
     */
    public DrinkSelectFragment() {
        filter = "";
    }

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
        view = inflater.inflate(R.layout.fragment_drink, container, false);

        String drinkType = this.getArguments().getString("DRINK_TYPE");

        if (drinks == null) {
            drinks = this.getArguments().getParcelableArrayList("DRINK_LIST");
            locIdFb = this.getArguments().getString("LOCATION");
            drinkSelected = new HashMap<>();

            for (Drink dr : drinks) {
                drinkSelected.put(dr.getIdFb(), false);
            }
        }

        setListViewItems(filter);

        return view;
    }

    /**
     * Set the list of view items according to the search filer
     * @param txtFilter the search string
     */
    public void setListViewItems(String txtFilter) {
        this.filter = txtFilter.toLowerCase().trim();
        if (drinks != null) {
            ListView listView = (ListView) view.findViewById(R.id.listLayout);
            listView.setScrollContainer(false);

            // Filter the list
            ArrayList<Drink> drinksFiltered = new ArrayList<>();
            for (int i = 0; i < drinks.size(); i++) {
                if (drinks.get(i).getName().toLowerCase().trim().contains(filter))
                    drinksFiltered.add(drinks.get(i));
            }

            // Recreate the list in the good format
            Drink[] items = new Drink[drinksFiltered.size()];
            for (int i = 0; i < drinksFiltered.size(); i++) {
                items[i] = drinksFiltered.get(i);
            }

            setListViewAdapter(listView, items);
        }
    }

    /**
     * Set the view list with the data and set the click listener
     * @param listView the list view that contain the data
     * @param items the items list to display
     */
    private void setListViewAdapter(final ListView listView, final Drink[] items) {
        CustomListAdapter adapter = new CustomListAdapter(getActivity(), items);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
            Drink selectedItem = items[position];

            drinkSelected.put(selectedItem.getIdFb(), !drinkSelected.get(selectedItem.getIdFb()));
            View v = listView.findViewWithTag(String.valueOf(position));
            if (drinkSelected.get(selectedItem.getIdFb())) {
                v.setBackgroundColor(Color.argb(100, 191, 191, 191));
            } else {
                v.setBackgroundColor(Color.argb(100, 255, 255, 255));
            }
            }
        });
    }

    /**
     * Custom adapter to put a custom list inside a listview
     */
    private class CustomListAdapter extends ArrayAdapter<Drink> {
        private final Activity context;
        private final Drink[] item;

        /**
         * Constructor of the adapter
         * @param context the parent context
         * @param item the list of items to set
         */
        CustomListAdapter(Activity context, Drink[] item) {
            super(context, R.layout.listview_drinks, item);

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
            View rowView = inflater.inflate(R.layout.listview_drinks, null, true);

            ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
            TextView txtTitle = (TextView) rowView.findViewById(R.id.item);
            TextView tvDescription = (TextView) rowView.findViewById(R.id.description);
            TextView tvAlcoolLevel = (TextView) rowView.findViewById(R.id.alcoolLevel);
            TextView tvRating = (TextView) rowView.findViewById(R.id.rating);

            imageView.setImageResource(item[position].getIcon());
            txtTitle.setText(item[position].getName());
            tvDescription.setText(item[position].getDescription().length() > 30 ? item[position].getDescription().substring(0, 27) + "..." : item[position].getDescription());
            tvAlcoolLevel.setText(item[position].getAlcoolLevel() + "%");
            tvRating.setText(item[position].getRating() + " / 5.0");

            if (drinkSelected.get(item[position].getIdFb())) {
                rowView.setBackgroundColor(Color.argb(100, 191, 191, 191));
            } else {
                rowView.setBackgroundColor(Color.argb(100, 255, 255, 255));
            }

            rowView.setTag(String.valueOf(position));

            return rowView;
        }
    }
}


