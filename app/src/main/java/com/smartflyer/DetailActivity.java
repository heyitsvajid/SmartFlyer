package com.smartflyer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

/***
 * Detail Activity that is launched when a list item is clicked.
 * It shows more info on the sport.
 */
public class DetailActivity extends AppCompatActivity {

    /**
     * Initializes the activity, filling in the data from the Intent.
     *
     * @param savedInstanceState Contains information about the saved state
     *                           of the activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Initialize the views.
        TextView iata = findViewById(R.id.iata);
        TextView name= findViewById(R.id.name);
        TextView address = findViewById(R.id.address);
        ImageView mAirportImage = findViewById(R.id.airportImage);

        // Set the text from the Intent extra.

        iata.setText(getIntent().getStringExtra("iata"));
        name.setText(getIntent().getStringExtra("name"));
        address.setText(getIntent().getStringExtra("city") + ", " + getIntent().getStringExtra("country"));

        // Load the image using the Glide library and the Intent extra.
//        Glide.with(this).load(getIntent().getIntExtra("image_resource",0))
//                .into(sportsImage);
        Glide.with(this)
                .load("https://d13k13wj6adfdf.cloudfront.net/urban_areas/San_Francisco_9q8yy-68f5c7173b.jpg") // image url
                .placeholder(R.drawable.app_logo) // any placeholder to load at start
                .error(R.drawable.app_logo)  // any image in case of error
                .override(200, 200) // resizing
                .centerCrop()
                .into(mAirportImage);  // imageview object

    }
}