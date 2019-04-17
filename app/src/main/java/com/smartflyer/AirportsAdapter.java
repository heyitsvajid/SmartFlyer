package com.smartflyer;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashMap;

/***
 * The adapter class for the RecyclerView, contains the airports data.
 */
public class AirportsAdapter extends RecyclerView.Adapter<AirportsAdapter.ViewHolder>  {

    // Member variables.
    private ArrayList<Airport> mAirportsData;
    private Context mContext;

    /**
     * Constructor that passes in the airports data and the context.
     *
     * @param airportsData ArrayList containing the airports data.
     * @param context Context of the application.
     */
    AirportsAdapter(Context context, ArrayList<Airport> airportsData) {
        this.mAirportsData = airportsData;
        this.mContext = context;
    }


    /**
     * Required method for creating the viewholder objects.
     *
     * @param parent The ViewGroup into which the new View will be added
     *               after it is bound to an adapter position.
     * @param viewType The view type of the new View.
     * @return The newly created ViewHolder.
     */
    @Override
    public AirportsAdapter.ViewHolder onCreateViewHolder(
            ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).
                inflate(R.layout.list_item, parent, false));
    }

    /**
     * Required method that binds the data to the viewholder.
     *
     * @param holder The viewholder into which the data should be put.
     * @param position The adapter position.
     */
    @Override
    public void onBindViewHolder(AirportsAdapter.ViewHolder holder,
                                 int position) {
        // Get current airport.
        Airport currentAirport = mAirportsData.get(position);

        // Populate the textviews with data.
        holder.bindTo(currentAirport);
    }

    /**
     * Required method for determining the size of the data set.
     *
     * @return Size of the data set.
     */
    @Override
    public int getItemCount() {
        return mAirportsData.size();
    }

    /**
     * ViewHolder class that represents each row of data in the RecyclerView.
     */
    class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener{

        // Member Variables for the TextViews
        private TextView icao;
        private TextView name;
        private ImageView mAirportImage;
        private TextView address;
        private ImageView openMapButton;
        /**
         * Constructor for the ViewHolder, used in onCreateViewHolder().
         *
         * @param itemView The rootview of the list_item.xml layout file.
         */
        ViewHolder(View itemView) {
            super(itemView);

            // Initialize the views.
            icao = itemView.findViewById(R.id.icao);
            name= itemView.findViewById(R.id.name);
            address = itemView.findViewById(R.id.address);
            mAirportImage = itemView.findViewById(R.id.airportImage);
            openMapButton = itemView.findViewById(R.id.mapButton);
            // Set the OnClickListener to the entire view.
            itemView.setOnClickListener(this);
        }

        void bindTo(Airport currentAirport){
            // Populate the textviews with data.
            name.setText(currentAirport.getName());
            icao.setText(currentAirport.getIata());
            address.setText(currentAirport.getCity() + ", " + currentAirport.getCountry());
            HashMap<String,String> latLng = new HashMap<>();
            latLng.put("lat",currentAirport.getLatitude());
            latLng.put("lng",currentAirport.getLongitude());
            openMapButton.setTag(latLng);
            // Load the images into the ImageView using the Glide library.
            Glide.with(mContext)
                    .load(currentAirport.getImage()) // image url
                    .placeholder(R.drawable.app_logo) // any placeholder to load at start
                    .error(R.drawable.app_logo)  // any image in case of error
                    .override(200, 200) // resizing
                    .centerCrop()
                    .into(mAirportImage);  // imageview object
        }

        /**
         * Handle click to show DetailActivity.
         *
         * @param view View that is clicked.
         */
        @Override
        public void onClick(View view) {
            Airport currentAirport = mAirportsData.get(getAdapterPosition());
            Intent detailIntent = new Intent(mContext, DetailActivity.class);
            detailIntent.putExtra("_id", currentAirport.get_id());
            detailIntent.putExtra("name", currentAirport.getName());
            detailIntent.putExtra("city", currentAirport.getCity());
            detailIntent.putExtra("country", currentAirport.getCountry());
            detailIntent.putExtra("iata", currentAirport.getIata());
            detailIntent.putExtra("image", currentAirport.getImage());
            mContext.startActivity(detailIntent);
        }
    }
}