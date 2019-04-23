package com.smartflyer;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;


public class RatingDialogue extends Dialog implements View.OnClickListener {
    public AppCompatActivity activity;
    public Button okButton;
    private SQLiteHandler sqLiteHandler;

    public  RatingDialogue(AppCompatActivity appCompatActivity){
        super(appCompatActivity);
        this.activity =  appCompatActivity;

    }

    @Override
    protected  void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog);
        final RatingBar ratingBar = (RatingBar)findViewById(R.id.rating);
        Button okButton = (Button)findViewById(R.id.dialogButtonOK);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, String> user =  new HashMap<>();
                sqLiteHandler =  new SQLiteHandler(getContext());
                user = sqLiteHandler.getLoggedInUser();
                HashMap<String,Object> rating =  new HashMap<>();
                rating.put("userEmail",user.get("email"));
                rating.put("rating",ratingBar.getRating());

//                API CALL TO SAVE RATING
                final RequestQueue queue = Volley.newRequestQueue(getContext());
                final String url = Config.BACKEND_URL + "rating/insert"; // your URL
                queue.start();
                JsonObjectRequest jsObjRequest = new
                        JsonObjectRequest(Request.Method.POST,
                        url,
                        new JSONObject(rating),
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    System.out.println(response.get("message"));
                                    Toast.makeText(getContext(),response.get("message").toString(),Toast.LENGTH_LONG).show();
                                } catch (JSONException e) {
                                    System.out.println(e.getMessage());
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println(error.getMessage());
                        Toast.makeText(getContext(),error.getMessage().toString(),Toast.LENGTH_SHORT);
                    }
                });
                queue.add(jsObjRequest);
                
            }
        });

    }
    @Override
    public void onClick(View v) {

    }


}
