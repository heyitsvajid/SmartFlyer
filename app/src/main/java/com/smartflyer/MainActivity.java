package com.smartflyer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = new Intent(MainActivity.this, WaitTimeFeedActivity.class);
        startActivity(intent);
    }

    public void skipLogin(View v){
        Intent intent = new Intent(MainActivity.this, SearchAirport.class);
        startActivity(intent);
    }
}
