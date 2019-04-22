package com.smartflyer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/***
 * Detail Activity that is launched when a list item is clicked.
 * It shows more info on the sport.
 */
public class DetailActivity extends AppCompatActivity {

    // Initialize the views.
    private SQLiteHandler sqLiteHandler;
    private NumberPicker hourPicker;
    private NumberPicker minutePicker;
    private RelativeLayout waitTimeForm;
    private ListView listView;
    private TextView iata;
    private TextView name;
    private TextView address;
    private TextView averageWaitTime;
    private ImageView mAirportImage;
    private String id = "";


    BarChart chart ;
    ArrayList<BarEntry> BARENTRY ;
    ArrayList<String> BarEntryLabels ;
    BarDataSet Bardataset ;
    BarData BARDATA ;

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
        waitTimeForm = (RelativeLayout) findViewById(R.id.waitTimeForm);
        listView = (ListView) findViewById(R.id.waitTimeList);

        setLayoutVisible(waitTimeForm);

        //Bar Chart Code
        chart = (BarChart) findViewById(R.id.chart1);

        BarData data = new BarData(getXAxisValues(), getDataSet());
        chart.setData(data);
      chart.setDescription(" ");
        chart.animateXY(2000, 2000);
        chart.invalidate();
        chart.setBackgroundColor(Color.WHITE);
        chart.getXAxis().setDrawGridLines(false);

        hourPicker = findViewById(R.id.hourPicker);
        minutePicker = findViewById(R.id.minutePicker);
        hourPicker.setMaxValue(10);
        hourPicker.setMinValue(0);
        minutePicker.setMaxValue(60);
        minutePicker.setMinValue(0);

        // Initialize the views.
        iata = findViewById(R.id.iata);
        name = findViewById(R.id.name);
        address = findViewById(R.id.address);
        averageWaitTime = findViewById(R.id.averageWaitTime);
        mAirportImage = findViewById(R.id.airportImage);

        id = getIntent().getStringExtra("_id");
        if (id != null && !id.equals("")) {
            getAirportStats(id);
        }
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

    private ArrayList getDataSet() {
        ArrayList dataSets = null;
        ArrayList valueSet1 = new ArrayList();
        BarEntry v1e1 = new BarEntry(110.000f, 0); // 0-4
        valueSet1.add(v1e1);
        BarEntry v1e2 = new BarEntry(40.000f, 1); // 4-8
        valueSet1.add(v1e2);
        BarEntry v1e3 = new BarEntry(60.000f, 2); // 8-12
        valueSet1.add(v1e3);
        BarEntry v1e4 = new BarEntry(30.000f, 3); // 12-16
        valueSet1.add(v1e4);
        BarEntry v1e5 = new BarEntry(90.000f, 4); // 16-20
        valueSet1.add(v1e5);
        BarEntry v1e6 = new BarEntry(100.000f, 5); // 20-24
        valueSet1.add(v1e6);

        BarDataSet barDataSet1 = new BarDataSet(valueSet1, "Average Wait Time Minuutes");
//        barDataSet1.setColor(Color.rgb(0, 155, 0));
        barDataSet1.setColors(ColorTemplate.COLORFUL_COLORS);

        dataSets = new ArrayList();
        dataSets.add(barDataSet1);
//        dataSets.add(barDataSet2);
        return dataSets;
    }

    private ArrayList getXAxisValues() {
        ArrayList xAxis = new ArrayList();
        xAxis.add("0-4");
        xAxis.add("4-8");
        xAxis.add("8-12");
        xAxis.add("12-16");
        xAxis.add("16-20");
        xAxis.add("20-24");
        return xAxis;
    }

    @Override
    protected void onStart() {
        //check user login state
        HashMap<String, String> user = sqLiteHandler.getLoggedInUser();
        if (user == null) {
            Intent intent = new Intent(DetailActivity.this, MainActivity.class);
            startActivity(intent);
            Toast.makeText(getBaseContext(), "Login to continue", Toast.LENGTH_LONG).show();
            finish();
        }
        super.onStart();
    }

    public void getAirportStats(final String id) {

        final Context con = this;
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        HashMap<String, String> params = new HashMap<String, String>();
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
                                            //User Trends Data
                                            JSONArray waitTimes = (JSONArray) response.get("waittimes");
                                            System.out.println(waitTimes);
                                            List<String> wait = new ArrayList<>();
                                            for (int i = 0; i < waitTimes.length(); i++) {
                                                JSONObject current = (JSONObject) waitTimes.get(i);
                                                String waitString = current.getString("created");

                                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                                                sdf.setTimeZone(TimeZone.getDefault());
                                                Date pubDate = sdf.parse(waitString);
                                                SimpleDateFormat dateString = new SimpleDateFormat("MM-dd-yyyy");
                                                SimpleDateFormat timeString = new SimpleDateFormat("HH:mm:ss");

                                                waitString = "Waited " +
                                                        current.getString("wait") + " mins" +
                                                        " on " + new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(pubDate) +
                                                        " at " + new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(pubDate);
                                                wait.add(waitString);
                                            }
                                            setListViewItems(wait);
                                        } catch (JSONException e) {
                                            System.out.println("Inner NOT WORKING!!" + e.getMessage());
                                            e.printStackTrace();
                                        } catch (ParseException e) {
                                            System.out.println("Inner NOT WORKING!!" + e.getMessage());
                                            e.printStackTrace();
                                        }
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                System.out.println("Outer NOT WORKING!!" + error.getMessage());
                            }
                        });
                        queue.add(jsObjRequest);
                    }
                }, 3000);
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
                Toast.makeText(this, "Future feature", Toast.LENGTH_SHORT).show();
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
        final String id = this.id;
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
                                            setLayoutInvisible(waitTimeForm);
                                            getAirportStats(id);
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

    public void getUserTrendsS() {

        final String id = getIntent().getStringExtra("_id");
        final Context con = this;
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        HashMap<String, String> params = new HashMap<String, String>();
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

                                            for (int i = 0; i < waitTimes.length(); i++) {
                                                JSONObject current = (JSONObject) waitTimes.get(i);
                                                String waitString = current.getString("created");
                                                try {
                                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                                                    Date pubDate = sdf.parse(waitString);
                                                    SimpleDateFormat dateString = new SimpleDateFormat("MM-dd-yyyy");
                                                    SimpleDateFormat timeString = new SimpleDateFormat("HH:mm:ss");

                                                    waitString = "Waited " +
                                                            current.getString("wait") + " mins" +
                                                            " on " + new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(pubDate) +
                                                            " at " + new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(pubDate);

                                                    //pubDate.get   new String(pubDate.toString());
                                                } catch (Exception e) {
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
                                System.out.println("Outer NOT WORKING!!" + error.getMessage());
                            }
                        });
                        queue.add(jsObjRequest);
                    }
                }, 3000);

    }

    public void setListViewItems(List<String> wait) {
        if (wait.isEmpty()) {
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