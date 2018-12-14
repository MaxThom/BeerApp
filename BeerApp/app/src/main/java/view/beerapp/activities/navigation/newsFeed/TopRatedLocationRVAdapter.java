package view.beerapp.activities.navigation.newsFeed;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import view.beerapp.R;
import view.beerapp.activities.location_drink.LocationDisplayActivity;
import view.beerapp.entities.Location;

/**
 * The adapter to show drinks in a recycler view.
 */
public class TopRatedLocationRVAdapter extends RecyclerView.Adapter<TopRatedLocationRVAdapter.ViewHolder> {
    private static final int MAX_LENGTH = 20;
    private Context mContext;
    private ArrayList<Location> locations;

    /**
     * Interface to set an onItemClick method to the views
     */
    public interface OnItemClickListener {
        void onItemClick(Location item);
    }

    /**
     * Adapter constructor
     * @param context origin context
     * @param locations locations to be shown in the list
     */
    TopRatedLocationRVAdapter(Context context, ArrayList<Location> locations) {
        this.mContext = context;
        this.locations = locations;
    }

    /**
     * Inflates the viewholder with the nearest top rated locations listview layout
     * @param parent to fetch the parent's context
     * @param viewType viewtype
     * @return the view
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listview_top_rated_locations, parent,false);
        return new ViewHolder(view);
    }

    /**
     * Binds drink data and click listeners to the items in the viewholder
     * @param holder viewholder
     * @param position item list position
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        //Sets the location info to the viewholder
        String title = locations.get(position).getTitle();
        title = title.length() > MAX_LENGTH ? title.substring(0, MAX_LENGTH-3) + "..." : title;
        holder.locationName.setText(title);
        holder.locationRating.setText(Double.toString(locations.get(position).getRating()));

        //Creation du clickListener de chaque item
        OnItemClickListener locationClickListener = new OnItemClickListener() {
            @Override
            public void onItemClick(Location item) {
                Location clickedLocation  = locations.get(position);
                Intent intent = new Intent(mContext, LocationDisplayActivity.class);
                intent.putExtra("LOCATION", clickedLocation.getIdFb());
                intent.putExtra("PARENT", "DASHBOARD");
                mContext.startActivity(intent);
            }
        };

        //Binds the onclick listeners to the views
        holder.bind(locations.get(position), locationClickListener);
    }

    /**
     * Get number of items in the list
     * @return item count
     */
    @Override
    public int getItemCount() {
        return locations.size();
    }

    /**
     * ViewHolder for the locations recycler view
     */
    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView locationName;
        private TextView locationRating;

        /**
         * View holder constructor. Fetches the item's layouts to be bind
         * @param itemView itemview
         */
        ViewHolder(View itemView ) {
            super(itemView);
            this.locationName = itemView.findViewById(R.id.top_rated_location_name);
            this.locationRating = itemView.findViewById(R.id.top_rated_star_rating);
        }

        /**
         * That will bind a Onclicklistener to the location item
         */
        void bind(final Location item, final TopRatedLocationRVAdapter.OnItemClickListener listener) {

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }
    }
}
