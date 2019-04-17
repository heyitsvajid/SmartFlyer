package com.smartflyer;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;


public class SearchAirport extends AppCompatActivity {
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;
    private Boolean mLocationPermissionsGranted = false;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private SQLiteHandler sqLiteHandler;

    private RecyclerView mRecyclerView;
    private ArrayList<Airport> mAirportsData;
    private AirportsAdapter mAdapter;
    private EditText edit_txt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_airport);
        sqLiteHandler = new SQLiteHandler(this);

        //check user login state
        HashMap<String,String> user = sqLiteHandler.getLoggedInUser();
        if(user!=null){
            Toast.makeText(getBaseContext(), "Hello " + user.get("name"), Toast.LENGTH_LONG).show();
        }else{
            Intent intent = new Intent(SearchAirport.this, MainActivity.class);
            startActivity(intent);
            Toast.makeText(getBaseContext(), "Login to continue", Toast.LENGTH_LONG).show();
            finish();
        }

        // Initialize the RecyclerView.
        mRecyclerView = findViewById(R.id.recyclerView);

        // Set the Layout Manager.
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the ArrayList that will contain the data.
        mAirportsData = new ArrayList<>();

        // Initialize the adapter and set it to the RecyclerView.
        mAdapter = new AirportsAdapter(this, mAirportsData);
        mRecyclerView.setAdapter(mAdapter);

        // Get the location.
        getDeviceLocation();
    }

    private void getDeviceLocation() {

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        getLocationPermission();
        Toast.makeText(getApplicationContext(), mLocationPermissionsGranted.toString(), Toast.LENGTH_SHORT).show();
        try{
            if(mLocationPermissionsGranted){
                Toast.makeText(getApplicationContext(), "Permission Granted To device", Toast.LENGTH_SHORT).show();
               final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){
                            Location currentLocation = (Location) task.getResult();
                            System.out.println("LOOK AT  "+currentLocation);
                            Toast.makeText(getApplicationContext(), currentLocation.toString(), Toast.LENGTH_SHORT).show();
                            HashMap<String, String> params = new HashMap<String,String>();
                            params.put("lat", String.valueOf(currentLocation.getLatitude())); // the entered data as the body.
                            params.put("lon", String.valueOf(currentLocation.getLongitude())); // the entered data as the body.
                            callAirportsApi(params,"airport/getNearest");
                            //Snackbar.make(rootView,"You can now search cafes",Snackbar.LENGTH_SHORT).show();

                        }else{
                            //Snackbar.make(rootView,"Location not found!",Snackbar.LENGTH_SHORT).show();

                        }
                    }
              });

            }
        }catch (SecurityException e){
            //Snackbar.make(rootView,e.getMessage(),Snackbar.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationPermissionsGranted = false;
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionsGranted = false;
                            return;
                        }
                    }
                    //init();
                    mLocationPermissionsGranted = true;
                }
            }
        }
    }

    public void onSearch(View v){
        edit_txt = (EditText) findViewById(R.id.input_search);
        String s = edit_txt.getText().toString().trim();
        if (s == null || s.length() == 0) {
            Toast.makeText(getApplicationContext(), "Please enter text.", Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(getApplicationContext(), "Searching....", Toast.LENGTH_SHORT).show();
            HashMap<String, String> params = new HashMap<String,String>();
            params.put("query", String.valueOf(s)); // the entered data as the body.
            callAirportsApi(params,"airport/search");
    }

    private void getLocationPermission(){

        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this,
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this,
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mLocationPermissionsGranted = true;

            }else{
                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        }else{
            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    public List<Airport> callAirportsApi(final HashMap<String, String> params ,final String url){
        List<Airport> result = new ArrayList<Airport>();
        final Context con = this;

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        final String URL = Config.BACKEND_URL + url; // your URL
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
                                            System.out.println("Success Reponse");
                                            JSONArray arr = (JSONArray)response.get("data");
                                            setData(arr);
                                            if(arr.length() > 0){
                                                Toast.makeText(getApplicationContext(), "Airport list updated.", Toast.LENGTH_SHORT).show();
                                            }else{
                                                Toast.makeText(getApplicationContext(), "No airports found.", Toast.LENGTH_SHORT).show();
                                            }
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
        return result;
    }


    /**
     * Initialize the sports data from resources.
     */
    private void setData(JSONArray arr) {
        mAirportsData.clear();
        try {
            for (int i = 0; i < arr.length(); i++) {
                System.out.println(arr.get(i));
                JSONObject o = (JSONObject)arr.get(i);
                Airport a = new Airport(o.getString("_id"),o.getString("name"),
                        o.getString("city"),o.getString("country"),o.getString("iata"),
                        o.getString("latitude"),o.getString("longitude"));
                mAirportsData.add(a);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Notify the adapter of the change.
        mAdapter.notifyDataSetChanged();
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
                Toast.makeText(this, "Future feature", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_logout:
                logout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public void logout(){
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
                        HashMap<String,String> user = sqLiteHandler.getLoggedInUser();
                        sqLiteHandler.logoutUser(user.get("email"));
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        Toast.makeText(getBaseContext(), "Login to continue", Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
    }
}
