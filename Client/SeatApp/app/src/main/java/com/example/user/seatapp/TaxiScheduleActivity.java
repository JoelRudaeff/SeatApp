package com.example.user.seatapp;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.widget.TextView;

public class TaxiScheduleActivity extends ActionBarActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taxi_schedule);

        try
        {
            Bundle extras = getIntent().getExtras();

            if(extras.getString("Current") != " ") // if the user entered a current place, continue...
            {
                String s = extras.getString("Current");

                TextView temp = (TextView) findViewById(R.id.Taxi_Current);
                temp.setText(s);
            }

            if (extras.getString("Destinaion") != " ") // if the user entered a destination place, continue...
            {
                String s = extras.getString("Destination");

                TextView temp = (TextView) findViewById(R.id.Taxi_Destination);
                temp.setText(s);
            }
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
