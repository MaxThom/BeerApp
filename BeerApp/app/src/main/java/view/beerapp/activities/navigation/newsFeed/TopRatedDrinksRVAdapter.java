package view.beerapp.activities.navigation.newsFeed;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import view.beerapp.R;
import view.beerapp.activities.location_drink.DrinkDisplayActivity;
import view.beerapp.entities.Drink;

/**
 * The adapter to show drinks in a recycler view.
 */
public class TopRatedDrinksRVAdapter extends RecyclerView.Adapter<TopRatedDrinksRVAdapter.ViewHolder> {
    private static final int MAX_LENGTH = 25;
    private Context mContext;
    private ArrayList<Drink> drinks;

    /**
     * Interface to set an onItemClick method to the views
     */
    public interface OnItemClickListener {
        void onItemClick(Drink item);
    }

    /**
     * Adapter constructor
     * @param context origin context
     * @param drinks drinks to be shown in the list
     */
    TopRatedDrinksRVAdapter(Context context, ArrayList<Drink> drinks) {
        this.mContext = context;
        this.drinks = drinks;
    }

    /**
     * Inflates the viewholder with the top rated drinks listview layout
     * @param parent to fetch the parent's context
     * @param viewType viewtype
     * @return the view
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listview_top_rated_drinks, parent,false);
        return new ViewHolder(view);
    }

    /**
     * Binds drink data and click listeners to the items in the viewholder
     * @param holder viewholder
     * @param position item list position
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        String name = drinks.get(position).getName();
        name = name.length() > MAX_LENGTH ? name.substring(0, MAX_LENGTH-3) + "..." : name;
        holder.drinkName.setText(name);
        holder.drinkAlcoholRate.setText(Double.toString(drinks.get(position).getAlcoolLevel()) + "%");
        holder.drinkIcon.setImageResource(drinks.get(position).getIcon());

        //Creation of every item's click listener
        OnItemClickListener drinkClickListener = new OnItemClickListener() {
            @Override
            public void onItemClick(Drink item) {
                Drink clickedDrink = drinks.get(position);
                Intent intent = new Intent(mContext, DrinkDisplayActivity.class);
                intent.putExtra("DRINK", clickedDrink.getIdFb());
                mContext.startActivity(intent);
            }
        };

        //Binds the drink to the listener
        holder.bind(drinks.get(position), drinkClickListener);
    }

    /**
     * Get number of items in the list
     * @return item count
     */
    @Override
    public int getItemCount() {
        return drinks.size();
    }

    /**
     * ViewHolder for the drinks recycler view
     */
    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView drinkName;
        private TextView drinkAlcoholRate;
        private ImageView drinkIcon;

        /**
         * View holder constructor. Fetches the item's layout to be bind
         * @param itemView itemview
         */
        ViewHolder(View itemView) {
            super(itemView);
            this.drinkName = itemView.findViewById(R.id.top_rated_location_name);
            this.drinkAlcoholRate = itemView.findViewById(R.id.top_rated_alcohol_rate);
            this.drinkIcon = itemView.findViewById(R.id.top_rated_drink_image);
        }

        /**
         * Method that will bind a Onclicklistener to the drink items
         */
        void bind(final Drink item, final OnItemClickListener listener) {

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }
    }
}
