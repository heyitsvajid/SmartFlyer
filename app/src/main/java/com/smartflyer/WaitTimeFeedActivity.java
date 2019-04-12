package com.smartflyer;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.loopj.android.http.*;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class WaitTimeFeedActivity extends AppCompatActivity {

    private TextView mTextMessage;
    TextView airportName;
    TextView terminal;
    EditText noOfPeople;
    EditText noOfActiveQueues;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.title_dashboard);
                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText(R.string.title_notifications);
                    return true;
            }

            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wait_time_feed);

         airportName = (TextView)findViewById(R.id.airport_name);
         terminal = (TextView)findViewById(R.id.terminal);
         noOfActiveQueues = (EditText)findViewById(R.id.no_of_active_queues);
         noOfPeople = (EditText)findViewById(R.id.no_of_people_before);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    public void feedData(View v){
        System.out.println("Method CALLED!!!");
        final RequestQueue queue = Volley.newRequestQueue(this);
        final String url = "http://localhost:3000/wait-times/insert"; // your URL

        queue.start();

                HashMap<String, String> params = new HashMap<String,String>();
                params.put("aiportName", airportName.getText().toString()); // the entered data as the body.
                params.put("terminal",terminal.getText().toString());
                params.put("noOfActiveQueues",noOfActiveQueues.getText().toString());
                params.put("noOfPeopleBefore",noOfPeople.getText().toString());

                JsonObjectRequest jsObjRequest = new
                        JsonObjectRequest(Request.Method.POST,
                        url,
                        new JSONObject(params),
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    Toast.makeText(WaitTimeFeedActivity.this,response.getString("message"),Toast.LENGTH_SHORT);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(WaitTimeFeedActivity.this,error.getMessage().toString(),Toast.LENGTH_SHORT);
                    }
                });
                queue.add(jsObjRequest);
            }


}
