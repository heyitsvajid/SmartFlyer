package com.smartflyer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class activity_leaderboard extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private List<String> list;
    private ArrayList<user> mLeaderboardData;
    private LeaderboardAdapter adapter;
    private SQLiteHandler sqLiteHandler;
    private static final String TAG = "activity_leaderboard";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        // Initialize the RecyclerView.
        recyclerView = findViewById(R.id.leaderboard_list);

        // Set the Layout Manager.
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // Initialize the ArrayList that will contain the data.
        mLeaderboardData = new ArrayList<>();

        //list = Arrays.asList(getResources().getStringArray(R.array.user));
        callAllUsers("user/getLeaderBoardData");

        // Initialize the adapter and set it to the RecyclerView.
        adapter = new LeaderboardAdapter(this,mLeaderboardData);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
    }


//    @Override
//    protected void onStart() {
//        super.onStart();
//        //check user login state
//        HashMap<String,String> user = sqLiteHandler.getLoggedInUser();
//        if(user!=null){
//
//        }else{
//            Intent intent = new Intent(activity_leaderboard.this, MainActivity.class);
//            startActivity(intent);
//            Toast.makeText(getBaseContext(), "Login to continue", Toast.LENGTH_LONG).show();
//            finish();
//        }
//    }


    public List<user> callAllUsers(final String url){
        List<user> result = new ArrayList<user>();
        final Context con = this;
        final HashMap<String, String> params = new HashMap<String, String>();
        params.put("name", "tejal"); // the entered data as the body.

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        final String URL = Config.BACKEND_URL + url; // your URL
                        final RequestQueue queue = Volley.newRequestQueue(con);
                        queue.start();
                        JsonObjectRequest jsObjRequest = new
                                JsonObjectRequest(Request.Method.GET,
                                URL,
                                null,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        try {

                                            Log.w(TAG, url+ " Response: " + response.toString());
                                            JSONArray arr = (JSONArray)response.get("data");
                                            Log.w(TAG, url+ " Response: " + response.toString());
                                            setData(arr);
                                            if(arr.length() > 0){
                                                Toast.makeText(getApplicationContext(), "User list updated.", Toast.LENGTH_SHORT).show();
                                            }else{
                                                Toast.makeText(getApplicationContext(), "No Users found.", Toast.LENGTH_SHORT).show();
                                            }
                                        } catch (JSONException e) {
                                            Log.w(TAG, url+ " JSONException: " + e.getMessage());
                                            e.printStackTrace();
                                        }
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.w(TAG, url+ " ERROR here: " + error.getMessage());
                            }
                        });
                        queue.add(jsObjRequest);
                    }
                }, 3);
        return result;
    }


    /**
     * Initialize the sports data from resources.
     */
    private void setData(JSONArray arr) {
        mLeaderboardData.clear();

        try {
            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = (JSONObject)arr.get(i);
                user a = new user(o.getString("_id"),o.getString("name"),o.getString("email"),
                        o.getInt("leaderboard_count")
                        );
                mLeaderboardData.add(a);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Notify the adapter of the change.
        adapter.notifyDataSetChanged();
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
                RatingDialogue ratingDialogue = new RatingDialogue(activity_leaderboard.this);
                ratingDialogue.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                ratingDialogue.setCancelable(true);
                ratingDialogue.setTitle("Rate App");
                ratingDialogue.show();
                return true;
            case R.id.leader_board:
                Intent intent = new Intent(activity_leaderboard.this, activity_leaderboard.class);
                startActivity(intent);
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


}
