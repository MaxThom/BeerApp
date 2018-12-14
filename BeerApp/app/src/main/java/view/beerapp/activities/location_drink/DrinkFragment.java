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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import view.beerapp.R;
import view.beerapp.entities.Drink;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Fragment used to show drinks in a non selectable list (as in the drinkListActivity)
 */
public class DrinkFragment extends Fragment {

    private View view;
    private ArrayList<Drink> drinks = null;
    private ListView listView;
    private Activity activity;

    /**
     * Default Constructor
     */
    public DrinkFragment() { }

    /**
     * Attach the context to this fragment
     *
     * @param context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof Activity) {
            this.activity = (Activity) context;
        }
    }

    /**
     * Detach the context of this fragment
     */
    @Override
    public void onDetach() {
        super.onDetach();
        this.activity = null;
    }

    /**
     * OnCreate
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

        listView = (ListView) view.findViewById(R.id.listLayout);
        listView.setScrollContainer(false);

        // Get the extra
        String drinkType = this.getArguments().getString("DRINK_TYPE");
        if (drinks == null)
            drinks = this.getArguments().getParcelableArrayList("DRINK_LIST");

        // Set the list with the items
        setListViewItems(drinks);

        return view;
    }

    /**
     * Set the items on the list.
     * This method is also used to refresh the list
     * @param drinks the list of items to display
     */
    public void setListViewItems(ArrayList<Drink> drinks) {
        if (drinks != null) {
            this.drinks = drinks;

            Drink[] items = new Drink[this.drinks.size()];
            for (int i = 0; i < this.drinks.size(); i++) {
                items[i] = this.drinks.get(i);
            }

            setListViewAdapter(listView, items);
        }
    }

    /**
     * Set the view list with the data and set the click listener
     * @param listView the list view that contain the data
     * @param items the items list to display
     */
    private void setListViewAdapter(ListView listView, final Drink[] items) {
        CustomListAdapter adapter = new CustomListAdapter(activity, items);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                final Context context = getApplicationContext();
                Drink selectedItem = items[+position];

                Intent intent = new Intent(context, DrinkDisplayActivity.class);
                intent.putExtra("DRINK", selectedItem.getIdFb());
                startActivityForResult(intent, 1);
            }
        });
    }

    /**
     * Custom adapter to put a custom list inside a listview
     */
    private static class CustomListAdapter extends ArrayAdapter<Drink> {

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

            return rowView;
        }
    }
}


