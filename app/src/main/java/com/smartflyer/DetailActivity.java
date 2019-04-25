package com.smartflyer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

/***
 * Detail Activity that is launched when a list item is clicked.
 * It shows more info on the sport.
 */
public class DetailActivity extends AppCompatActivity {

    private static final String TAG = "DetailActivity";
    private SQLiteHandler sqLiteHandler;
    private String id;
    BarChart chart;
    ArrayList<BarEntry> BARENTRY;
    ArrayList<String> BarEntryLabels;
    BarDataSet Bardataset;
    BarData BARDATA;
    private NumberPicker hourPicker;
    private NumberPicker minutePicker;
    private RelativeLayout waitTimeForm;
    private ListView listView;
    private TextView iata;
    private TextView name;
    private TextView address;
    private TextView averageWaitTime;
    private TextView lastSixHourAverageWaitTime;
    private ImageView mAirportImage;

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
        chart = (BarChart) findViewById(R.id.chart1);

        setLayoutVisible(waitTimeForm);
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
        lastSixHourAverageWaitTime = findViewById(R.id.lastSixHourAverage);
        mAirportImage = findViewById(R.id.airportImage);

        id = getIntent().getStringExtra("_id");
        if (id != null && !id.equals("")) {
            getAirportStats(id);
        }
        // Set the text from the Intent extra.
        iata.setText(getIntent().getStringExtra("iata"));
        name.setText(getIntent().getStringExtra("name"));
        address.setText(getIntent().getStringExtra("city") + ", " + getIntent().getStringExtra("country"));
        // Load the image using the Glide library and the Intent extra.
        Glide.with(this)
                .load(getIntent().getStringExtra("image")) // image url
                .placeholder(R.drawable.app_logo) // any placeholder to load at start
                .error(R.drawable.app_logo)  // any image in case of error
                .override(200, 200) // resizing
                .centerCrop()
                .into(mAirportImage);  // imageview object

    }

    private ArrayList getDataSet(HashMap<String,Long> graphData) {
        ArrayList dataSets = null;
        ArrayList valueSet1 = new ArrayList();
        BarEntry v1e1 = new BarEntry(graphData.get("0-3"), 0); // 0-4
        valueSet1.add(v1e1);
        BarEntry v1e2 = new BarEntry(graphData.get("4-7"), 1); // 4-8
        valueSet1.add(v1e2);
        BarEntry v1e3 = new BarEntry(graphData.get("8-11"), 2); // 8-12
        valueSet1.add(v1e3);
        BarEntry v1e4 = new BarEntry(graphData.get("12-15"), 3); // 12-16
        valueSet1.add(v1e4);
        BarEntry v1e5 = new BarEntry(graphData.get("16-19"), 4); // 16-20
        valueSet1.add(v1e5);
        BarEntry v1e6 = new BarEntry(graphData.get("20-23"), 5); // 20-24
        valueSet1.add(v1e6);

        BarDataSet barDataSet1 = new BarDataSet(valueSet1, "Average Wait Time Minutes");
        barDataSet1.setColors(ColorTemplate.JOYFUL_COLORS);

        dataSets = new ArrayList();
        dataSets.add(barDataSet1);
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
                                            Log.w(TAG, "airport/getOne Response :" + response.toString());
                                            //User Trends Data
                                            String averageWaitTime = response.get("averageWaitTime").toString();
                                            JSONArray waitTimes = (JSONArray) response.get("waittimes");

                                            //Wait time distribution
                                            HashMap<String,Long> graphData = new HashMap<>();
                                            HashMap<String,Long> graphCount = new HashMap<>();

                                            graphData.put("0-3",0l);
                                            graphData.put("4-7",0l);
                                            graphData.put("8-11",0l);
                                            graphData.put("12-15",0l);
                                            graphData.put("16-19",0l);
                                            graphData.put("20-23",0l);
                                            graphCount.put("0-3",0l);
                                            graphCount.put("4-7",0l);
                                            graphCount.put("8-11",0l);
                                            graphCount.put("12-15",0l);
                                            graphCount.put("16-19",0l);
                                            graphCount.put("20-23",0l);
                                            //Last 6 hour Average
                                            int count = 0;
                                            long minutes = 0;

                                            List<String> averageWaitList = new ArrayList<>();
                                            int currentHour = new Date().getHours();
                                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                                            sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
                                            for (int i = 0; i < waitTimes.length(); i++) {
                                                JSONObject current = (JSONObject) waitTimes.get(i);
                                                String waitString = current.getString("created");
                                                Date pubDate = sdf.parse(waitString);
                                                SimpleDateFormat dateString = new SimpleDateFormat("MM-dd-yyyy");
                                                dateString.setTimeZone(TimeZone.getDefault());
                                                SimpleDateFormat timeString = new SimpleDateFormat("HH:mm:ss");
                                                timeString.setTimeZone(TimeZone.getDefault());
                                                int waitTimeHour = Integer.parseInt(timeString.format(pubDate).substring(0,2));
                                                switch (waitTimeHour){
                                                    case 0:
                                                    case 1:
                                                    case 2:
                                                    case 3:
                                                        graphData.put("0-3",graphData.get("0-3")+Long.parseLong(current.getString("wait")));
                                                        graphCount.put("0-3",graphCount.get("0-3")+1);

                                                        break;
                                                    case 4:
                                                    case 5:
                                                    case 6:
                                                    case 7:
                                                        graphData.put("4-7",graphData.get("4-7")+Long.parseLong(current.getString("wait")));
                                                        graphCount.put("4-7",graphCount.get("4-7")+1);
                                                        break;
                                                    case 8:
                                                    case 9:
                                                    case 10:
                                                    case 11:
                                                        graphData.put("8-11",graphData.get("8-11")+Long.parseLong(current.getString("wait")));
                                                        graphCount.put("8-11",graphCount.get("8-11")+1);
                                                        break;
                                                    case 12:
                                                    case 13:
                                                    case 14:
                                                    case 15:
                                                        graphData.put("12-15",graphData.get("12-15")+Long.parseLong(current.getString("wait")));
                                                        graphCount.put("12-15",graphCount.get("12-15")+1);
                                                        break;
                                                    case 16:
                                                    case 17:
                                                    case 18:
                                                    case 19:
                                                        graphData.put("16-19",graphData.get("16-19")+Long.parseLong(current.getString("wait")));
                                                        graphCount.put("16-19",graphCount.get("16-19")+1);
                                                        break;
                                                    case 20:
                                                    case 21:
                                                    case 22:
                                                    case 23:
                                                        graphData.put("20-23",graphData.get("20-23")+Long.parseLong(current.getString("wait")));
                                                        graphCount.put("20-23",graphCount.get("20-23")+1);
                                                        break;
                                                }
                                                int date = Integer.parseInt(dateString.format(pubDate).substring(3,5));

                                                if(date == new Date().getDate() && Math.abs(currentHour-waitTimeHour)<=6){
                                                    count++;
                                                    minutes+=Long.parseLong(current.getString("wait"));
                                                    Log.w(TAG, "Count :" + count);
                                                    Log.w(TAG, "Minutes :" + minutes);
                                                }
                                                Log.w(TAG, "Current Hour: " + currentHour);
                                                Log.w(TAG, "Submitted Hour: " + waitTimeHour);
                                                waitString = "Waited " +
                                                        current.getString("wait") + " mins" +
                                                        " on " + dateString.format(pubDate) +
                                                        " at " + timeString.format(pubDate);
                                                averageWaitList.add(waitString);
                                            }
                                            String lastSixHourAverageWaitTime = "N/A";
                                            Log.w(TAG,"Graph Minutes: " + graphData.toString());
                                            Log.w(TAG,"Graph Count: " + graphCount.toString());

                                            if(graphCount.get("0-3")>0){
                                                graphData.put("0-3",graphData.get("0-3")/graphCount.get("0-3"));
                                            }
                                            if(graphCount.get("4-7")>0){
                                                graphData.put("4-7",graphData.get("4-7")/graphCount.get("4-7"));
                                            }
                                            if(graphCount.get("8-11")>0){
                                                graphData.put("8-11",graphData.get("8-11")/graphCount.get("8-11"));
                                            }
                                            if(graphCount.get("12-15")>0){
                                                graphData.put("12-15",graphData.get("12-15")/graphCount.get("12-15"));
                                            }
                                            if(graphCount.get("16-19")>0){
                                                graphData.put("16-19",graphData.get("16-19")/graphCount.get("16-19"));
                                            }
                                            if(graphCount.get("20-23")>0){
                                                graphData.put("20-23",graphData.get("20-23")/graphCount.get("20-23"));
                                            }

                                            if(count>0){
                                                lastSixHourAverageWaitTime = String.valueOf(minutes/count);
                                            }

                                            setUserTrends(averageWaitTime,lastSixHourAverageWaitTime,averageWaitList,graphData);
                                        } catch (JSONException e) {
                                            Log.w(TAG, "JSONException :" + e.getMessage());
                                            e.printStackTrace();
                                        } catch (ParseException e) {
                                            Log.w(TAG, "ParseException :" + e.getMessage());
                                            e.printStackTrace();
                                        }
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.w(TAG, "airport/getOne ERROR:" + error.getMessage());
                            }
                        });
                        queue.add(jsObjRequest);
                    }
                }, 1);
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
                                        Log.w(TAG, "airport/addUserWaitTime Response: " + response.toString());
                                        Toast.makeText(con, "Time Submitted Successfully.", Toast.LENGTH_LONG).show();
                                            setLayoutInvisible(waitTimeForm);
                                            getAirportStats(id);
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(con, "Error submitting time. Try Later.", Toast.LENGTH_LONG).show();
                                Log.w(TAG, "airport/addUserWaitTime ERROR: " + error.getMessage());
                            }
                        });
                        queue.add(jsObjRequest);
                    }
                }, 3);
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

    public void setUserTrends(String averageWait,String lastSixHourAverageWait, List<String> waitList,HashMap<String,Long> graphData) {
        //Total Average Time
        averageWaitTime.setText(averageWait + " mins");
        lastSixHourAverageWaitTime.setText(lastSixHourAverageWait + " mins");

        //User Submissions
        if (waitList.isEmpty()) {
            waitList.add("No trends available currently.");
            waitList.add("");
        }
        WaitTimeListAdapter whatever = new WaitTimeListAdapter(this, waitList);
        listView.setAdapter(whatever);
        setListViewHeightBasedOnChildren(listView);

        //Wait Time Distribution
        BarData data = new BarData(getXAxisValues(), getDataSet(graphData));
        chart.setData(data);
        chart.setDescription(" ");
        chart.animateXY(2000, 2000);
        chart.invalidate();
        chart.setBackgroundColor(Color.WHITE);
        chart.getXAxis().setDrawGridLines(false);
        chart.getAxisLeft().setDrawGridLines(false);
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