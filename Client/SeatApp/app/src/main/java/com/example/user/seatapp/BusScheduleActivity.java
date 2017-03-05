package com.example.user.seatapp;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.widget.TextView;

public class BusScheduleActivity extends ActionBarActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_schedule);

        try
        {
            Bundle extras = getIntent().getExtras();

            if(extras.getString("Current") != " ")
            {
                String s = extras.getString("Current");

                TextView temp = (TextView) findViewById(R.id.Bus_Current);
                temp.setText(s);
            }

            if(extras.getString("Destination") != " ")
            {
                String s = extras.getString("Destination");

                TextView temp = (TextView) findViewById(R.id.Bus_Destination);
                temp.setText(s);
            }
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
