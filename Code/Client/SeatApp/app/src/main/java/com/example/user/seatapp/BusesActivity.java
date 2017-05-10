package com.example.user.seatapp;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class BusesActivity extends ActionBarActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buses);

        Spinner current_spinner = (Spinner) findViewById(R.id.CurrentSpinner);
        Spinner destination_spinner = (Spinner) findViewById(R.id.DestinationSpinner);
        Spinner company_spinner = (Spinner) findViewById(R.id.CompanySpinner);
        Spinner time_spinner = (Spinner) findViewById(R.id.TimeSpinner);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> places_adapter = ArrayAdapter.createFromResource(this, R.array.cities_array, android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> companies_adapter = ArrayAdapter.createFromResource(this, R.array.bus_companies_array, android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> time_adapter = ArrayAdapter.createFromResource(this, R.array.trains_time, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears - the spinner type
        places_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        companies_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        time_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        current_spinner.setAdapter(places_adapter);
        destination_spinner.setAdapter(places_adapter);
        company_spinner.setAdapter(companies_adapter);
        time_spinner.setAdapter(time_adapter);
    }

    public void startBusScheduleActivity(View view)
    {
        Spinner current_spinner = (Spinner) findViewById(R.id.CurrentSpinner);
        Spinner destination_spinner = (Spinner) findViewById(R.id.DestinationSpinner);
        Spinner company_spinner = (Spinner) findViewById(R.id.CompanySpinner);
        Spinner leaving_time_spinner = (Spinner) findViewById(R.id.TimeSpinner);

        Intent intent = new Intent(this, BusScheduleActivity.class);
        intent.putExtra("current", current_spinner.getSelectedItem().toString());
        intent.putExtra("destination", destination_spinner.getSelectedItem().toString());
        intent.putExtra("company", company_spinner.getSelectedItem().toString());
        intent.putExtra("leaving time", leaving_time_spinner.getSelectedItem().toString());

        startActivity(intent);
    }

}
