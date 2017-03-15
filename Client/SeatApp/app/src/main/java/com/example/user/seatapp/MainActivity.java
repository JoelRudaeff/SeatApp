package com.example.user.seatapp;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;

public class MainActivity extends ActionBarActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button Train_schedule_button = (Button) findViewById(R.id.Train_button);
        final Button Bus_schedule_button = (Button) findViewById(R.id.Bus_button);
        final Button Taxi_schedule_button = (Button) findViewById(R.id.Taxi_button);
    }

    public void startBuses(View view) // when the button is clicked, open this activity
    {
        Intent intent = new Intent(this, BusesActivity.class);
        startActivity(intent);
    }

    public void startTrains(View view) // when the button is clicked, open this activity
    {
        Intent intent = new Intent(this, TrainsActivity.class);
        startActivity(intent);
    }

    public void startTaxis(View view) // when the button is clicked, open this activity
    {
        Intent intent = new Intent(this, TaxiScheduleActivity.class);
        startActivity(intent);
    }


}
