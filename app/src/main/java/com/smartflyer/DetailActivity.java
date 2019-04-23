package com.smartflyer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/***
 * Detail Activity that is launched when a list item is clicked.
 * It shows more info on the sport.
 */
public class DetailActivity extends AppCompatActivity {

    private SQLiteHandler sqLiteHandler;
    private NumberPicker hourPicker;
    private NumberPicker minutePicker;
    private RelativeLayout tv;
    private ListView listView;

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
        sqLiteHandler = new SQLiteHandler(this);
        tv = (RelativeLayout) findViewById(R.id.waitTimeForm);
        listView = (ListView) findViewById(R.id.waitTimeList);

        //check user login state
        HashMap<String, String> user = sqLiteHandler.getLoggedInUser();
        if (user == null) {
            Intent intent = new Intent(DetailActivity.this, MainActivity.class);
            startActivity(intent);
            Toast.makeText(getBaseContext(), "Login to continue", Toast.LENGTH_LONG).show();
            finish();
        }
        setLayoutVisible(tv);
        getUserTrends();

        hourPicker = findViewById(R.id.hourPicker);
        minutePicker = findViewById(R.id.minutePicker);
        hourPicker.setMaxValue(10);
        hourPicker.setMinValue(0);
        minutePicker.setMaxValue(60);
        minutePicker.setMinValue(0);

        // Initialize the views.
        TextView iata = findViewById(R.id.iata);
        TextView name = findViewById(R.id.name);
        TextView address = findViewById(R.id.address);
        TextView averageWaitTime = findViewById(R.id.averageWaitTime);
        ImageView mAirportImage = findViewById(R.id.airportImage);


        // Set the text from the Intent extra.

        iata.setText(getIntent().getStringExtra("iata"));
        name.setText(getIntent().getStringExtra("name"));
        address.setText(getIntent().getStringExtra("city") + ", " + getIntent().getStringExtra("country"));
        averageWaitTime.setText("" + getIntent().getStringExtra("averageWaitTime"));

        // Load the image using the Glide library and the Intent extra.
        Glide.with(this)
                .load(getIntent().getStringExtra("image")) // image url
                .placeholder(R.drawable.app_logo) // any placeholder to load at start
                .error(R.drawable.app_logo)  // any image in case of error
                .override(200, 200) // resizing
                .centerCrop()
                .into(mAirportImage);  // imageview object

    }

    public void logout(View v) {
        GoogleSignInClient mGoogleSignInClient;
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        Toast.makeText(getBaseContext(), "Logout Called", Toast.LENGTH_LONG).show();
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        HashMap<String, String> user = sqLiteHandler.getLoggedInUser();
                        sqLiteHandler.logoutUser(user.get("email"));
                        Intent intent = new Intent(DetailActivity.this, MainActivity.class);
                        startActivity(intent);
                        Toast.makeText(getBaseContext(), "Login to continue", Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar actions click
        switch (item.getItemId()) {
            case R.id.rate_app:
                RatingDialogue ratingDialogue = new RatingDialogue(DetailActivity.this);
                ratingDialogue.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                ratingDialogue.setCancelable(true);
                ratingDialogue.setTitle("Rate App");
                ratingDialogue.show();
                return true;
            case R.id.action_logout:
                logout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public void logout() {
        GoogleSignInClient mGoogleSignInClient;
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        Toast.makeText(getBaseContext(), "Logout Called", Toast.LENGTH_LONG).show();
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        HashMap<String, String> user = sqLiteHandler.getLoggedInUser();
                        sqLiteHandler.logoutUser(user.get("email"));
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        Toast.makeText(getBaseContext(), "Login to continue", Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
    }

    public void submitWaitTime(View v) {
        int hour = hourPicker.getValue();
        int minute = minutePicker.getValue();

        if (hour == 0 && minute == 0) {
            Toast.makeText(this, "Select time before submit.", Toast.LENGTH_LONG).show();
            return;
        }

        HashMap<String, String> user = sqLiteHandler.getLoggedInUser();
        final int wait = minute + (60 * hour);
        final String email = user.get("email");
        final String id = getIntent().getStringExtra("_id");
        final Context con = this;

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        HashMap<String, String> params = new HashMap<String, String>();
                        params.put("email", email); // the entered data as the body.
                        params.put("wait", String.valueOf(wait));
                        params.put("id", id);
                        final String URL = Config.BACKEND_URL + "airport/addUserWaitTime"; // your URL
                        final RequestQueue queue = Volley.newRequestQueue(con);

                        queue.start();
                        JsonObjectRequest jsObjRequest = new
                                JsonObjectRequest(Request.Method.POST,
                                URL,
                                new JSONObject(params),
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        try {
                                            Toast.makeText(con, "Time Submitted Successfully.", Toast.LENGTH_LONG).show();
                                            setLayoutInvisible(tv);
                                            getUserTrends();
                                        } catch (Exception e) {
                                            Toast.makeText(con, "Error submitting time.", Toast.LENGTH_LONG).show();
                                            System.out.println("Inner NOT WORKING!!" + e.getMessage());
                                            e.printStackTrace();
                                        }
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(con, "Error submitting time.", Toast.LENGTH_LONG).show();
                                System.out.println("Outer NOT WORKING!!" + error.getMessage());
                            }
                        });
                        queue.add(jsObjRequest);
                    }
                }, 3000);
    }

    public void setLayoutInvisible(RelativeLayout tv) {
        if (tv.getVisibility() == View.VISIBLE) {
            tv.setVisibility(View.GONE);
        }
    }

    public void setLayoutVisible(RelativeLayout tv) {
        if (tv.getVisibility() == View.GONE) {
            tv.setVisibility(View.VISIBLE);
        }
    }

    public void getUserTrends() {

        final String id = getIntent().getStringExtra("_id");
        final Context con = this;
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        HashMap<String, String> params = new HashMap<String,String>();
                        params.put("id", id); // the entered data as the body.
                        final String URL = Config.BACKEND_URL + "airport/getOne"; // your URL
                        final RequestQueue queue = Volley.newRequestQueue(con);
                        queue.start();
                        JsonObjectRequest jsObjRequest = new
                                JsonObjectRequest(Request.Method.POST,
                                URL,
                                new JSONObject(params),
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        try {
                                            JSONArray waitTimes = (JSONArray) response.get("waittimes");
                                            System.out.println(waitTimes);
                                            List<String> wait = new ArrayList<>();

                                            for(int i = 0; i<waitTimes.length() ; i++){
                                                JSONObject current = (JSONObject)waitTimes.get(i);
                                                String waitString = current.getString("created");
                                                try{
                                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                                                    Date pubDate = sdf.parse(waitString);
                                                    SimpleDateFormat dateString = new SimpleDateFormat("MM-dd-yyyy");
                                                    SimpleDateFormat timeString = new SimpleDateFormat("HH:mm:ss");

                                                    waitString = "Waited " +
                                                            current.getString("wait") + " mins" +
                                                            " on " + new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(pubDate) +
                                                            " at " + new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(pubDate);

                                                //pubDate.get   new String(pubDate.toString());
                                                }catch (Exception e){
                                                    e.printStackTrace();
                                                }
                                                wait.add(waitString);
                                            }
                                            setListViewItems(wait);
                                        } catch (JSONException e) {
                                            System.out.println("Inner NOT WORKING!!" + e.getMessage());
                                            e.printStackTrace();
                                        }
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                System.out.println("Outer NOT WORKING!!"+ error.getMessage());
                            }
                        });
                        queue.add(jsObjRequest);
                    }
                }, 3000);

    }

    public void setListViewItems(List<String> wait){
        if(wait.isEmpty()){
            wait.add("No trends available currently.");
            wait.add("");
        }
        WaitTimeListAdapter whatever = new WaitTimeListAdapter(this, wait);
        listView.setAdapter(whatever);
        setListViewHeightBasedOnChildren(listView);
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) return;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(),
                View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0) view.setLayoutParams(new
                    ViewGroup.LayoutParams(desiredWidth,
                    RelativeLayout.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();

        params.height = totalHeight + (listView.getDividerHeight() *
                (listAdapter.getCount() - 1));

        listView.setLayoutParams(params);
        listView.requestLayout();

    }
}